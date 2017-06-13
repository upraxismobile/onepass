package com.fgtit.app;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.fgtit.data.GlobalData;
import com.fgtit.onepass.R;
import com.fgtit.utils.ToastUtil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateApp {
    private static final int DOWN_NOSDCARD = 0;
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_ERROR=3;
    private static final int DIALOG_TYPE_LATEST = 0;
    private static final int DIALOG_TYPE_FAIL = 1;
    private static final int DIALOG_TYPE_INTERNETERROR = 2;
    private static UpdateApp updateapp;
    private Context mContext;
    // 通知对话框
    private Dialog noticeDialog;
    // 下载对话框
    private Dialog downloadDialog;
    // 进度条
    private ProgressBar mProgress;
    // 显示下载数值
    private TextView mProgressText;
    // 查询动画
    private ProgressDialog mProDialog;
    // '已经是最新' 或者 '无法获取最新版本' 的对话框
    private Dialog latestOrFailDialog;
    // 返回的安装包url
    private String apkUrl = "";
    // 进度值
    private int progress;
    // 下载线程
    private Thread downLoadThread;
    // 终止标记
    private boolean interceptFlag;
    // 提示语
    private String updateMsg = "";
    // 下载包保存路径
    private String savePath = "";
    // apk保存完整路径
    private String apkFilePath = "";
    // 临时下载文件路径
    private String tmpFilePath = "";
    // 下载文件大小
    private String apkFileSize;
    // 已下载文件大小
    private String tmpFileSize;
    private String curVersionName = "";
    private int curVersionCode;
    private AppDetail mDownload;
    //private String checkUrl="http://192.168.1.124/apk/update.xml";
    
    private boolean ischeck=false;
    
    private Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		ischeck=false;
    	    switch (msg.what) {
    	    case DOWN_UPDATE:
        		mProgress.setProgress(progress);
        		mProgressText.setText(tmpFileSize + "/" + apkFileSize);
        		break;
    	    case DOWN_OVER:
    	    	downloadDialog.dismiss();
        		installApk();
        		break;
    	    case DOWN_NOSDCARD:
        		downloadDialog.dismiss();
        		Toast.makeText(mContext, mContext.getString(R.string.txt_upd06),Toast.LENGTH_SHORT).show();
        		break;
    	    case DOWN_ERROR:
        		downloadDialog.dismiss();
        		if(msg.arg1==0){
        			Toast.makeText(mContext, mContext.getString(R.string.txt_upd07), Toast.LENGTH_SHORT).show();
        		}else if(msg.arg1==1||msg.arg1==2){
        			Toast.makeText(mContext, mContext.getString(R.string.txt_upd08),Toast.LENGTH_SHORT).show();
        		}
        		break;
    	    }
    	};
    };
    
    public static UpdateApp getInstance() {
    	if (updateapp == null) {
    	    updateapp = new UpdateApp();
    	}
    	updateapp.interceptFlag = false;
    	return updateapp;
    }
    
    public void DownLoader(Context context, AppDetail download) {
    	this.mContext = context;
    	this.mDownload = download;
    	showDownloadDialog();
    }
    
    public void setAppContext(Context context){
    	this.mContext = context;
    }
    
    /**
     * 检查App更新
     * @param context
     * @param isShowMsg
     * 是否显示提示消息
     */
    public void checkAppUpdate(/*Context context, */final boolean isShowMsg,final boolean notmain) {
    	//this.mContext = context;
    	if(ischeck)
    		return;
    	ischeck=true;
    	//Toast.makeText(this.mContext, "检查更新...", Toast.LENGTH_SHORT).show();
    	//ToastUtil.showToastTop(this.mContext, "检查更新...");
    	
    	getCurrentVersion(mContext);    	
    	if (isShowMsg) {
    	    if (mProDialog == null)
    	    	mProDialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.txt_upd09),true, true);
    	    else if (mProDialog.isShowing()|| (latestOrFailDialog != null && latestOrFailDialog.isShowing()))
    	    	return;
    	}
    	
    	final Handler handler = new Handler() {
    	    public void handleMessage(Message msg) {
    	    	
    	    	ischeck=false;
    	    	
    	    	// 进度条对话框不显示 - 检测结果也不显示
        		if (mProDialog != null && !mProDialog.isShowing()) {
        		    return;
        		}
        		// 关闭并释放释放进度条对话框
        		if (isShowMsg && mProDialog != null) {
        		    mProDialog.dismiss();
        		    mProDialog = null;
        		}
        		// 显示检测结果
        		if (msg.what == 1) {
        		    mDownload = (AppDetail) msg.obj;
        		    if (mDownload != null) {
        			if (curVersionCode < mDownload.getVersionCode()) {
        			    apkUrl = mDownload.getUri()+mDownload.getFileName();
        			    updateMsg = mDownload.getAppHistory();
        			    showNoticeDialog();
        			} else if (isShowMsg) {
        			    if (notmain) {
        			    	showLatestOrFailDialog(DIALOG_TYPE_LATEST);
        			    }
        			}
        		    }
        		}else if(msg.what==-1&&isShowMsg){
        			 showLatestOrFailDialog(DIALOG_TYPE_INTERNETERROR);
        		}else if (isShowMsg) {
        		    showLatestOrFailDialog(DIALOG_TYPE_FAIL);
        		}
    	    }
    	};
	
    	new Thread() {
    		public void run() {
    			Message msg = new Message();
    			try {
    				DefaultHttpClient client = new DefaultHttpClient();
    				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
    				HttpGet get = new HttpGet(GlobalData.getInstance().UpdateUrl);
    				HttpResponse response = client.execute(get);
    				if (response.getStatusLine().getStatusCode() == 200) {
    					HttpEntity entity = response.getEntity();
    					//InputStream stream = new ByteArrayInputStream( EntityUtils.toString(entity, "gb2312").getBytes());
    					InputStream stream = new ByteArrayInputStream( EntityUtils.toString(entity, "UTF-8").getBytes());
    					AppDetail update = AppDetail.parseXML(stream);
    					msg.what = 1;
    					msg.obj = update;
    				}else{
    					msg.what = -1;
    				}
    			} catch (Exception e) {
    			    e.printStackTrace();
    			    msg.what = -1;
    			}
    			handler.sendMessage(msg);
    		}
    	}.start();
    	
    }
    
    /*显示'已经是最新'或者'无法获取版本信息'对话框*/
    private void showLatestOrFailDialog(int dialogType) {
    	String ToastMsg="";
    	if (latestOrFailDialog != null) {
    	    // 关闭并释放之前的对话框
    	    latestOrFailDialog.dismiss();
    	    latestOrFailDialog = null;
    	}
//    	AlertDialog.Builder builder = new Builder(mContext);
//    	builder.setTitle("系统提示");
    	if (dialogType == DIALOG_TYPE_LATEST) {
//    	    builder.setMessage("您当前已经是最新版本");
    	    ToastMsg=mContext.getString(R.string.txt_upd10);
    	} else if (dialogType == DIALOG_TYPE_FAIL) {
//    	    builder.setMessage("无法获取版本更新信息");
    		ToastMsg=mContext.getString(R.string.txt_upd11);
    	}else if(dialogType==DIALOG_TYPE_INTERNETERROR){
//    		builder.setMessage("网络故障，无法连接服务器");
    		ToastMsg=mContext.getString(R.string.txt_upd12);
    	}
    	Toast.makeText(mContext, ToastMsg, Toast.LENGTH_SHORT).show();
        }
        /*获取当前客户端版本信息*/
        public String getCurrentVersion(Context context) {
    	try {
    	    PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    	    curVersionName = info.versionName;
    	    curVersionCode = info.versionCode;
    	} catch (NameNotFoundException e) {
    	    e.printStackTrace(System.err);
    	}
    	return curVersionName;
    }
    
    /*显示版本更新通知对话框*/
    private void showNoticeDialog() {
    	AlertDialog.Builder builder = new Builder(mContext);
    	builder.setTitle(mContext.getString(R.string.txt_upd01));
    	builder.setMessage(updateMsg);
    	builder.setCancelable(false);
    	builder.setPositiveButton(mContext.getString(R.string.txt_upd02), new OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    		dialog.dismiss();
    		showDownloadDialog();
    	    }
    	});
    	builder.setNegativeButton(mContext.getString(R.string.txt_upd03), new OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    		dialog.dismiss();
    	    }
    	});
    	noticeDialog = builder.create();
    	noticeDialog.show();
    }
    
    /*显示下载对话框*/
    private void showDownloadDialog() {
    	AlertDialog.Builder builder = new Builder(mContext);
    	builder.setTitle(mContext.getString(R.string.txt_upd04));
    	final LayoutInflater inflater = LayoutInflater.from(mContext);
    	View v = inflater.inflate(R.layout.dialog_updateapp, null);
    	mProgress = (ProgressBar) v.findViewById(R.id.progress);
    	mProgressText = (TextView) v.findViewById(R.id.title);
    	builder.setView(v);
    	builder.setCancelable(false);
    	builder.setNegativeButton(mContext.getString(R.string.txt_upd05), new OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    		dialog.dismiss();
    		interceptFlag = true;
    	    }
    	});
    	builder.setOnCancelListener(new OnCancelListener() {
    	    @Override
    	    public void onCancel(DialogInterface dialog) {
    		dialog.dismiss();
    		interceptFlag = true;
    	    }
    	});
    	downloadDialog = builder.create();
    	downloadDialog.setCanceledOnTouchOutside(false);
    	downloadDialog.show();
    	downloadApk();
    }
    
    private Runnable mdownApkRunnable = new Runnable() {
    	Message error_msg=new Message();
    	@Override
    	public void run(){
    	    try {
    	    	String apkName = mDownload.getFileName().replace(".apk","")+".apk";
        		String tmpApk =  mDownload.getFileName().replace(".apk","")+".tmp";
        		// 判断是否挂载了SD卡
        		String storageState = Environment.getExternalStorageState();
        		if (storageState.equals(Environment.MEDIA_MOUNTED)) {
        		    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FGTIT/";
        		    File file = new File(savePath);
        		    if (!file.exists()) {
        			file.mkdirs();
        		    }
        		    apkFilePath = savePath + apkName;
        		    tmpFilePath = savePath + tmpApk;
        		}
        		// 没有挂载SD卡，无法下载文件
        		if (apkFilePath == null || apkFilePath == "") {
        		    mHandler.sendEmptyMessage(DOWN_NOSDCARD);
        		    return;
        		}
        		File ApkFile = new File(apkFilePath);
        		// 是否已下载更新文件
//        		if (ApkFile.exists()) {
//        		    downloadDialog.dismiss();
//        		    installApk();
//        		    return;
//        		}
        		// 输出临时下载文件
        		File tmpFile = new File(tmpFilePath);
        		FileOutputStream fos = new FileOutputStream(tmpFile);
        		URL url = new URL(mDownload.getUri()+mDownload.getFileName());
        		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        		try {
        		    conn.connect();
        		} catch (ConnectTimeoutException e) {
        		    error_msg.what=DOWN_ERROR;
        		    error_msg.arg1=0;
        		    mHandler.sendMessage(error_msg);
        		}
        		int length = conn.getContentLength();
        		InputStream is = conn.getInputStream();
        		// 显示文件大小格式：2个小数点显示
        		DecimalFormat df = new DecimalFormat("0.00");
        		// 进度条下面显示的总文件大小
        		apkFileSize = df.format((float) length / 1024 / 1024) + "MB";
        		int count = 0;
        		byte buf[] = new byte[1024];
    			do {
    		    	int numread = is.read(buf);
    		    	count += numread;
    		    	// 进度条下面显示的当前下载文件大小
    		    	tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
    		    	// 当前进度值
    		    	progress = (int) (((float) count / length) * 100);
    		    	// 更新进度
    		    	mHandler.sendEmptyMessage(DOWN_UPDATE);
    		    	if (numread <= 0) {
    		    		// 下载完成 - 将临时下载文件转成APK文件
    		    		if (tmpFile.renameTo(ApkFile)) {
    		    			// 通知安装
    		    			mHandler.sendEmptyMessage(DOWN_OVER);
    		    		}
    		    		break;
    		    	}
    		    	fos.write(buf, 0, numread);
    			} while (!interceptFlag);// 点击取消就停止下载
    			fos.close();
    			is.close();
    	    } catch (MalformedURLException e) {
    	    	error_msg.what=DOWN_ERROR;
    	    	error_msg.arg1=1;
    	    	mHandler.sendMessage(error_msg);
    	    	e.printStackTrace();
    	    } catch (IOException e) {
    	    	error_msg.what=DOWN_ERROR;
    	    	error_msg.arg1=2;
    	    	mHandler.sendMessage(error_msg);
    	    	e.printStackTrace();
    	    }
    	}
    };
    
    /**
     * 下载apk
     * @param url
     */
    private void downloadApk() {
    	downLoadThread = new Thread(mdownApkRunnable);
    	downLoadThread.start();
    }
    
    /**
     * 安装apk
     * @param url
     */
    private void installApk() {
    	File apkfile = new File(apkFilePath);
    	if (!apkfile.exists()) {
    	    return;
    	}
    	Intent i = new Intent(Intent.ACTION_VIEW);
    	i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");
    	mContext.startActivity(i);
    }
}