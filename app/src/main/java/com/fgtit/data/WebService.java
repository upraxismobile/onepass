package com.fgtit.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.os.StrictMode;
import android.util.Base64;

public class WebService {
	
	private static WebService instance;
	
	public static WebService getInstance() {
    	if(null == instance) {
    		instance = new WebService();
    	}
    	return instance;
    }
	
	public void SeThreadPolicyt(){
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
	}
	
	public void saveToFile(String fileName, InputStream in) throws IOException { 
        FileOutputStream fos = null;    
        BufferedInputStream bis = null;    
        int BUFFER_SIZE = 1024; 
        byte[] buf = new byte[BUFFER_SIZE];    
        int size = 0;    
        bis = new BufferedInputStream(in);    
        fos = new FileOutputStream(fileName);    
        while ( (size = bis.read(buf)) != -1)     
          fos.write(buf, 0, size);    
        fos.close();    
        bis.close();    
    }
	
	public boolean WorkItemListGet(){
    	String filename=GlobalData.getInstance().GetDir()+"/work.xml";
    	if(GlobalData.getInstance().IsFileExists(filename)){
    		GlobalData.getInstance().DeleteFile(filename);
   	 	}
   	 	HttpConnSoap2 webservice = new HttpConnSoap2();
   	    String methodName = "WorkItemListGet";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	           	        
   	    InputStream inputStream = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(inputStream!=null){
   	       	//GlobalData.workList  = XmlParase.paraseWorkItemList(inputStream);        		     	        	    	        	
   	       	//XDataIO.WriteXmlFile(XmlParase.HouseIndexToXml(GlobalData.houseIndexList),filename,"utf-8");
   	       	try {
				saveToFile(filename,inputStream);
			} catch (IOException e) {
			}
   	       	return true;
   	    }else{
   	       	return false;
   	    }        	 
    }
	
	public boolean LineItemListGet(){
    	String filename=GlobalData.getInstance().GetDir()+"/line.xml";
    	if(GlobalData.getInstance().IsFileExists(filename)){
    		GlobalData.getInstance().DeleteFile(filename);
   	 	}
   	 	HttpConnSoap2 webservice = new HttpConnSoap2();
   	    String methodName = "LineItemListGet";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	           	        
   	    InputStream inputStream = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(inputStream!=null){
   	       	//GlobalData.workList  = XmlParase.paraseWorkItemList(inputStream);        		     	        	    	        	
   	        //XDataIO.WriteXmlFile(XmlParase.HouseIndexToXml(GlobalData.houseIndexList),filename,"utf-8");
   	        try {
					saveToFile(filename,inputStream);
			} catch (IOException e) {
			}
        	return true;
        }else{
        	return false;
        }        	 
    }
	
	public boolean DeptItemListGet(){
    	String filename=GlobalData.getInstance().GetDir()+"/dept.xml";
    	if(GlobalData.getInstance().IsFileExists(filename)){
    		GlobalData.getInstance().DeleteFile(filename);
   	 	}
    	
    	HttpConnSoap2 webservice = new HttpConnSoap2();
   	    String methodName = "DeptItemListGet";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	           	        
   	    InputStream inputStream = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(inputStream!=null){
   	     	//GlobalData.workList  = XmlParase.paraseWorkItemList(inputStream);        		     	        	    	        	
   	       	//XDataIO.WriteXmlFile(XmlParase.HouseIndexToXml(GlobalData.houseIndexList),filename,"utf-8");
   	       	try {
				saveToFile(filename,inputStream);
			} catch (IOException e) {
			}
   	       	return true;
   	    }else{
   	       	return false;
   	    }
    }

	public AdminItem AdminItemGet(String username){
		HttpConnSoap2 webservice = new HttpConnSoap2();
		String methodName = "AdminItemGet";//方法名
		ArrayList<String> paramList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		paramList.add("username");   	    valueList.add(username);

   	    InputStream inputStream = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(inputStream!=null){
   	    	ArrayList<AdminItem> resultList=XmlParase.paraseAdminItem(inputStream);
   	    	if(resultList!=null){
   	    		if(resultList.size()>0){
   	    			return resultList.get(0);
   	    		}
   	    	}
   	    }
		return null;
	}
	
	public boolean AdminItemLogonLog(String username,String datestr,String logontype,String devicesn){
		HttpConnSoap webservice = new HttpConnSoap();
   	    String methodName = "AdminItemLogonLog";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	    ArrayList<String> returnList = new ArrayList<String>();
   	    paramList.add("username");   	    valueList.add(username);
   	    paramList.add("datestr");   	    valueList.add(datestr);
   	    paramList.add("logontype");   	    valueList.add(logontype);
   	    paramList.add("devicesn");   	    valueList.add(devicesn);

   	    returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		for(int i=0;i<returnList.size();i++){
   	    			if(returnList.get(i).equals("true")){
   	    				return true;
   	    			}
   	    		}
   	    	}
   	    }
   	    return false;
	}
	
	public boolean AdminItemUpdate(AdminItem ai){
		HttpConnSoap webservice = new HttpConnSoap();
   	    String methodName = "AdminItemUpdate";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	    ArrayList<String> returnList = new ArrayList<String>();
   	    paramList.add("username");   	    valueList.add(ai.username);
   	    paramList.add("password");   	    valueList.add(ai.password);
   	    paramList.add("realname");   	    valueList.add(ai.realname);
   	    paramList.add("idcardno");   	    valueList.add(ai.idcardno);
   	    paramList.add("fingerm");   	    valueList.add(ai.fingerm);
   	    paramList.add("fingers");   	    valueList.add(ai.fingers);
   	    paramList.add("photo");   	    	valueList.add(ai.photo);

   	    returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		for(int i=0;i<returnList.size();i++){
   	    			if(returnList.get(i).equals("true")){
   	    				return true;
   	    			}
   	    		}
   	    	}
   	    }
   	    return false;
	}
	
	public boolean RecordItemAppend(RecordItem ri){
		HttpConnSoap webservice = new HttpConnSoap();
   	    String methodName = "RecordItemAppend";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	    ArrayList<String> returnList = new ArrayList<String>();
   	    paramList.add("id");   	    	valueList.add(ri.id);
   	    paramList.add("name");   	    valueList.add(ri.name);
   	    paramList.add("datetime");   	valueList.add(ri.datetime);
   	    paramList.add("lat");   	    valueList.add(ri.lat);
   	    paramList.add("lng");   	    valueList.add(ri.lng);
   	    paramList.add("worktype");   	valueList.add(ri.worktype);
   	    paramList.add("linetype");   	valueList.add(ri.linetype);
   	    paramList.add("depttype");   	valueList.add(ri.depttype);
   	    paramList.add("type");   	    valueList.add(ri.type);

   	    returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		for(int i=0;i<returnList.size();i++){
   	    			if(returnList.get(i).equals("true")){
   	    				return true;
   	    			}
   	    		}
   	    	}
   	    }
   	    return false;
	}
	
	public ArrayList<RecordItem> RecordItemListGet(String qtype,String qfed,String qdat){
		HttpConnSoap2 webservice = new HttpConnSoap2();
		String methodName = "RecordItemListGet";//方法名
		ArrayList<String> paramList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		paramList.add("qtype");   	    valueList.add(qtype);
		paramList.add("qfed");   	    valueList.add(qfed);
		paramList.add("qdat");   	    valueList.add(qdat);

   	    InputStream inputStream = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(inputStream!=null){
   	    	return XmlParase.paraseRecordItemList(inputStream);
   	    }
		return null;
	}
	
	public boolean UserItemAppend(UserItem ui){
		HttpConnSoap webservice = new HttpConnSoap();
		String methodName = "UserItemAppend";//方法名
		ArrayList<String> paramList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		ArrayList<String> returnList = new ArrayList<String>();
		paramList.clear();
		valueList.clear();
		paramList.add("id");   	    	valueList.add(ui.id);
		paramList.add("name");   	    valueList.add(ui.name);
		paramList.add("worktype");   	valueList.add(ui.worktype);
		paramList.add("linetype");   	valueList.add(ui.linetype);
		paramList.add("depttype");   	valueList.add(ui.depttype);
		paramList.add("type");   	    valueList.add(String.valueOf(ui.type));
		paramList.add("gender");   	    valueList.add(String.valueOf(ui.gender));
		paramList.add("statu");   	    valueList.add(String.valueOf(ui.statu));
		paramList.add("enroldate");   	valueList.add(ui.enroldate);
		paramList.add("phone");   	    valueList.add(ui.phone);
		paramList.add("remark");   	    valueList.add(ui.remark);
		paramList.add("cardsn");   	    valueList.add(ui.cardsn);
		paramList.add("template1");   	valueList.add(ui.template1);
		paramList.add("template2");   	valueList.add(ui.template2);
		paramList.add("photo");   	    valueList.add(ui.photo);

		returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		for(int i=0;i<returnList.size();i++){
   	    			if(returnList.get(i).equals("true")){
   	    				return true;
   	    			}
   	    		}
   	    	}
   	    }
   	    return false;
	}
	
	public boolean UserItemDelete(String id){
		HttpConnSoap webservice = new HttpConnSoap();
   	    String methodName = "UserItemDelete";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	    ArrayList<String> returnList = new ArrayList<String>();
   	    paramList.add("id");
   	    valueList.add(id);

   	    returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		for(int i=0;i<returnList.size();i++){
   	    			if(returnList.get(i).equals("true")){
   	    				return true;
   	    			}
   	    		}
   	    	}
   	    }
   	    return false;
	}
	
	public boolean UserItemIsExists(String id){
		HttpConnSoap webservice = new HttpConnSoap();
   	    String methodName = "UserItemIsExists";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	    ArrayList<String> returnList = new ArrayList<String>();
   	    paramList.add("id");
   	    valueList.add(id);

   	    returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		for(int i=0;i<returnList.size();i++){
   	    			if(returnList.get(i).equals("true")){
   	    				return true;
   	    			}
   	    		}
   	    	}
   	    }
   	    return false;
	}
	
	public boolean UserItemListGet(String qtype,String qfed,String qdat){
		String filename=GlobalData.getInstance().GetDir()+"/userslist.xml";
    	if(GlobalData.getInstance().IsFileExists(filename)){
    		GlobalData.getInstance().DeleteFile(filename);
   	 	}
    	
    	HttpConnSoap2 webservice = new HttpConnSoap2();
   	    String methodName = "UserItemListGet";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	    paramList.add("qtype");   	    valueList.add(qtype);
   	    paramList.add("qfed");   	    valueList.add(qfed);
		paramList.add("qdat");   	    valueList.add(qdat);
		
   	    InputStream inputStream = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(inputStream!=null){
   	     	//GlobalData.workList  = XmlParase.paraseWorkItemList(inputStream);        		     	        	    	        	
   	       	//XDataIO.WriteXmlFile(XmlParase.HouseIndexToXml(GlobalData.houseIndexList),filename,"utf-8");
   	       	try {
				saveToFile(filename,inputStream);
			} catch (IOException e) {
			}
   	       	return true;
   	    }else{
   	       	return false;
   	    }
	}
	
	public boolean UserItemUpdate(UserItem ui){
		HttpConnSoap webservice = new HttpConnSoap();
		String methodName = "UserItemUpdate";//方法名
		ArrayList<String> paramList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		ArrayList<String> returnList = new ArrayList<String>();
		paramList.add("id");   	    	valueList.add(ui.id);
		paramList.add("name");   	    valueList.add(ui.name);
		paramList.add("worktype");   	valueList.add(ui.worktype);
		paramList.add("linetype");   	valueList.add(ui.linetype);
		paramList.add("depttype");   	valueList.add(ui.depttype);
		paramList.add("type");   	    valueList.add(String.valueOf(ui.type));
		paramList.add("gender");   	    valueList.add(String.valueOf(ui.gender));
		paramList.add("statu");   	    valueList.add(String.valueOf(ui.statu));
		paramList.add("enroldate");   	valueList.add(ui.enroldate);
		paramList.add("phone");   	    valueList.add(ui.phone);
		paramList.add("remark");   	    valueList.add(ui.remark);
		paramList.add("cardsn");   	    valueList.add(ui.cardsn);
		paramList.add("template1");   	valueList.add(ui.template1);
		paramList.add("template2");   	valueList.add(ui.template2);
		paramList.add("photo");   	    valueList.add(ui.photo);

		returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		for(int i=0;i<returnList.size();i++){
   	    			if(returnList.get(i).equals("true")){
   	    				return true;
   	    			}
   	    		}
   	    	}
   	    }
   	    return false;
	}
	
	public boolean UploadFileToServer(String localfile,String rmfilepath,String rmfilename){
		ArrayList<String> arrayList = new ArrayList<String>();  
		ArrayList<String> brrayList = new ArrayList<String>();  
		ArrayList<String> crrayList = new ArrayList<String>();  
		HttpConnSoap Soap = new HttpConnSoap();
		arrayList.clear();  
		brrayList.clear();
		crrayList.clear();
		arrayList.add("filename");	brrayList.add(rmfilepath);
		arrayList.add("filepath");	brrayList.add(rmfilepath);
		String xmldat="";
		try{  
			File file=new File(localfile);                
			FileInputStream  fis= new FileInputStream(file);
			int count=(int) file.length();
			byte[] buffer = new byte[count];
			fis.read(buffer);
			xmldat=Base64.encodeToString(buffer,Base64.DEFAULT);
			fis.close();  
		}catch(Exception e){  
		} 
		arrayList.add("filexml");	brrayList.add(xmldat);
		crrayList=Soap.GetWebServre(GlobalData.getInstance().WebService,"UploadFileToServer", arrayList, brrayList);
		if(crrayList!=null){
			if(crrayList.size()>0){
  				 for(int i=0;i<crrayList.size();i++){
  					 if(crrayList.get(i).equals("true")){
  						 return true;
  					 }
  				 }
  			 }
		}
		return false;
	}
	
	public int ServerMatchFingerprint(String pSrc,String pDst){
		HttpConnSoap webservice = new HttpConnSoap();
		String methodName = "ServerMatchFingerprint";//方法名
		ArrayList<String> paramList = new ArrayList<String>();
		ArrayList<String> valueList = new ArrayList<String>();
		ArrayList<String> returnList = new ArrayList<String>();
		paramList.add("pSrc");	valueList.add(pSrc);
		paramList.add("pDst");	valueList.add(pDst);
   	    returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		return Integer.valueOf(returnList.get(0));
   	    	}
   	    }
   	    return 0;
	}
	
	public int ServerLoadUsersList(){
		HttpConnSoap webservice = new HttpConnSoap();
   	    String methodName = "ServerLoadUsersList";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	    ArrayList<String> returnList = new ArrayList<String>();

   	    returnList = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(returnList!=null){
   	    	if(returnList.size()>0){
   	    		return Integer.valueOf(returnList.get(0));
   	    	}
   	    }
   	    return 0;
	}
	
	public UserItem ServerUserItemOfMatch(String fingerprint, int score){
    	HttpConnSoap2 webservice = new HttpConnSoap2();
   	    String methodName = "ServerUserItemOfMatch";//方法名
   	    ArrayList<String> paramList = new ArrayList<String>();
   	    ArrayList<String> valueList = new ArrayList<String>();
   	    paramList.add("fingerprint");	valueList.add(fingerprint);
   	    paramList.add("score");   	    valueList.add(String.valueOf(score));
				
   	    InputStream inputStream = webservice.GetWebServre(GlobalData.getInstance().WebService,methodName, paramList, valueList);
   	    if(inputStream!=null){
   	    	ArrayList<UserItem> resultList=XmlParase.paraseUserItemList(inputStream);
   	    	if(resultList!=null){
   	    		if(resultList.size()>0){
   	    			return resultList.get(0);
   	    		}
   	    	}
   	    }
   	    return null;
	}
}
