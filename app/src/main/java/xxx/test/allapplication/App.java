package xxx.test.allapplication;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.liulishuo.filedownloader.FileDownloader;

import xxx.test.allapplication.utils.MIUIUtil;

/**
 * Created by liujun on 16/12/20.
 */

public class App extends Application{
    private static boolean isMIUI;
    public static int mSystemUiVisibility = -1;
    private static App app;
    public static int COUNT = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        isMIUI = MIUIUtil.isMIUI();
        app = this;
        FileDownloader.init(getApplicationContext());
    }

    public static boolean isMIUI() {
        return isMIUI;
    }

    public static App getInstance(){
        return app;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
