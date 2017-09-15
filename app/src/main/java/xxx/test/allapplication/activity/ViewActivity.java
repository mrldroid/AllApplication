package xxx.test.allapplication.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.lang.reflect.Method;
import xxx.test.allapplication.R;
import xxx.test.allapplication.custom.MyView;

public class ViewActivity extends AppCompatActivity {

    @BindView(R.id.activity_view)
    RelativeLayout rl;
    @BindView(R.id.view)
    MyView myView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);
        //myView.setScaleX(2f);
        myView.post(new Runnable() {
            @Override public void run() {
                Log.i("neo","left = "+myView.getLeft());
            }
        });
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.i("neo", "height = " + metrics.heightPixels);//height = 1776
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        Log.i("neo", "x = " + point.x + " y = " + point.y);//x = 1080 y = 1776
        Log.i("neo", "getNavigationBarHeight = " + getNavigationBarHeight(this));//getNavigationBarHeight = 144
        Log.i("neo", "getStatusBarHeight = " + getStatusBarHeight(this));//getStatusBarHeight = 72
        Log.i("neo", "getActionBarHeight = " + getActionBarHeight(this));//getActionBarHeight = 168.0
//        rl.post(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("neo", "rl.getHeight() = " + rl.getHeight());//rl.getHeight() = 1536
//            }
//        });
//        User user = new Gson().fromJson("{\"xxx\":\"怪盗kidou\",\"ag\":24,\"email_address\":\"ikidou@example.com\"}\n",User.class);
//        Log.i("neo","user = "+user.name+" "+user.address+" "+user.age);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("neo","ViewActivity onResume");

    }

    /**
     * 获取虚拟按键栏高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static float getActionBarHeight(Context context) {
//第一种方法
//        TypedArray actionbarSizeTypedArray = context.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
//        float dimension = actionbarSizeTypedArray.getDimension(0, 0);
//        actionbarSizeTypedArray.recycle();
//        return dimension;
        //第二种方法
        TypedValue tv = new TypedValue();
        float result = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data,context.getResources().getDisplayMetrics());
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    private static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }
}
