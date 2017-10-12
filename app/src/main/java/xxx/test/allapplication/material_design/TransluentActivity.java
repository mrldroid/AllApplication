package xxx.test.allapplication.material_design;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import xxx.test.allapplication.R;
import xxx.test.allapplication.utils.StatusBarUtil;

public class TransluentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        StatusBarUtil.setColor(this, ContextCompat.getColor(this,R.color.colorPrimaryDark),0);


    }
}
