package xxx.test.allapplication.base;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by liujun on 17/9/26.
 */

public abstract class MyDialog extends AppCompatDialog{
    protected Context mContext;

    public MyDialog(Context context) {
        super(context);
        init(context);
    }

    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        setContentView(getLayoutId());
        setView();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = getWidth();
            attributes.height = getHeight();
            attributes.gravity = Gravity.BOTTOM;
            window.setAttributes(attributes);
            //window.setWindowAnimations(R.style.AnimRightInRightOut);
        }
        setCanceledOnTouchOutside(true);
    }

    protected abstract int getHeight();
    protected abstract void setView();
    protected abstract int getLayoutId();
    protected abstract int getWidth();

}
