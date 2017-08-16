package xxx.test.allapplication.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import xxx.test.allapplication.activity.CameraActivity2;
import xxx.test.allapplication.utils.SharedPreferencesUtil;
/**超级宝查克的拍照界面*/
public class PreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	
	private Paint mPaint = new Paint();
	private Camera mCamera;
	private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
	private Matrix mCamera_to_preview_matrix = new Matrix();
	private Matrix mPreview_to_camera_matrix = new Matrix();
	private SurfaceHolder mHolder = null;
	//检测两个手指在屏幕上做缩放
	private ScaleGestureDetector mScaleGestureDetector;
	//控制CameraAcitity的接口
	private PreviewInterface mPreviewInterface;
	private Context mContext;
	public Bitmap mPhotobm; 
	/*预览状态等*/		
	private boolean mApp_is_paused = true;
	private boolean mHas_surface = false;
	private boolean mHas_aspect_ratio = false;
	private boolean mIs_preview_started = false;
	private boolean mTouch_was_multitouch = false;
	private double mAspect_ratio = 0.0f;
	private int mZoom_factor = 0;
	private int mDisplay_orientation = 0;
	private int mCameraId = 0;
	private int mCurrent_rotation = 0; // orientation relative to camera's
										// orientation (used for parameters.setOrientation())
	/*情景模式、白平衡等设置*/				
	public List<String> mSupported_flash_values = null; // our "values" format
	private List<String> mColor_effects = null;
	private List<String> mScene_modes = null;
	private List<String> mWhite_balances = null;
	private List<String> mExposures = null;
	private int mMin_exposure = 0;
	private int mMax_exposure = 0;
	/*手势缩放*/
	private List<Size> mSupported_preview_sizes = null;
	private List<Size> mSizes = null;
	private int mResolution_w = 0;
	private int mResolution_h = 0;
	private int mMax_zoom_factor = 0;
	private boolean mHas_zoom = false;// 是否支持手势
	private List<Integer> mZoom_ratios = null;
	/*对焦*/
	private boolean mHas_focus_area = false;
	private boolean mSuccessfully_focused = false;
	private int mFocus_screen_x = 0;
	private int mFocus_screen_y = 0;
	private int mFocus_success = FOCUS_DONE;
	private static final int FOCUS_WAITING = 0;
	private static final int FOCUS_SUCCESS = 1;
	private static final int FOCUS_FAILED = 2;
	private static final int FOCUS_DONE = 3;
	private long mFocus_complete_time = -1;
	private long mSuccessfully_focused_time = -1;
	/*电量与存储*/
	private IntentFilter mBattery_ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	private boolean mHas_battery_frac = false;
	private float mBattery_frac = 0.0f;
	private long mLast_battery_time = 0;
	public int mCount_cameraAutoFocus = 0;
	public int mCount_cameraTakePicture = 0;
	public boolean mHas_received_location = false;
	public boolean mTest_low_memory = false;
	public boolean mTest_have_angle = false;
	public float mTest_angle = 0.0f;
	public String mTest_last_saved_image = null;

	PreviewSurfaceView(Context context) {
		this(context, null, null);
	}

	@SuppressWarnings("deprecation")
	public PreviewSurfaceView(Context context, Bundle savedInstanceState,
			PreviewInterface previewInterface) {
		super(context);
		this.mPreviewInterface = previewInterface;
		this.mContext = context;
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.ø
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // deprecated
		mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	// 赋值手指在屏幕上做缩放处理
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (PreviewSurfaceView.this.mCamera != null && PreviewSurfaceView.this.mHas_zoom) {
				PreviewSurfaceView.this.scaleZoom(detector.getScaleFactor());
			}
			return true;
		}
	}

	private void calculatePreviewToCameraMatrix() {
		mCamera_to_preview_matrix.reset();
		// from http://developer.android.com/reference/android/hardware/Camera.Face.html#rect
		Camera.getCameraInfo(mCameraId, mCameraInfo);
		// Need mirror for front camera.
		boolean mirror = (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
		mCamera_to_preview_matrix.setScale(mirror ? -1 : 1, 1);
		// This is the value for android.hardware.Camera.setDisplayOrientation.
		mCamera_to_preview_matrix.postRotate(mDisplay_orientation);
		// Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
		// UI coordinates range from (0, 0) to (width, height).
		mCamera_to_preview_matrix.postScale(this.getWidth() / 2000f, this.getHeight() / 2000f);
		mCamera_to_preview_matrix.postTranslate(this.getWidth() / 2f, this.getHeight() / 2f);
	}

	// 计算对焦区域
	@SuppressLint("NewApi")
	private ArrayList<Camera.Area> getAreas(float x, float y) {
		float[] coords = { x, y };
		calculatePreviewToCameraMatrix();
		mPreview_to_camera_matrix.mapPoints(coords);
		float focus_x = coords[0];
		float focus_y = coords[1];
		int focus_size = 50;
		Rect rect = new Rect();
		rect.left = (int) focus_x - focus_size;
		rect.right = (int) focus_x + focus_size;
		rect.top = (int) focus_y - focus_size;
		rect.bottom = (int) focus_y + focus_size;
		if (rect.left < -1000) {
			rect.left = -1000;
			rect.right = rect.left + 2 * focus_size;
		} else if (rect.right > 1000) {
			rect.right = 1000;
			rect.left = rect.right - 2 * focus_size;
		}
		if (rect.top < -1000) {
			rect.top = -1000;
			rect.bottom = rect.top + 2 * focus_size;
		} else if (rect.bottom > 1000) {
			rect.bottom = 1000;
			rect.top = rect.bottom - 2 * focus_size;
		}
		ArrayList<Camera.Area> areas = new ArrayList<Camera.Area>();
		areas.add(new Camera.Area(rect, 1000));
		return areas;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScaleGestureDetector.onTouchEvent(event);
		if (event.getPointerCount() != 1) {
			mTouch_was_multitouch = true;
			return true;
		}
		if (event.getAction() != MotionEvent.ACTION_UP) {
			if (event.getAction() == MotionEvent.ACTION_DOWN && event.getPointerCount() == 1) {
				mTouch_was_multitouch = false;
			}
			return true;
		}
		if (mTouch_was_multitouch) {
			return true;
		}
		// note, we always try to force start the preview (in case is_preview_paused has become false)
		// 开启预览
		startCameraPreview();
		cancelAutoFocus();
		if (mCamera != null) {

			Camera.Parameters parameters = mCamera.getParameters();
			String focus_mode = parameters.getFocusMode();
			this.mHas_focus_area = false;
			// 得到支持的最大区域范围 ，如果为0 表示 区域是不支持
			if (parameters.getMaxNumFocusAreas() != 0
					&& (focus_mode.equals(Camera.Parameters.FOCUS_MODE_AUTO)
							|| focus_mode.equals(Camera.Parameters.FOCUS_MODE_MACRO)
							|| focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) || focus_mode
								.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))) {
				this.mHas_focus_area = true;
				this.mFocus_screen_x = (int) event.getX();
				this.mFocus_screen_y = (int) event.getY();

				ArrayList<Camera.Area> areas = getAreas(event.getX(), event.getY());
				parameters.setFocusAreas(areas); // 设置焦点区域

				// also set metering areas
				if (parameters.getMaxNumMeteringAreas() != 0) { // 获取领域最大数量。如果该值为0，计量面积不支持。
					parameters.setMeteringAreas(areas); // 集计量范围
				}

				try {
					mCamera.setParameters(parameters);
				} catch (RuntimeException e) {
					// just in case something has gone wrong
					//FileLog.getInstance().addErrorLog("init 244 camera", e);
					e.printStackTrace();
				}
			} else if (parameters.getMaxNumMeteringAreas() != 0) {
				// don't set has_focus_area in this mode
				ArrayList<Camera.Area> areas = getAreas(event.getX(), event.getY());
				parameters.setMeteringAreas(areas);

				try {
					mCamera.setParameters(parameters);
				} catch (RuntimeException e) {
					//FileLog.getInstance().addErrorLog("init 258 camera", e);

					// just in case something has gone wrong
					e.printStackTrace();
				}
			}
		}
		tryAutoFocus(false, true);
		return true;
	}

	public void clearFocusAreas() {
		if (mCamera == null) {
			return;
		}
		cancelAutoFocus();
		Camera.Parameters parameters = mCamera.getParameters();
		boolean update_parameters = false;
		if (parameters.getMaxNumFocusAreas() > 0) {
			parameters.setFocusAreas(null);
			update_parameters = true;
		}
		if (parameters.getMaxNumMeteringAreas() > 0) {
			parameters.setMeteringAreas(null);
			update_parameters = true;
		}
		if (update_parameters) {
			mCamera.setParameters(parameters);
		}
		mHas_focus_area = false;
		mFocus_success = FOCUS_DONE;
		mSuccessfully_focused = false;
		this.invalidate();
		// Log.d(TAG, "camera parameters null? " +
		// (camera.getParameters().getFocusAreas()==null));
	}

	public void surfaceCreated(SurfaceHolder holder) {
		this.mHas_surface = true;
		this.openCamera();
		this.setWillNotDraw(false); // http://stackoverflow.com/questions/2687015/extended-surfaceviews-ondraw-method-never-called
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		this.mHas_surface = false;
		this.closeCamera();
	}

	private void closeCamera() {
		mHas_focus_area = false;
		mFocus_success = FOCUS_DONE;
		mSuccessfully_focused = false;
		mHas_received_location = false;
		if (mCamera != null) {
			mCamera.stopPreview();
			this.mIs_preview_started = false;
			mCamera.release();
			mCamera = null;
		}
		// this.invalidate();
	}

	public int getCameraSize() {
		return Camera.getNumberOfCameras();// 得到摄像头的个数
	}

	// 切换摄像头
	public void switchCamera() {
		try {
			if (mCameraId == 1) {
				mCameraId = 0;
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				mCamera = Camera.open(mCameraId);
				try {
					mCamera.setPreviewDisplay(mHolder);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mCamera.startPreview();// 开始预览
			} else {
				mCameraId = 1;
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
				mCamera = Camera.open(mCameraId);
				try {
					mCamera.setPreviewDisplay(mHolder);
				} catch (IOException e) {
					e.printStackTrace();
				}
				mCamera.startPreview();// 开始预览
			}
		} catch (Exception e) {
			mCameraId = 0;
			openCamera();
		}
	}

	// 启动相机
	private void openCamera() {
		// need to init everything now, in case we don't open the camera (but
		// these may already be initialised from an earlier call - e.g., if we
		// are now switching to another camera)
		mHas_received_location = false;
		mHas_focus_area = false;
		mFocus_success = FOCUS_DONE;
		mSuccessfully_focused = false;
		mScene_modes = null;
		mHas_zoom = false;
		mColor_effects = null;
		mWhite_balances = null;
		mExposures = null;
		mMin_exposure = 0;
		mMax_exposure = 0;
		mSizes = null;
		mZoom_ratios = null;
		mMax_zoom_factor = 0;
		mSupported_flash_values = null;
		if (!this.mHas_surface) {
			return;
		}
		if (this.mApp_is_paused) {
			return;
		}
		if (mCamera == null) {
			try {
				mCamera = Camera.open(mCameraId);
			} catch (RuntimeException e) {
				//FileLog.getInstance().addErrorLog("init 352 camera ", e);
				e.printStackTrace();
				mCamera = null;
			}
			if (mCamera != null) {
				try {
					mCamera.setPreviewDisplay(mHolder);
				} catch (IOException e) {
				//	FileLog.getInstance().addErrorLog("init 362 camera", e);
					e.printStackTrace();
				}
				Camera.Parameters parameters = mCamera.getParameters();

				// get available scene modes important, from docs:
				// "Changing scene mode may override other parameters (such as flash mode, focus mode, white balance).
				// For example, suppose originally flash mode is on and
				// supported flash modes are on/off. In night scene mode, both flash mode and supported flash mode may be
				// changed to off. After setting scene mode, applications should call getParameters 
				// to know if some parameters are changed."
				if (mPreviewInterface.isCameraEdit()) {
					String scene = SharedPreferencesUtil.getinstance().GetStringSharedPreferences(mContext, "scene_mode");
					if (!"".equals(scene)) {
						parameters.setSceneMode(scene);
						// need to read back parameters, see comment above
						mCamera.setParameters(parameters);
					}
				} else {
					mScene_modes = parameters.getSupportedSceneModes();
					String scene_mode = setupValuesPref(mScene_modes, Camera.Parameters.SCENE_MODE_AUTO);
					if (scene_mode != null && !parameters.getSceneMode().equals(scene_mode)) {
						mPreviewInterface.updateParam("scene_mode", scene_mode);
						parameters.setSceneMode(scene_mode);
						// need to read back parameters, see comment above
						mCamera.setParameters(parameters);
					}
				}
				parameters = mCamera.getParameters();

				this.mHas_zoom = parameters.isZoomSupported();

				if (this.mHas_zoom) {
					this.mMax_zoom_factor = parameters.getMaxZoom();
					try {
						this.mZoom_ratios = parameters.getZoomRatios();
					} catch (NumberFormatException e) {
						//FileLog.getInstance().addErrorLog("init 404 camera", e);

						// crash java.lang.NumberFormatException: Invalid int:
						// " 500" reported in v1.4 on device "es209ra", Android 4.1, 3 Jan 2014
						// this is from java.lang.Integer.invalidInt(Integer.java:138) -
						// unclear if this is a bug in Open Camera, all we can do for now is catch it
						e.printStackTrace();
						this.mHas_zoom = false;
						this.mZoom_ratios = null;
					}
				}

				// get available color effects
				mColor_effects = parameters.getSupportedColorEffects();
				String color_effect = setupValuesPref(mColor_effects, Camera.Parameters.EFFECT_NONE);
				if (color_effect != null) {
					parameters.setColorEffect(color_effect);
				}

				// get available white balances
				mWhite_balances = parameters.getSupportedWhiteBalance();
				String white_balance = setupValuesPref(mWhite_balances, Camera.Parameters.WHITE_BALANCE_AUTO);
				if (white_balance != null) {
					parameters.setWhiteBalance(white_balance);
				}

				// get min/max exposure
				mExposures = null;
				mMin_exposure = parameters.getMinExposureCompensation();
				mMax_exposure = parameters.getMaxExposureCompensation();
				if (mMin_exposure != 0 || mMax_exposure != 0) {
					mExposures = new Vector<String>();
					for (int i = mMin_exposure; i <= mMax_exposure; i++) {
						mExposures.add("" + i);
					}
					String exposure_s = setupValuesPref(mExposures, "0");
					if (exposure_s != null) {
						try {
							int exposure = Integer.parseInt(exposure_s);
							parameters.setExposureCompensation(exposure);
						} catch (NumberFormatException exception) {
							//FileLog.getInstance().addErrorLog("init 444 camera", exception);
						}
					}
				}

				if (mPreviewInterface.isCameraEdit()) {
					String[] resolution_size = SharedPreferencesUtil.getinstance()
							.GetStringSharedPreferences(mContext,"resolution_size").split(",");
					parameters.setPictureSize(Integer.parseInt(resolution_size[0]), Integer.parseInt(resolution_size[1]));
				} else {
					mSizes = parameters.getSupportedPictureSizes();
					getPictureSize(); // 获取图片分辨率
					parameters.setPictureSize(mResolution_w, mResolution_h); // 设置图片分辨率
					mPreviewInterface.updateParam("resolution_size", mResolution_w + "," + mResolution_h);
				}
				// get available sizes

				parameters.setJpegQuality(85);
				mCamera.setParameters(parameters);

				// we do flash and focus after setting parameters, as these are
				// done by calling separate functions, that themselves set the
				// parameters directly
				List<String> supported_flash_modes = parameters.getSupportedFlashModes(); // Android format
				// View flashButton = (View) activity.findViewById(R.id.flash);
				tryAutoFocus(false, false);

				mPreviewInterface.updateCloudEdUI(supported_flash_modes);

				// flashButton.setVisibility(supported_flash_values != null ?
				// View.VISIBLE : View.GONE);
				// Must set preview size before starting camera preview
				// and must do it after setting photo vs video mode
				setPreviewSize(); // need to call this when we switch cameras,
									// not just when we run for the first time
				// Must call startCameraPreview after checking if face detection
				// is present - probably best to call it after setting all
				// parameters that we want
				startCameraPreview();
				this.invalidate();
			}
		}
	}

	// 获取图片分辨率
	private void getPictureSize() {
		mResolution_w = 0;
		mResolution_h = 0;
		HashMap<String, ArrayList<Size>> hashMap = new HashMap<String, ArrayList<Size>>();
		for (int i = 0; i < mSizes.size(); i++) {
			Size size = mSizes.get(i);
			// 1280, 960 1920, 1080
			if (size.width == 1920 && size.height == 1080) {
				mResolution_w = 1920;
				mResolution_h = 1080;
				return;
			}
			String ratio = String.format("%.2f", (double) size.width
					/ size.height);
			if (!hashMap.containsKey(ratio)) {
				ArrayList<Size> list = new ArrayList<Size>();
				list.add(size);
				hashMap.put(ratio, list);
			} else {
				hashMap.get(ratio).add(size);
			}

		}
		if (0 == mResolution_w && 0 == mResolution_h) {
			// 取接近 1280*960 分辨率 1920, 1080
			ArrayList<Size> arrayList = hashMap.get("1.78");
			// ArrayList<Size> arrayList2 = hashMap.get("1.33");
			if (null != arrayList && 0 != arrayList.size()) {
				ArrayList<Integer> seize = new ArrayList<Integer>();
				ArrayList<Integer> copyseize = new ArrayList<Integer>();
				for (Size size : arrayList) {
					seize.add(Math.abs(1920 * 1080 - size.width * size.height));
				}
				// Collections.copy(copyseize, seize);
				copyseize.addAll(seize);
				Collections.sort(seize);
				mResolution_w = arrayList.get(copyseize.indexOf(seize.get(0))).width;
				mResolution_h = arrayList.get(copyseize.indexOf(seize.get(0))).height;

			} else {
				ArrayList<Integer> seize = new ArrayList<Integer>();
				ArrayList<Integer> copyseize = new ArrayList<Integer>();
				for (Size sizee : mSizes) {
					seize.add(Math.abs(1920 * 1080 - sizee.width * sizee.height));
				}
				// Collections.copy(copyseize, seize);
				copyseize.addAll(seize);
				Collections.sort(seize);
				mResolution_w = mSizes.get(copyseize.indexOf(seize.get(0))).width;
				mResolution_h = mSizes.get(copyseize.indexOf(seize.get(0))).height;
			}
		}
	}

	// setupValuesPref(color_effects, "preference_color_effect",
	// Camera.Parameters.EFFECT_NONE);
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// surface size is now changed to match the aspect ratio of camera
		// preview - so we shouldn't change the preview to match the surface
		// size, so no need to restart preview here
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		if (mCamera == null) {
			return;
		}
	}

	// 设置取景框比例
	private void setPreviewSize() {
		if (mCamera == null) {
			return;
		}
		if (mIs_preview_started) {
			// throw new RuntimeException();
		}
		// set optimal preview size
		Camera.Parameters parameters = mCamera.getParameters();

		if (mPreviewInterface.isCameraEdit()) {
			String preview_sizes = SharedPreferencesUtil.getinstance().GetStringSharedPreferences(mContext, "preview_size");
			if (!"".equals(preview_sizes)) {
				String[] preview_size = preview_sizes.split(",");
				parameters.setPreviewSize(Integer.parseInt(preview_size[0]), Integer.parseInt(preview_size[1]));
				this.setAspectRatio(((double) Integer.parseInt(preview_size[0]))
						/ (double) Integer.parseInt(preview_size[1]));
				mCamera.setParameters(parameters);
			}
		} else {
			mSupported_preview_sizes = parameters.getSupportedPreviewSizes();
			if (mSupported_preview_sizes.size() > 0) {
				Size best_size = getOptimalPreviewSize(mSupported_preview_sizes);
				parameters.setPreviewSize(best_size.width, best_size.height);
				this.setAspectRatio(((double) parameters.getPreviewSize().width)
						/ (double) parameters.getPreviewSize().height);
				mCamera.setParameters(parameters);
				mPreviewInterface.updateParam("preview_size", best_size.width + "," + best_size.height);
			}
		}
	}

	// 获取取景框比例
	public Size getOptimalPreviewSize(List<Size> sizes) {
		final double ASPECT_TOLERANCE = 0.05;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		Point display_size = new Point();
		Activity activity = (Activity) this.getContext();
		{
			Display display = activity.getWindowManager().getDefaultDisplay();
			display.getSize(display_size);
		}
		// double targetRatio = getTargetRatio(display_size);
		double targetRatio = ((double) display_size.x) / (double) display_size.y;

		int targetHeight = Math.min(display_size.y, display_size.x);
		if (targetHeight <= 0) {
			targetHeight = display_size.y;
		}
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		if (optimalSize == null) {
			// can't find match for aspect ratio, so find closest one
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				double ratio = (double) size.width / size.height;
				if (Math.abs(ratio - targetRatio) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(ratio - targetRatio);
				}
			}
		}
		return optimalSize;
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		if (!this.mHas_aspect_ratio) {
			super.onMeasure(widthSpec, heightSpec);
			return;
		}
		int previewWidth = MeasureSpec.getSize(widthSpec);
		int previewHeight = MeasureSpec.getSize(heightSpec);

		// Get the padding of the border background.
		int hPadding = getPaddingLeft() + getPaddingRight();
		int vPadding = getPaddingTop() + getPaddingBottom();

		// Resize the preview frame with correct aspect ratio.
		previewWidth -= hPadding;
		previewHeight -= vPadding;

		boolean widthLonger = previewWidth > previewHeight;
		int longSide = (widthLonger ? previewWidth : previewHeight);
		int shortSide = (widthLonger ? previewHeight : previewWidth);
		if (longSide > shortSide * mAspect_ratio) {
			longSide = (int) ((double) shortSide * mAspect_ratio);
		} else {
			shortSide = (int) ((double) longSide / mAspect_ratio);
		}
		if (widthLonger) {
			previewWidth = longSide;
			previewHeight = shortSide;
		} else {
			previewWidth = shortSide;
			previewHeight = longSide;
		}

		// Add the padding of the border.
		previewWidth += hPadding;
		previewHeight += vPadding;

		// Ask children to follow the new preview dimension.
		super.onMeasure(
				MeasureSpec.makeMeasureSpec(previewWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(previewHeight, MeasureSpec.EXACTLY));
	}

	private void setAspectRatio(double ratio) {
		// if( ratio <= 0.0 )
		// throw new IllegalArgumentException();

		mHas_aspect_ratio = true;
		if (mAspect_ratio != ratio) {
			mAspect_ratio = ratio;
			requestLayout();
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (this.mApp_is_paused) {
			return;
		}
		CameraActivity2 main_activity = (CameraActivity2) this.getContext();
		final float scale = getResources().getDisplayMetrics().density;
		canvas.save();
		canvas.rotate(0, canvas.getWidth() / 2, canvas.getHeight() / 2);
		if (mCamera == null) {
			mPaint.setColor(Color.WHITE);
			mPaint.setTextSize(16 * scale + 0.5f); // convert dps to pixels
			mPaint.setTextAlign(Paint.Align.CENTER);
			int pixels_offset = (int) (20 * scale + 0.5f); // convert dps to
															// pixels
			canvas.drawText("没有获取到相机权限", canvas.getWidth() / 2,
					canvas.getHeight() / 2, mPaint);
			canvas.drawText("请在再系统设置-应用管理-查客", canvas.getWidth() / 2,
					canvas.getHeight() / 2 + pixels_offset, mPaint);
			canvas.drawText("权限管理-隐私相关-使用摄像头", canvas.getWidth() / 2,
					canvas.getHeight() / 2 + 2 * pixels_offset, mPaint);
		}
		{
			if (!this.mHas_battery_frac || System.currentTimeMillis() > this.mLast_battery_time + 60000) {
				// only check periodically - unclear if checking is costly in any way
				Intent batteryStatus = main_activity.registerReceiver(null, mBattery_ifilter);
				int battery_level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				int battery_scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				mHas_battery_frac = true;
				mBattery_frac = battery_level / (float) battery_scale;
				mLast_battery_time = System.currentTimeMillis();
			}
			// battery_frac = 0.2999f; // test
			int battery_x = (int) (5 * scale + 0.5f); // convert dps to pixels
			int battery_y = battery_x;
			int battery_width = (int) (5 * scale + 0.5f); // convert dps to pixels
			int battery_height = 4 * battery_width;
			mPaint.setColor(Color.WHITE);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(battery_x, battery_y, battery_x + battery_width,
					battery_y + battery_height, mPaint);
			mPaint.setColor(mBattery_frac >= 0.3f ? Color.GREEN : Color.RED);
			mPaint.setStyle(Paint.Style.FILL);
			canvas.drawRect(battery_x + 1, battery_y + 1
					+ (1.0f - mBattery_frac) * (battery_height - 2), battery_x
					+ battery_width - 1, battery_y + battery_height - 1, mPaint);
		}

		canvas.restore();
		{
			if (this.mFocus_success != FOCUS_DONE) {
				int size = (int) (50 * scale + 0.5f); // convert dps to pixels
				if (this.mFocus_success == FOCUS_SUCCESS)
					mPaint.setColor(Color.GREEN);
				else if (this.mFocus_success == FOCUS_FAILED)
					mPaint.setColor(Color.RED);
				else
					mPaint.setColor(Color.WHITE);
				mPaint.setStyle(Paint.Style.STROKE);
				int pos_x = 0;
				int pos_y = 0;
				if (mHas_focus_area) {
					pos_x = mFocus_screen_x;
					pos_y = mFocus_screen_y;
				} else {
					pos_x = canvas.getWidth() / 2;
					pos_y = canvas.getHeight() / 2;
				}
				canvas.drawRect(pos_x - size, pos_y - size, pos_x + size, pos_y
						+ size, mPaint);
				if (mFocus_complete_time != -1
						&& System.currentTimeMillis() > mFocus_complete_time + 1000) {
					mFocus_success = FOCUS_DONE;
				}
				mPaint.setStyle(Paint.Style.FILL); // reset
			}
			this.invalidate();
		}
	}

	// 更改闪光
	public void updateFlash(int new_flash_index) {
		// updates the Flash button, and Flash camera mode
		if (mSupported_flash_values != null) {
			String flash_value = mSupported_flash_values.get(new_flash_index);
			this.setFlash(flash_value);
		}
	}

	// 设置闪光
	private void setFlash(String flash_value) {
		cancelAutoFocus();
		if (mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			String flash_mode = convertFlashValueToMode(flash_value);
			if (flash_mode.length() > 0
					&& !flash_mode.equals(parameters.getFlashMode())) {
				parameters.setFlashMode(flash_mode);
				mCamera.setParameters(parameters);
			}
		}
	}

	// this returns the flash mode indicated by the UI, rather than from the
	// camera parameters (may be different, e.g., in startup autofocus!)
	// 获取对应的支持闪光模式
	private String convertFlashValueToMode(String flash_value) {
		String flash_mode = "";
		if (flash_value.equals("flash_off")) {
			flash_mode = Camera.Parameters.FLASH_MODE_OFF;
		} else if (flash_value.equals("flash_on")) {
			flash_mode = Camera.Parameters.FLASH_MODE_ON;
		} else if (flash_value.equals("flash_auto")) {
			flash_mode = Camera.Parameters.FLASH_MODE_AUTO;
		}

		else if (flash_value.equals("flash_torch")) {
			flash_mode = Camera.Parameters.FLASH_MODE_TORCH;
		} else if (flash_value.equals("flash_red_eye")) {
			flash_mode = Camera.Parameters.FLASH_MODE_RED_EYE;
		}
		return flash_mode;
	}

	// 拍照
	public void takePicturePressed() {
		if (mCamera == null) {
			// this.phase = PHASE_NORMAL;
			return;
		}
		if (!this.mHas_surface) {
			// this.phase = PHASE_NORMAL;
			return;
		}
		// make sure that preview running (also needed to hide trash/share icons)
		this.startCameraPreview();
		takePicture();
	}

	// 拍照之前对焦一次
	private void takePicture() {
		if (mCamera == null) {
			return;
		}
		if (!this.mHas_surface) {
			return;
		}
		mFocus_success = FOCUS_DONE; // clear focus rectangle
		Camera.Parameters parameters = mCamera.getParameters();
		String focus_mode = parameters.getFocusMode();

		if (this.mSuccessfully_focused
				&& System.currentTimeMillis() < this.mSuccessfully_focused_time + 5000) {
			takePictureWhenFocused();
		} else if (focus_mode.equals(Camera.Parameters.FOCUS_MODE_AUTO)
				|| focus_mode.equals(Camera.Parameters.FOCUS_MODE_MACRO)) {
			Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					takePictureWhenFocused();
				}
			};
			try {
				mCamera.autoFocus(autoFocusCallback);
				mCount_cameraAutoFocus++;
			} catch (RuntimeException e) {
				// just in case? We got a RuntimeException report here from 1
				// user on Google Play:
				// 21 Dec 2013, Xperia Go, Android 4.1
				//FileLog.getInstance().addErrorLog("init 914 camera", e);

				autoFocusCallback.onAutoFocus(false, mCamera);
				e.printStackTrace();
			}
		} else {
			takePictureWhenFocused();
		}
		this.invalidate();
	}

	/**
	 * 获取相机返回data 转换bitmap 赋值给backgimag 背景
	 */
	private void takePictureWhenFocused() {
		// should be called when auto-focused
		if (mCamera == null) {
			return;
		}
		if (!this.mHas_surface) {
			return;
		}
		mSuccessfully_focused = false; // so next photo taken will require an
										// autofocus
		// ShutterCallback shutter, PictureCallback raw, PictureCallback jpeg
		Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
			// don't do anything here, but we need to implement the callback to
			// get the shutter sound (at least on Galaxy Nexus and Nexus 7)
			public void onShutter() {

			}
		};

		Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera cam) {
				mPreviewInterface.onPictureTaken(data, cam);
			}
		};

		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setRotation(mCurrent_rotation);
		mCamera.setParameters(parameters);

		try {
			mCamera.takePicture(shutterCallback, null, jpegPictureCallback);
			mCount_cameraTakePicture++;
		} catch (RuntimeException e) {
			//FileLog.getInstance().addErrorLog("init 1012 camera", e);

			// just in case? We got a RuntimeException report here from 1
			// user on Google Play; I also encountered it myself once of
			// Galaxy Nexus when starting up
			e.printStackTrace();
		}
	}

	// 重新拍照
	public void rePhotograph() {
		if (mPhotobm != null) {
			mPhotobm.recycle();
			mPhotobm = null;
		}
		mPreviewInterface.updateUIforRephoto();
		mIs_preview_started = false; // preview automatically stopped due to taking photo
		startCameraPreview();
		System.gc();
	}

	/**
	 * 对焦
	 * 
	 * @param startup
	 * @param manual
	 */
	public void tryAutoFocus(final boolean startup, final boolean manual) {
		// manual: whether user has requested autofocus (by touching screen)
		try {
			if (mCamera != null && this.mHas_surface && this.mIs_preview_started) {
				// it's only worth doing autofocus when autofocus has an effect
				// (i.e., auto or macro mode)
				Camera.Parameters parameters = mCamera.getParameters();
				String focus_mode = parameters.getFocusMode();
				// getFocusMode() is documented as never returning null, however
				// I've had null pointer exceptions reported in Google Play from
				// the below line (v1.7),
				// on Galaxy Tab 10.1 (GT-P7500), Android 4.0.3 - 4.0.4; HTC EVO
				// 3D X515m (shooteru), Android 4.0.3 - 4.0.4
				if (focus_mode != null && (focus_mode.equals(Camera.Parameters.FOCUS_MODE_AUTO) || focus_mode.equals(Camera.Parameters.FOCUS_MODE_MACRO))) {
					// 自动聚焦变量回调
					Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
						@Override
						public void onAutoFocus(boolean success, Camera camera) {
							mPreviewInterface.setAutoFocus(true);
							autoFocusCompleted(manual, success, false);
						}
					};
					this.mFocus_success = FOCUS_WAITING;
					this.mFocus_complete_time = -1;
					this.mSuccessfully_focused = false;
					try {
						mCamera.autoFocus(autoFocusCallback);
						mCount_cameraAutoFocus++;
					} catch (RuntimeException e) {
						//FileLog.getInstance().addErrorLog("init 1167 camera", e);

						// just in case? We got a RuntimeException report here
						// from 1 user on Google Play
						autoFocusCallback.onAutoFocus(false, mCamera);
						e.printStackTrace();
					}
				} else if (mHas_focus_area) {
					// do this so we get the focus box, for focus modes that
					// support focus area, but don't support autofocus
					mFocus_success = FOCUS_SUCCESS;
					mFocus_complete_time = System.currentTimeMillis();
				}
				this.invalidate();
			}
		} catch (Exception e) {
		}
	}

	private void cancelAutoFocus() {
		if (mCamera != null) {
			mCamera.cancelAutoFocus();
			autoFocusCompleted(false, false, true);
		}
	}

	private void autoFocusCompleted(boolean manual, boolean success,
			boolean cancelled) {
		if (cancelled) {
			mFocus_success = FOCUS_DONE;
		} else {
			mFocus_success = success ? FOCUS_SUCCESS : FOCUS_FAILED;
			mFocus_complete_time = System.currentTimeMillis();
		}
		if (manual && !cancelled && success) {
			mSuccessfully_focused = true;
			mSuccessfully_focused_time = mFocus_complete_time;
		}

		this.invalidate();
	}

	// 开启预览
	private void startCameraPreview() {
		// if( camera != null && !this.isTakingPhotoOrOnTimer() && !is_preview_started ) {
		if (mCamera != null && !mIs_preview_started) {
			Camera.Parameters parameters = mCamera.getParameters();
			// calling setParameters here with continuous video focus mode
			// causes preview to not restart on Galaxy Nexus?! (fine on my Nexus 7)
			// issue seems to specifically be with setParameters (i.e., the
			// problem occurs even if we don't setRecordingHint)
			String focus_mode = parameters.getFocusMode(); // 获取焦点模式
			if (focus_mode != null && !focus_mode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				// parameters.setRecordingHint(this.is_video);
				parameters.setRecordingHint(false);
				mCamera.setParameters(parameters);
			}
			mCamera.startPreview();
			this.mIs_preview_started = true;
		}
	}

	public void onResume() {
		this.mApp_is_paused = false;
		this.openCamera();
	}

	public void onPause() {
		this.mApp_is_paused = true;
		this.closeCamera();
	}

	public void onSaveInstanceState(Bundle state) {
		state.putInt("cameraId", mCameraId);
		state.putInt("zoom_factor", mZoom_factor);
	}

	// must be static, to safely call from other Activities
	public static String getFlashPreferenceKey(int cameraId) {
		return "flash_value_" + cameraId;
	}

	// must be static, to safely call from other Activities
	public static String getResolutionPreferenceKey(int cameraId) {
		return "camera_resolution_" + cameraId;
	}

	/**
	 * values 是否包含default_value 不包含返回集合第一个元素
	 * 
	 * @param values
	 * @param default_value
	 * @return
	 */
	private String setupValuesPref(List<String> values, String default_value) {
		String value = null;
		if (null != values && 0 != values.size()) {
			if (values.contains(default_value)) {
				value = default_value;
			} else {
				value = values.get(0);
			}
		}
		return value;
	}

	// 设置缩放值
	public void scaleZoom(float scale_factor) {
		if (this.mCamera != null && this.mHas_zoom) {
			float zoom_ratio = this.mZoom_ratios.get(mZoom_factor) / 100.0f;
			zoom_ratio *= scale_factor;

			int new_zoom_factor = mZoom_factor;
			if (zoom_ratio <= 1.0f) {
				new_zoom_factor = 0;
			} else if (zoom_ratio >= mZoom_ratios.get(mMax_zoom_factor) / 100.0f) {
				new_zoom_factor = mMax_zoom_factor;
			} else {
				// find the closest zoom level
				if (scale_factor > 1.0f) {
					// zooming in
					for (int i = mZoom_factor; i < mZoom_ratios.size(); i++) {
						if (mZoom_ratios.get(i) / 100.0f >= zoom_ratio) {
							new_zoom_factor = i;
							break;
						}
					}
				} else {
					// zooming out
					for (int i = mZoom_factor; i >= 0; i--) {
						if (mZoom_ratios.get(i) / 100.0f <= zoom_ratio) {
							new_zoom_factor = i;
							break;
						}
					}
				}
			}
			zoomTo(new_zoom_factor, true);
		}
	}

	// 根据缩放值，设置相机setZoom
	public void zoomTo(int new_zoom_factor, boolean update_seek_bar) {
		if (new_zoom_factor < 0)
			new_zoom_factor = 0;
		if (new_zoom_factor > mMax_zoom_factor)
			new_zoom_factor = mMax_zoom_factor;
		// problem where we crashed due to calling this function with null
		// camera should be fixed now, but check again just to be safe
		if (new_zoom_factor != mZoom_factor && mCamera != null) {
			Camera.Parameters parameters = mCamera.getParameters();
			if (parameters.isZoomSupported()) {
				parameters.setZoom((int) new_zoom_factor);
				try {
					mCamera.setParameters(parameters);
					mZoom_factor = new_zoom_factor;
				} catch (RuntimeException e) {
					//FileLog.getInstance().addErrorLog("init 1323 camera", e);

					// crash reported in v1.3 on device
					// "PANTONE 5 SoftBank 107SH (SBM107SH)"
					e.printStackTrace();
				}
				clearFocusAreas();
			}
		}
	}

	/**
	 * 获取view的成像
	 * 
	 * @param view
	 * @return
	 */
	public Bitmap takeScreen(View view) {
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(true));
		view.setDrawingCacheEnabled(false);
		view.destroyDrawingCache();
		return bitmap;
	}
}
