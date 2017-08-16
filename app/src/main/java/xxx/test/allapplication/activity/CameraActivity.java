package xxx.test.allapplication.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import xxx.test.allapplication.R;
import xxx.test.allapplication.utils.CameraHelper;
import xxx.test.allapplication.utils.FileUtils;
import xxx.test.allapplication.utils.ImageUtils;
import xxx.test.allapplication.utils.Prompt;

import static xxx.test.allapplication.R.id.tv_bottom;
import static xxx.test.allapplication.R.id.tv_left;
import static xxx.test.allapplication.R.id.tv_right;
import static xxx.test.allapplication.R.id.tv_top;

public class CameraActivity extends AppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener {

    private String mPath;
    private String mPicName = "demo.jpg";
    private Point mPointBefore = new Point(1400, 900);
    @BindView(R.id.surfaceView)
    SurfaceView mSurfaceView;
    @BindView(R.id.img_background)
    ImageView img_background;
    @BindViews({tv_left, tv_right, tv_top, tv_bottom})
    List<TextView> list;
    private CameraHelper.SurfaceCallbackSimple mSurfaceCallback;
    private CameraHelper.OrientationSensorListener mOrientation;

    private Bitmap mBitmap;
    private byte[] mData;
    CameraHelper cameraHelper;
    /**
     * 相机回调
     */
    private final Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSurfaceView.getLayoutParams();
                float left = list.get(0).getWidth() - layoutParams.leftMargin;
                float top = list.get(2).getHeight() - layoutParams.topMargin;
                float rigtht = list.get(1).getWidth() - layoutParams.leftMargin;
                float bottom = list.get(3).getHeight() - layoutParams.topMargin;
                Log.i("neo","拍照结果大小: "+data.length);
                mData = data;
                showSurface(false);
            } catch (Throwable e) {
                e.printStackTrace();
                showSurface(true);
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        mSurfaceView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mPath = Environment.getExternalStorageDirectory().toString() +"/xxxxx/";
        cameraHelper = new CameraHelper();
        mSurfaceCallback = cameraHelper.initCamera(this, mSurfaceView, mPointBefore);
        mOrientation = cameraHelper.initOrientation(this);
        showSurface(true);
    }
    /**
     * 重新拍照，把各个view都初始化刚开始的状态
     */
    private void showSurface(boolean show) {
        Camera camera = mSurfaceCallback.getCamera();
        if (show) {
            if (camera != null && mSurfaceCallback.isCamerable()) {
                camera.startPreview();
            }
        } else {
            if (camera != null) {
                camera.stopPreview();
            }
        }
    }
    public void take(View view){
        if (mSurfaceCallback.isCamerable()) {
            try {
                mSurfaceCallback.getCamera().autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        // 自动对焦成功后才拍摄
                        if (success) {
                            try {
                                mSurfaceCallback.getCamera().takePicture(null, null, pictureCallback);
                            } catch (Exception e) {
                                Prompt.showToast("相机调用失败请重新拍照");
                            }
                        } else {
                            Prompt.showToast("焦距不准,请重拍!");
                        }
                    }
                });
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    }

    public void save(View view){
        savePicture();
    }
    private void savePicture() {

        try {
//            FileUtils.writeFile(ImageUtils.getSmallBitmap(mBitmap, 350), mPath, dizhi, false);
            Bitmap bitmap = BitmapFactory.decodeByteArray(mData, 0, mData.length);;
//            Bitmap bitmap = ImageUtils.byte2Bitmap(mData, cameraHelper.getmPicSize(), 1000, 500);
            FileUtils.writeFile(ImageUtils.bitmap2Bytes(bitmap),mPath,mPicName,false);
            Prompt.showToast("保存成功");
        } catch (Throwable e) {
            e.printStackTrace();
            Prompt.showToast("内存不足，保存失败");
            return;
        }

    }
    @Override
    public void onGlobalLayout() {//SurfaceView大小与预览比例保持一致
        mSurfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        float width = ((View) img_background.getParent()).getWidth();//横屏拍照，只限用于宽度大于高度
        float height = ((View) img_background.getParent()).getHeight();
        RelativeLayout.LayoutParams lpTop = (RelativeLayout.LayoutParams) list.get(2).getLayoutParams();
        RelativeLayout.LayoutParams lpBottom = (RelativeLayout.LayoutParams) list.get(3).getLayoutParams();
        RelativeLayout.LayoutParams lpLeft = (RelativeLayout.LayoutParams) list.get(0).getLayoutParams();
        RelativeLayout.LayoutParams lpRight = (RelativeLayout.LayoutParams) list.get(1).getLayoutParams();
        if (mPointBefore.x / width > mPointBefore.y / height) {//需要拍取的图片宽，当前的预览大小窄，压缩预览高度。
            float desireHeight = mPointBefore.y * width / mPointBefore.x;
            int dh = (int) ((height - desireHeight) / 2);
            lpTop.height = (dh);
            lpBottom.height = (dh);
        } else {//压缩宽度
            float desireWidth = mPointBefore.x * height / mPointBefore.y;
            int dw = (int) ((width - desireWidth) / 2);
            lpLeft.width = (dw);
            lpRight.width = (dw);
        }
        list.get(0).requestLayout();
        list.get(1).requestLayout();
        list.get(2).requestLayout();
        list.get(3).requestLayout();
    }
}
