package com.fgtit.onepass;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.fgtit.onepass.R;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.net.SocketClient;
import com.fgtit.utils.ExtApi;
import com.fgtit.app.ActivityList;
import com.fgtit.app.UpdateApp;
import com.fgtit.data.GlobalData;
import com.fgtit.data.UserItem;
import com.fgtit.data.XmlParase;
import com.fgtit.device.BluetoothReader;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.fpi.MtGpio;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android_serialport_api.SerialPort;

public class MainActivity extends Activity {

	private static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	
	private static final int RE_WORK0 = 0;
	private static final int RE_WORK1 = 1;
    private static final int RE_WORK2 = 2;
    
	private String btAddress="";
	
	private Menu mainMenu; 
	private TextView txtView;
	private Button btn01,btn02,btn03;
	private long exitTime = 0;
	private WakeLock wakeLock;
	
	private Timer startTimer; 
    private TimerTask startTask; 
    Handler startHandler;
    
    private ProgressDialog progressDialog;
    
    private SoundPool soundPool;
    private int soundIda,soundIdb;
    private boolean soundflag=false;
    
    private MapView 	mMapView;
    private BaiduMap	mBaiduMap; 
    private BitmapDescriptor mCurrentMarker;
    private LocationMode mCurrentMode;
    
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
        SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_main);
		
		this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);		
//		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		ActivityList.getInstance().IsUseNFC=ExtApi.IsSupportNFC(this);
		SerialPort sp=new SerialPort();
		if(sp.getmodel().equals("b82")){
			ActivityList.getInstance().IsPad=true;
		}else{
			ActivityList.getInstance().IsPad=false;
		}
		
		//线程
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		
		txtView=(TextView)findViewById(R.id.textView1);
		
		//MtGpio.getInstance().FPPowerSwitch(true);	
		
		//
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(15000);//设置发起定位请求的间隔时间为15000ms
		option.setIsNeedAddress(true);//返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
						
		mLocationClient = new LocationClient(getApplicationContext());		
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		//mLocationClient.requestLocation();
		
        //获取地图控件引用  
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        
        //开启定位图层
        mBaiduMap.setMyLocationEnabled(true);        
        //设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）  
        mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);  
        //mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
        mBaiduMap.setMyLocationConfigeration(config);
                
        
		btn01=(Button)findViewById(R.id.button1);
		btn01.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SignOnActivity.class);
				//startActivity(intent);
				startActivityForResult(intent,RE_WORK1);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
		});
		
		btn02=(Button)findViewById(R.id.button2);
		btn02.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SignOffActivity.class);
				//startActivity(intent);
				startActivityForResult(intent,RE_WORK2);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
		});
		
		/*
		btn03=(Button)findViewById(R.id.button3);
		btn03.setOnClickListener(new View.OnClickListener() {
			//@Override
			public void onClick(View v) {		
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		*/
		
		//LocationInit();
		
		//保持常亮 方式一	//让当前界面保持屏幕的不暗不关闭
      	//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      	//保持常亮 方式二
      	PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
      	wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "sc");
      	wakeLock.acquire(); //设置保持唤醒
      	
      	ActivityList.getInstance().setMainContext(this);
      	ActivityList.getInstance().LoadConfig();
      	
      	GlobalData.getInstance().SetContext(this);
      	GlobalData.getInstance().CreateDir();
      	GlobalData.getInstance().LoadFileList();
      	//GlobalData.getInstance().LoadUsersList();
      	GlobalData.getInstance().LoadConfig();
      	//GlobalData.getInstance().LoadRecordsList();
      	GlobalData.getInstance().LoadWorkList();
        GlobalData.getInstance().LoadLineList();
        GlobalData.getInstance().LoadDeptList();
        
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundIda = soundPool.load(this, R.raw.start, 1);
    	soundIdb = soundPool.load(this, R.raw.stop, 1);
    	soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {  
    		@Override  
    		public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {  
    			soundflag = true;    //  表示加载完成   
    	 	}  
    	});
    	
    	if(FPMatch.getInstance().InitMatch()==0){
    		Toast.makeText(getApplicationContext(), "Init Matcher Fail!", Toast.LENGTH_SHORT).show();
    	}else{
    		//Toast.makeText(getApplicationContext(), "Init Matcher OK!", Toast.LENGTH_SHORT).show();
    	}
    	
    	LatLng ll = new LatLng(ActivityList.getInstance().MapLat,ActivityList.getInstance().MapLng);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,ActivityList.getInstance().MapZoom);
        mBaiduMap.animateMapStatus(u);
    	
    	UpdateApp.getInstance().setAppContext(this);
    	LoadUserListThread();
    	
    	setFpIoState(true);
    	
    	DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int densityDpi = metric.densityDpi;
        switch(densityDpi){
        case 210:{	//7 inch
        		LinearLayout mapLayout=(LinearLayout)findViewById(R.id.mapLayout);
        		mapLayout.getLayoutParams().height=920;
        	}
        	break;
        case 240:{	//5 inch-
        	}
        	break;
        }
	}
	
	private void setFpIoState(boolean isOn){
		int  state =  0 ;
		if(isOn){
			state = 1;
		}else{
			state = 0;
		}
		Intent i = new Intent("ismart.intent.action.fingerPrint_control");
		i.putExtra("state", state);
		sendBroadcast(i);
	}
	
	public Handler mLoadHandler=new Handler(){
		public void handleMessage(Message msg)  
        {  
            switch(msg.what)  
            {  
            case 1:  
            	if(progressDialog==null){
            		progressDialog = new ProgressDialog(MainActivity.this);    
                	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
                	//progressDialog.setTitle("Please Wait");    
                	progressDialog.setMessage("Please Wait,Load Count : "+String.valueOf(msg.arg1) +" ...");    
                	//progressDialog.setIcon(android.R.drawable.btn_star);    
                	progressDialog.setIndeterminate(false);    
                	progressDialog.setCancelable(false);    
                	progressDialog.show();   
            	}
                break;  
            case 2:
            	Toast.makeText(getApplicationContext(), "User Count:"+String.valueOf(GlobalData.getInstance().userList.size()), Toast.LENGTH_SHORT).show();
            	if(progressDialog!=null){
            		progressDialog.dismiss();
            		progressDialog=null;
            	}
            	break;
            default:  
            	Toast.makeText(getApplicationContext(), "Please Wait,Load Count : "+String.valueOf(msg.arg1) +" ...", Toast.LENGTH_SHORT).show();
                break;        
            }  
            super.handleMessage(msg);  
        }  
	};
	 
	void LoadUserListThread(){
		Thread thread=new Thread(new Runnable(){  
            @Override  
            public void run(){
            	int count=GlobalData.getInstance().GetUsersCount();
            	if(count>200){
                	Message message=new Message();
                	message.what=1;  
                	message.arg1=count;
                    mLoadHandler.sendMessage(message);
               	
                    GlobalData.getInstance().LoadUsersList();

                    message.what=2;  
                    mLoadHandler.sendMessage(message);
                }else{
                	Message message=new Message();
                	message.what=3;  
                	message.arg1=count;
                    mLoadHandler.sendMessage(message);               	
                    GlobalData.getInstance().LoadUsersList();
                }
            }  
        });  
        thread.start(); 
	}
	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
		            return ;
			/*
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			} 
 
			txtView.setText(sb.toString());
			*/
			
			StringBuffer sb = new StringBuffer(256);
			switch(location.getLocType()){
			case 61:
				sb.append("Satellite positioning");
				GlobalData.getInstance().glocal=true;
				break;
			case 66:
				sb.append("Offline positioning");
				GlobalData.getInstance().glocal=true;
				break;
			case 161:
				sb.append("Network positioning");
				GlobalData.getInstance().glocal=true;
				break;
			default:
				sb.append("Positioning failure");
				GlobalData.getInstance().glocal=false;
				break;
			}
			
			sb.append("  Time: ");
			sb.append(location.getTime());
			sb.append("\nLatitude: ");
			sb.append(location.getLatitude());			
			sb.append("  Longitude: ");
			sb.append(location.getLongitude());
			
			txtView.setText(sb.toString());
			
			GlobalData.getInstance().glat=location.getLatitude();
			GlobalData.getInstance().glng=location.getLongitude();
			ActivityList.getInstance().MapLat=location.getLatitude();
			ActivityList.getInstance().MapLng=location.getLongitude();
			
			//开启定位图层
	        //mBaiduMap.setMyLocationEnabled(true);
			//构造定位数据  ,此处设置开发者获取到的方向信息，顺时针0-360
	        MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection()).latitude(location.getLatitude()).longitude(location.getLongitude()).build();  
	        //设置定位数据
	        mBaiduMap.setMyLocationData(locData);
	        //设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标） 
	        //mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
	        //mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
	        //mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
	        //MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
	        //mBaiduMap.setMyLocationConfigeration(config);
	        
	        if(Math.abs(ActivityList.getInstance().MapZoom-mBaiduMap.getMapStatus().zoom)>=1.0f){
	        	ActivityList.getInstance().MapZoom=mBaiduMap.getMapStatus().zoom;
	        	ActivityList.getInstance().SetConfigByVal("MapZoom",ActivityList.getInstance().MapZoom);
	        }
		}
	}
		
	@Override
    public void onStart() {
        super.onStart();
        TimerStart();
	}
	
	@Override  
    protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();  
		TimeStop();
	
		wakeLock.release(); //解除保持唤醒		
		soundPool.release();  
    	soundPool = null;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();  
		wakeLock.release();//解除保持唤醒
	}
	        
    @Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();  
		wakeLock.acquire(); //设置保持唤醒
	}
    
	public void TimerStart()
    {
		startTimer = new Timer(); 
		startHandler = new Handler() { 
            @SuppressLint("HandlerLeak")
			@Override 
            public void handleMessage(Message msg) { 
            	//mLocationClient.requestLocation();
                super.handleMessage(msg); 
            }
        };
        startTask = new TimerTask() { 
            @Override 
            public void run() { 
                Message message = new Message(); 
                message.what = 1; 
                startHandler.sendMessage(message); 
            } 
        }; 
        startTimer.schedule(startTask, 15000, 15000); 
    }
    
    public void TimeStop()
    {
    	if (startTimer!=null)
		{  
    		startTimer.cancel();  
    		startTimer = null;  
    		startTask.cancel();
    		startTask=null;
		}
    }
	
	
	
    
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){  
	    	exitApplication();
	    	return true;  
	    } else if(keyCode == KeyEvent.KEYCODE_HOME){  
 	    	return true;  
 	    }
	    return super.onKeyDown(keyCode, event);  
	} 
	public void exitApplication(){
		if((System.currentTimeMillis()-exitTime) > 2000){  
    		Toast.makeText(getApplicationContext(), getString(R.string.txt_exitinfo), Toast.LENGTH_SHORT).show();
    		exitTime = System.currentTimeMillis();  
    	}  
    	else{  
    		
    		ActivityList.getInstance().SetConfigByVal("MapLat",String.valueOf(ActivityList.getInstance().MapLat));
    		ActivityList.getInstance().SetConfigByVal("MapLng",String.valueOf(ActivityList.getInstance().MapLng));
    		
    		finish();  
    		System.exit(0);
    		//AppList.getInstance().exit();
        	}
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		mainMenu=menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case android.R.id.home:
			exitApplication();
			return true;		
		case R.id.action_refresh:
			//if(GlobalData.getInstance().glocal){
			//	txtView.setText("Net Location:"+String.valueOf(GlobalData.getInstance().glat)+","+String.valueOf(GlobalData.getInstance().glng));
			//}
			mLocationClient.requestLocation();
			return true;
		case R.id.action_manage:{
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				//btReader.SetMessageHandler(btHandler);
				//Intent serverIntent = new Intent(this, DeviceListActivity.class);
				//startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case RE_WORK0:
        	break;
        case RE_WORK1:
            break;
        case RE_WORK2:
        	break;
        }
    }
	
	private void LocationInit()	{
		LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, "GPS Location", Toast.LENGTH_SHORT).show();
			GpsLocationInit();
			return;
		}
		else{
			Toast.makeText(this, "Net Location", Toast.LENGTH_SHORT).show();
			NetLocationInit();
		}
	}
	
	private void GetLocationInfo(Location location) {
        if(location!=null){
        	GlobalData.getInstance().glocal=true;
        	GlobalData.getInstance().glat=location.getLatitude();
        	GlobalData.getInstance().glng=location.getLongitude();
            //SetMapCenter(location.getLatitude(),location.getLongitude());
			//SetMapMaker(location.getLatitude(),location.getLongitude());
			txtView.setText("Net Location :  "+String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude()));
        }else {
        	GlobalData.getInstance().glocal=false;
            txtView.setText("No location found");
        }

    }
	
	private void NetLocationInit()
	{
		// 获取到LocationManager对象
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 创建一个Criteria对象
		Criteria criteria = new Criteria();
		// 设置粗略精确度
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// 设置是否需要返回海拔信息
		criteria.setAltitudeRequired(false);
		// 设置是否需要返回方位信息
		criteria.setBearingRequired(false);
		// 设置是否允许付费服务
		criteria.setCostAllowed(true);
		// 设置电量消耗等级
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		// 设置是否需要返回速度信息
		criteria.setSpeedRequired(false);
		// 根据设置的Criteria对象，获取最符合此标准的provider对象 41
		String currentProvider = locationManager.getBestProvider(criteria, true);
				
		// 根据当前provider对象获取最后一次位置信息 44
		Location currentLocation = locationManager.getLastKnownLocation(currentProvider);
		// 如果位置信息为null，则请求更新位置信息 46
		if (currentLocation == null){
			locationManager.requestLocationUpdates(currentProvider,5000, 0,netlocationListener);
		}else{			
			GetLocationInfo(currentLocation);
			locationManager.requestLocationUpdates(currentProvider,5000,0,netlocationListener);	//LocationManager.GPS_PROVIDER
		}
	}
	
    private LocationListener netlocationListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
        	GetLocationInfo(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            
        }

        @Override
        public void onProviderEnabled(String provider) {
            
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            
        }
    };
   
    private void GpsLocationInit()
    {
    	LocationManager gpslocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	// 查找到服务信息
    	Criteria criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	// 高精度
    	criteria.setAltitudeRequired(false);
    	criteria.setBearingRequired(false);
    	criteria.setCostAllowed(true);
    	criteria.setPowerRequirement(Criteria.POWER_LOW);
    	// 低功耗
    	String currentProvider = gpslocationManager.getBestProvider(criteria, true);
    	Location gpsLocation = gpslocationManager.getLastKnownLocation(currentProvider);
		//String gpsProvider = gpslocationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
		//Location gpsLocation = gpslocationManager.getLastKnownLocation(gpsProvider);
		
        // 如果位置信息为null，则请求更新位置信息
        if (gpsLocation == null) {
        	//gpslocationManager.requestLocationUpdates(gpsProvider, 0, 0,gpslocationListener);
        	gpslocationManager.requestLocationUpdates(currentProvider, 0, 0,gpslocationListener);
        }else{
        	GetLocationInfo(gpsLocation);
        	gpslocationManager.requestLocationUpdates(currentProvider, 0, 0,gpslocationListener);
        }
		gpslocationManager.addGpsStatusListener(gpsListener);
    }
    
    private LocationListener gpslocationListener = new LocationListener(){
        @Override
        public void onLocationChanged(Location location) {
        	GetLocationInfo(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        // 状态改变时调用107
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    
    private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
        // GPS状态发生变化时触发
        @Override
        public void onGpsStatusChanged(int event) {
            // 获取当前状态
            switch (event) {
            // 第一次定位时的事件
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
            // 开始定位的事件
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            // 发送GPS卫星状态事件
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //Toast.makeText(MainMapActivity.this, "GPS_EVENT_SATELLITE_STATUS",Toast.LENGTH_SHORT).show();
                break;
            // 停止定位事件
            case GpsStatus.GPS_EVENT_STOPPED:
                break;
            }
        }
    };

}
