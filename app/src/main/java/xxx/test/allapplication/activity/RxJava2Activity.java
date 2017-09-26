package xxx.test.allapplication.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import xxx.test.allapplication.R;

public class RxJava2Activity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java2);
        List<String> list = new ArrayList<>();
        list.add("H");
        list.add("e");
        list.add("l");
        list.add("l");
        list.add("o");

     //   Observable.fromIterable(list).filter(s -> !s.equals("l")).subscribe(s -> Log.i("neo",s));
        Log.i("neo",validate("liujun","s")+"");

        System.out.println(scale(10,4));
//        Observable.just(scale(10,4))
//                .subscribe(value-> Log.i("neo","。。。。"+value+""));

    }

    private  boolean validate(String username, String password) {
//        final boolean[] isValid = {true};
//        Observable.just(username, password).subscribe(s -> {
//            if (!(s != null && !s.isEmpty() && s.length() > 3))
//                throw new RuntimeException();
//        }, throwable -> isValid[0] = false);
//        return isValid[0];
        return false;
    }

    private  float scale(int width, int height){
        return width/height*.3f;
    }
}
