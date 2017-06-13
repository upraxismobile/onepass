package com.fgtit.onepass;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.fgtit.onepass.R;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.utils.ToastUtil;
import com.fgtit.utils.ExtApi;
import com.fgtit.app.ActivityList;
import com.fgtit.data.GlobalData;
import com.fgtit.data.UserItem;
import com.fgtit.device.BluetoothReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.fpi.MtGpio;
import android.fpi.MtRfid;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.scanner.CaptureActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.AsyncFingerprint;
import android_serialport_api.AsyncFingerprint.OnDownCharListener;
import android_serialport_api.AsyncFingerprint.OnGenCharListener;
import android_serialport_api.AsyncFingerprint.OnGetImageListener;
import android_serialport_api.AsyncFingerprint.OnRegModelListener;
import android_serialport_api.AsyncFingerprint.OnSearchListener;
import android_serialport_api.AsyncFingerprint.OnStoreCharListener;
import android_serialport_api.AsyncFingerprint.OnUpCharListener;
import android_serialport_api.AsyncFingerprint.OnUpImageListener;
import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortManager;

public class EnrollActivity extends Activity {
	
	private EditText editText1,editText2,editText6,editText7,editText8,editText9,editText10,editText11,editText12;
	private TextView text1,text2,text3;
	private ImageView imgPhoto,imgFinger1,imgFinger2;
	
	private byte[] jpgbytes=null;
	
    private byte[] model1=new byte[512];
	private byte[] model2=new byte[512];
	private boolean isenrol1=false;
	private boolean isenrol2=false;
	private int savecount=0;
	private int mDeviceType=0;
	
	private ImageView fpImage;
	private TextView  tvFpStatus;
	private AsyncFingerprint vFingerprint;
	private Dialog fpDialog=null;
	private int	iFinger=0;
	private boolean	bIsUpImage=true;
	private int count;
	private boolean bcheck=false;
	
	//Barcode
	private SerialPort mSerialPort = null;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private byte[] databuf=new byte[1024];
	private int datasize=0;
	private int soundIda;
	private SoundPool soundPool;
	
	private Timer TimerBarcode=null; 
    private TimerTask TaskBarcode=null; 
    private Handler HandlerBarcode;
	
    //RFID
    private Timer TimerCard=null; 
    private TimerTask TaskCard=null; 
    private Handler HandlerCard;
    private int	rfidtype=0;
    private MtRfid rfid=null;
    
    //NFC
    private NfcAdapter nfcAdapter;       
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    
    UserItem person = new UserItem();
    public String CardSN="";
	
    private Spinner spin1,spin2;
    
    private boolean	bIsCancel=false;
    private boolean	bCapture=false;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enroll);

		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		editText1=(EditText)findViewById(R.id.editText1);
		editText2=(EditText)findViewById(R.id.editText2);
		editText6=(EditText)findViewById(R.id.editText6);
		editText7=(EditText)findViewById(R.id.editText7);
		editText8=(EditText)findViewById(R.id.editText8);
		editText9=(EditText)findViewById(R.id.editText9);
		editText10=(EditText)findViewById(R.id.editText10);
		editText11=(EditText)findViewById(R.id.editText11);
		editText12=(EditText)findViewById(R.id.editText12);
		
		text1=(TextView)findViewById(R.id.textView3);
		text2=(TextView)findViewById(R.id.textView4);
		text3=(TextView)findViewById(R.id.textView5);
		
		imgPhoto=(ImageView)findViewById(R.id.imageView1);
		imgPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bCapture=true;
				Intent intent = new Intent(EnrollActivity.this, CameraExActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("id","1");
				intent.putExtras(bundle);
				startActivityForResult(intent,0);
			}
		});
		
		imgFinger1=(ImageView)findViewById(R.id.imageView2);
		imgFinger1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				FPDialog(1);
			}
		});
		
		imgFinger2=(ImageView)findViewById(R.id.imageView3);
		imgFinger2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FPDialog(2);
			}
		});
		
		final ImageView imgBardcode1d=(ImageView)findViewById(R.id.imageView4);
		imgBardcode1d.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				ToastUtil.showToastTop(EnrollActivity.this,"Please sweep Barcode...");
				BarcodeOpen();
			}
		});
		
		final ImageView imgBardcode2d=(ImageView)findViewById(R.id.imageView5);
		imgBardcode2d.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				bCapture=true;
				Intent intent = new Intent(EnrollActivity.this, CaptureActivity.class);
				startActivityForResult(intent,0);
			}
		});
		
		final ImageView imgCard=(ImageView)findViewById(R.id.imageView6);
		imgCard.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				ToastUtil.showToastTop(EnrollActivity.this,"Please put the card...");
				ReadCardSn();
			}
		});
		
		
		//类型
		spin1=(Spinner)findViewById(R.id.spinner1);
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource( this, R.array.us1_array, android.R.layout.simple_spinner_item); 
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spin1.setAdapter(adapter1);
		spin1.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override 
			public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3){ 
				person.type=pos;
			}

			@Override 
			public void onNothingSelected(AdapterView<?> arg0) {  
			    //nothing to do 
			} 
		});
		spin1.setSelection(1);
						
		//识别类型
		spin2=(Spinner)findViewById(R.id.spinner2); 
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource( this, R.array.us2_array, android.R.layout.simple_spinner_item); 
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		spin2.setAdapter(adapter2);
		spin2.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override 
		    public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3){ 
				//person.ident=pos;
		    }

			@Override 
			public void onNothingSelected(AdapterView<?> arg0) {  
		    } 
		});
		spin2.setSelection(0);

		
		soundPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        soundIda = soundPool.load(this, R.raw.dong, 1);
        
        //Card
        InitReadCard();
        //Barcode
        openSerialPort();        
        vFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
        FPInit();
	}
	
	private void workExit(){
		if(SerialPortManager.getInstance().isOpen()){
			bIsCancel=true;			
			SerialPortManager.getInstance().closeSerialPort();
			CloseReadCard();
			//BarcodeClose();
			
			//if(fpDialog.isShowing()){
			//	fpDialog.cancel();
			//}
			
			this.finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(resultCode){
		case 1:{ 
			bCapture=false;
			Bundle bl= data.getExtras();
			String barcode=bl.getString("barcode");
			editText9.setText(barcode);
		 	}
			break;
		case 2:
			break;
		case 3:{
			bCapture=false;
			Bundle bl= data.getExtras();
			String id=bl.getString("id");
			Toast.makeText(EnrollActivity.this, "Pictures Finish", Toast.LENGTH_SHORT).show();
			byte[] photo=bl.getByteArray("photo");
			if(photo!=null){
				try{  
		           	Matrix matrix = new Matrix();
		           	Bitmap bm = BitmapFactory.decodeByteArray(photo, 0, photo.length);  
		            Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay(); 
		            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		            android.hardware.Camera.getCameraInfo(0, info);
		            int degrees = 0;
		            switch(display.getRotation()){
		                case Surface.ROTATION_0:	degrees = 0;	break;
		                case Surface.ROTATION_90:	degrees = 90;	break;
		                case Surface.ROTATION_180:	degrees = 180;	break;
		                case Surface.ROTATION_270:	degrees = 270;	break;
		            }
		            int result;
		            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
		            	result = (info.orientation + degrees) % 360;
		                result = (360 - result) % 360;
		            } else {
		                result = (info.orientation - degrees + 360) % 360;
		            }
		            matrix.preRotate(result);
		            Bitmap nbm=Bitmap.createBitmap(bm ,0,0, bm .getWidth(), bm .getHeight(),matrix,true);
			                
		            ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		            nbm.compress(Bitmap.CompressFormat.JPEG, 80, out);//将图片压缩到流中  
		            jpgbytes= out.toByteArray();
		                
		            Bitmap bitmap =BitmapFactory.decodeByteArray(jpgbytes, 0, jpgbytes.length);
		            imgPhoto.setImageBitmap(bitmap);
				}catch(Exception e){  
				}
			}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enroll, menu);
		return true;
	}
	
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){  
	    	AlertDialog.Builder builder = new Builder(this);
	    	builder.setTitle("Back");
	    	builder.setMessage("Data not save, back?");
	    	//builder.setCancelable(false);
	    	builder.setPositiveButton("Cancel", new OnClickListener() {
	    	    @Override
	    	    public void onClick(DialogInterface dialog, int which) {
	    	    	dialog.dismiss();	    	    	
	    	    }
	    	});
	    	builder.setNegativeButton("Back", new OnClickListener() {
	    	    @Override
	    	    public void onClick(DialogInterface dialog, int which) {
	    	    	dialog.dismiss();
	    	    	workExit();
	    	    }
	    	});
	    	builder.create().show();
	    	return true;  
	    }
	    return super.onKeyDown(keyCode, event);  
	} 
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case android.R.id.home:			
			workExit();
			return true;
		case R.id.action_save:{
				if(CheckInputData(1)){					
					person.id=(editText1.getText().toString());
					person.name=(editText2.getText().toString());
					if(isenrol1){
						person.template1=ExtApi.BytesToBase64(model1,model1.length);
						person.bytes1=new byte[model1.length];
						System.arraycopy(model1, 0, person.bytes1, 0,model1.length);
					}
					if(isenrol2){
						person.template2=ExtApi.BytesToBase64(model2,model2.length);
						person.bytes2=new byte[model2.length];
						System.arraycopy(model2, 0, person.bytes2, 0,model2.length);
					}
					//if(jpgbytes!=null)
					//	person.photo=ExtApi.BytesToBase64(jpgbytes,jpgbytes.length);
					if(CardSN.length()>4)
						person.cardsn=(CardSN);
					else
						person.cardsn=("null");
					person.barcode1d=(editText11.getText().toString());
					person.barcode2d=(editText12.getText().toString());							
					GlobalData.getInstance().userList.add(person);
					//GlobalData.getInstance().SaveUsersList();
					GlobalData.getInstance().SaveUserByID(person,jpgbytes);
					
					//Test
					/*
					for(int t=0;t<2000;t++){
						person.id="Test"+String.valueOf(t+1);
						GlobalData.getInstance().userList.add(person);
						GlobalData.getInstance().SaveUserByID(person,jpgbytes);
					}
					*/
					
					Toast.makeText(EnrollActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
					CloseReadCard();
					SerialPortManager.getInstance().closeSerialPort();
					finish();
				}
			}
			return true;
		case R.id.action_make:{
				//if(CheckInputData(0))
				{
					if(!isenrol1){
						Toast.makeText(EnrollActivity.this, "Please Input Template One", Toast.LENGTH_SHORT).show();
						return true;
					}
					if(!isenrol2){
						Toast.makeText(EnrollActivity.this, "Please Input Template Two", Toast.LENGTH_SHORT).show();
						return true;
					}
					byte[] databuf=new byte[1024];
					int size=1024;
					System.arraycopy(model1,0, databuf, 0, 512);
					System.arraycopy(model2,0, databuf, 512, 512);
					
					//MainActivity.btReader.WriteCard(databuf,size);
				}
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

			
	private boolean CheckInputData(int type){
		int len=editText1.getText().toString().length();
		if(len<=0){
			Toast.makeText(EnrollActivity.this, "Please enter the numbers", Toast.LENGTH_SHORT).show();
			return false;
		}
		len=editText2.getText().toString().length();
		if(len<=0){
			Toast.makeText(EnrollActivity.this, "Please enter a name", Toast.LENGTH_SHORT).show();
			return false;
		}
		/*
		if(mRefSize1<=0){
			Toast.makeText(EnrollActivity.this, "Please Input Template One", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(mRefSize2<=0){
			Toast.makeText(EnrollActivity.this, "Please Input Template Two", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(!iscap){
			Toast.makeText(EnrollActivity.this, "Please Take Photo", Toast.LENGTH_SHORT).show();
			return false;
		}
		*/
		if(type==1){
			if(GlobalData.getInstance().IsHaveUserItem(editText1.getText().toString())){
				Toast.makeText(EnrollActivity.this, "ID Exists", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}
		
	//指纹登记
	private void FPDialog(int i){
    	iFinger=i;
    	AlertDialog.Builder builder = new Builder(EnrollActivity.this);
    	builder.setTitle("Registration fingerprint");
    	final LayoutInflater inflater = LayoutInflater.from(EnrollActivity.this);
    	View vl = inflater.inflate(R.layout.dialog_enrolfinger, null);
    	fpImage = (ImageView) vl.findViewById(R.id.imageView1);
    	tvFpStatus= (TextView) vl.findViewById(R.id.textview1);		    	
    	builder.setView(vl);
    	builder.setCancelable(false);
    	builder.setNegativeButton("Cancel", new OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	//SerialPortManager.getInstance().closeSerialPort();
    	    	dialog.dismiss();
    	    }
    	});
    	builder.setOnCancelListener(new OnCancelListener() {
    	    @Override
    	    public void onCancel(DialogInterface dialog) {
    	    	//SerialPortManager.getInstance().closeSerialPort();
    	    	dialog.dismiss();		    		
    	    }
    	});
    	
    	fpDialog = builder.create();
    	fpDialog.setCanceledOnTouchOutside(false);
    	fpDialog.show();
    	    	
    	FPProcess();
    }
	
	private void FPInit(){
		//指纹处理
		vFingerprint.setOnGetImageListener(new OnGetImageListener() {
			@Override
			public void onGetImageSuccess() {
				if(!bIsCancel){					
					if(bcheck){
						vFingerprint.FP_GetImage();
					}else{
						if(bIsUpImage){
							vFingerprint.FP_UpImage();
							tvFpStatus.setText(getString(R.string.txt_fpdisplay));
						}else{
							tvFpStatus.setText(getString(R.string.txt_fpprocess));
							vFingerprint.FP_GenChar(count);
						}
					}
				}
			}

			@Override
			public void onGetImageFail() {
				if(!bIsCancel){
					if(bcheck){
						bcheck=false;
						tvFpStatus.setText(getString(R.string.txt_fpplace));
						vFingerprint.FP_GetImage();
						count++;
					}else{
						vFingerprint.FP_GetImage();
					}
				}
			}
		});
		
		vFingerprint.setOnUpImageListener(new OnUpImageListener() {
			@Override
			public void onUpImageSuccess(byte[] data) {
				Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
				fpImage.setImageBitmap(image);
				//fpImage.setBackgroundDrawable(new BitmapDrawable(image));
				vFingerprint.FP_GenChar(count);
				tvFpStatus.setText(getString(R.string.txt_fpprocess));
			}

			@Override
			public void onUpImageFail() {
			}
		});
		
		vFingerprint.setOnGenCharListener(new OnGenCharListener() {
			@Override
			public void onGenCharSuccess(int bufferId) {
				if (bufferId == 1) {
					bcheck=true;    					
					tvFpStatus.setText(getString(R.string.txt_fplift));
					vFingerprint.FP_GetImage();
				} else if (bufferId == 2) {
					vFingerprint.FP_RegModel();
				}
			}

			@Override
			public void onGenCharFail() {
				tvFpStatus.setText(getString(R.string.txt_fpfail));
			}
		});
		
		vFingerprint.setOnRegModelListener(new OnRegModelListener() {

			@Override
			public void onRegModelSuccess() {
				vFingerprint.FP_UpChar();
				//tvFpStatus.setText(getString(R.string.txt_fpenrolok));
			}

			@Override
			public void onRegModelFail() {
				tvFpStatus.setText(getString(R.string.txt_fpenrolfail));
			}
		});

		vFingerprint.setOnUpCharListener(new OnUpCharListener() {

			@Override
			public void onUpCharSuccess(byte[] model) {
				
				for(int i=0;i<GlobalData.getInstance().userList.size();i++){
					if(GlobalData.getInstance().userList.get(i).bytes1!=null){
						if(FPMatch.getInstance().MatchTemplate(model, GlobalData.getInstance().userList.get(i).bytes1)>60){
							tvFpStatus.setText(getString(R.string.txt_fpduplicate));
							return;
						}
					}
					if(GlobalData.getInstance().userList.get(i).bytes2!=null){
						if(FPMatch.getInstance().MatchTemplate(model, GlobalData.getInstance().userList.get(i).bytes2)>60){
							tvFpStatus.setText(getString(R.string.txt_fpduplicate));
							return;
						}
					}
				}
				
				if(iFinger==1){
        			editText6.setText(getString(R.string.txt_fpenrolok));
        			System.arraycopy(model, 0, EnrollActivity.this.model1,0,512);
        			isenrol1=true;
				}else{
        			editText7.setText(getString(R.string.txt_fpenrolok));
        			System.arraycopy(model, 0, EnrollActivity.this.model2,0,512);
        			isenrol2=true;
				}
				tvFpStatus.setText(getString(R.string.txt_fpenrolok));
				fpDialog.cancel();
			}

			@Override
			public void onUpCharFail() {
				tvFpStatus.setText(getString(R.string.txt_fpenrolfail));
			}
		});
		
	}
	
	private void FPProcess(){
    		count = 1;
    		tvFpStatus.setText(getString(R.string.txt_fpplace));
    		try {
    			Thread.currentThread();
    			Thread.sleep(200);
    		}catch (InterruptedException e)
    		{
    			e.printStackTrace();
    		}
    		vFingerprint.FP_GetImage();
     }

	//一维条码
	//条码
    public void BarcodeOpen(){
    	if(mDeviceType==0){
    		MtGpio mt=new MtGpio();
    		mt.BCPowerSwitch(true);
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
    
    public void BarcodeClose(){    	
    	if (mReadThread != null)
			mReadThread.interrupt();
		closeSerialPort();
		mSerialPort = null;		
		if(mDeviceType==0){
    		MtGpio mt=new MtGpio();
    		mt.BCReadSwitch(true);
    		mt.BCPowerSwitch(false);
    	}else{
    		
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
		} catch (IOException e) {
		} catch (InvalidParameterException e) {
		}
    }
    
    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			String path = "/dev/ttyMT1";
			//int baudrate = 9600;	//1D
			int baudrate = 115200;	//2D
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}
			mSerialPort = new SerialPort();
			if(mSerialPort.getmodel().equals("FP07")){
				path = "/dev/ttyMT2";
				mDeviceType=1;
				baudrate = 9600;
			}else{
				path = "/dev/ttyMT1";
				mDeviceType=0;
			}
			mSerialPort.OpenDevice(new File(path), baudrate, 0,SerialPort.DEVTYPE_UART);
		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
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
					Toast.makeText(EnrollActivity.this, "Read barcodes fail", Toast.LENGTH_SHORT).show();	
					return;
				}
			}
		}
	}
    
    protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				System.arraycopy(buffer, 0, databuf,datasize,size);					
				datasize=datasize+size;
				if(TimerBarcode==null){
					TimerBarcodeStart();
				}
			}
		});
	}
    
    public void TimerBarcodeStart() {
		TimerBarcode = new Timer(); 
		HandlerBarcode = new Handler() { 
			@Override 
            public void handleMessage(Message msg) { 
				TimerBarcodeStop();
				if(datasize>0){
					byte tp[]=new byte[datasize];
					System.arraycopy(databuf, 0, tp,0,datasize);
					editText8.setText(new String(tp));
					soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
					datasize=0;
				}
                super.handleMessage(msg); 
            }
        };
        TaskBarcode = new TimerTask() { 
            @Override 
            public void run() { 
                Message message = new Message(); 
                message.what = 1; 
                HandlerBarcode.sendMessage(message); 
            } 
        }; 
        TimerBarcode.schedule(TaskBarcode, 1000, 1000); 
    }
	
	public void TimerBarcodeStop() {
    	if (TimerBarcode!=null) {  
    		TimerBarcode.cancel();  
    		TimerBarcode = null;  
    		TaskBarcode.cancel();
    		TaskBarcode=null;
		}
    }
	
	public void InitReadCard(){
		if(ActivityList.getInstance().IsUseNFC){
			nfcAdapter = NfcAdapter.getDefaultAdapter(this);
			if (nfcAdapter == null) {
				Toast.makeText(this, "Device does not support NFC!", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}
			if (!nfcAdapter.isEnabled()) {
				Toast.makeText(this, "Enable the NFC function in the system settings!", Toast.LENGTH_SHORT).show();
				finish();
				return;
			}  
			
			mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
			mFilters = new IntentFilter[]{
		                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
		                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
		                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};
		}else{
			if(rfid==null)
				rfid=new MtRfid();
			rfid.RfidInit();
			rfid.SetContext(this);
		}  
	}
	
	public void CloseReadCard(){
    	if(ActivityList.getInstance().IsUseNFC){
		}else{
			TimerCardStop();
	    	rfid.RfidClose();	//Close
		} 
    }
	
	public void ReadCardSn(){
		if(ActivityList.getInstance().IsUseNFC){
		}else{
			TimerCardStart();
		}        
	}
	
	//RFID
	public void TimerCardStart() {
		TimerCard = new Timer(); 
		HandlerCard = new Handler() { 
			@Override 
	        public void handleMessage(Message msg) { 
				TimerCardStop();

				int[] sn=new int[8];
				//byte[] cardsn=rfid.IntArrayToByteArray(sn,4);
				
				//count++;
				if(rfid.RfidGetSn(sn)==0){
					String cardstr=/*Integer.toString(count)+":"+*/
							Integer.toHexString(sn[0]&0xFF).toUpperCase()+
	    					Integer.toHexString(sn[1]&0xFF).toUpperCase()+
	    					Integer.toHexString(sn[2]&0xFF).toUpperCase()+
	    					Integer.toHexString(sn[3]&0xFF).toUpperCase();
					
					for(int i=0;i<GlobalData.getInstance().userList.size();i++){
						if(GlobalData.getInstance().userList.get(i).cardsn.indexOf(cardstr)>=0){
							Toast.makeText(EnrollActivity.this,"Failed,Duplicate registration!", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					
					editText10.setText(cardstr);
					CardSN=cardstr;
					
					int[] buffer=new int[4096];
					switch(rfidtype){
					case 0:
						break;
					case 1:
						if(rfid.RfidReadFullCard(sn, buffer, 256)==0){
							byte[] b=rfid.IntArrayToByteArray(buffer, 256);
							editText1.setText(new String(b));
							Toast.makeText(EnrollActivity.this,"Read card Success", Toast.LENGTH_SHORT).show();	
						}
						break;
					case 2:{
							String txt=editText1.getText().toString();
							byte[] b=txt.getBytes();
							int[] ir=rfid.ByteArrayToIntArray(b,b.length);
							for(int i=0;i<ir.length;i++){
								buffer[i]=ir[i];
							}
							if(rfid.RfidWriteFullCard(sn, buffer, 256)==0){
								Toast.makeText(EnrollActivity.this,"Write card success", Toast.LENGTH_SHORT).show();		
							}
						}
						break;
					}
					soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
				}
	            super.handleMessage(msg); 
	        }
	    };
	    TaskCard = new TimerTask() { 
	        @Override 
	        public void run() { 
	            Message message = new Message(); 
	            message.what = 1; 
	            HandlerCard.sendMessage(message); 
	        } 
	    }; 
	    TimerCard.schedule(TaskCard, 500, 500); 
	}

	public void TimerCardStop() {
		if (TimerCard!=null) {  
			TimerCard.cancel();  
			TimerCard = null;  
			TaskCard.cancel();
			TaskCard=null;
		}
	}
	
	//NFC
	
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		processIntent(intent);
	}
	
	private void processIntent(Intent intent){
		byte[] sn = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
		String cardstr=/*Integer.toString(count)+":"+*/
				Integer.toHexString(sn[0]&0xFF).toUpperCase()+
				Integer.toHexString(sn[1]&0xFF).toUpperCase()+
				Integer.toHexString(sn[2]&0xFF).toUpperCase()+
				Integer.toHexString(sn[3]&0xFF).toUpperCase();
		
		for(int i=0;i<GlobalData.getInstance().userList.size();i++){
			if(GlobalData.getInstance().userList.get(i).cardsn.indexOf(cardstr)>=0){
				Toast.makeText(EnrollActivity.this,"Failed,Duplicate registration!", Toast.LENGTH_SHORT).show();
				return;
			}
		}		
		editText10.setText(cardstr);
		CardSN=cardstr;
		//soundPool.play(soundIda, 1.0f, 0.5f, 1, 0, 1.0f);
	}  
	
	@Override
	public void onPause() {
		if(ActivityList.getInstance().IsUseNFC){
			if (nfcAdapter != null)
				nfcAdapter.disableForegroundDispatch(this);
		}
		PowerManager pm = (PowerManager)getSystemService(this.POWER_SERVICE);
		if(!pm.isScreenOn()){
			if(!bCapture){
				workExit();
			}
		}
		super.onPause();
	}
    
	@Override
	protected void onResume() {
		super.onResume();
		if(ActivityList.getInstance().IsUseNFC){
			if (nfcAdapter != null)
				nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,null);
		}
	} 
}
