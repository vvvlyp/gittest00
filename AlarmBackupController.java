package com.pbn.boss.web.system;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import com.pbn.boss.model.nms.cm.NEObject;
import com.pbn.boss.model.nms.cm.NEObjectManager;
import com.pbn.boss.model.nms.meta.NMSConfig;
import com.pbn.boss.model.system.CUserInfo;
import com.pbn.boss.model.system.AlarmIfoConfig;
import com.pbn.boss.model.system.BackupConfig;
import com.pbn.boss.web.service.ServiceLocator;

public class AlarmBackupController implements Controller {

static Logger logger = Logger.getLogger(AlarmBackupController.class);
	
	private NMSConfig nmscfg = ServiceLocator.getNMSConfig();

	@SuppressWarnings("unchecked")
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json");
		
		PrintWriter out = response.getWriter();
		
		String action = request.getParameter("action");
		String userlanclass = (String) request.getSession().getAttribute("user-language");
		
		int userid=0;
 		CUserInfo userobj=(CUserInfo)request.getSession().getAttribute("obj");
		if(userobj!=null){
		  userid=userobj.getUserID();
		}
		
		HashMap<String, String> result_map = new HashMap<String, String>();
		
		logger.info("action==="+action);
		
		try {
			
			String strSql = "select * from BACKUP_CONFIG";
			
			if(action != null){
				
				AlarmIfoConfig alarmIfo = new AlarmIfoConfig(nmscfg.getJT(), nmscfg.getDATABASETYPE());
				
				if(action.equals("getAllAlarmBackupCount")){
					
					JSONArray allArray = new JSONArray();
					JSONObject allObject = new JSONObject();
					
				
					Vector<BackupConfig> vec = alarmIfo.query(strSql);
					int result = 0;
					result = vec.size();
					allObject.put("result", result);
					allArray.put(allObject);
					
					out.print(allArray);
					out.flush();
					out.close();
					
					return null;
					
				}else if(action.equalsIgnoreCase("getalarmbackuprlist")){
					
						JSONArray alarmArr = new JSONArray();
						
						//Vector<CoreDeviceConfig> coreVec = new Vector<CoreDeviceConfig>();
						Vector<BackupConfig> alarmVec = new Vector<BackupConfig>();
						
						alarmVec = alarmIfo.query(strSql);
						
						for(BackupConfig backUpConfig: alarmVec){
							JSONObject alarmObj = new JSONObject();    
					/*
							NEObject headendObj = (NEObject)nmscfg.getNEObjectByID(coreDeviceConfig.getHeadendId());
							
						*/	
							
							alarmObj.put("id", backUpConfig.getAlarmtype());  
					
							
					
					//***
							if(backUpConfig.getAlarmtype()==1){
								alarmObj.put("type1", "活动告警");
							}else if(backUpConfig.getAlarmtype()==2){
								alarmObj.put("type1", "历史告警");
						
							}
							
							if(backUpConfig.getTime()==1){
								alarmObj.put("type2", "一天");
							}else if(backUpConfig.getTime()==2){
								alarmObj.put("type2", "一个月");
							}else if(backUpConfig.getTime()==3){
								alarmObj.put("type2", "半年");
							}else if(backUpConfig.getTime()==4){
								alarmObj.put("type2", "一年");
							}
							
							if(backUpConfig.getBackupmode()==1){
								alarmObj.put("type3", "手动备份");
							}else if(backUpConfig.getBackupmode()==2){
								alarmObj.put("type3", "自动备份");
							}
						/*	coreObj.put("odfCode1", coreDeviceConfig.getOdfCode1());
							coreObj.put("odfCode2", coreDeviceConfig.getOdfCode2());
							coreObj.put("routeLength", coreDeviceConfig.getRouteLength());
							coreObj.put("routeVia", coreDeviceConfig.getRouteVia());  */
						/*	if(parentHeadend!=null){
								alarmObj.put("parentHeadend", parentHeadend.getNEName());
							}else{
								alarmObj.put("parentHeadend", "");
							}*/
						//	alarmObj.put("parentName", parentObj.getNEName());
						//	alarmObj.put("parentIp", parentObj.getHostIP());  
							
							
							alarmArr.put(alarmObj);
						
						}
						
//						logger.info("coreArr.tostring=="+coreArr.toString());
						
						out.print(alarmArr);
						out.flush();
						out.close();
						
						return null;
				}else if(action.equals("add")){
					String headendId = "-1";
					String parentHeadendId = "-1";
					result_map.put("headend", headendId);
					result_map.put("parentHeadend", parentHeadendId);
					
					return new ModelAndView("system/addalarmifo", result_map);
					
				}/*
				 else if(action.equals("selectHeadend")){
					
					String headend = (String)request.getParameter("headend");
					
					JSONArray jsonArr = new JSONArray();
					Vector<NEObject> objVec = new Vector<NEObject>();
					NEObjectManager objMgr = new NEObjectManager(nmscfg.getJT(), nmscfg.getDATABASETYPE());
					String sql = "select * from ne_object where module_type in(5,6,16) and room_id="+headend;
					objVec = objMgr.query(sql);
					
					for(NEObject neObj : objVec){
						JSONObject jsonObject = new JSONObject();
						jsonObject.append("typeid", neObj.getNEID());
						jsonObject.append("typename", neObj.getNEName());
						jsonArr.put(jsonObject);
					}
					
					out.println(jsonArr.toString());
					out.flush();
					out.close();
					
					return null;*/
				/*}else if(action.equals("selectParentHeadend")){
					
					String headend = (String)request.getParameter("parentHeadend");
					
					JSONArray jsonArr = new JSONArray();
					Vector<NEObject> objVec = new Vector<NEObject>();
					NEObjectManager objMgr = new NEObjectManager(nmscfg.getJT(), nmscfg.getDATABASETYPE());
					String sql = "select * from ne_object where module_type in(5,6,16) and room_id="+headend;
					objVec = objMgr.query(sql);
					
					for(NEObject neObj : objVec){
						JSONObject jsonObject = new JSONObject();
						jsonObject.append("typeid", neObj.getNEID());
						jsonObject.append("typename", neObj.getNEName());
						jsonArr.put(jsonObject);
					}
					
					out.println(jsonArr.toString());
					out.flush();
					out.close();
					
					return null;
				}*/
				else if(action.equals("save")){
					
					
					String AlarmType = (String)request.getParameter("AlarmType");
					String Time = (String)request.getParameter("Time");
					String BackUpMode = (String)request.getParameter("BackUpMode");
				
					
					BackupConfig backupInfo = new BackupConfig();
				
				//	backupInfo.setAlarmtype(Integer.parseInt(nmscfg.getSeqValue("BACKUP_CONFIG")));
					backupInfo.setAlarmtype(Integer.parseInt(AlarmType));
					backupInfo.setTime(Integer.parseInt(Time));
					backupInfo.setBackupmode(Integer.parseInt(BackUpMode));
					alarmIfo.add(backupInfo);
					
					out.print("ok");
					out.flush();
					out.close();
					
					return null;
				}
			 else if(action.equals("delete")){
					
					String corelist = request.getParameter("delist");
					try
					{
						int coreid = 0;
						if(!corelist.equals("")){
							for(int i=0;i<corelist.split(",").length;i++)
							{
								coreid = Integer.parseInt(corelist.split(",")[i]);
								alarmIfo.delete(coreid);
							}
						}
						out.print(1);
					} 
					catch(Exception e)
					{
						logger.error(e.toString(),e);
						out.print(0);
					}
					out.flush();
					out.close();
					return null;
				}
			}
			
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
				
			return new ModelAndView("system/alarmbackup", result_map);
		}
	}

