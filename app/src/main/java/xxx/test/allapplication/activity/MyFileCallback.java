package xxx.test.allapplication.activity;

import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public abstract class MyFileCallback extends MyCallback<File>
{
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;


    public MyFileCallback(String destFileDir, String destFileName)
    {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }


    @Override
    public File parseNetworkResponse(Response response, int id) throws Exception
    {
        return saveFile(response,id);
    }


    public File saveFile(Response response,final int id) throws IOException
    {

        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        try
        {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            //每隔百分之几通知一次
//            int count = (int) (total/(1024*1024*7));
            int count = 1;
            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);


            int tmp = 0;
            int downloadCount = 0;


            while ((len = is.read(buf)) != -1)
            {
                sum += len;
                fos.write(buf, 0, len);
                final long finalSum = sum;


                tmp = (int) (finalSum * 100 / total);
                //为了防止频繁的通知导致应用吃紧，百分比增加7才通知一次
                if(downloadCount == 0 || tmp-count > downloadCount) {
                    downloadCount += count;
                    OkHttpUtils.getInstance().getDelivery().execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            inProgress(finalSum,total,id);

                        }
                    });
                }



            }
            fos.flush();

            return file;

        } finally
        {
            try
            {
                response.body().close();
                if (is != null) is.close();
            } catch (IOException e)
            {
            }
            try
            {
                if (fos != null) fos.close();
            } catch (IOException e)
            {
            }

        }
    }


}