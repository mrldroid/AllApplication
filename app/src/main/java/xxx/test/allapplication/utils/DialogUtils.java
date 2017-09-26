package xxx.test.allapplication.utils;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by liujun on 17/9/26.
 */

public class DialogUtils {
    public static AppCompatDialog show(Context context,int theme,int layoutId,int width,int height,int animationStyle){
        final AppCompatDialog dialog = new AppCompatDialog(context,theme);
        dialog.setContentView(layoutId);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = width;
            attributes.height = height;
            attributes.gravity = Gravity.BOTTOM;
            window.setAttributes(attributes);
            window.setWindowAnimations(animationStyle);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        return dialog;
    }
}
