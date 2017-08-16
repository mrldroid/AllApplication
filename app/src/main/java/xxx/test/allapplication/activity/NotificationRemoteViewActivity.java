package xxx.test.allapplication.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import xxx.test.allapplication.R;

public class NotificationRemoteViewActivity extends AppCompatActivity {

    private String mLocalPath = Environment.getExternalStorageDirectory().getPath() + "/apk/";
    private String apkName = "myapk";
    private RemoteViews remoteViews;
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("neo", "onCreate"+Thread.currentThread().getId()+"");
        setContentView(R.layout.activity_notification_remote_view);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .cookieJar(cookieJar)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

    }
    static int num = 0;
    public void download(View view) {
        OkHttpUtils
                .get()
                .url("https://c6.xinstatic.com/f1/app_package/11/20170421/1039/XinUCL-3.1.4-44-debug-defaults_314_UMENG_CHANNEL_应用宝_yingyongbao_sign.apk")
                .build()
                .execute(new MyFileCallback(mLocalPath, apkName) {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        createNotification();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        refreshNotificationState(null, false);
                    }

                    @Override
                    public void inProgress(long progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        Log.i("neo","inProgress progress = "+progress+" total = "+total);
                        if(num == 0) {
                            Log.i("neo", Thread.currentThread().getId()+"");
                            num++;
                        }
                        setNotificationProgress(total, progress);
                    }

                    @Override
                    public void onResponse(Object response, int id) {
                        Log.i("neo","id = "+id+"......"+response.toString());
                        File pkg = new File(mLocalPath, apkName + ".apk");
                        File f = (File)response;
                        f.renameTo(pkg);//改名以标记下载完成
                        refreshNotificationState(pkg, true);
                        installNewApk(NotificationRemoteViewActivity.this, pkg, false);
                    }

                });
    }

    protected void createNotification() {
        mNotification = new Notification();
        mNotification.icon = android.R.drawable.stat_sys_download;
        mNotification.tickerText = "正在下载";
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_item);
        remoteViews.setImageViewBitmap(R.id.notificationImage, bitmap);

        remoteViews.setTextViewText(R.id.notificationTitle, "正在下载");
        remoteViews.setTextViewText(R.id.notificationPercent, "0%");
        remoteViews.setProgressBar(R.id.notificationProgress, 100, 0, false);
        if (android.os.Build.VERSION.SDK_INT>=16){
            mNotification.bigContentView = remoteViews;
            mNotification.contentView = remoteViews;
        }else{
            mNotification.contentView = remoteViews;
        }
        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(123, mNotification);
    }

    /**
     * 正在下载，更新通知栏进度条
     */
    private void setNotificationProgress(long total, long current) {
        int progress = (int) (current * 100 / total);
        remoteViews.setTextViewText(R.id.notificationPercent,  progress+ "%");
        remoteViews.setProgressBar(R.id.notificationProgress, 100, progress, false);
        mNotification.contentView = remoteViews;
        mNotificationManager.notify(123, mNotification);
    }

    protected void refreshNotificationState(File pkg, boolean isSuccess) {
        remoteViews.setTextViewText(R.id.notificationTitle, isSuccess ? "下载完成，点击安装" : "下载失败，请重试");
        remoteViews.setTextViewText(R.id.notificationPercent,  "100%");
        remoteViews.setProgressBar(R.id.notificationProgress, 100, 100, false);
        mNotification.contentView = remoteViews;
        if (isSuccess) {
            Intent updateIntent = new Intent(Intent.ACTION_VIEW);
//            updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            updateIntent.setDataAndType(Uri.fromFile(pkg), "application/vnd.android.package-archive");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
            mNotification.contentIntent = pendingIntent;
        }
        mNotificationManager.notify(123, mNotification);
    }

    protected void installNewApk(Activity context, File file, boolean is_force) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        if (is_force) {
            context.finish();
        }
    }

    public void toast(View view) {
        Toast.makeText(this, "sssssss", Toast.LENGTH_SHORT).show();
    }
}
