package xxx.test.allapplication.activity;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        AndPermission.with(this)
    .permission(Manifest.permission.CAMERA)
    .requestCode(300)
                .callback(this)
                .start();
        Log.i("neo","ActivityCompat = "+ ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA));
        Log.i("neo","PermissionChecker = "+ PermissionChecker.checkCallingOrSelfPermission(this, Manifest.permission.CAMERA));
        AppOpsManager systemService = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        if(systemService!= null){
            try {
                Method method = AppOpsManager.class.getDeclaredMethod("checkOp",Integer.TYPE,Integer.TYPE,String.class);
                Field field = AppOpsManager.class.getDeclaredField("OP_CAMERA");
                field.setAccessible(true);
                method.setAccessible(true);
                int op = field.getInt(systemService);
                Log.i("neo"," op = "+op);
                int checkOp = (int) method.invoke(systemService,op, Binder.getCallingUid(),getPackageName());
                Log.i("neo","AppOpsManager = "+AppOpsManager.MODE_ALLOWED);
                Log.i("neo","checkOp == "+checkOp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        preview = new PreviewSurfaceView(this,savedInstanceState,this);
        mContainer.addView(preview);
    }

    // The 300 is the the requestCode().
    @PermissionYes(300)
    private void getPermissionYes(List<String> grantedPermissions) {
        // Successfully.
        Log.i("neo","getPermissionYes");
    }

    @PermissionNo(300)
    private void getPermissionNo(List<String> deniedPermissions) {
        // Failure.
        Log.i("neo","getPermissionNo");
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
