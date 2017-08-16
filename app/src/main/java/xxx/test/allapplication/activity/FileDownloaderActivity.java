package xxx.test.allapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;

import xxx.test.allapplication.R;

public class FileDownloaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_downloader);
        FileDownloader.getImpl().bindService();
        FileDownloader.getImpl().addServiceConnectListener(new FileDownloadConnectListener() {
            @Override
            public void connected() {
                Log.i("neo","connected");
            }

            @Override
            public void disconnected() {
                Log.i("neo","disconnected");
            }
        });
    }
}
