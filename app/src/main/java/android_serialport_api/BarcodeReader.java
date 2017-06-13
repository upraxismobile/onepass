package android_serialport_api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Timer;
import java.util.TimerTask;

import android.fpi.MtGpio;
import android.os.Handler;
import android.os.Message;

public class BarcodeReader {
	private static BarcodeReader instance;
	
	public static final int STATE_BARCODE = 1;
	
	private SerialPort mSerialPort = null;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private byte[] databuf=new byte[1024];
	private int datasize=0;
	private int mDeviceType=0; 
	
	private Timer getTimer=null; 
    private TimerTask getTask=null; 
    private Handler getHandler;
    
    private Handler msgHandler=null;
    
    public static BarcodeReader getInstance() {
    	if(null == instance) {
    		instance = new BarcodeReader();
    	}
    	return instance;
    }
    
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while(!isInterrupted()/*true*/) {
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null) return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						onDataReceived(buffer, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	public void SetMessageHandler(Handler handler){
    	msgHandler=handler;
    }
	
	private void SendMssage(int cmd,int state,int size,byte[] buffer){
		if(msgHandler!=null)
    	msgHandler.obtainMessage(cmd,state,size,buffer).sendToTarget();
    }
	
	protected void onDataReceived(final byte[] buffer, final int size) {
		System.arraycopy(buffer, 0, databuf,datasize,size);					
		datasize=datasize+size;
		if(getTimer==null){
			getTimerStart();
		}
	}
	
	public void getTimerStart() {
		getTimer = new Timer(); 
		getHandler = new Handler() { 
			@Override 
            public void handleMessage(Message msg) { 
				getTimerStop();
				if(datasize>0){
					byte tp[]=new byte[datasize];
					System.arraycopy(databuf, 0, tp,0,datasize);
					//editText.setText(new String(tp));
					//beepSound.playBeepSoundAndVibrate();
					//soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
					SendMssage(STATE_BARCODE,1,datasize,tp);
					datasize=0;
				}
                super.handleMessage(msg); 
            }
        };
        getTask = new TimerTask() { 
            @Override 
            public void run() { 
                Message message = new Message(); 
                message.what = 1; 
                getHandler.sendMessage(message); 
            } 
        }; 
        getTimer.schedule(getTask, 1000, 1000); 
    }
	
	public void getTimerStop() {
    	if (getTimer!=null) {  
    		getTimer.cancel();  
    		getTimer = null;  
    		getTask.cancel();
    		getTask=null;
		}
    }
    
	public void openSerialPort(){
    	try {
			mSerialPort = getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (SecurityException e) {
			//DisplayError(R.string.error_security);
		} catch (IOException e) {
			//DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			//DisplayError(R.string.error_configuration);
		}
    }
	
	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			String path = "/dev/ttyMT1";
			int baudrate = 9600;	//1D
			//int baudrate = 115200;	//2D
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}
			mSerialPort = new SerialPort();
			if(mSerialPort.getmodel().equals("b82")){
				path = "/dev/ttyMT2";
				baudrate = 9600;
				mDeviceType=1;
			}else{
				path = "/dev/ttyMT1";
				mDeviceType=0;
			}
			mSerialPort.OpenDevice(new File(path), baudrate, 0,SerialPort.DEVTYPE_UART);
		}
		return mSerialPort;
	}
	
	public void closeSerialPort() {
		if (mReadThread != null)
			mReadThread.interrupt();
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
	
	public void BarcodeClose(){    	
		if(mDeviceType==0){
			MtGpio mt=new MtGpio();
			mt.BCPowerSwitch(false);
			mt.BCReadSwitch(true);
		}else{
			
		}
    }
	
	public void BarcodeOpen(){
		if(mDeviceType==0){
			MtGpio mt=new MtGpio();
			mt.BCReadSwitch(true);
			try {
				Thread.currentThread();
				Thread.sleep(200);
			}catch (InterruptedException e)
			{
				e.printStackTrace();
			}
    		datasize=0;
    		mt.BCReadSwitch(false);
		}else{
			byte[] cmd=new byte[2];
			cmd[0]=(0x1b);
			cmd[1]=(0x31);
			try {
				mOutputStream.write(cmd);
			} catch (IOException e) {
			}
		}
    }
}
