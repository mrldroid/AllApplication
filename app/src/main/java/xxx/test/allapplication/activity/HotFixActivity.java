package xxx.test.allapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import xxx.test.allapplication.R;

public class HotFixActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix);
    }

    public void calculate(View view) {
        int result = 10/0;
        Toast.makeText(this,result+"",Toast.LENGTH_LONG).show();
    }

    public void fix(View view) {
    }
}
