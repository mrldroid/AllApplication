package xxx.test.allapplication.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;

import xxx.test.allapplication.R;

public class CustomeAnimationActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custome_animation);
        Log.i("neo","abi "+ Arrays.toString(Build.SUPPORTED_ABIS));

    }
}
