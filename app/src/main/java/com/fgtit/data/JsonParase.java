package com.fgtit.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.fasterxml.jackson.databind.ObjectMapper;  

public class JsonParase {
	//ObjectMapper mapper = new ObjectMapper(); 
	
	public static String UserItemListToJson(List<UserItem> list){
		String jsonresult="";	
		JSONArray jsonarray1 = new JSONArray();
		 try {
			 for(int i=0;i<list.size();i++){	                                                         
				 JSONObject jsonObj1 = new JSONObject();		
				 jsonObj1.put("id", list.get(i).id);	
				 jsonObj1.put("name", list.get(i).name);
				 jsonObj1.put("worktype", list.get(i).worktype);
				 jsonObj1.put("linetype", list.get(i).linetype);	
				 jsonObj1.put("depttype", list.get(i).depttype);
				 jsonObj1.put("type", list.get(i).type);
				 jsonObj1.put("gender", list.get(i).gender);	
				 jsonObj1.put("statu", list.get(i).statu);
				 jsonObj1.put("enroldate", list.get(i).enroldate);
				 jsonObj1.put("phone", list.get(i).phone);	
				 jsonObj1.put("remark", list.get(i).remark);
				 jsonObj1.put("cardsn", list.get(i).cardsn);
				 jsonObj1.put("template1", list.get(i).template1);	
				 jsonObj1.put("template2", list.get(i).template2);
				 //jsonObj1.put("photo", list.get(i).photo);
				 //jsonObj1.put("barcode1d", list.get(i).barcode1d);	
				 //jsonObj1.put("barcode2d", list.get(i).barcode2d);
				 jsonarray1.put(jsonObj1);
			 }
			 jsonresult = jsonarray1.toString();  
        } catch (JSONException e) {  
            e.printStackTrace();  
        }  
		return jsonresult;
	}
	
	public static void WriteJsonFile(String filepath,List<UserItem> list) {
		String writeString = UserItemListToJson(list);
        try{
        	File file  = new File(filepath); 
	        FileOutputStream fos = new FileOutputStream(file);  
	        byte[] buffer = writeString.getBytes();  
	        fos.write(buffer);   
	        fos.close();  
        }catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	public static List<UserItem> JsonToUserItemList(String json){
		List<UserItem> list=new ArrayList<UserItem>();		
		if(json.startsWith("error")){  
            return null;  
        }  
        try {
           /* JSONObject jsonObject=new JSONObject(json); 
            JSONArray jsonArray1=jsonObject.getJSONArray("");*/
            JSONArray jsonArray1 = new JSONArray(json);
            for(int i=0;i<jsonArray1.length();i++){
            	UserItem hi=new UserItem();
            	JSONObject jsonObject1 = jsonArray1.getJSONObject(i); 
            	hi.id=jsonObject1.getString("id");
				hi.name=jsonObject1.getString("name");
				hi.worktype=jsonObject1.getString("worktype");
				hi.linetype=jsonObject1.getString("linetype");
				hi.depttype=jsonObject1.getString("depttype");
				hi.type=Integer.valueOf(jsonObject1.getString("type"));
				hi.gender=Integer.valueOf(jsonObject1.getString("gender"));
				hi.statu=Integer.valueOf(jsonObject1.getString("statu"));
				hi.enroldate=jsonObject1.getString("enroldate");
				hi.phone=jsonObject1.getString("phone");
				hi.remark=jsonObject1.getString("remark");
				hi.cardsn=jsonObject1.getString("cardsn");
				hi.template1=jsonObject1.getString("template1");
				hi.template2=jsonObject1.getString("template2");
				//hi.photo=jsonObject1.getString("photo");
				//hi.barcode1d=jsonObject1.getString("barcode1d");
				//hi.barcode2d=jsonObject1.getString("barcode2d");
				list.add(hi);
            }
        } catch (JSONException e) {  
            e.printStackTrace();
        }  
		return list;
	}

	public static List<UserItem> ReadJsonFile(String filepath){
		List<UserItem> his=new ArrayList<UserItem>();	
		String str = null;
	    try{
	    	File styleFile= new File(filepath);
	        FileInputStream inputStream= new FileInputStream(styleFile);
	        int size = inputStream.available();
	        byte[]buffer = new byte[size];
	        inputStream.read(buffer);
	        inputStream.close();
	        str=EncodingUtils.getString(buffer,"UTF-8");	 
	        his=JsonToUserItemList(str);
	    }catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return his;		  
	}
	
}
