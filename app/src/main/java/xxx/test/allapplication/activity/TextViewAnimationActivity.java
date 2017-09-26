package xxx.test.allapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xxx.test.allapplication.R;

public class TextViewAnimationActivity extends AppCompatActivity {
    AnimatorSet animatorSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view_animation);
        final ImageView imgLayer = (ImageView) findViewById(R.id.imgLayer);
        final ImageView imgRight = (ImageView) findViewById(R.id.imgRight);
        final ImageView shadow = (ImageView) findViewById(R.id.shadow);
        final TextView sale_car_left_txt = (TextView) findViewById(R.id.sale_car_left_txt);
        final TextView text = (TextView) findViewById(R.id.text);
        final RelativeLayout activity_text_view_animation = (RelativeLayout) findViewById(R.id.activity_text_view_animation);
        activity_text_view_animation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                activity_text_view_animation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = sale_car_left_txt.getMeasuredWidth();
                ObjectAnimator animator = ObjectAnimator.ofFloat(imgLayer, "translationX", 0, width);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(shadow, "translationX", 0, imgRight.getLeft()-shadow.getRight());
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(text, "alpha", 0, 1);
                ObjectAnimator animator4 = ObjectAnimator.ofFloat(text, "alpha", 1, 0);
                animator.setDuration(800);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        imgLayer.setVisibility(View.VISIBLE);
                        super.onAnimationStart(animation);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        imgLayer.setVisibility(View.GONE);
                    }
                });


                animator2.setDuration(400);
                animator2.setInterpolator(new AccelerateDecelerateInterpolator());
                animator2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        shadow.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        shadow.setVisibility(View.GONE);
                    }
                });

                animator3.setDuration(300);
                animator3.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        text.setVisibility(View.VISIBLE);
                        super.onAnimationStart(animation);
                    }
                });
                animator4.setDuration(300);
                animator4.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        text.setVisibility(View.GONE);
                    }
                });
                 animatorSet = new AnimatorSet();
                animatorSet.playSequentially(animator, animator2,animator3,animator4);
                animatorSet.setStartDelay(500);
                animatorSet.start();
            }
        });


    }

    public void restart(View view) {
        animatorSet.clone();
        animatorSet.start();
    }
}
