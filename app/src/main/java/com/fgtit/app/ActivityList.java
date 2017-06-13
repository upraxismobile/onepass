package com.fgtit.app;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

//public class ActivityList extends Application{
public class ActivityList {	
	private List<Activity> activityList = new LinkedList<Activity>();	
	private static ActivityList instance;
	private Context pcontext;
	private Context ccontext;
	
	public boolean 	IsUseNFC=true;
	public boolean  IsPad=false;
	public String  	PassWord="";
	public String	DeviceSN="";
	public String	WebAddr="";
	public String	UpdateUrl="";
	public String	WebService="";
	public boolean	IsOnline=false;
	public boolean	IsUpImage=true;
	public float    MapZoom=18.0f;
	public double 	MapLat=0.0;
	public double 	MapLng=0.0;
	
	private ActivityList(){ 
    }
    
    public static ActivityList getInstance() {
    	if(null == instance) {
    		instance = new ActivityList();
    	}
    	return instance;
    }

    public void setMainContext(Context context){
    	pcontext=context;
    }
    
    public void setCurrContext(Context context){
    	ccontext=context;
    }
    
    public void Relogon(){
    	for(Activity activity:activityList) {
    		activity.finish();
    	}
    }
    
    public Context getCurrContext(){
    	return ccontext;
    }
    
    public void addActivity(Activity activity){
    	ccontext=activity;
    	activityList.add(activity);
    }
    
    public void removeActivity(Activity activity){
    	activityList.remove(activity);
    }
    
    public void exit(){
    	for(Activity activity:activityList) {
    		activity.finish();
    	}
    	System.exit(0);
    }
    
    public void SetConfigByVal(String name,String val){
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(pcontext);
		Editor edit=sp.edit();
		edit.putString(name,val);
		edit.commit();
	}

	public String GetConfigByVal(String name){
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(pcontext);
		return sp.getString(name,"");
	}
	
	public void SetConfigByVal(String name,float val){
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(pcontext);
		Editor edit=sp.edit();
		edit.putFloat(name,val);
		edit.commit();
	}

	public void SaveConfig(){
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(pcontext);
		Editor edit=sp.edit();
		edit.putString("WebAddr",WebAddr);
		edit.putString("UpdateUrl",UpdateUrl);
		edit.putString("PassWord",PassWord);
		edit.putBoolean("IsOnline", IsOnline);
		//edit.putBoolean("IsUpImage", IsUpImage);
		edit.putFloat("MapZoom", MapZoom);
		edit.putString("MapLat", String.valueOf(MapLat));
		edit.putString("MapLng", String.valueOf(MapLng));
		edit.commit();
	}

	public void LoadConfig(){
		SharedPreferences sp;
		sp = PreferenceManager.getDefaultSharedPreferences(pcontext);
		WebAddr=sp.getString("WebAddr","http://www.biofgt.con/OnePass/");
		UpdateUrl=WebAddr+"apk/update.xml";
		WebService=WebAddr+"OnePassService.asmx";
		PassWord=sp.getString("PassWord","1010");
		IsOnline=sp.getBoolean("IsOnline", false);
		//IsUpImage=sp.getBoolean("IsUpImage", false);
		MapZoom=sp.getFloat("MapZoom",18.0f);
		MapLat=Double.parseDouble(sp.getString("MapLat","22.653393"));
		MapLng=Double.parseDouble(sp.getString("MapLng","114.057853"));
		
		DeviceSN=((TelephonyManager) pcontext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}
}
