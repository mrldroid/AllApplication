package xxx.test.allapplication.custom;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import xxx.test.allapplication.App;
import xxx.test.allapplication.R;

public class LinkSpan extends ClickableSpan {

    private View.OnClickListener listener;
    private int textColor = ContextCompat.getColor(App.getInstance(),R.color.colorAccent);

    public LinkSpan(View.OnClickListener listener) {
        this.listener = listener;
    }

    public LinkSpan(View.OnClickListener listener, int textColor) {
        this.listener = listener;
        this.textColor = textColor;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(textColor);
        ds.setFlags(Paint.ANTI_ALIAS_FLAG);
        ds.setFlags(ds.getFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }
}