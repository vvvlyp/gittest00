package com.pbn.boss.model.system;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.pbn.boss.model.nms.meta.NMSConfig;
import com.pbn.boss.model.system.CQuery;

public class AlarmIfoConfig implements CQuery{
static Logger logger = Logger.getLogger(AlarmIfoConfig.class);
    
    private JdbcTemplate jt;
    int dbtype=NMSConfig.DATABASE_MySQL;
    
    public AlarmIfoConfig(JdbcTemplate jt,int dbtype){
        
        this.jt = jt;
        this.dbtype = dbtype;
    }

  public Object getObject(String strSQL) {
 
        return getBackupConfigInfo(strSQL);
    }

    public Vector query(String strSQL) {
 
        return getBackupConfigList(strSQL);
    }
    
    private BackupConfig getBackupConfigInfo(String StrSql){
        class SRowReader implements ResultSetExtractor
        {
           public BackupConfig obj=null;
           public Object extractData(ResultSet rs) throws SQLException,DataAccessException 
             {
                if(rs.next()){
                    obj = new BackupConfig();          
                  
                    
                    obj.setAlarmtype(rs.getInt("ALARMTYPE"));
                    obj.setTime(rs.getInt("TIME"));
                    obj.setBackupmode(rs.getInt("BACKUPMODE"));

                   
                }
                return null;
             }//callback method
        }
         SRowReader reader=new SRowReader();  
         jt.query(StrSql,reader);
         return reader.obj;
    }
    
    private Vector getBackupConfigList(String strSql){

        class MRowReader implements RowCallbackHandler
        {
               public Vector vlist=new Vector();
               public void processRow(ResultSet rs) throws SQLException 
                {
            	    BackupConfig obj = new BackupConfig();          
                  
            	    obj.setAlarmtype(rs.getInt("ALARMTYPE"));
                    obj.setTime(rs.getInt("TIME"));
                    obj.setBackupmode(rs.getInt("BACKUPMODE"));

                    vlist.add(obj);
                }//callback method
        }
        MRowReader reader=new MRowReader();
        jt.query(strSql,reader);
        return reader.vlist;
    }
  //this is new change  
    public int delete(BackupConfig obj){
        String strSQL="delete from BACKUP_CONFIG where " +
                "ALARMTYPE="+obj.getAlarmtype();
        return jt.update(strSQL);
    }
    
    public int delete(int id){
        String strSQL="delete from BACKUP_CONFIG where " +
                "ALARMTYPE="+id;
        return jt.update(strSQL);
    }
   

    public void add(BackupConfig obj){
   
        String strSql = "insert into BACKUP_CONFIG("+
                        "ALARMTYPE, " +
                        "TIME, " +
                        "BACKUPMODE) VALUES( "
                + obj.getAlarmtype() + ","
                + obj.getTime() + "," 
                + obj.getBackupmode()+")";
                
        logger.info(strSql);
        jt.update(strSql);  
    }

}
