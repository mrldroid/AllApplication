package xxx.test.allapplication.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

public class ApkExtract {
    public static String extract(Context context) {
        context = context.getApplicationContext();
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        String apkPath = applicationInfo.sourceDir;
        Log.d("hongyang", apkPath);
        return apkPath;
    }
}