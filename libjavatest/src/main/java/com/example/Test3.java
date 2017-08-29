package com.example;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by liujun on 17/8/16.
 */

public class Test3 {
  public static void main(String[] args) {
    InputStream input = null;
    try {
      input = new FileInputStream("/Users/neo/Android/AllApplication/libjavatest/src/main/java/com/example/hello.txt");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    try{
      //byte[] buf = new byte[1024];
      //int off = 0;
      //int bytesRead = 0;
      //while((bytesRead=input.read(buf, off, 1024-off ))!=-1){
      //  off += bytesRead;
      //  System.out.println("off = "+off);
      //}
      //String data = new String(buf, 0, off, "UTF-8");
      //System.out.println(data);
      OutputStream output = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int bytesRead = 0;
      while((bytesRead=input.read(buf))!=-1){
        System.out.println("字节数:"+bytesRead);
        output.write(buf, 0, bytesRead);

      }
      //String data = output.toString("UTF-8");
      //System.out.println(data);
    }catch (Exception e){

    }finally{
      try {
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
