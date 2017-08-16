package xxx.test.allapplication.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * initCamera调用这一句话，可以从返回的callback里拿到camera并且设置点击对焦和测光
 * @author dsr
 *
 */
public class CameraHelper {
	private LinkedList<Area> meteringAreas;
	private LinkedList<Area> focusAreas;
	private SurfaceCallbackSimple mCallBack;

	public Size getmPicSize() {
		return mPicSize;
	}

	private Size mPicSize;

	/**
	 * 
	 * @param parameters 相机的参数
	 * @param previewWidth 预览的最大宽
	 * @param previewHeight 预览的最大高
	 * @param picWidth 拍取图片的最小宽
	 * @param picHeight 拍取图片的最小高
	 */
	public void setSize(Parameters parameters, int previewWidth, int previewHeight, int picWidth, int picHeight) {
		List<Size> picsizes = parameters.getSupportedPictureSizes();
		List<Size> previewSizes = parameters.getSupportedPreviewSizes();
		Size previewSize = null;
		Size picSize = null;
		Comparator<Size> b2sSize = new Comparator<Size>() {

			@Override
			public int compare(Size lhs, Size rhs) {
				return rhs.width - lhs.width;
			}
		};
		Collections.sort(previewSizes, b2sSize);//从大到小排序
		Collections.sort(picsizes, b2sSize);
		while (previewSizes.size() > 1) {//去除小于预览大小的size
			int lastOne = previewSizes.size() - 1;
			Size size = previewSizes.get(lastOne);
			if (size.width < previewWidth / 3 || size.height < previewHeight / 3) {
				previewSizes.remove(lastOne);
			} else {
				break;
			}
		}
		while (picsizes.size() > 1) {//去除小于需要图片大小的的size
			int lastOne = picsizes.size() - 1;
			Size size = picsizes.get(lastOne);
			if (size.width < picWidth || size.height < picHeight) {
				picsizes.remove(lastOne);
			} else {//也是移除掉比想拍摄的图片小的
				break;
			}
		}
		TreeMap<Double, Size> preViewMap = new TreeMap<Double, Size>();
		for (int i = 0; i < previewSizes.size(); i++) {//先放大的后放小的，如果比例相等用小的比例
			Size size = previewSizes.get(i);
			preViewMap.put((double) size.width / size.height, size);
		}
		for (Size size : picsizes) {//从可拍取图片宽高比差值最小的大小找 相等宽高比的预览大小
			double key = (double) size.width / size.height;
			if (preViewMap.containsKey(key)) {
				picSize = size;//找预览大小比例与实际大小比例相同的实际比例最大的
				previewSize = preViewMap.get(key);
				break;
			}
		}
		if (picSize == null || previewSize == null) {
			previewSize = previewSizes.get(previewSizes.size() - 1);
			picSize = picsizes.get(picsizes.size() - 1);
		}
		mPicSize = picSize;
		Log.i("neo","preview width = "+previewSize.width+" height = "+previewSize.height);
		Log.i("neo","finalSzie width = "+picSize.width+" height = "+picSize.height);
		parameters.setPreviewSize(previewSize.width, previewSize.height);
		parameters.setPictureSize(picSize.width, picSize.height);// 设置照片的大小
	}

	@SuppressLint({ "ClickableViewAccessibility", "NewApi" })
	public void initFocusMeterArea(Context context, View v, MotionEvent event, Camera mCamera) {
		if (mCamera == null) {
			return;
		}
		Parameters parameters = mCamera.getParameters();
		int maxNumMeteringAreas = parameters.getMaxNumMeteringAreas();
		Rect rect = new Rect(0, 0, 0, 0);
		calculateTapArea(context, (int) event.getX(), (int) event.getY(), 2.5f, rect, v, 0);
		if (maxNumMeteringAreas > 0) {
			if (meteringAreas == null) {
				meteringAreas = new LinkedList<Area>();
			}
			if (meteringAreas.size() == maxNumMeteringAreas) {
				meteringAreas.remove(0);
			}
			Area area = new Area(rect, 1000);
			meteringAreas.addLast(area);
			parameters.setMeteringAreas(meteringAreas);
		}
		int maxNumFocusAreas = parameters.getMaxNumFocusAreas();
		if (maxNumFocusAreas > 0) {
			if (focusAreas == null) {
				focusAreas = new LinkedList<Area>();
			}
			if (focusAreas.size() == maxNumFocusAreas) {
				focusAreas.remove(0);
			}
			Area area = new Area(rect, 1000);
			focusAreas.addLast(area);
			parameters.setFocusAreas(focusAreas);
		}
		mCamera.setParameters(parameters);
		try {
			mCamera.autoFocus(null);
		} catch (Exception e) {
		}
	}

	public void calculateTapArea(Context context, int x, int y, float areaMultiple, Rect rect, View v, int displayOrientation) {
		int areaSize = (int) (Math.min(v.getWidth(), v.getHeight()) * areaMultiple / 20);
		int left = clamp(x - areaSize, 0, v.getWidth() - 2 * areaSize);
		int top = clamp(y - areaSize, 0, v.getHeight() - 2 * areaSize);
		RectF rectF = new RectF(left, top, left + 2 * areaSize, top + 2 * areaSize);
		Matrix mMatrix = new Matrix();
		if (v.getWidth() != 0 && v.getHeight() != 0) {
			Matrix matrix = new Matrix();
			matrix.postRotate(displayOrientation);
			matrix.postScale(v.getWidth() / 2000f, v.getHeight() / 2000f);
			matrix.postTranslate(v.getWidth() / 2f, v.getHeight() / 2f);
			matrix.invert(mMatrix);
		}
		mMatrix.mapRect(rectF);
		rectFToRect(rectF, rect);
	}

	private int clamp(int x, int min, int max) {
		if (x > max)
			return max;
		if (x < min)
			return min;
		return x;
	}

	private void rectFToRect(RectF rectF, Rect rect) {
		rect.left = Math.round(rectF.left);
		rect.right = Math.round(rectF.right);
		rect.bottom = Math.round(rectF.bottom);
		rect.top = Math.round(rectF.top);
	}

	@SuppressLint("ClickableViewAccessibility")
	@SuppressWarnings("deprecation")
	public SurfaceCallbackSimple initCamera(final Activity context, SurfaceView surfaceview, Point picSize) {
		if (mCallBack == null) {
			mCallBack = new SurfaceCallbackSimple(picSize, surfaceview, context);
		}
		surfaceview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceview.getHolder().setFixedSize(picSize.x, picSize.y);
		surfaceview.getHolder().setKeepScreenOn(true);// 屏幕常亮
		surfaceview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				initFocusMeterArea(context, v, event, mCallBack.getCamera());
				return true;
			}
		});
		surfaceview.getHolder().addCallback(mCallBack);// 为SurfaceView的句柄添加一个回数调函
		return mCallBack;
	}

	public OrientationSensorListener initOrientation(Context context) {
		SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		OrientationSensorListener listener = new OrientationSensorListener();
		sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
		return listener;
	}

	public class SurfaceCallbackSimple implements Callback {
		private Camera mCamera;
		private final Point picSize;
		private final SurfaceView surfaceview;
		private int width = -1;
		private int height = -1;
		private int status = 0;
		private final Activity activity;

		public Camera getCamera() {
			return mCamera;
		}

		public boolean isCamerable() {
			return status == 1;
		}

		public SurfaceCallbackSimple(Point picSize, SurfaceView surfaceview, Activity activity) {
			this.picSize = picSize;
			this.surfaceview = surfaceview;
			this.activity = activity;
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		}

		// 开始拍照时调用该方法
		@SuppressWarnings("deprecation")
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				if (null == mCamera) {
					mCamera = Camera.open();
					mCamera.setPreviewDisplay(holder);
					mCamera.setErrorCallback(new CameraonError());
					Camera.Parameters parameters = mCamera.getParameters();
					parameters.setPictureFormat(PixelFormat.JPEG);
					parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
					if (picSize.x < 1080 && picSize.y < 1920) {
						setSize(parameters, picSize.x, picSize.y, picSize.x, picSize.y);
					} else {
						setSize(parameters, 1080, 1920, picSize.x, picSize.y);
					}
					mCamera.setParameters(parameters);
					mCamera.startPreview();
					if (width == -1 && height == -1) {
						width = surfaceview.getWidth();
						height = surfaceview.getHeight();
						Size previewSize = parameters.getPreviewSize();
						float sufaceRadio = (float) width / height;
						float previewRadio = (float) previewSize.width / previewSize.height;
						RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) surfaceview.getLayoutParams();
						if (layoutParams == null) {
							layoutParams = new RelativeLayout.LayoutParams(-1, -1);
						}
						if (sufaceRadio > previewRadio) {//surface宽了，需要扩大高度
							int desireHeight = width * previewSize.height / previewSize.width;
							layoutParams.width = width;
							layoutParams.height = desireHeight;
							layoutParams.topMargin = (height - desireHeight) / 2;
							layoutParams.bottomMargin = (height - desireHeight + 1) / 2;
						} else {
							int desireWidth = height * previewSize.width / previewSize.height;
							layoutParams.width = desireWidth;
							layoutParams.height = height;
							layoutParams.leftMargin = (width - desireWidth) / 2;
							layoutParams.rightMargin = (width - desireWidth + 1) / 2;
						}
						surfaceview.setLayoutParams(layoutParams);
						surfaceview.requestLayout();
					}
				}
				status = 1;
			} catch (Exception e) {
				e.printStackTrace();
				if (mCamera != null) {
					mCamera.release();
					mCamera = null;
				}
				try {
					Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
					activity.startActivity(intent);
				} catch (Exception e2) {
					try {
						Intent intent = new Intent(Settings.ACTION_SETTINGS);
						activity.startActivity(intent);
					} catch (Exception e3) {
					}
				}
			}
		}

		// 停止拍照时调用该方法
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			status = 0;
			if (mCamera != null) {
				mCamera.setPreviewCallback(null);
				mCamera.stopPreview();
				mCamera.release(); // 释放照相机
				mCamera = null;
			}
		}
	}

	/**
	 * 相机异常捕抓
	 * 
	 * @author
	 * 
	 */
	public final class CameraonError implements Camera.ErrorCallback {

		@Override
		public void onError(int error, Camera camera) {
			if (error == android.hardware.Camera.CAMERA_ERROR_SERVER_DIED) {
				Prompt.showToast("相机服务器启动失败");
				Log.e("error", "CAMERA_ERROR_SERVER_DIED--相机服务器挂了");

			} else if (error == android.hardware.Camera.CAMERA_ERROR_UNKNOWN) {
				Prompt.showToast("未知的相机错误");
				Log.e("error", "CAMERA_ERROR_UNKNOWN--未知的相机错误");
			}
		}
	}

	public class OrientationSensorListener implements SensorEventListener {
		private int degree;
		private static final int _DATA_X = 0;
		private static final int _DATA_Y = 1;
		private static final int _DATA_Z = 2;
		public static final int ORIENTATION_UNKNOWN = -1;

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
		}

		public int getDegree() {
			return degree;
		}

		@Override
		public synchronized void onSensorChanged(SensorEvent event) {
			float[] values = event.values;
			int orientation = ORIENTATION_UNKNOWN;
			float X = -values[_DATA_X];
			float Y = -values[_DATA_Y];
			float Z = -values[_DATA_Z];
			float magnitude = X * X + Y * Y;
			// Don't trust the angle if the magnitude is small compared to the y value
			if (magnitude * 4 >= Z * Z) {
				float OneEightyOverPi = 57.29577957855f;
				float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
				orientation = 90 - Math.round(angle);
				// normalize to 0 - 359 range
				while (orientation >= 360) {
					orientation -= 360;
				}
				while (orientation < 0) {
					orientation += 360;
				}
				if (orientation > 45 && orientation < 135) {
					degree = 180;
					 Log.e("180", "右横");
				} else if (orientation > 135 && orientation < 225) {
					degree = 270;
					 Log.e("270", "倒屏");
				} else if (orientation > 225 && orientation < 315) {
					degree = 0;
					 Log.e("0", "左横");
				} else if ((orientation > 315 && orientation < 360)
						|| (orientation > 0 && orientation < 45)) {
					degree = 90;
					 Log.e("90", "竖屏");
				}
			}

		}

	}

}
