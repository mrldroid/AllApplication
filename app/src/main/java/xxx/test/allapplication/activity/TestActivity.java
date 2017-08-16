package xxx.test.allapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import xxx.test.allapplication.App;
import xxx.test.allapplication.R;

public class TestActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test);
    App.COUNT = 100;
  }

  public void test(View view) {
    startActivity(new Intent(this,GlideActivity.class));
  }
}
