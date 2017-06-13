package com.fgtit.device;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.fpi.MtRfid;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.os.Message;
import android_serialport_api.AsyncFingerprint;
import android_serialport_api.AsyncFingerprint.OnRegModelListener;
import android_serialport_api.SerialPortManager;
import android_serialport_api.AsyncFingerprint.OnGenCharExListener;
import android_serialport_api.AsyncFingerprint.OnGetImageExListener;
import android_serialport_api.AsyncFingerprint.OnUpCharListener;
import android_serialport_api.AsyncFingerprint.OnUpImageExListener;

public class FullReader {
	
	private static FullReader instance;
	
	public static FullReader getInstance() {
    	if(null == instance) {
    		instance = new FullReader(null,null);
    	}
    	return instance;
    }
	//Message
	public static final int MSG_FPM_DAT=1;
		public static final int MSG_FPM_ERROR=-1;
		public static final int MSG_FPM_PLACE=1;
		public static final int MSG_FPM_LIFT=2;
		public static final int MSG_FPM_UPIMG=3;
		public static final int MSG_FPM_DPIMG=4;
		public static final int MSG_FPM_GENFP=5;
		public static final int MSG_FPM_UPFP=6;
		public static final int MSG_FPM_ENROL=7;
		public static final int MSG_FPM_CAPTURE=8;
		
	public static final int MSG_NFC_DAT=2;
	public static final int MSG_BRD_DAT=3;
	public static final int MSG_RFI_DAT=4;

	public static final int MSG_FPU_DAT=5;
	
	public static final int MSG_BTF_DAT=6;
	public static final int MSG_BTC_DAT=7;
	
	private Context mContext=null;
	private Handler mHandler=null;
	
	//Fingerprint Module
	private AsyncFingerprint vFingerprint;
	private boolean			 bIsCancel=false;
	private boolean			 bfpWork=false;
	private boolean 		 bfpEnrol=false;
	private boolean			 bfpStdImg=false;
	private boolean			 bfpUpImage=false;
	private boolean 		 bfpCheck=false;
	private int				 fpCount=0;
		
	//Card
	public boolean 	IsUseNFC=false;
	//RFID
	//private int	rfidtype=0;
	private MtRfid rfid=null;
	private Timer xTimer=null; 
    private TimerTask xTask=null; 
    private Handler xHandler;
    
	//NFC
    private NfcAdapter nfcAdapter;       
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    	
	
	public FullReader(Context context,Handler handler){
		mContext=context;
		mHandler=handler;
	}
	
	public void SetContextHandler(Context context,Handler handler){
		mContext=context;
		mHandler=handler;
	}
	
	private void SendStatus(int cmd,int state){
		if(mHandler!=null)
			mHandler.obtainMessage(cmd,state,-1).sendToTarget();
    }
	
	private void SendMessage(int cmd,int state,int size,byte[] buffer){
		if(mHandler!=null)
			mHandler.obtainMessage(cmd,state,size,buffer).sendToTarget();
	}
	
	//Fingerprint Module
	public void OpenFP(){
		vFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
		InitFP();
	}
	
	public void CloseFP(){
		SerialPortManager.getInstance().closeSerialPort();
	}
	
	public void EnrolFP(){
		bfpEnrol=true;
		bfpWork=true;
		bfpCheck=false;
		if(bfpStdImg)
			vFingerprint.FP_GetImage();
		else
			vFingerprint.FP_GetImageEx();
		SendStatus(MSG_FPM_DAT,MSG_FPM_PLACE);
		fpCount=1;
	}
	
	public void CaptureFP(){
		bfpEnrol=false;
		bfpWork=true;
		if(bfpStdImg)
			vFingerprint.FP_GetImage();
		else
			vFingerprint.FP_GetImageEx();
		SendStatus(MSG_FPM_DAT,MSG_FPM_PLACE);
		fpCount=1;
	}
	
	public void StopFP(){
		bIsCancel=true;
		bfpWork=false;
	}
	
	private void InitFP(){
		vFingerprint.setOnGetImageExListener(new OnGetImageExListener() {
			@Override
			public void onGetImageExSuccess() {
				if(bfpCheck){
					vFingerprint.FP_GetImageEx();
				}else{
					if(bfpUpImage){
						vFingerprint.FP_UpImageEx();
						SendStatus(MSG_FPM_DAT,MSG_FPM_UPIMG);
					}else{
						SendStatus(MSG_FPM_DAT,MSG_FPM_GENFP);
						vFingerprint.FP_GenCharEx(fpCount);
					}
				}
			}
			@Override
			public void onGetImageExFail() {
				if(!bIsCancel){
					if(bfpCheck){
						bfpCheck=false;
						//SendStatus(MSG_FPM_DAT,MSG_FPM_PLACE);
						vFingerprint.FP_GetImageEx();
						fpCount++;
					}else{
						vFingerprint.FP_GetImageEx();
					}
				}
			}
		});
			
		vFingerprint.setOnUpImageExListener(new OnUpImageExListener() {
			@Override
			public void onUpImageExSuccess(byte[] data) {
				SendMessage(MSG_FPM_DAT,MSG_FPM_UPIMG,data.length,data);
				SendStatus(MSG_FPM_DAT,MSG_FPM_GENFP);
				vFingerprint.FP_GenCharEx(fpCount);
			}
			@Override
			public void onUpImageExFail() {
				bfpWork=false;
				SendStatus(MSG_FPM_DAT,MSG_FPM_ERROR);
			}
		});
			
		vFingerprint.setOnGenCharExListener(new OnGenCharExListener() {
			@Override
			public void onGenCharExSuccess(int bufferId) {
				if(bfpEnrol){
					if(bufferId==1){
						bfpCheck=true;
						SendStatus(MSG_FPM_DAT,MSG_FPM_LIFT);
						vFingerprint.FP_GetImageEx();
					}else if(bufferId==2){
						vFingerprint.FP_RegModel();
					}
				}else{
					SendStatus(MSG_FPM_DAT,MSG_FPM_UPFP);
					vFingerprint.FP_UpChar();
				}
			}

			@Override
			public void onGenCharExFail() {
				bfpWork=false;
				SendStatus(MSG_FPM_DAT,MSG_FPM_ERROR);				
			}
		});
		
		vFingerprint.setOnRegModelListener(new OnRegModelListener() {
			@Override
			public void onRegModelSuccess() {
				SendStatus(MSG_FPM_DAT,MSG_FPM_UPFP);
				vFingerprint.FP_UpChar();
			}
			@Override
			public void onRegModelFail() {
				bfpWork=false;
				SendStatus(MSG_FPM_DAT,MSG_FPM_ERROR);
			}
		});
			
		vFingerprint.setOnUpCharListener(new OnUpCharListener() {
			@Override
			public void onUpCharSuccess(byte[] model) {
				if(bfpEnrol)
					SendMessage(MSG_FPM_DAT,MSG_FPM_ENROL,model.length,model);
				else
					SendMessage(MSG_FPM_DAT,MSG_FPM_CAPTURE,model.length,model);
				bfpWork=false;					
			}

			@Override
			public void onUpCharFail() {
				bfpWork=false;	
				SendStatus(MSG_FPM_DAT,MSG_FPM_ERROR);
			}
		});
    }
	
	//	
	public void OpenCard(){
		if (NfcAdapter.getDefaultAdapter(mContext) == null)
			IsUseNFC=false;
		else
			IsUseNFC=true;
		
		if(IsUseNFC){
			nfcAdapter = NfcAdapter.getDefaultAdapter(mContext);
			if (nfcAdapter == null) {
				SendStatus(MSG_NFC_DAT,-1);
				return;
			}
			if (!nfcAdapter.isEnabled()) {
				SendStatus(MSG_NFC_DAT,0);
				return;
			}  
			
			mPendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			mFilters = new IntentFilter[]{
		                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
		                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
		                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
		}else{
			if(rfid==null)
				rfid=new MtRfid();
			rfid.RfidInit();	//Open
			rfid.SetContext(mContext);
		}  
	}
	
	public void CloseCard(){
		if(IsUseNFC){
		}else{
			xTimerStop();
			rfid.RfidClose();	//Close
		}
	}
	
	public void ReadCard(){
		if(IsUseNFC){
		}else{
			xTimerStart();
		}
	}
	/*
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		processIntent(intent);
	}
	*/
	public void processIntent(Intent intent){
		byte[] sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
		SendMessage(MSG_NFC_DAT,1,sn.length,sn);
	}
	
	@SuppressLint("HandlerLeak") 
	public void xTimerStart() {
		xTimer = new Timer(); 
		xHandler = new Handler() { 
			@Override 
	        public void handleMessage(Message msg) { 
				int[] cardsn=new int[8];
				if(rfid.RfidGetSn(cardsn)==0){
					byte[] sn=new byte[4];
					sn[0]=(byte) (cardsn[0]&0xFF);
					sn[1]=(byte) (cardsn[1]&0xFF);
					sn[2]=(byte) (cardsn[2]&0xFF);
					sn[3]=(byte) (cardsn[3]&0xFF);
					SendMessage(MSG_RFI_DAT,1,sn.length,sn);
				}
	            super.handleMessage(msg); 
	        }
	    };
	    xTask = new TimerTask() { 
	        @Override 
	        public void run() { 
	            Message message = new Message(); 
	            message.what = 1; 
	            xHandler.sendMessage(message); 
	        } 
	    }; 
	    xTimer.schedule(xTask, 1000, 1000); 
	}

	public void xTimerStop() {
		if (xTimer!=null) {  
			xTimer.cancel();  
			xTimer = null;  
			xTask.cancel();
			xTask=null;
		}
	}
	
	//Bardcode
	public void OpenBarcode(int bandrate){
		
	}
	
	public void CloseBarcode(){
		
	}
	
	public void Pause(){
		if(IsUseNFC){
			if (nfcAdapter != null)
				nfcAdapter.disableForegroundDispatch((Activity) mContext);
		}
	}
	
	public void Resume(){
		if(IsUseNFC){
			if (nfcAdapter != null)
				nfcAdapter.enableForegroundDispatch((Activity) mContext, mPendingIntent, mFilters,null);
		}
	}
}
