package android.scanner.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.scanner.camera.CameraManager;
import android.util.AttributeSet;
import android.view.View;
import com.fgtit.onepass.R;

import com.google.zxing.ResultPoint;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * <br/>
 * <br/>
 * ����ͼ�Ǹ����������Ԥ����ͼ֮�ϵ�һ����ͼ��ɨ��������ԭ����ʵ����Ԥ����ͼ�ϻ��Ŀ����ֲ㣬
 * �м����µĲ��ֱ���͸����������һ�������ߣ�ʵ���ϸ���������չʾ���ѣ���ɨ�蹦��û���κι�ϵ��
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	/**
	 * ˢ�½����ʱ��
	 */
	private static final long ANIMATION_DELAY = 10L;
	private static final int OPAQUE = 0xFF;

	private int CORNER_PADDING;

	/**
	 * ɨ����е��м��ߵĿ��
	 */
	private static int MIDDLE_LINE_WIDTH;

	/**
	 * ɨ����е��м��ߵ���ɨ������ҵļ�϶
	 */
	private static int MIDDLE_LINE_PADDING;

	/**
	 * �м�������ÿ��ˢ���ƶ��ľ���
	 */
	private static final int SPEEN_DISTANCE = 10;

	/**
	 * ���ʶ��������
	 */
	private Paint paint;

	/**
	 * �м们���ߵ����λ��
	 */
	private int slideTop;

	/**
	 * �м们���ߵ���׶�λ��
	 */
	private int slideBottom;

	private static final int MAX_RESULT_POINTS = 20;

	private Bitmap resultBitmap;

	/**
	 * ���ڲ����ɫ
	 */
	private final int maskColor;
	private final int resultColor;

	private final int resultPointColor;
	private List<ResultPoint> possibleResultPoints;

	private List<ResultPoint> lastPossibleResultPoints;

	/**
	 * ��һ�λ��ƿؼ�
	 */
	boolean isFirst = true;

	private CameraManager cameraManager;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		//CORNER_PADDING = dip2px(context, 0.0F);
		CORNER_PADDING = dip2px(context, 0.0F)+40;
		//MIDDLE_LINE_PADDING = dip2px(context, 20.0F);
		MIDDLE_LINE_PADDING = dip2px(context, 40.0F);
		MIDDLE_LINE_WIDTH = dip2px(context, 3.0F);
		

		paint = new Paint(Paint.ANTI_ALIAS_FLAG); // ���������

		Resources resources = getResources();
		maskColor = resources.getColor(R.color.viewfinder_mask); // ���ڲ���ɫ
		resultColor = resources.getColor(R.color.result_view);

		resultPointColor = resources.getColor(R.color.possible_result_points);
		possibleResultPoints = new ArrayList<ResultPoint>(5);
		lastPossibleResultPoints = null;

	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}
		Rect frame = cameraManager.getFramingRect();
		if (frame == null) {
			return;
		}

		// �������ڲ�
		drawCover(canvas, frame);

		if (resultBitmap != null) { // ����ɨ������ͼ
			// Draw the opaque result bitmap over the scanning rectangle
			paint.setAlpha(0xA0);
			canvas.drawBitmap(resultBitmap, null, frame, paint);
		}
		else {

			// ��ɨ�����ϵĽ�
			drawRectEdges(canvas, frame);

			// ����ɨ����
			drawScanningLine(canvas, frame);

			List<ResultPoint> currentPossible = possibleResultPoints;
			Collection<ResultPoint> currentLast = lastPossibleResultPoints;
			if (currentPossible.isEmpty()) {
				lastPossibleResultPoints = null;
			}
			else {
				possibleResultPoints = new ArrayList<ResultPoint>(5);
				lastPossibleResultPoints = currentPossible;
				paint.setAlpha(OPAQUE);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentPossible) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 6.0f, paint);
				}
			}
			if (currentLast != null) {
				paint.setAlpha(OPAQUE / 2);
				paint.setColor(resultPointColor);
				for (ResultPoint point : currentLast) {
					canvas.drawCircle(frame.left + point.getX(), frame.top
							+ point.getY(), 3.0f, paint);
				}
			}

			// ֻˢ��ɨ�������ݣ������ط���ˢ��
			postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
					frame.right, frame.bottom);

		}
	}

	/**
	 * ����ɨ����
	 * 
	 * @param canvas
	 * @param frame
	 *            ɨ���
	 */
	private void drawScanningLine(Canvas canvas, Rect frame) {

		// ��ʼ���м��߻��������ϱߺ����±�
		if (isFirst) {
			isFirst = false;
			slideTop = frame.top;
			slideBottom = frame.bottom;
		}

		// �����м����,ÿ��ˢ�½��棬�м���������ƶ�SPEEN_DISTANCE
		slideTop += SPEEN_DISTANCE;
		if (slideTop >= slideBottom) {
			slideTop = frame.top;
		}

		// ��ͼƬ��Դ��ɨ����
		Rect lineRect = new Rect();
		lineRect.left = frame.left + MIDDLE_LINE_PADDING;
		lineRect.right = frame.right - MIDDLE_LINE_PADDING;
		lineRect.top = slideTop;
		lineRect.bottom = (slideTop + MIDDLE_LINE_WIDTH);
		canvas.drawBitmap(((BitmapDrawable) (BitmapDrawable) getResources()
				.getDrawable(R.drawable.scan_laser)).getBitmap(), null,
				lineRect, paint);

	}

	/**
	 * �������ڲ�
	 * 
	 * @param canvas
	 * @param frame
	 */
	private void drawCover(Canvas canvas, Rect frame) {

		// ��ȡ��Ļ�Ŀ�͸�
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		// Draw the exterior (i.e. outside the framing rect) darkened
		paint.setColor(resultBitmap != null ? resultColor : maskColor);

		// ����ɨ����������Ӱ���֣����ĸ����֣�ɨ�������浽��Ļ���棬ɨ�������浽��Ļ����
		// ɨ��������浽��Ļ��ߣ�ɨ�����ұߵ���Ļ�ұ�
		canvas.drawRect(0, 0, width, frame.top, paint);
		canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
		canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
				paint);
		canvas.drawRect(0, frame.bottom + 1, width, height, paint);
	}

	/**
	 * ��淽�ε��ĸ���
	 * 
	 * @param canvas
	 * @param frame
	 */
	private void drawRectEdges(Canvas canvas, Rect frame) {

		paint.setColor(Color.WHITE);
		paint.setAlpha(OPAQUE);

		Resources resources = getResources();
		/**
		 * ��Щ��Դ�����û�����й�������Ҫÿ��ˢ�¶��½�
		 */
		Bitmap bitmapCornerTopleft = BitmapFactory.decodeResource(resources,
				R.drawable.scan_corner_top_left);
		Bitmap bitmapCornerTopright = BitmapFactory.decodeResource(resources,
				R.drawable.scan_corner_top_right);
		Bitmap bitmapCornerBottomLeft = BitmapFactory.decodeResource(resources,
				R.drawable.scan_corner_bottom_left);
		Bitmap bitmapCornerBottomRight = BitmapFactory.decodeResource(
				resources, R.drawable.scan_corner_bottom_right);

		canvas.drawBitmap(bitmapCornerTopleft, frame.left + CORNER_PADDING,
				frame.top + CORNER_PADDING, paint);
		canvas.drawBitmap(bitmapCornerTopright, frame.right - CORNER_PADDING
				- bitmapCornerTopright.getWidth(), frame.top + CORNER_PADDING,
				paint);
		canvas.drawBitmap(bitmapCornerBottomLeft, frame.left + CORNER_PADDING,
				2 + (frame.bottom - CORNER_PADDING - bitmapCornerBottomLeft
						.getHeight()), paint);
		canvas.drawBitmap(bitmapCornerBottomRight, frame.right - CORNER_PADDING
				- bitmapCornerBottomRight.getWidth(), 2 + (frame.bottom
				- CORNER_PADDING - bitmapCornerBottomRight.getHeight()), paint);

		bitmapCornerTopleft.recycle();
		bitmapCornerTopleft = null;
		bitmapCornerTopright.recycle();
		bitmapCornerTopright = null;
		bitmapCornerBottomLeft.recycle();
		bitmapCornerBottomLeft = null;
		bitmapCornerBottomRight.recycle();
		bitmapCornerBottomRight = null;

	}

	public void drawViewfinder() {
		Bitmap resultBitmap = this.resultBitmap;
		this.resultBitmap = null;
		if (resultBitmap != null) {
			resultBitmap.recycle();
		}
		invalidate();
	}

	/**
	 * Draw a bitmap with the result points highlighted instead of the live
	 * scanning display.
	 * 
	 * @param barcode
	 *            An image of the decoded barcode.
	 */
	public void drawResultBitmap(Bitmap barcode) {
		resultBitmap = barcode;
		invalidate();
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > MAX_RESULT_POINTS) {
				// trim it
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}

	/**
	 * dpתpx
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

}
