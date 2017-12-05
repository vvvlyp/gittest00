package com.pbn.boss.model.hibernate.system;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
@Entity
@Table(name="BACKUP_CONFIG")
public class BackupConfig {
	@Id
	 @GenericGenerator(name ="ALARMTYPE",strategy = "assigned")
	@Column(name ="ALARMTYPE", nullable = false)	

	    /**
	     * 1:master
	     * 2:slave
	     * 3:opsw
	     */
	   
	    private int alarmtype;
	    
	    @Column(name ="TIME")
	    private int time;
	    
	    @Column(name ="BACKUPMODE")
	    private int backupmode;
	    
	    
	  
	    

}
