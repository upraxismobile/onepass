package com.fgtit.onepass;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fgtit.onepass.R;
import com.fgtit.data.UserItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraActivity extends Activity //implements SurfaceHolder.Callback
{
	private ImageView imagePhoto;
	private TextView textStatus;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private boolean iscap=false;
	private String filePath;  
	 
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_activity);
		
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		
		imagePhoto=(ImageView)findViewById(R.id.imageView1);
		textStatus=(TextView)findViewById(R.id.textview1);
		textStatus.setText("Please Capture Photo ...");
			       
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.capture, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id){
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_back:
			CameraActivity.this.finish();  
			return true;
		case R.id.action_capture:{
			String state = Environment.getExternalStorageState(); // 判断是否存在sd
			if (state.equals(Environment.MEDIA_MOUNTED)) { // 直接调用系统的照相机
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE/*android.media.action.IMAGE_CAPTURE*/); 
				filePath = getFileName(); 
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath))); 
				startActivityForResult(intent, 1);
			}
			 else { 
				 textStatus.setText("Check the phone if there is an SD card"); 
			 }	
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		 if (1 == requestCode) { // 系统相机返回处理 
			 if (resultCode == Activity.RESULT_OK) {
				 iscap=true;
				 textStatus.setText("Photographs success"); 
				 //Bitmap cameraBitmap = (Bitmap) data.getExtras().get("data");
				 // 处理图像 
				 //imagePhoto.setImageBitmap(zoomImg(cameraBitmap,320,240,1));
				  
				 BitmapFactory.Options options = new BitmapFactory.Options(); 
				 options.inSampleSize = 2; 
				 Bitmap bm = BitmapFactory.decodeFile(filePath, options);
				 //imagePhoto.setImageBitmap(bm);
				 //*
				 Bitmap nm=zoomImg(bm,320,480,1);
				 imagePhoto.setImageBitmap(nm);				 
				 try {
					savePngBitmap(nm,filePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// */
				saveJpgBytes(nm);
			 } 
		 }
		 super.onActivityResult(requestCode, resultCode, data); 
	}
	
	   private String getFileName() {  
		   String saveDir = Environment.getExternalStorageDirectory() + "/fgtit";  
		   File dir = new File(saveDir);  
		   if (!dir.exists()) {  
			   dir.mkdir(); // 创建文件夹  
		   }  
		   //用日期作为文件名，确保唯一性  
		   Date date = new Date();  
		   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");  
		   String fileName = saveDir + "/" + formatter.format(date) + ".PNG";  
		   return fileName;  
	}  

	   
	public static Bitmap zoomImg(Bitmap bm, int newWidth ,int newHeight,int mode){
	    // 获得图片的宽高
	   int width = bm.getWidth();
	   int height = bm.getHeight();
	    // 计算缩放比例
	   float scaleWidth = ((float) newWidth) / width;
	   float scaleHeight = ((float) newHeight) / height;
	    // 取得想要缩放的matrix参数
	   Matrix matrix = new Matrix();
	   if(mode==1)
		   matrix.postScale(scaleWidth, scaleWidth);
	   else
		   matrix.postScale(scaleWidth, scaleHeight);
	    // 得到新的图片
	   Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	   return newbm;
	 }
	
	 public void savePngBitmap(Bitmap mBitmap,String filename) throws IOException
	 {
	        File f = new File(filename);
	        f.createNewFile();
	        FileOutputStream fOut = null;
	        try {
	                fOut = new FileOutputStream(f);
	        } catch (FileNotFoundException e) {
	                e.printStackTrace();
	        }
	     mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
	        try {
	                fOut.flush();
	        } catch (IOException e) {
	                e.printStackTrace();
	        }
	        try {
	                fOut.close();
	        } catch (IOException e) {
	                e.printStackTrace();
	        }
	 }
	 
	 public void saveJpgBitmap(Bitmap mBitmap,String filename) throws IOException
	 {
	        File f = new File(filename);
	        f.createNewFile();
	        FileOutputStream fOut = null;
	        try {
	                fOut = new FileOutputStream(f);
	        } catch (FileNotFoundException e) {
	                e.printStackTrace();
	        }
	        mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
	        try {
	                fOut.flush();
	        } catch (IOException e) {
	                e.printStackTrace();
	        }
	        try {
	                fOut.close();
	        } catch (IOException e) {
	                e.printStackTrace();
	        }
	 }
	 
	 public byte[] saveJpgBytes(Bitmap mBitmap)
	 {
		 ByteArrayOutputStream fOut = null;
		 fOut = new ByteArrayOutputStream ();
	     mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
	     byte[] byteArray = fOut.toByteArray();
	     return byteArray;
	 }
}
