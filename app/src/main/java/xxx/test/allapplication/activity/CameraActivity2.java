package xxx.test.allapplication.activity;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xxx.test.allapplication.R;
import xxx.test.allapplication.custom.PreviewInterface;
import xxx.test.allapplication.custom.PreviewSurfaceView;

public class CameraActivity2 extends AppCompatActivity implements PreviewInterface{

    @BindView(R.id.container)
    FrameLayout mContainer;
    private PreviewSurfaceView preview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        ButterKnife.bind(this);
        preview = new PreviewSurfaceView(this,savedInstanceState,this);
        mContainer.addView(preview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != preview.mPhotobm){
            preview.mPhotobm.recycle();
            preview.mPhotobm = null;
        }
    }

    @Override
    public void updateUIforRephoto() {

    }

    @Override
    public void updateFlash() {

    }

    @Override
    public void onPictureTaken(byte[] data, Camera cam) {

    }

    @Override
    public void setAutoFocus(boolean isAutoFoces) {

    }

    @Override
    public boolean isCameraEdit() {
        return false;
    }

    @Override
    public void updateParam(String key, String value) {

    }

    @Override
    public void updateCloudEdUI(List<String> supported_flash_modes) {

    }
}
