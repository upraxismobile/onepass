package com.fgtit.onepass;

import java.io.IOException;
import java.security.InvalidParameterException;

import com.fgtit.onepass.R;
import com.fgtit.fpcore.FPMatch;
import com.fgtit.utils.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android_serialport_api.AsyncFingerprint;
import android_serialport_api.AsyncFingerprint.OnGenCharExListener;
import android_serialport_api.AsyncFingerprint.OnGenCharListener;
import android_serialport_api.AsyncFingerprint.OnGetImageExListener;
import android_serialport_api.AsyncFingerprint.OnGetImageListener;
import android_serialport_api.AsyncFingerprint.OnRegModelListener;
import android_serialport_api.AsyncFingerprint.OnUpImageExListener;
import android_serialport_api.AsyncFingerprint.OnUpImageListener;
import android_serialport_api.AsyncFingerprint.OnUpCharListener;
import android_serialport_api.SerialPortManager;

public class FingerprintActivity extends Activity implements
		OnClickListener {
	
	private AsyncFingerprint registerFingerprint;
	private AsyncFingerprint validateFingerprint;
	private Button start;
	private Button validate;
	private Button back;
	private ImageView fingerprintImage;

	private CheckBox checkBox;
	private ProgressDialog progressDialog;
	
	private int count;
	private boolean IsUpImage=false;
	
	public byte mRefList[][]=new byte[2048][512];
    public int	mRefCount=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fingerprint);
		
		FPMatch.getInstance().InitMatch();
		
		initView();
		initViewListener();
		initData();		
	}

	private void initView() {
		start = (Button) findViewById(R.id.start);
		validate = (Button) findViewById(R.id.validateZhiwen);
		back = (Button) findViewById(R.id.backRegister);
		fingerprintImage = (ImageView) findViewById(R.id.fingerprintImage);
		
		checkBox=(CheckBox)findViewById(R.id.checkBox1);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override            
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				IsUpImage=arg1;          
				}        
			});
		
	}
    
	private void initData() {
		if(!SerialPortManager.getInstance().isOpen()){
			try {
				SerialPortManager.getInstance().openSerialPort();
			} catch (InvalidParameterException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!SerialPortManager.getInstance().isOpen()){
			ToastUtil.showToast(this, "Open Port Fail£¡");
			return;
		}
		registerFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
		validateFingerprint = SerialPortManager.getInstance().getNewAsyncFingerprint();
		
		registerFingerprint.setOnGetImageListener(new OnGetImageListener() {
			@Override
			public void onGetImageSuccess() {
				cancleProgressDialog();
				if(IsUpImage){
					registerFingerprint.FP_UpImage();
					showProgressDialog("Plase Wait ...");
				}else{
					registerFingerprint.FP_GenChar(count);
				}
			}

			@Override
			public void onGetImageFail() {
				registerFingerprint.FP_GetImage();
			}
		});
		
		registerFingerprint.setOnUpImageListener(new OnUpImageListener() {
			@Override
			public void onUpImageSuccess(byte[] data) {
				Log.i("whw", "up image data.length="+data.length);
				Bitmap image = BitmapFactory.decodeByteArray(data, 0,data.length);
				fingerprintImage.setBackgroundDrawable(new BitmapDrawable(image));
				registerFingerprint.FP_GenChar(count);
			}

			@Override
			public void onUpImageFail() {
				Log.i("whw", "up image fail");
			}
		});

		registerFingerprint.setOnGenCharListener(new OnGenCharListener() {
			@Override
			public void onGenCharSuccess(int bufferId) {
				if (bufferId == 1) {
					cancleProgressDialog();
					showProgressDialog("Place Finger£¡");
					registerFingerprint.FP_GetImage();
					count++;
				} else if (bufferId == 2) {
					registerFingerprint.FP_RegModel();
				}
			}

			@Override
			public void onGenCharFail() {
				cancleProgressDialog();
				ToastUtil.showToast(FingerprintActivity.this,"Enrol Fail£¡");
			}
		});

		registerFingerprint.setOnRegModelListener(new OnRegModelListener() {

			@Override
			public void onRegModelSuccess() {
				cancleProgressDialog();
				registerFingerprint.FP_UpChar();
				//ToastUtil.showToast(FingerprintActivity.this, "Enrol OK£¡");
			}

			@Override
			public void onRegModelFail() {
				cancleProgressDialog();
				ToastUtil.showToast(FingerprintActivity.this, "Enrol Fail£¡");
			}
		});

		registerFingerprint.setOnUpCharListener(new OnUpCharListener() {
			@Override
			public void onUpCharSuccess(byte[] model) {
				cancleProgressDialog();
				Log.i("whw", "#################model.length="+model.length);
								
				if(mRefCount<2048){
					//System.arraycopy(model, 0, mRefList[mRefCount],0, model.length);
					System.arraycopy(model, 0, mRefList[mRefCount],0, 512);
					mRefCount++;
					ToastUtil.showToast(FingerprintActivity.this, "Enrol OK:"+String.valueOf(mRefCount));
				}
			}

			@Override
			public void onUpCharFail() {
				cancleProgressDialog();
				ToastUtil.showToast(FingerprintActivity.this, "Enrol Fail£¡");
			}
		});
		
		validateFingerprint.setOnGetImageExListener(new OnGetImageExListener() {
			@Override
			public void onGetImageExSuccess() {
				cancleProgressDialog();
				if(IsUpImage){
					validateFingerprint.FP_UpImageEx();
					showProgressDialog("Plase Wait...");
				}else{
					validateFingerprint.FP_GenCharEx(1);
				}
			}

			@Override
			public void onGetImageExFail() {
				validateFingerprint.FP_GetImageEx();
			}
		});
		
		validateFingerprint.setOnUpImageExListener(new OnUpImageExListener() {
			@Override
			public void onUpImageExSuccess(byte[] data) {
				Log.i("whw", "up image data.length="+data.length);
				Bitmap image = BitmapFactory.decodeByteArray(data, 0,data.length);
				//fingerprintImage.setImageBitmap(image);
				fingerprintImage.setBackgroundDrawable(new BitmapDrawable(image));
				validateFingerprint.FP_GenCharEx(1);
			}

			@Override
			public void onUpImageExFail() {
				Log.i("whw", "up image fail");
			}
		});
		
		validateFingerprint.setOnGenCharExListener(new OnGenCharExListener() {
			@Override
			public void onGenCharExSuccess(int bufferId) {
				validateFingerprint.FP_UpChar();
				Log.i("whw", "validateFingerprint onGenCharSuccess bufferId="+bufferId);
			}

			@Override
			public void onGenCharExFail() {
				cancleProgressDialog();
				ToastUtil.showToast(FingerprintActivity.this,"Fail£¡");
				Log.i("whw", "validateFingerprint onGenCharFail");
			}
		});

		validateFingerprint.setOnUpCharListener(new OnUpCharListener() {

			@Override
			public void onUpCharSuccess(byte[] model) {
				cancleProgressDialog();
				for(int i=0;i<mRefCount;i++){
            		int score=FPMatch.getInstance().MatchTemplate(mRefList[i],model);
            		if(score>50){
            			ToastUtil.showToast(FingerprintActivity.this,("ID:"+String.valueOf(i+1)+"   Scope:"+String.valueOf(score)));
            			return;
            		}            			
            	}
				ToastUtil.showToast(FingerprintActivity.this,"No Match");
			}

			@Override
			public void onUpCharFail() {
				cancleProgressDialog();
				ToastUtil.showToast(FingerprintActivity.this, "Fail -1£¡");
				Log.i("whw", "validateFingerprint onDownCharFail");
			}
			
		});
		
		
	}

	private void initViewListener() {
		start.setOnClickListener(this);
		validate.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			count = 1;
			showProgressDialog("Place Finger£¡");
			registerFingerprint.FP_GetImage();
			Log.i("whw", "send end");
			break;
		case R.id.validateZhiwen:
			showProgressDialog("Place Finger£¡");
			validateFingerprint.FP_GetImageEx();
			break;
		case R.id.backRegister:
			SerialPortManager.getInstance().closeSerialPort();
			finish();
			break;
		default:
			break;
		}
	}

	private void showProgressDialog(String message) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(message);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	private void cancleProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.cancel();
			progressDialog = null;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			SerialPortManager.getInstance().closeSerialPort();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStop() {
		cancleProgressDialog();
		super.onStop();
	}

}
