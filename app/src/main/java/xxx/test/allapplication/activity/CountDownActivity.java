package xxx.test.allapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import xxx.test.allapplication.R;
import xxx.test.allapplication.custom.CustomCountDownTimer;

public class CountDownActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down);

        CustomCountDownTimer countDownTimer = new CustomCountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("neo","onTick = "+millisUntilFinished+"    "+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                Log.i("neo","onFinish");
            }
        };
        countDownTimer.start();
    }
}
