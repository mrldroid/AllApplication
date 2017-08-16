package xxx.test.allapplication.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;
import java.util.List;

import xxx.test.allapplication.R;

public class Zip4jActivity extends AppCompatActivity {
    NumberProgressBar number_progress_bar;
    Handler handler,mainHandler;

    HandlerThread handlerThread;
    ProgressMonitor progressMonitor;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip4j);
        number_progress_bar = (NumberProgressBar) findViewById(R.id.number_progress_bar);
        text = (TextView) findViewById(R.id.text);
        handlerThread = new HandlerThread("neo");
        handlerThread.start();
        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                number_progress_bar.setProgress(progressMonitor.getPercentDone());
                mainHandler.sendEmptyMessageDelayed(0,100);
            }
        };
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0);
                mainHandler.sendEmptyMessageDelayed(0,800);
            }
        });
        handler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("正在解压...");
                    }
                });
                try {
                    String path = Environment.getExternalStorageDirectory()+"/XTOOL/Vehicles";
                    File file = new File(path);
                    long max = 0;

                    for (File f : file.listFiles()){
                        final long len = f.length();
                        max += len;
                        ZipFile zipFile = new ZipFile(path+"/"+f.getName());
                        progressMonitor = zipFile.getProgressMonitor();
                        zipFile.setFileNameCharset("GBK");

                        if (!zipFile.isValidZipFile()) {   // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
                            Log.i("neo",f.getName()+"压缩包不合法");
                            continue;
                        }
                        FileHeader fh;
                        List fileHeaders = zipFile.getFileHeaders();
                        // zipFile.extractAll(dest);
                        final int total = fileHeaders.size();
                        Log.i("neo","total = "+total);
                        for (int i = 0; i < total; i++) {
                            fh = (FileHeader) fileHeaders.get(i);
                           // Log.i("neo","fh.getFileName() = "+fh.getFileName());
                            if(fh.getFileName().contains("MACOSX"))continue;
                            zipFile.extractFile(fh, path);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText("解压完成...");
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }
}
