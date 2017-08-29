package xxx.test.allapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.client.android.CaptureActivity;
import com.jaeger.library.StatusBarUtil;
import xxx.test.allapplication.activity.CameraActivity;
import xxx.test.allapplication.activity.CameraActivity2;
import xxx.test.allapplication.activity.CountDownActivity;
import xxx.test.allapplication.activity.CustomeAnimationActivity;
import xxx.test.allapplication.activity.FileDownloaderActivity;
import xxx.test.allapplication.activity.GlideActivity;
import xxx.test.allapplication.activity.HotFixActivity;
import xxx.test.allapplication.activity.NDKActivity;
import xxx.test.allapplication.activity.NestedFragmentActivity;
import xxx.test.allapplication.activity.NotificationRemoteViewActivity;
import xxx.test.allapplication.activity.RetrofitActivity;
import xxx.test.allapplication.activity.RxJava2Activity;
import xxx.test.allapplication.activity.SlidingMenuActivity;
import xxx.test.allapplication.activity.SpannableActivity;
import xxx.test.allapplication.activity.StaticLayoutActivity;
import xxx.test.allapplication.activity.TestActivity;
import xxx.test.allapplication.activity.TextViewAnimationActivity;
import xxx.test.allapplication.activity.ViewActivity;
import xxx.test.allapplication.activity.Zip4jActivity;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.mSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        StatusBarUtil.setColor(this, ContextCompat.getColor(this,R.color.colorPrimaryDark),0);
        imageView = (ImageView) findViewById(R.id.imageView);
        TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText("neoeneas");
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        View view = viewGroup.getChildAt(0);
        viewGroup.addView(textView);
    }

    public void spannable(View view){
        startActivity(new Intent(this,SpannableActivity.class));
    }
    public void staticLayout(View view){
        startActivity(new Intent(this,StaticLayoutActivity.class));
    }
    public void view(View view){
        startActivity(new Intent(this, ViewActivity.class));
    }
    public void glide(View view){
        startActivity(new Intent(this, GlideActivity.class));
    }
    public void camera(View view){
        startActivity(new Intent(this, CameraActivity.class));
    }
    public void camera2(View view){
        startActivity(new Intent(this, CameraActivity2.class));
    }
    public void nested(View view){
        startActivity(new Intent(this, NestedFragmentActivity.class));
    }
    public void zip4j(View view){
        startActivity(new Intent(this, Zip4jActivity.class));
    }
    public void fileDownloader(View view){
        startActivity(new Intent(this, FileDownloaderActivity.class));
    }
    public void animate(View view){
        startActivity(new Intent(this, CustomeAnimationActivity.class));
    }
    public void retrofit(View view){
        startActivity(new Intent(this, RetrofitActivity.class));
    }public void rxjava2(View view){
        startActivity(new Intent(this, RxJava2Activity.class));
    }
    public void ndk(View view){
        startActivity(new Intent(this, NDKActivity.class));
    }


    public void notify(View view) {
        startActivity(new Intent(this, NotificationRemoteViewActivity.class));
    }
    public void text(View view) {
        startActivity(new Intent(this, TextViewAnimationActivity.class));
    }

    private static final int SCAN_REQUEST_CODE = 10;
    public void scan(View view) {
        startActivityForResult(new Intent(this,CaptureActivity.class),SCAN_REQUEST_CODE);
    }
    public void hotfix(View view) {
        startActivity(new Intent(this, HotFixActivity.class));
    }
    public void countDown(View view) {
        startActivity(new Intent(this, CountDownActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCAN_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    String result = data.getStringExtra("data");
                    Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(data.getParcelableExtra("bitmap"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void test(View view) {
        startActivity(new Intent(this,TestActivity.class));
    }

    public void slide(View view) {
        startActivity(new Intent(this, SlidingMenuActivity.class));
    }
}
