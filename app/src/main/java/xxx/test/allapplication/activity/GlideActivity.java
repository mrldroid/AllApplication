package xxx.test.allapplication.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import xxx.test.allapplication.App;
import xxx.test.allapplication.R;

public class GlideActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView imgCenterCrop;
    @BindView(R.id.imageView2)
    ImageView imgFitCenter;
    @BindView(R.id.imageView1)
    ImageView imgDefault;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide);
        Log.i("neo","Count = "+ App.COUNT);
        ButterKnife.bind(this);
        Glide.with(this).load(resourceIdToUri(this,R.mipmap.ic_launcher)).centerCrop().into(imgCenterCrop);
        Glide.with(this).load(resourceIdToUri(this,R.mipmap.ic_launcher)).fitCenter().into(imgFitCenter);
        Glide.with(this).load(resourceIdToUri(this,R.mipmap.ic_launcher)).into(imgDefault);
    }

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";

    private static Uri resourceIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }
}
