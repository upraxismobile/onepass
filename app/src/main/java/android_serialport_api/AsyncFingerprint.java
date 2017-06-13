package android_serialport_api;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.fgtit.utils.DataUtils;
import com.fgtit.utils.ToastUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class AsyncFingerprint extends Handler {

	private static final int FP_GetImage = 0x01;
	private static final int FP_GenChar = 0x02;
	private static final int FP_Match = 0x03;
	private static final int FP_Search = 0x04;
	private static final int FP_RegModel = 0x05;
	private static final int FP_StoreChar = 0x06;
	private static final int FP_LoadChar = 0x07;
	private static final int FP_UpChar = 0x08;

	private static final int FP_DownChar = 0x09;
	private static final int FP_UpImage = 0x0a;
	private static final int FP_DownImage = 0x0b;
	private static final int FP_DeleteChar = 0x0c;
	private static final int FP_Empty = 0x0d;
	private static final int FP_Enroll = 0x10;
	private static final int FP_Identify = 0x11;
	private Handler mWorkerThreadHandler;
	
	private static final int  FP_GetImageEX = 0x30;
	private static final int  FP_UpImageEX = 0x31;
	private static final int  FP_GenCharEX = 0x32;
	
	/**
	 * ��Ӧ����ͼ�����ݹ�40044�ֽ�
	 */
	private static final int UP_IMAGE_RESPONSE_SIZE = 40044;
	
	private static final int UP_IMAGEEX_RESPONSE_SIZE = 16521;
	
	/**
	 * ��Ӧ��������ֵ���ݹ�568�ֽ�
	 */
	private static final int UP_CHAR_RESPONSE_SIZE = 568;

	/**
	 * ������:��λ���������ݴ�ŵ�
	 */
	private byte[] data = new byte[1024 * 50];

	private byte[] buffer = new byte[1024 * 50];

	private boolean bCancel=false;
	
	protected AsyncFingerprint(Looper looper) {
		createHandler(looper);
	}

	private Handler createHandler(Looper looper) {
		return mWorkerThreadHandler = new WorkerHandler(looper);
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case FP_GetImage:
			if (onGetImageListener == null) {
				return;
			}
			if (msg.arg1 == 0) {
				onGetImageListener.onGetImageSuccess();
			} else {
				onGetImageListener.onGetImageFail();
			}
			break;
		case FP_UpImage:
			if (onUpImageListener == null) {
				return;
			}
			if (msg.obj != null) {
				onUpImageListener.onUpImageSuccess((byte[]) msg.obj);
			} else {
				onUpImageListener.onUpImageFail();
			}
			break;
		case FP_GenChar:
			if (onGenCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onGenCharListener.onGenCharSuccess(msg.arg2);
				} else {
					onGenCharListener.onGenCharFail();
				}
			}
			break;
		case FP_RegModel:
			if (onRegModelListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					Log.i("whw", "onRegModelListener");
					onRegModelListener.onRegModelSuccess();
				} else {
					onRegModelListener.onRegModelFail();
				}
			}
			break;
		case FP_UpChar:
			if (onUpCharListener == null) {
				return;
			} else {
				if (msg.obj != null) {
					onUpCharListener.onUpCharSuccess((byte[]) msg.obj);
				} else {
					onUpCharListener.onUpCharFail();
				}
			}
			break;
		case FP_DownChar:
			if (onDownCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onDownCharListener.onDownCharSuccess();
				} else {
					onDownCharListener.onDownCharFail();
				}
			}
			break;
		case FP_Match:
			if (onMatchListener == null) {
				return;
			} else {
				if ((Boolean) msg.obj) {
					onMatchListener.onMatchSuccess();
				} else {
					onMatchListener.onMatchFail();
				}
			}
			break;
		case FP_StoreChar:
			if (onStoreCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onStoreCharListener.onStoreCharSuccess();
				} else {
					onStoreCharListener.onStoreCharFail();
				}
			}
			break;
		case FP_LoadChar:
			if (onLoadCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onLoadCharListener.onLoadCharSuccess();
				} else {
					onLoadCharListener.onLoadCharFail();
				}
			}
			break;
		case FP_Search:
			if (onSearchListener == null) {
				return;
			} else {
				byte[] result = (byte[]) msg.obj;
				if (result != null) {
					if (result[9] == 0x00) {
						short pageId = getShort(result[10], result[11]);
						short matchScore = getShort(result[12], result[13]);
						onSearchListener.onSearchSuccess(pageId, matchScore);
						return;
					}
				}
				onSearchListener.onSearchFail();
			}
			break;
		case FP_DeleteChar:
			if (onDeleteCharListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onDeleteCharListener.onDeleteCharSuccess();
				} else {
					onDeleteCharListener.onDeleteCharFail();
				}
			}
			break;
		case FP_Empty:
			if (onEmptyListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onEmptyListener.onEmptySuccess();
				} else {
					onEmptyListener.onEmptyFail();
				}
			}
			break;
		case FP_Enroll:
			if (onEnrollListener == null) {
				return;
			} else {
				byte[] result = (byte[]) msg.obj;
				if (result != null) {
					if (result[9] == 0x00) {
						short pageId = getShort(result[10], result[11]);
						onEnrollListener.onEnrollSuccess(pageId);
						return;
					}
				}
				onEnrollListener.onEnrollFail();
			}
			break;
		case FP_Identify:
			if (onIdentifyListener == null) {
				return;
			} else {
				byte[] result = (byte[]) msg.obj;
				if (result != null) {
					if (result[9] == 0x00) {
						short pageId = getShort(result[10], result[11]);
						short matchScore = getShort(result[12], result[13]);
						onIdentifyListener
								.onIdentifySuccess(pageId, matchScore);
						return;
					}
				}
				onIdentifyListener.onIdentifyFail();
			}
			break;
		case FP_GetImageEX:
			if (onGetImageExListener == null) {
				return;
			}
			if (msg.arg1 == 0) {
				onGetImageExListener.onGetImageExSuccess();
			} else {
				onGetImageExListener.onGetImageExFail();
			}
			break;
		case FP_UpImageEX:
			if (onUpImageExListener == null) {
				return;
			}
			if (msg.obj != null) {
				onUpImageExListener.onUpImageExSuccess((byte[]) msg.obj);
			} else {
				onUpImageExListener.onUpImageExFail();
			}
			break;
		case FP_GenCharEX:
			if (onGenCharExListener == null) {
				return;
			} else {
				if (msg.arg1 == 0) {
					onGenCharExListener.onGenCharExSuccess(msg.arg2);
				} else {
					onGenCharExListener.onGenCharExFail();
				}
			}
			break;
		default:
			break;
		}
	}

	protected class WorkerHandler extends Handler {
		public WorkerHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FP_GetImage:
				int valueGetImage = PSGetImage();
				AsyncFingerprint.this.obtainMessage(FP_GetImage, valueGetImage,
						-1).sendToTarget();
				break;
			case FP_UpImage:
				byte[] imageData = PSUpImage();
				AsyncFingerprint.this.obtainMessage(FP_UpImage, imageData)
						.sendToTarget();
				break;
			case FP_GenChar:
				int valueGenChar = PSGenChar(msg.arg1);
				AsyncFingerprint.this.obtainMessage(FP_GenChar, valueGenChar,
						msg.arg1).sendToTarget();
				break;
			case FP_RegModel:
				int valueRegModel = PSRegModel();
				AsyncFingerprint.this.obtainMessage(FP_RegModel, valueRegModel,
						-1).sendToTarget();
				break;
			case FP_UpChar:
				byte[] charData = PSUpChar();
				AsyncFingerprint.this.obtainMessage(FP_UpChar, charData)
						.sendToTarget();
				break;
			case FP_DownChar:
				int valueDownChar = PSDownChar((byte[]) msg.obj);
				AsyncFingerprint.this.obtainMessage(FP_DownChar, valueDownChar,
						-1).sendToTarget();
				break;
			case FP_Match:
				boolean valueMatch = PSMatch();
				AsyncFingerprint.this.obtainMessage(FP_Match,
						Boolean.valueOf(valueMatch)).sendToTarget();
				break;
			case FP_StoreChar:
				int valueStoreChar = PSStoreChar(msg.arg1, msg.arg2);
				AsyncFingerprint.this.obtainMessage(FP_StoreChar,
						valueStoreChar, -1).sendToTarget();
				break;
			case FP_LoadChar:
				int valueLoadChar = PSLoadChar(msg.arg1, msg.arg2);
				AsyncFingerprint.this.obtainMessage(FP_LoadChar, valueLoadChar,
						-1).sendToTarget();
				break;
			case FP_Search:
				byte[] result = PSSearch(msg.arg1, msg.arg2, (Integer) msg.obj);
				AsyncFingerprint.this.obtainMessage(FP_Search, result)
						.sendToTarget();
				break;
			case FP_DeleteChar:
				int valueDeleteChar = PSDeleteChar((short) msg.arg1,
						(short) msg.arg2);
				AsyncFingerprint.this.obtainMessage(FP_DeleteChar,
						valueDeleteChar, -1).sendToTarget();
				break;
			case FP_Empty:
				int valueEmpty = PSEmpty();
				AsyncFingerprint.this.obtainMessage(FP_Empty, valueEmpty, -1)
						.sendToTarget();
				break;
			case FP_Enroll:
				byte[] valueEnroll = PSEnroll();
				AsyncFingerprint.this.obtainMessage(FP_Enroll, valueEnroll)
						.sendToTarget();
				break;
			case FP_Identify:
				byte[] valueIdentify = PSIdentify();
				AsyncFingerprint.this.obtainMessage(FP_Identify, valueIdentify)
						.sendToTarget();
				break;
			case FP_GetImageEX:
				int valueGetImageEx = PSGetImageEx();
				AsyncFingerprint.this.obtainMessage(FP_GetImageEX, valueGetImageEx,
						-1).sendToTarget();
				break;
			case FP_UpImageEX:
				byte[] imageDataEx = PSUpImageEx();
				AsyncFingerprint.this.obtainMessage(FP_UpImageEX, imageDataEx)
						.sendToTarget();
				break;
			case FP_GenCharEX:
				int valueGenCharEx = PSGenCharEx(msg.arg1);
				AsyncFingerprint.this.obtainMessage(FP_GenCharEX, valueGenCharEx,
						msg.arg1).sendToTarget();
				break;
			default:
				break;
			}
		}
	}

	private OnGetImageListener onGetImageListener;

	private OnUpImageListener onUpImageListener;

	private OnGenCharListener onGenCharListener;

	private OnRegModelListener onRegModelListener;

	private OnUpCharListener onUpCharListener;

	private OnDownCharListener onDownCharListener;

	private OnMatchListener onMatchListener;

	private OnStoreCharListener onStoreCharListener;

	private OnLoadCharListener onLoadCharListener;

	private OnSearchListener onSearchListener;

	private OnDeleteCharListener onDeleteCharListener;

	private OnEmptyListener onEmptyListener;

	private OnEnrollListener onEnrollListener;

	private OnIdentifyListener onIdentifyListener;
	
	private OnGetImageExListener onGetImageExListener;
	private OnUpImageExListener onUpImageExListener;
	private OnGenCharExListener onGenCharExListener;
	
	public void setOnGetImageListener(OnGetImageListener onGetImageListener) {
		this.onGetImageListener = onGetImageListener;
	}

	public void setOnUpImageListener(OnUpImageListener onUpImageListener) {
		this.onUpImageListener = onUpImageListener;
	}

	public void setOnGenCharListener(OnGenCharListener onGenCharListener) {
		this.onGenCharListener = onGenCharListener;
	}

	public void setOnRegModelListener(OnRegModelListener onRegModelListener) {
		this.onRegModelListener = onRegModelListener;
	}

	public void setOnUpCharListener(OnUpCharListener onUpCharListener) {
		this.onUpCharListener = onUpCharListener;
	}

	public void setOnDownCharListener(OnDownCharListener onDownCharListener) {
		this.onDownCharListener = onDownCharListener;
	}

	public void setOnMatchListener(OnMatchListener onMatchListener) {
		this.onMatchListener = onMatchListener;
	}

	public void setOnStoreCharListener(OnStoreCharListener onStoreCharListener) {
		this.onStoreCharListener = onStoreCharListener;
	}

	public void setOnLoadCharListener(OnLoadCharListener onLoadCharListener) {
		this.onLoadCharListener = onLoadCharListener;
	}

	public void setOnSearchListener(OnSearchListener onSearchListener) {
		this.onSearchListener = onSearchListener;
	}

	public void setOnDeleteCharListener(
			OnDeleteCharListener onDeleteCharListener) {
		this.onDeleteCharListener = onDeleteCharListener;
	}

	public void setOnEmptyListener(OnEmptyListener onEmptyListener) {
		this.onEmptyListener = onEmptyListener;
	}

	public void setOnEnrollListener(OnEnrollListener onEnrollListener) {
		this.onEnrollListener = onEnrollListener;
	}

	public void setOnIdentifyListener(OnIdentifyListener onIdentifyListener) {
		this.onIdentifyListener = onIdentifyListener;
	}

	public void setOnGetImageExListener(OnGetImageExListener onGetImageExListener) {
		this.onGetImageExListener = onGetImageExListener;
	}

	public void setOnUpImageExListener(OnUpImageExListener onUpImageExListener) {
		this.onUpImageExListener = onUpImageExListener;
	}

	public void setOnGenCharExListener(OnGenCharExListener onGenCharExListener) {
		this.onGenCharExListener = onGenCharExListener;
	}

	
	public interface OnGetImageListener {
		void onGetImageSuccess();

		void onGetImageFail();
	}

	public interface OnUpImageListener {
		void onUpImageSuccess(byte[] data);

		void onUpImageFail();
	}

	public interface OnGenCharListener {
		void onGenCharSuccess(int bufferId);

		void onGenCharFail();
	}

	public interface OnRegModelListener {
		void onRegModelSuccess();

		void onRegModelFail();
	}

	public interface OnUpCharListener {
		void onUpCharSuccess(byte[] model);

		void onUpCharFail();
	}

	public interface OnDownCharListener {
		void onDownCharSuccess();

		void onDownCharFail();
	}

	public interface OnMatchListener {
		void onMatchSuccess();

		void onMatchFail();
	}

	public interface OnStoreCharListener {
		void onStoreCharSuccess();

		void onStoreCharFail();
	}

	public interface OnLoadCharListener {
		void onLoadCharSuccess();

		void onLoadCharFail();
	}

	public interface OnSearchListener {
		void onSearchSuccess(int pageId, int matchScore);

		void onSearchFail();
	}

	public interface OnDeleteCharListener {
		void onDeleteCharSuccess();

		void onDeleteCharFail();
	}

	public interface OnEmptyListener {
		void onEmptySuccess();

		void onEmptyFail();
	}

	public interface OnEnrollListener {
		void onEnrollSuccess(int pageId);

		void onEnrollFail();
	}

	public interface OnIdentifyListener {
		void onIdentifySuccess(int pageId, int matchScore);

		void onIdentifyFail();
	}

	public interface OnGetImageExListener {
		void onGetImageExSuccess();

		void onGetImageExFail();
	}

	public interface OnUpImageExListener {
		void onUpImageExSuccess(byte[] data);

		void onUpImageExFail();
	}

	public interface OnGenCharExListener {
		void onGenCharExSuccess(int bufferId);

		void onGenCharExFail();
	}
	
	public void FP_GetImage() {
		SystemClock.sleep(100);
		mWorkerThreadHandler.sendEmptyMessage(FP_GetImage);
	}

	public void FP_UpImage() {
		SystemClock.sleep(50);
		mWorkerThreadHandler.sendEmptyMessage(FP_UpImage);
	}

	public void FP_GenChar(int bufferId) {
		SystemClock.sleep(50);
		mWorkerThreadHandler.obtainMessage(FP_GenChar, bufferId, -1)
				.sendToTarget();
	}

	public void FP_RegModel() {
		SystemClock.sleep(50);
		mWorkerThreadHandler.sendEmptyMessage(FP_RegModel);
	}

	public void FP_UpChar() {
		SystemClock.sleep(50);
		mWorkerThreadHandler.sendEmptyMessage(FP_UpChar);
	}

	public void FP_DownChar(byte[] model) {
		mWorkerThreadHandler.obtainMessage(FP_DownChar, model).sendToTarget();
	}

	public void FP_Match() {
		mWorkerThreadHandler.sendEmptyMessage(FP_Match);
	}

	public void FP_StoreChar(int bufferId, int pageId) {
		mWorkerThreadHandler.obtainMessage(FP_StoreChar, bufferId, pageId)
				.sendToTarget();
	}

	public void FP_LoadChar(int bufferId, int pageId) {
		mWorkerThreadHandler.obtainMessage(FP_LoadChar, bufferId, pageId)
				.sendToTarget();
	}

	public void FP_Search(int bufferId, int startPageId, int pageNum) {
		mWorkerThreadHandler.obtainMessage(FP_Search, bufferId, startPageId,
				pageNum).sendToTarget();
	}

	public void FP_DeleteChar(int pageIDStart, int delNum) {
		mWorkerThreadHandler.obtainMessage(FP_DeleteChar, pageIDStart, delNum)
				.sendToTarget();
	}

	public void FP_Empty() {
		mWorkerThreadHandler.sendEmptyMessage(FP_Empty);
	}

	public void FP_Enroll() {
		mWorkerThreadHandler.sendEmptyMessage(FP_Enroll);
	}

	public void FP_Identify() {
		mWorkerThreadHandler.sendEmptyMessage(FP_Identify);
	}

	public void FP_GetImageEx() {
		mWorkerThreadHandler.sendEmptyMessage(FP_GetImageEX);
	}

	public void FP_UpImageEx() {
		mWorkerThreadHandler.sendEmptyMessage(FP_UpImageEX);
	}

	public void FP_GenCharEx(int bufferId) {
		mWorkerThreadHandler.obtainMessage(FP_GenCharEX, bufferId, -1)
				.sendToTarget();
	}
	
	/**
	 * ¼��ָ��ͼ��
	 * 
	 * @return ����ֵΪȷ���� ȷ����=00H��ʾ¼��ɹ� ȷ����=01H��ʾ�հ��д� ȷ����=02H��ʾ������������ָ
	 *         ȷ����=03H��ʾ¼�벻�ɹ� ȷ����=-1 ��ʾʧ��
	 */
	private synchronized int PSGetImage() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x03, 0x01, 0x00, 0x05 };
		sendCommand(command);		
		int length = SerialPortManager.getInstance().read(buffer,12,300);
		printlog("PSGetImage", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}

	/**
	 * ��ImageBuffer�е�ָ��ͼ�����������ļ�����charBuffer1��CharBuffer2��
	 * 
	 * @param bufferId
	 *            ��CharBuffer1:1h,CharBuffer2:2h��
	 * @return ����ֵΪȷ���� ȷ����=00H��ʾ���������ɹ� ȷ����=01H��ʾ�հ��д� ȷ����=06H��ʾָ��ͼ��̫�Ҷ�������ͼ��
	 *         ȷ����=07H��ʾָ��ͼ����������������̫�ٶ����������� ȷ����=15H��ʾͼ�񻺳�����û����Чԭʼͼ��������ͼ�� ȷ����=-1
	 *         ��ʾʧ��
	 */
	private synchronized byte PSGenChar(int bufferId) {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 01, (byte) 0x00, (byte) 0x04,
				(byte) 0x02, (byte) bufferId, (byte) 0x00,
				(byte) (0x7 + bufferId) };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer,12,500);
		printlog("PSGenChar", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}

	/**
	 * �ϲ���������ģ�壬��CharBuffer1��CharBuffer2�е������ļ��ϲ�����ģ�壬�������CharBuffer1��CharBuffer2
	 * �С�
	 * 
	 * @return ����ֵΪȷ���� ȷ����=00H��ʾ�ϲ��ɹ� ȷ����=01H��ʾ�հ��д� ȷ����=0aH��ʾ�ϲ�ʧ�ܣ���öָ�Ʋ�����ͬһ��ָ��
	 *         ȷ����=-1 ��ʾʧ��
	 */
	private synchronized byte PSRegModel() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x03, 0x05, 0x00, 0x09 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 12,100);
		printlog("PSRegModel", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}

	/**
	 * ��CharBuffer�е�ģ�崢�浽ָ����pageId�ŵ�flash���ݿ�λ�� bufferId:ֻ��Ϊ1h��2h
	 * pageId����ΧΪ0~1010
	 * 
	 * @return ����ֵΪȷ���� ȷ����=00H��ʾ����ɹ� ȷ����=01H��ʾ�հ��д� ȷ����=0bH��ʾPageID����ָ�ƿⷶΧ
	 *         ȷ����=18H��ʾдFLASH���� ȷ����=-1 ��ʾʧ��
	 */
	private synchronized byte PSStoreChar(int bufferId, int pageId) {
		byte[] pageIDArray = short2byte((short) pageId);
		// Log.i("whw", "pageid hex=" + DataUtils.toHexString(pageIDArray));
		int checkSum = 0x01 + 0x00 + 0x06 + 0x06 + bufferId
				+ (pageIDArray[0] & 0xff) + (pageIDArray[1] & 0xff);
		byte[] checkSumArray = short2byte((short) checkSum);
		// Log.i("whw",
		// "checkSumArray hex=" + DataUtils.toHexString(checkSumArray)
		// + "    checkSum=" + checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x06, (byte) 0x06, (byte) bufferId,
				(byte) pageIDArray[0], (byte) pageIDArray[1],
				(byte) checkSumArray[0], (byte) checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer,12,100);
		printlog("PSStoreChar", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}

	/**
	 * ��flash ���ݿ���ָ��pageId�ŵ�ָ��ģ����뵽ģ�建����CharBuffer1��CharBuffer2
	 * bufferId:ֻ��Ϊ1h��2h pageId����ΧΪ0~1023
	 * 
	 * @param index
	 *            pageId��
	 * @return ����ֵΪȷ���� ȷ����=00H��ʾ�����ɹ� ȷ����=01H��ʾ�հ��д� ȷ����=0cH��ʾ�����д��ģ���д�
	 *         ȷ����=0BH��ʾPageID����ָ�ƿⷶΧ ȷ����=-1 ��ʾʧ��
	 */
	private synchronized byte PSLoadChar(int bufferId, int pageId) {
		byte[] pageIDArray = short2byte((short) pageId);
		int checkSum = 0x01 + 0x00 + 0x06 + 0x07 + bufferId
				+ (pageIDArray[0] & 0xff) + (pageIDArray[1] & 0xff);
		byte[] checkSumArray = short2byte((short) checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x06, (byte) 0x07, (byte) bufferId,
				(byte) pageIDArray[0], (byte) pageIDArray[1],
				(byte) checkSumArray[0], (byte) checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer,12, 100);
		printlog("PSLoadChar", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}

	private synchronized byte[] PSSearch(int bufferId, int startPageId,
			int pageNum) {
		byte[] startPageIDArray = short2byte((short) startPageId);
		byte[] pageNumArray = short2byte((short) pageNum);
		int checkSum = 0x01 + 0x00 + 0x08 + 0x04 + bufferId
				+ (startPageIDArray[0] & 0xff) + (startPageIDArray[1] & 0xff)
				+ (pageNumArray[0] & 0xff) + (pageNumArray[1] & 0xff);
		byte[] checkSumArray = short2byte((short) checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x08, (byte) 0x04, (byte) bufferId,
				(byte) startPageIDArray[0], (byte) startPageIDArray[1],
				(byte) pageNumArray[0], (byte) pageNumArray[1],
				(byte) checkSumArray[0], (byte) checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 16,100);
		printlog("PSSearch", length);
		if (length == 16) {
			byte[] result = new byte[16];
			System.arraycopy(buffer, 0, result, 0, length);
			return result;
		}
		return null;
	}

	/**
	 * ��ȷ�ȶ�CharBuffer1��CharBuffer2�е������ļ� ע���:��λ�����ص��������滹��һ���÷֣����÷ִ��ڵ���50ʱ��ָ��ƥ��
	 * 
	 * @return true��ָ��ƥ��ɹ� false���ȶ�ʧ��
	 */
	private synchronized boolean PSMatch() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x03, (byte) 0x00, (byte) 0x07 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 14,100);
		printlog("PSMatch", length);
		if (length == 14) {
			if (buffer[9] == 0x00) {
				return score(buffer[10], buffer[11]);
			}
		}
		return false;
	}

	private synchronized byte[] PSEnroll() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x10, (byte) 0x00, (byte) 0x14 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 14,600);
		printlog("PSEnroll", length);
		if (length == 14) {
			byte[] result = new byte[length];
			System.arraycopy(buffer, 0, result, 0, length);
			return result;
		}
		return null;
	}

	/**
	 * 
	 * @return -1:�������� -2��û�������� >=0:��������ҳ�� 0-1023
	 */
	private synchronized byte[] PSIdentify() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x11, (byte) 0x00, (byte) 0x15 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 16,600);
		printlog("PSIdentify", length);
		if (length == 16) {
			byte[] result = new byte[length];
			System.arraycopy(buffer, 0, result, 0, length);
			return result;
		}
		return null;
	}

	/**
	 * ɾ��ģ��
	 * 
	 * @param pageIDStart
	 * @param delNum
	 * @return
	 */
	private synchronized byte PSDeleteChar(short pageIDStart, short delNum) {
		byte[] pageIDArray = short2byte(pageIDStart);
		byte[] delNumArray = short2byte(delNum);
		int checkSum = 0x01 + 0x07 + 0x0c + (pageIDArray[0] & 0xff)
				+ (pageIDArray[1] & 0xff) + (delNumArray[0] & 0xff)
				+ (delNumArray[1] & 0xff);
		byte[] checkSumArray = short2byte((short) checkSum);
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x07, (byte) 0x0c, pageIDArray[0], pageIDArray[1],
				delNumArray[0], delNumArray[1], checkSumArray[0],
				checkSumArray[1] };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 12,100);
		printlog("PSDeleteChar", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}

	private synchronized byte PSEmpty() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x0d, (byte) 0x00, (byte) 0x11 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer,12, 100);
		printlog("PSEmpty", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}

	/**
	 * �������������е������ļ��ϴ�����λ��(Ĭ�ϵ�����������Ϊcharbuffer1)
	 * 
	 * @return byte[]������Ϊ512�ֽڳɹ� ����ʧ�� null:�ϴ������ļ�ʧ��
	 */
	private synchronized byte[] PSUpChar() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x04, (byte) 0x08, (byte) 0x01, (byte) 0x00, (byte) 0x0e };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, UP_CHAR_RESPONSE_SIZE,100);
		printlog("PSUpChar", 12);
		// ��ӦΪ12�ֽڣ���4�����ݰ���ÿ����Ϊ139�ֽڣ����Է��ص����ֽ���Ϊ568�ֽ�
		if (length >= UP_CHAR_RESPONSE_SIZE) {
			index = 12;// ���ݰ�����ʼ�±�
			packetNum = 0;
			byte[] packets = new byte[UP_CHAR_RESPONSE_SIZE];
			System.arraycopy(buffer, 0, packets, 0, UP_CHAR_RESPONSE_SIZE);
			return parsePacketDataEx(packets);			
		}
		return null;

	}

	/**
	 * ��λ�����������ļ���ģ�������������(Ĭ�ϵĻ�����ΪCharBuffer2)
	 * 
	 * @param model
	 *            :ָ�Ƶ������ļ�
	 * @return ����ֵΪȷ���� ȷ����=00H��ʾ���سɹ� ȷ����=-1 ��ʾʧ��
	 */
	private synchronized byte PSDownChar(byte[] model) {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x04, (byte) 0x09, (byte) 0x02, (byte) 0x00, (byte) 0x10 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer,12, 100);
		printlog("PSDownChar", length);
		if (length == 12 && buffer[9] == 0x00) {
			sendData(model);
			return 0x00;
		}
		return -1;
	}

	/**
	 * ��ͼ�񻺳����������ϴ�����λ��
	 * 
	 * @return byte[]��length��СΪ36k ��һ���ֽں����������أ�ÿ������ռ4bits null:�ϴ�ʧ��
	 */
	private synchronized byte[] PSUpImage() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x0a, (byte) 0x00, (byte) 0x0e };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, UP_IMAGE_RESPONSE_SIZE,100);
		Log.i("whw", "PSUpImage length=" + length);
		if (length >= UP_IMAGE_RESPONSE_SIZE) {
			byte[] packets = new byte[length];
			System.arraycopy(buffer, 0, packets, 0, length);
			index = 12;
			packetNum = 0;
			byte[] data = parsePacketData(packets);
			return getFingerprintImage(data);
		}
		return null;

	}
	
	
	/**
	 * ¼��ָ��ͼ��
	 * 
	 * @return ����ֵΪȷ���� ȷ����=00H��ʾ¼��ɹ� ȷ����=01H��ʾ�հ��д� ȷ����=02H��ʾ������������ָ
	 *         ȷ����=03H��ʾ¼�벻�ɹ� ȷ����=-1 ��ʾʧ��
	 */
	private synchronized int PSGetImageEx() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x03, 0x30, 0x00, 0x34 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, 12,250);
		printlog("PSGetImageEx", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}

	/**
	 * ��ImageBuffer�е�ָ��ͼ�����������ļ�����charBuffer1��CharBuffer2��
	 * 
	 * @param bufferId
	 *            ��CharBuffer1:1h,CharBuffer2:2h��
	 * @return ����ֵΪȷ���� ȷ����=00H��ʾ���������ɹ� ȷ����=01H��ʾ�հ��д� ȷ����=06H��ʾָ��ͼ��̫�Ҷ�������ͼ��
	 *         ȷ����=07H��ʾָ��ͼ����������������̫�ٶ����������� ȷ����=15H��ʾͼ�񻺳�����û����Чԭʼͼ��������ͼ�� ȷ����=-1
	 *         ��ʾʧ��
	 */
	private synchronized byte PSGenCharEx(int bufferId) {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 01, (byte) 0x00, (byte) 0x04,
				(byte) 0x32, (byte) bufferId, (byte) 0x00,
				(byte) (0x37 + bufferId) };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer,12, 500);
		printlog("PSGenCharEx", length);
		if (length == 12) {
			return buffer[9];
		}
		return -1;
	}


	/**
	 * ��ͼ�񻺳����������ϴ�����λ��
	 * 
	 * @return byte[]��length��СΪ36k ��һ���ֽں����������أ�ÿ������ռ4bits null:�ϴ�ʧ��
	 */
	private synchronized byte[] PSUpImageEx() {
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0x01, (byte) 0x00,
				(byte) 0x03, (byte) 0x31, (byte) 0x00, (byte) 0x35 };
		sendCommand(command);
		int length = SerialPortManager.getInstance().read(buffer, UP_IMAGEEX_RESPONSE_SIZE,100);
		Log.i("whw", "PSUpImageEx length=" + length);
		if (length >= UP_IMAGEEX_RESPONSE_SIZE) {
			byte[] packets = new byte[length];
			System.arraycopy(buffer, 0, packets, 0, length);
			index = 12;
			packetNum = 0;
			byte[] data = parsePacketData(packets);
			return getFingerprintImageEx(data);
		}
		return null;

	}
	
	

	/**
	 * ����ָ��ģ�����ݰ�512�ֽ�,��Ϊ4�η��ͣ�3�����ݰ���һ�ν�����
	 * 
	 * @param data
	 */
	private void sendData(byte[] data) {
		// ���ݰ�ָ��ͷ
		byte[] dataPrefix = { (byte) 0xef, (byte) 0x01, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x02,
				(byte) 0x00, (byte) 0x82 };
		// ������ָ��ͷ
		byte[] endPrefix = { (byte) 0xef, (byte) 0x01, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x08,
				(byte) 0x00, (byte) 0x82 };
		byte[] command = new byte[dataPrefix.length + 128 + 2];
		for (int i = 0; i < 4; i++) {
			if (i == 3) {
				System.arraycopy(endPrefix, 0, command, 0, endPrefix.length);
			} else {
				System.arraycopy(dataPrefix, 0, command, 0, dataPrefix.length);
			}
			System.arraycopy(data, i * 128, command, dataPrefix.length, 128);
			short sum = 0;
			for (int j = 6; j < command.length - 2; j++) {
				sum += (command[j] & 0xff);
			}
			byte[] size = short2byte(sum);
			command[command.length - 2] = size[0];
			command[command.length - 1] = size[1];
			sendCommand(command);
			SystemClock.sleep(20);
		}
	}

	private int index;// ���ݰ�����ʼ�±�
	private int packetNum;// ���ݰ��ĸ���

	private byte[] parsePacketData(byte[] packet) {
		int dstPos = 0;
		int packageLength = 0;
		int size = 0;
		do {
			packageLength = getShort(packet[index + 7], packet[index + 8]);			
			System.arraycopy(packet, index + 9, data, dstPos, packageLength - 2);
			dstPos += packageLength - 2;// 2��У���
			packetNum++;
			size += packageLength - 2;
			Log.i("xpb", "**************size=" + size);
			if(bCancel)
				return null;
		} while (moveToNext(index + 6, packageLength, packet));
		if (size != 0) {
			byte[] dataPackage = new byte[size];
			Log.i("xpb", "**************packetNum=" + packetNum);
			System.arraycopy(data, 0, dataPackage, 0, size);
			return dataPackage;
		}
		return null;
	}
	
	private byte[] parsePacketDataEx(byte[] packet) {
		int dstPos = 0;
		int packageLength = 0;
		int size = 0;
		do {
			packageLength = getShort(packet[index + 7], packet[index + 8]);			
			System.arraycopy(packet, index + 9, data, dstPos, packageLength - 2);
			dstPos += packageLength - 2;// 2��У���
			packetNum++;
			size += packageLength - 2;
			Log.i("xpb", "**************size=" + size);
			if(size==512)
				break;
			if(bCancel)
				return null;
		} while (moveToNext(index + 6, packageLength, packet));
		if (size != 0) {
			byte[] dataPackage = new byte[size];
			Log.i("xpb", "**************packetNum=" + packetNum);
			System.arraycopy(data, 0, dataPackage, 0, size);
			return dataPackage;
		}
		return null;
	}

	private boolean moveToNext(int position, int packageLength, byte[] packet) {
		if (packet[position] == 0x02) {
			index += packageLength + 9;
			return true;
		}
		return false;
	}

	public byte[] getFingerprintImage(byte[] data) {
		if (data == null) {
			return null;
		}
		byte[] imageData = new byte[data.length * 2];
		// Log.i("whw", "*****************data.length="+data.length);
		for (int i = 0; i < data.length; i++) {
			imageData[i * 2] = (byte) (data[i] & 0xf0);
			imageData[i * 2 + 1] = (byte) (data[i] << 4 & 0xf0);
		}

		byte[] bmpData = toBmpByte(256, packetNum, imageData);
		return bmpData;
	}
	
	public byte[] getFingerprintImageEx(byte[] data) {
		if (data == null) {
			return null;
		}
		byte[] imageData = new byte[data.length * 2];
		// Log.i("whw", "*****************data.length="+data.length);
		for (int i = 0; i < data.length; i++) {
			imageData[i * 2] = (byte) (data[i] & 0xf0);
			imageData[i * 2 + 1] = (byte) (data[i] << 4 & 0xf0);
		}

		byte[] bmpData = toBmpByte(152,200, imageData);
		return bmpData;
	}

	/**
	 * �����ݴ����ڴ�
	 */
	private byte[] toBmpByte(int width, int height, byte[] data) {
		byte[] buffer = null;
		try {
			// // ����������ļ�����
			// java.io.FileOutputStream fos = new
			// java.io.FileOutputStream(path);
			// // ����ԭʼ�������������
			// java.io.DataOutputStream dos = new java.io.DataOutputStream(fos);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			// ���ļ�ͷ�ı�����ֵ
			int bfType = 0x424d; // λͼ�ļ����ͣ�0��1�ֽڣ�
			int bfSize = 54 + 1024 + width * height;// bmp�ļ��Ĵ�С��2��5�ֽڣ�
			int bfReserved1 = 0;// λͼ�ļ������֣�����Ϊ0��6-7�ֽڣ�
			int bfReserved2 = 0;// λͼ�ļ������֣�����Ϊ0��8-9�ֽڣ�
			int bfOffBits = 54 + 1024;// �ļ�ͷ��ʼ��λͼʵ������֮����ֽڵ�ƫ������10-13�ֽڣ�

			// �������ݵ�ʱ��Ҫע��������������ڴ���Ҫռ�����ֽڣ�
			// Ȼ����ѡ����Ӧ��д�뷽�������������Լ��������������
			// �����ļ�ͷ����
			dos.writeShort(bfType); // ����λͼ�ļ�����'BM'
			dos.write(changeByte(bfSize), 0, 4); // ����λͼ�ļ���С
			dos.write(changeByte(bfReserved1), 0, 2);// ����λͼ�ļ�������
			dos.write(changeByte(bfReserved2), 0, 2);// ����λͼ�ļ�������
			dos.write(changeByte(bfOffBits), 0, 4);// ����λͼ�ļ�ƫ����

			// ����Ϣͷ�ı�����ֵ
			int biSize = 40;// ��Ϣͷ������ֽ�����14-17�ֽڣ�
			int biWidth = width;// λͼ�Ŀ�18-21�ֽڣ�
			int biHeight = -height;// λͼ�ĸߣ�22-25�ֽڣ�
			int biPlanes = 1; // Ŀ���豸�ļ��𣬱�����1��26-27�ֽڣ�
			int biBitcount = 8;// ÿ�����������λ����28-29�ֽڣ���������1λ��˫ɫ����4λ��16ɫ����8λ��256ɫ������24λ�����ɫ��֮һ��
			int biCompression = 0;// λͼѹ�����ͣ�������0����ѹ������30-33�ֽڣ���1��BI_RLEBѹ�����ͣ���2��BI_RLE4ѹ�����ͣ�֮һ��
			int biSizeImage = width * height;// ʵ��λͼͼ��Ĵ�С��������ʵ�ʻ��Ƶ�ͼ���С��34-37�ֽڣ�
			int biXPelsPerMeter = 0;// λͼˮƽ�ֱ��ʣ�ÿ����������38-41�ֽڣ��������ϵͳĬ��ֵ
			int biYPelsPerMeter = 0;// λͼ��ֱ�ֱ��ʣ�ÿ����������42-45�ֽڣ��������ϵͳĬ��ֵ
			int biClrUsed = 256;// λͼʵ��ʹ�õ���ɫ���е���ɫ����46-49�ֽڣ������Ϊ0�Ļ���˵��ȫ��ʹ����
			int biClrImportant = 0;// λͼ��ʾ��������Ҫ����ɫ��(50-53�ֽ�)�����Ϊ0�Ļ���˵��ȫ����Ҫ

			// ��Ϊjava�Ǵ�˴洢����ôҲ����˵ͬ�����������
			// ��������ǰ�С�˶�ȡ��������ǲ��ı���ֽ����ݵ�˳��Ļ�����ô�����Ͳ���������ȡ��
			// �������ȵ��÷�����int����ת��Ϊ���byte���ݣ����Ұ�С�˴洢��˳��

			// ������Ϣͷ����
			dos.write(changeByte(biSize), 0, 4);// ������Ϣͷ���ݵ����ֽ���
			dos.write(changeByte(biWidth), 0, 4);// ����λͼ�Ŀ�
			dos.write(changeByte(biHeight), 0, 4);// ����λͼ�ĸ�
			dos.write(changeByte(biPlanes), 0, 2);// ����λͼ��Ŀ���豸����
			dos.write(changeByte(biBitcount), 0, 2);// ����ÿ������ռ�ݵ��ֽ���
			dos.write(changeByte(biCompression), 0, 4);// ����λͼ��ѹ������
			dos.write(changeByte(biSizeImage), 0, 4);// ����λͼ��ʵ�ʴ�С
			dos.write(changeByte(biXPelsPerMeter), 0, 4);// ����λͼ��ˮƽ�ֱ���
			dos.write(changeByte(biYPelsPerMeter), 0, 4);// ����λͼ�Ĵ�ֱ�ֱ���
			dos.write(changeByte(biClrUsed), 0, 4);// ����λͼʹ�õ�����ɫ��
			dos.write(changeByte(biClrImportant), 0, 4);// ����λͼʹ�ù�������Ҫ����ɫ��

			// �����ɫ������
			byte[] palatte = new byte[1024];
			for (int i = 0; i < 256; i++) {
				palatte[i * 4] = (byte) i;
				palatte[i * 4 + 1] = (byte) i;
				palatte[i * 4 + 2] = (byte) i;
				palatte[i * 4 + 3] = 0;
			}
			dos.write(palatte);

			dos.write(data);
			// �ر����ݵĴ���
			dos.flush();
			buffer = baos.toByteArray();
			dos.close();
			// fos.close();
			baos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * ��һ��int����תΪ��С��˳�����е��ֽ�����
	 * 
	 * @param data
	 *            int����
	 * @return ��С��˳�����е��ֽ�����
	 */
	private byte[] changeByte(int data) {
		byte b4 = (byte) ((data) >> 24);
		byte b3 = (byte) (((data) << 8) >> 24);
		byte b2 = (byte) (((data) << 16) >> 24);
		byte b1 = (byte) (((data) << 24) >> 24);
		byte[] bytes = { b1, b2, b3, b4 };
		return bytes;
	}

	private short getShort(byte b1, byte b2) {
		short temp = 0;
		temp |= (b1 & 0xff);
		temp <<= 8;
		temp |= (b2 & 0xff);
		return temp;
	}

	private byte[] short2byte(short s) {
		byte[] size = new byte[2];
		size[1] = (byte) (s & 0xff);
		size[0] = (byte) ((s >> 8) & 0xff);
		return size;
	}

	/**
	 * ָ�Ƶ÷ֱȶ�,>=50�ַ���true���ȶԳɹ�
	 * 
	 * @return
	 */
	private boolean score(byte b1, byte b2) {
		byte[] temp = { b1, b2 };
		short score = 0;
		score |= (temp[0] & 0xff);
		score <<= 8;
		score |= (temp[1] & 0xff);
		Log.i("whw", "---------------score="+score);
		return score >= 50;
	}

	private void sendCommand(byte[] command) {
//		Log.i("whw", "sendCommand hex=" + DataUtils.toHexString(command));
		try {
			SerialPortManager.getInstance().write(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printlog(String tag, int length) {
		byte[] temp = new byte[length];
		System.arraycopy(buffer, 0, temp, 0, length);
		Log.i("xpb", tag + "=" + DataUtils.toHexString(temp));
	}
	
	public void sendTest(){
		byte[] command = { (byte) 0xef, (byte) 0x01, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff, 0x01, 0x00, 0x03, 0x01, 0x00, 0x05 };
		sendCommand(command);		
	}
	
	public void Cancel(boolean sw) {
		bCancel=sw;
	}
}
