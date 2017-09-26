package xxx.test.allapplication.base;

import android.content.Context;

/**
 * Created by liujun on 17/9/26.
 */

public class CustomDialog extends MyDialog {
    public CustomDialog(Context context) {
        super(context);
    }

    protected CustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected int getHeight() {
        return 0;
    }

    @Override
    protected void setView() {

    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected int getWidth() {
        return 0;
    }
}
