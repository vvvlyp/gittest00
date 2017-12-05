package com.pbn.boss.web.system;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import com.pbn.boss.model.nms.meta.NMSConfig;
import com.pbn.boss.model.nms.meta.SystemDBBackup;
import com.pbn.boss.model.nms.meta.SystemDBBackupManager;
import com.pbn.boss.model.nms.meta.SystemDBBackupPoint;
import com.pbn.boss.model.system.CAgentInfo;
import com.pbn.boss.model.system.CAgentInfoManager;
import com.pbn.boss.model.system.CSystemFunction;
import com.pbn.boss.model.system.CSystemFunctionManager;
import com.pbn.boss.util.ScheduleJobProxy;
import com.pbn.boss.web.service.ServiceLocator;
import com.pbn.boss.util.TypeChange;

/**
 * 
 * <b>Application name:</b>NMSE<br>
 * <b>Application describing:</b>about the database backup management controller <br>
 * <b>Copyright:</b>Copyright &copy; 2013 PBN>
 * <b>Company:</b>PBN<br>
 * <b>Date:</b>2013-11-27<br>
 * @author walt
 * @version $Revision: 1.14 $
 */
public class AlarmBackupTest implements Controller
{

    static Logger logger = Logger.getLogger(AlarmBackupTest.class);
    NMSConfig nmscfg = ServiceLocator.getNMSConfig();
    
    
    @SuppressWarnings("unchecked")
    public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse rep) throws Exception
    {
        req.setCharacterEncoding("utf-8");
        rep.setCharacterEncoding("utf-8");
        rep.setContentType("text/json");
        
        String userlanclass = (String)req.getSession().getAttribute("user-language");
        //String leadoption = "";
        
        PrintWriter out = rep.getWriter();
        
        String action = req.getParameter("action");
        
        CAgentInfoManager agentInfoMgr = new CAgentInfoManager(nmscfg.getJT(),nmscfg.getDATABASETYPE());
        
        logger.info("action=="+action);
        
        SystemDBBackupManager dbMgr = null;
        SystemDBBackup dbBackup = null;
        Vector<SystemDBBackup> vector = null;
        
        if(action != null){
            
            
        	if(action.equals("getDBname")){
        	    BasicDataSource dataSource = ServiceLocator.getDataSource();
        		String dbName = getDbNameByUrl(dataSource.getUrl(),nmscfg.getDATABASETYPE());
        		JSONArray jsonArr = new JSONArray();
        		JSONObject jsonObj = new JSONObject();
        		jsonObj.put("dbname", dbName);
        		jsonArr.put(jsonObj);
        		out.print(jsonArr);
                out.flush();
                out.close();
        		
        	}else if(action.equals("getBackupList")){//get the backup list from database
                
                logger.info("===get backup list====");
                
                dbMgr = new SystemDBBackupManager(nmscfg.getJT(), nmscfg.getDATABASETYPE());
                dbBackup = new SystemDBBackup();
                
                JSONArray jsonArr = new JSONArray();
                try
                {
                    vector = dbMgr.query("select * from system_db_backup");
                    
                    if(vector.size()>0){
                        
                        for(int i=0;i<vector.size();i++){
                            JSONObject jsonObj = new JSONObject();
                            
                            dbBackup = (SystemDBBackup)vector.get(i);
                            
                            if(dbBackup != null){
                                
                                jsonObj.put("id", dbBackup.getItemId());
                                jsonObj.put("name", dbBackup.getItemName());
                                jsonObj.put("type", dbBackup.getBackupType());
                                jsonObj.put("path", dbBackup.getBackupPath());
                                jsonObj.put("prefix", dbBackup.getFilePrefix());
                                
                                jsonArr.put(jsonObj);
                            }
                            
                        }
                    }
                }
                catch (Exception e)
                {
                   logger.error(e);
                }
                
                out.print(jsonArr);
                out.flush();
                out.close();
                
                return null;
            //create the backup to the database
            //and the backup type unique
            }else if(action.equals("createBackup")){
                
                logger.info("===create the backup===");
                String name = req.getParameter("name");
                String type = req.getParameter("type");
                String path = req.getParameter("path");
                String prefix = req.getParameter("prefix");
                
                dbMgr = new SystemDBBackupManager(nmscfg.getJT(), nmscfg.getDATABASETYPE());
                dbBackup = new SystemDBBackup();
                
                JSONObject jsonObj = new JSONObject();
                JSONArray jsonArr = new JSONArray();
                
                try
                {
                    vector = dbMgr.query("select * from system_db_backup where item_name = '" + name + "' and backup_type = '" + type + "'");
                    
                    if(vector.size()>0){
                    	jsonObj.put("result", -2);
                        jsonArr.put(jsonObj);
                        
                        out.print(jsonArr);
                        
                        out.flush();
                        out.close();
                        return null;
                    }
                    dbBackup.setItemId(Integer.parseInt(nmscfg.getSeqValue("BACKUPDB_ID")));
                    dbBackup.setItemName(name);
                    dbBackup.setBackupType(Integer.parseInt(type));
                    dbBackup.setBackupPath(path);
                    dbBackup.setFilePrefix(prefix);
                    
                    if(dbBackup != null){
                        
                        dbMgr.add(dbBackup);
                        
                        jsonObj.put("result", 0);
                        jsonArr.put(jsonObj);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    
                    jsonObj.put("result", -1);
                    jsonArr.put(jsonObj);
                }
                
                out.print(jsonArr);
                out.flush();
                out.close();
                
                return null;
             //modify backup from database   
            }else if(action.equals("modifyBackup")){
                
                logger.info("====modify database backup====");
                
                String id = req.getParameter("id");
                String name = req.getParameter("name");
                String type = req.getParameter("type");
                String path = req.getParameter("path");
                String prefix = req.getParameter("prefix");
                
                dbMgr = new SystemDBBackupManager(nmscfg.getJT(), nmscfg.getDATABASETYPE());
                dbBackup = new SystemDBBackup();
                
                JSONObject jsonObj = new JSONObject();
                JSONArray jsonArr = new JSONArray();
                
                try
                {
                    dbBackup.setItemId(Integer.parseInt(id));
                    dbBackup.setItemName(name);
                    dbBackup.setBackupType(Integer.parseInt(type));
                    dbBackup.setBackupPath(path);
                    dbBackup.setFilePrefix(prefix);
                    
                    if(dbBackup != null){
                        
                        dbMgr.update(dbBackup);
                        
                        jsonObj.put("result", 0);
                        jsonArr.put(jsonObj);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    
                    jsonObj.put("result", -1);
                    jsonArr.put(jsonObj);
                }
                
                out.print(jsonArr);
                out.flush();
                out.close();
                
                return null;
            //delete backup from database
            }else if(action.equals("deleteBackup")){
                
                logger.info("====delete database backup====");
                
                String id = req.getParameter("id");
                
                dbMgr = new SystemDBBackupManager(nmscfg.getJT(), nmscfg.getDATABASETYPE());
                dbBackup = new SystemDBBackup();
                
                JSONObject jsonObj = new JSONObject();
                JSONArray jsonArr = new JSONArray();
                
                try
                {
                    dbBackup.setItemId(Integer.parseInt(id));
                    dbMgr.deleteByItemId(dbBackup);
                    
                    jsonObj.put("result", 0);
                    jsonArr.put(jsonObj);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    
                    jsonObj.put("result", -1);
                    jsonArr.put(jsonObj);
                }
                
                out.print(jsonArr);
                out.flush();
                out.close();
                
                return null;
            //manual backup
            }else if(action.equals("manualBackup")){
                
                logger.info("====manual database backup====");
                
                String type = req.getParameter("type");
                String notes = req.getParameter("notes");
                notes = TypeChange.toUTF8(notes);
                notes = TypeChange.replaceQuotes(notes);
                
                JSONObject jsonObj = new JSONObject();
                JSONArray jsonArr = new JSONArray();
                
                Vector agents = null;
                
                try
                {
                    
                    agents = agentInfoMgr.query("select * from agent_info");
                    
                    String result = "-1";
                    
                    if(agents.size()>0){
                        
                        for(int i=0;i<agents.size();i++){
                            
                            CAgentInfo agentInfo = (CAgentInfo)agents.get(i);
                            if(agentInfo!=null && agentInfo.getStatus() == 1){
                                ScheduleJobProxy proxy = new ScheduleJobProxy();
                                proxy.setAgentInfo(agentInfo);
                                result = proxy.startManualBackupJob(type,notes);
                                break;
                            }
                        }
                    }
                    
                    if(result.equals("0")){
                        jsonObj.put("result", 0);
                        jsonArr.put(jsonObj);
                    }else {
                        jsonObj.put("result", -1);
                        jsonArr.put(jsonObj);
                    }
                    
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    
                    jsonObj.put("result", -1);
                    jsonArr.put(jsonObj);
                }
                
                out.print(jsonArr);
                out.flush();
                out.close();
                
                return null;
            //get restore point list
            }else if(action.equals("getRestorePointList")){
                
                logger.info("===get restore point list===");
                
                String databaseName = req.getParameter("databaseName");
                String startTime = req.getParameter("startTime");
                String endTime = req.getParameter("endTime");
                
                JSONArray jsonArr = new JSONArray();
                
                Vector agents= null;
                Vector resultList = null;
                try
                {
                    
                    agents = agentInfoMgr.query("select * from agent_info");
                    
                    if(agents.size()>0){
                        
                        for(int i=0;i<agents.size();i++){
                            
                            CAgentInfo agentInfo = (CAgentInfo)agents.get(i);
                            if(agentInfo != null){
                                ScheduleJobProxy proxy = new ScheduleJobProxy();
                                proxy.setAgentInfo(agentInfo);
                                resultList = proxy.getRestorePoint(databaseName,startTime,endTime);
                            }
                        }
                    }
                    
                    if(resultList.size()>0){
                        
                        for(int i=0;i<resultList.size();i++){
                            
                            JSONObject jsonObj = new JSONObject();
                            SystemDBBackupPoint backPoint = (SystemDBBackupPoint) resultList.get(i);
                            jsonObj.put("item_id", backPoint.getItemId());
                            jsonObj.put("database", backPoint.getBackDatabase());
                            jsonObj.put("backtype", backPoint.getBackType());
                            jsonObj.put("backtime", backPoint.getBackupTime());
                            jsonObj.put("backstatus", backPoint.getBackUpStatus());
                            jsonObj.put("backpoint_filepath", backPoint.getPointFilePath());
                            jsonObj.put("notes", backPoint.getNotes());
                            jsonArr.put(jsonObj);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    
                }
                logger.info(jsonArr.toString());
                out.print(jsonArr);
                out.flush();
                out.close();
                
                return null;
            //restore database
            }else if(action.equals("restoreDatabase")){
                
                logger.info("====restore database====");
                
                String item_id = req.getParameter("item_id");
                String backup_time = req.getParameter("backup_time");
                
                JSONObject jsonObj = new JSONObject();
                JSONArray jsonArr = new JSONArray();
                Vector agents = null;
                try
                {
                    agents = agentInfoMgr.query("select * from agent_info");
                    
                    String result = "-1";
                    
                    if(agents.size()>0){
                        
                        for(int i=0;i<agents.size();i++){
                            
                            CAgentInfo agentInfo = (CAgentInfo)agents.get(i);
                            if(agentInfo!=null){
                                ScheduleJobProxy proxy = new ScheduleJobProxy();
                                proxy.setAgentInfo(agentInfo);
                                result = proxy.restoreDatabase(item_id, backup_time);
                            }
                        }
                    }
                    
                    if(result.equals("0")){
                        jsonObj.put("result", 0);
                        jsonArr.put(jsonObj);
                        ServiceLocator.getWebJMSProxy().sendConfigMessage(null,nmscfg.MSG_DB_RESTORE);
                    }else {
                        jsonObj.put("result", -1);
                        jsonArr.put(jsonObj);
                    }
                    
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                    
                    jsonObj.put("result", -1);
                    jsonArr.put(jsonObj);
                }
                
                out.print(jsonArr);
                out.flush();
                out.close();
                
                return null;
            }
        }
        
        
        ModelAndView mv = new ModelAndView("system/dbbackup");
        //leadoption = Leadoption(userlanclass);
        //mv.addObject("leadoption", leadoption);
        mv.addObject("language", userlanclass);
        return mv;
    }
    
    
    
    private String getDbNameByUrl(String url, int databasetype) {
		// TODO Auto-generated method stub
    	String dbName = "nms3e2";
    	if(databasetype == NMSConfig.DATABASE_ORACLE){
    		dbName = url.substring(url.lastIndexOf(":") + 1,url.length());
    	}else{
    		dbName = url.substring(url.lastIndexOf("/") + 1,url.length());
    	}
		return dbName;
	}



	/**
     * display title for the current page
     * @param userlanclass
     * @param index
     * @return
     */
    private String Leadoption(String userlanclass){
        NMSConfig nmscfg=ServiceLocator.getNMSConfig();
        CSystemFunctionManager menumgr = new CSystemFunctionManager(nmscfg.getJT(),nmscfg.getDATABASETYPE());
        CSystemFunction sysfuntopobj = menumgr.getMenuObject(0, 6,nmscfg.getLANCLASS());
        CSystemFunction sysfunparentobj = menumgr.getMenuObject(6, 49,nmscfg.getLANCLASS());
        CSystemFunction sysfunchildobj = menumgr.getMenuObject(49,85,nmscfg.getLANCLASS());
        StringBuffer buffer = new StringBuffer();
        buffer.append("<div class='body_title'>");
        buffer.append("<span Align='left'><a href= "+sysfuntopobj.getFunctionURL()+" >"+nmscfg.getLabel(sysfuntopobj.getFunctionName(), userlanclass)+"/"+"</a></span>");
        buffer.append("<span Align='left'><a href= "+sysfuntopobj.getFunctionURL()+">"+nmscfg.getLabel(sysfunparentobj.getFunctionName(), userlanclass)+"/"+"</a></span>");
        buffer.append("<span Align='left'><a href="+sysfunchildobj.getFunctionURL()+" >"+nmscfg.getLabel(sysfunchildobj.getFunctionName(), userlanclass)+"</a></span>");
        buffer.append("</div>");
        return buffer.toString();
    }

}
