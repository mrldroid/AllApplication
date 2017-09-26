package xxx.test.allapplication.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import xxx.test.allapplication.R;
import xxx.test.allapplication.utils.Prompt;

public class NDKActivity extends AppCompatActivity {

    static {
        System.loadLibrary("neo");   //defaultConfig.ndk.moduleName
    }
    public native static String getStringFromC();
    public native String modifyField();
    //拆分
    public native static void diff(String path,String path_pattern,int count);
    //合并
    public native static void patch(String path_pattern,int count,String merge_path);
    //生成差分包
    public native static void bisdiff(String oldPath,String newPath,String patch_path);
    public native static void bspatch(String oldPath,String newPath,String patch_path);

    private String key = "hello";
    private String path = Environment.getExternalStorageDirectory()+"/neo.jpg";
    private String root = Environment.getExternalStorageDirectory()+"";
    private String patch_path = Environment.getExternalStorageDirectory()+"/ha.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndk);
        TextView textView  = (TextView) findViewById(R.id.tv);
        modifyField();
        textView.setText(key);
    }

    public void diff(View view) {
        diff(path,root+"/neo_%d.jpg",4);
    }

    public void patch(View view) {
        patch(root+"/neo_%d.jpg",4,patch_path);
    }

    String oldPath = root+"/xxxxx/old.apk";
    String newPath = root+"/xxxxx/new.apk";
    String patchPath = root+"/xxxxx/new_old.patch";
    String mergePath = root+"/xxxxx/merge.apk";
    public void bsdiff(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bisdiff(oldPath,newPath,patchPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Prompt.showToast("生成差分包完成");
                    }
                });
            }
        }).start();

    }

    public void bspatch(View view) {
//        new Thread(() -> {
//            bspatch(oldPath,mergePath,patchPath);
//            runOnUiThread(() -> {
//                Prompt.showToast("合并差分包完成");
//            });
//        }).start();



    }
}
