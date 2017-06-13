package com.fgtit.device;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fgtit.utils.ExtApi;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BluetoothReader {

	public final static byte CMD_PASSWORD=0x01;	//密码
	public final static byte CMD_ENROLID=0x02;		//登记到设备
    public final static byte CMD_VERIFY=0x03;		//在设备验证
    public final static byte CMD_IDENTIFY=0x04;	//在设备搜索
    public final static byte CMD_DELETEID=0x05;	//在设备删除
    public final static byte CMD_CLEARID=0x06;		//在设备清空
    public final static byte CMD_ENROLHOST=0x07;	//登记到主机
    public final static byte CMD_CAPTUREHOST=0x08;	//采集到主机
    public final static byte CMD_MATCH=0x09;		//比对
    public final static byte CMD_WRITECARD=0x0A;	//写卡
    public final static byte CMD_READCARD=0x0B;	//读卡
    public final static byte CMD_CARDID=0x0C;		//卡序列号比对
    public final static byte CMD_CARDFINGER=0x0D;	//指纹卡比对
    public final static byte CMD_CARDSN=0x0E;		//读卡序列号
    public final static byte CMD_SIGNSTART=0x10;
    public final static byte CMD_SIGNSTOP=0x11;
    public final static byte CMD_SIGNINFO=0x12;
    
    public final static byte CMD_PRINTCMD=0x20;	//打印
    public final static byte CMD_PRINTTEXT=0x21;
    public final static byte CMD_PRINTBARCODE=0x22;
    
    public static final int DEVSTATE_NONE = 0;
    public static final int DEVSTATE_CONNECTING = 2;
    public static final int DEVSTATE_CONNECTED = 3;
    public static final int DEVSTATE_UNCONNECT = 4;
    public static final int DEVSTATE_LOSTCONNECT = 5;
    
    public static final int	  MSG_DEVSTATE = 1;
        
    public BluetoothReaderService mReaderService = null;
	public String mConnectedDeviceName = null;
	
	private Handler msgHandler;
	
    public byte mRefData[]=new byte[512];
    public int mRefSize=0;
    public byte mMatData[]=new byte[512];
    public int mMatSize=0;
    public byte mCardSn[]=new byte[8];
    public byte mCardData[]=new byte[4096];
    public int mCardSize=0;
    public byte mTempData[]=new byte[4];
    
    public void SetMessageHandler(Handler handler){
    	msgHandler=handler;
    }
    
    private void SendStatus(int state){
    	msgHandler.obtainMessage(MSG_DEVSTATE,state,-1).sendToTarget();
    }
    
    private void SendMssage(int cmd,int state,int size,byte[] buffer){
    	msgHandler.obtainMessage(cmd,state,size,buffer).sendToTarget();
    }
    
    /*
    
    private void SendMssage(int cmd,int state,int type,Object obj){
    	msgHandler.obtainMessage(cmd,state,type,obj).sendToTarget();
    }
    
    private void SendMssage(int cmd,int state,int type,Message msg){
    	//msgHandler
    }
    
    private void SendStatus(int cmd,int state,int type,String txt){
    	Message msg=msgHandler.obtainMessage(cmd,state,type);
    	Bundle bundle = new Bundle();
    	bundle.putString("STATUS",txt);
    	msg.setData(bundle);
    	msgHandler.sendMessage(msg);
    }
    */
    
	public void Start(){
		if (mReaderService != null) {
            if (mReaderService.getState() == BluetoothReaderService.STATE_NONE) {
              mReaderService.start();
            }
        }
	}
	
	public void Stop(){
		if (mReaderService != null) mReaderService.stop();
	}
	
	public void Setup(Context context){
		if (mReaderService == null){
			mReaderService = new BluetoothReaderService(context, mHandler);
		}
	}
	
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case BluetoothReaderService.MESSAGE_STATE_CHANGE:
            	SendStatus(msg.arg1);
            	/*
                switch (msg.arg1) {
                case BluetoothReaderService.STATE_CONNECTED:
                    //mTitle.setText(R.string.title_connected_to);
                    //mTitle.append(mConnectedDeviceName);
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothReaderService.STATE_CONNECTING:
                    //mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothReaderService.STATE_LISTEN:
                case BluetoothReaderService.STATE_NONE:
                    //mTitle.setText(R.string.title_not_connected);
                    break;
                case BluetoothReaderService.STATE_UNCONNECT:
                	break;
                case BluetoothReaderService.STATE_LOSTCONNECT:
                	break;
                }
                */
                break;
            case BluetoothReaderService.MESSAGE_WRITE:
                //byte[] writeBuf = (byte[]) msg.obj;
                //AddStatusListHex(writeBuf,writeBuf.length);
                break;
            case BluetoothReaderService.MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;                
                //AddStatusList("Len="+Integer.toString(msg.arg1));
                //AddStatusListHex(readBuf,msg.arg1);                
                //检查长度和校验后在处理
                ReceiveCommand(readBuf);                
                break;
            case BluetoothReaderService.MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(BluetoothReaderService.DEVICE_NAME);
                //Toast.makeText(getApplicationContext(), "Connected to "  + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case BluetoothReaderService.MESSAGE_TOAST:
                //Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                break;
            }
        }
	};
	
	private void memset(byte[] pbuf,int size)
	{
		for(int i=0;i<size;i++)
		{
			pbuf[i]=0;
		}
	}
	
	private void memcpy(byte[] dstbuf,int dstoffset,byte[] srcbuf,int srcoffset,int size)
	{
		for(int i=0;i<size;i++)
		{
			dstbuf[dstoffset+i]=srcbuf[srcoffset+i];
		}
	}
	
	private int memcmp(byte[] dstbuf,byte[] srcbuf,int size)
	{
		for(int i=0;i<size;i++)
		{
			if(dstbuf[i]!=srcbuf[i])
				return -1;
		}
		return 0;
	}
	
	private int calcCheckSum(byte[] buffer,int size)
	{
		int sum=0;
		for(int i=0;i<size;i++)
		{
			sum=sum+buffer[i];
		}
		return (sum & 0x00ff);
	}
	
	public void EnrolHost(){
		SendCommand(CMD_ENROLHOST,null,0);
	}
	
	public void WriteCard(byte[] data,int size ){
		SendCommand(CMD_WRITECARD,data,size);
	}
	
	public void SignStart(){
		SendCommand(CMD_SIGNSTART,null,0);
	}
	
	public void SignStop(){
		SendCommand(CMD_SIGNSTOP,null,0);
	}
	
	public void PrintCmd(byte[] data,int size){
		SendCommand(CMD_PRINTCMD,data,size);
	}
	
	public void PrintText(String txt,int size){
		//SendCommand(CMD_PRINTTEXT,txt.getBytes(),txt.length());
		SendCommand(CMD_PRINTTEXT,txt.getBytes(),size);
	}
	
    private void SendCommand(byte cmdid,byte[] data,int size)
    {
    	int sendsize=9+size;
    	byte[] sendbuf = new byte[sendsize];
    	sendbuf[0]='F';
    	sendbuf[1]='T';
    	sendbuf[2]=0;
    	sendbuf[3]=0;
    	sendbuf[4]=cmdid;
    	sendbuf[5]=(byte)(size);
    	sendbuf[6]=(byte)(size>>8);
    	if(size>0)
    	{
    		for(int i=0;i<size;i++)
    		{
    			sendbuf[7+i]=data[i];
    		}
    	}
    	int sum=calcCheckSum(sendbuf,(7+size));
    	sendbuf[7+size]=(byte)(sum);
    	sendbuf[8+size]=(byte)(sum>>8);
    	mReaderService.write(sendbuf);
    }
    
    private void ReceiveCommand(byte[] data)
    {
    	if((data[0]=='F')&&(data[1]=='T'))
    	{
    		switch(data[4])
    		{
    		case CMD_PASSWORD:	//密码
    			{    				
    			}
    			break;
    		case CMD_ENROLID:	//登记到设备
    			{
    				if(data[7]==1){
    					int id=data[8]+(data[9]<<8);
    				}else{
    				}
    			}
    			break;
    		case CMD_VERIFY:	//在设备验证
    			{
    				if(data[7]==1){
    					
    				}else{
    				}
    			}
    			break;
    		case CMD_IDENTIFY:	//在设备搜索
    			{
    				if(data[7]==1){
    					int id=(byte)(data[8])+((data[9]<<8)&0xFF00);
    				}else{
    					
    				}
    			}
    			break;
    		case CMD_DELETEID:
    			{
    				if(data[7]==1){
    					
    				}else{
    					
    				}
    			}
    			break;
    		case CMD_CLEARID:
    			{
    				if(data[7]==1){
    				}else{
    				}
    			}
    			break;
    		case CMD_ENROLHOST:	//登记到主机
    			{
    				int size=(byte)(data[5])+((data[6]<<8)&0xFF00)-1;
    				if(data[7]==1){
    					//memcpy(mRefData,0,data,8,size);
    					System.arraycopy(data,8,mRefData,0,size);
    					mRefSize=size;    					
    					SendMssage(CMD_ENROLHOST,1,mRefSize,mRefData);
    					//ExtApi.SaveBytesToFile(data,"/sdcard/debug.dat");
    					//ExtApi.SaveBytesToFile(mRefData,"/sdcard/refdat.dat");
    				}else{
    					SendMssage(CMD_ENROLHOST,0,0,mTempData);
    				}
    			}
    			break;
    		case CMD_CAPTUREHOST:	//采集到主机
    			{
    				int size=(byte)(data[5])+((data[6]<<8)&0xFF00)-1;
    				if(data[7]==1){
    					//memcpy(mMatData,0,data,8,size);
    					System.arraycopy(data,8,mMatData,0,size);
    					mMatSize=size;
    					SendMssage(CMD_CAPTUREHOST,1,mMatSize,mMatData);
    				}else{
    					SendMssage(CMD_CAPTUREHOST,0,0,mTempData);
    				}
    			}
    			break;
    		case CMD_MATCH:	//比对
    			{
    				int score=(byte)(data[8])+((data[9]<<8)&0xFF00);
    				if(score>50){
    				}else{
    				}
    			}
    			break;
    		case CMD_WRITECARD:	//写卡
    			{
    				if(data[7]==1){
    					int size=(byte)(data[5])+((data[6]<<8)&0xFF00)-1;
        				if(size>0){
        					//memcpy(mCardSn,0,data,8,size);
        					System.arraycopy(data,8,mCardSn,0,size);
        				}
    					SendMssage(CMD_WRITECARD,1,size,mCardSn);
    				}else{
    					SendMssage(CMD_WRITECARD,0,0,mTempData);
    				}
    			}
    			break;
    		case CMD_READCARD:	//读卡
    			{
    				int size=(byte)(data[5])+((data[6]<<8)&0xFF00)-1;
    				if(data[7]==1){
    					//memcpy(mCardData,0,data,8,size);
    					System.arraycopy(data,8,mCardData,0,size);
    					mCardSize=size;
    				}else{
    					
    				}
    			}
    			break;
    		case CMD_CARDID:	//卡序列号比对
    			{
    				if(data[7]==1){
    					
    				}else{
    				}
    			}
    			break;
    		case CMD_CARDFINGER:	//指纹卡比对
				{
					if(data[7]==1){
						
					}else{
					}
				}
				break;
    		case CMD_CARDSN:		//读卡序列号
    			{
    				int size=(byte)(data[5])+((data[6]<<8)&0xFF00)-1;
    				if(data[7]==1){
    					//memcpy(mCardSn,0,data,8,size);
    					System.arraycopy(data,8,mCardSn,0,size);
    					//AddStatusList("Read Card SN Succeed:"+Integer.toHexString(mCardSn[0])+Integer.toHexString(mCardSn[1])+Integer.toHexString(mCardSn[2])+Integer.toHexString(mCardSn[3]));
    				}else{
    				}
    			}
    			break;
    		case CMD_SIGNSTART:{
    				if(data[7]==1){
    					SendMssage(CMD_SIGNSTART,1,0,mTempData);
					}else{
						SendMssage(CMD_SIGNSTART,0,0,mTempData);
					}
    			}
    			break;
    		case CMD_SIGNSTOP:{
    				if(data[7]==1){
    					SendMssage(CMD_SIGNSTOP,1,0,mTempData);
					}else{
						SendMssage(CMD_SIGNSTOP,0,0,mTempData);
					}
    			}
    			break;
    		case CMD_SIGNINFO:{
    				if(data[7]==1){
    					int size=(byte)(data[5])+((data[6]<<8)&0xFF00)-1;
    					if(size>0){
    						//memcpy(mCardSn,0,data,8,size);
    						System.arraycopy(data,8,mCardSn,0,size);
    					}
    					SendMssage(CMD_SIGNINFO,1,size,mCardSn);
    				}else{
    					SendMssage(CMD_SIGNINFO,0,0,mTempData);
    				}
    			}
    			break;
    		case CMD_PRINTCMD:{
    				if(data[7]==1){
    					SendMssage(CMD_PRINTCMD,1,0,mTempData);
    				}else{
    					SendMssage(CMD_PRINTCMD,0,0,mTempData);
    				}
    			}
    			break;
    		case CMD_PRINTTEXT:{
    				if(data[7]==1){
						SendMssage(CMD_PRINTTEXT,1,0,mTempData);
					}else{
						SendMssage(CMD_PRINTTEXT,0,0,mTempData);
					}
    			}
    			break;
    		}
    	}
    }
    
}
