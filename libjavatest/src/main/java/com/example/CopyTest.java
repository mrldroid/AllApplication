package com.example;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Created by liujun on 17/8/25.
 */

public class CopyTest {
  public static void main(String[] args) {
    int [] src = {1,2,3,4,5};
    int [] des = new int[5];
    System.arraycopy(src,1,des,0,4);
    System.out.println(Arrays.toString(des));
    int[] temp = Arrays.copyOf(src,5);
    System.out.println(Arrays.toString(temp));
    //new BufferedOutputStream(new DataOutputStream(new FileOutputStream("")));
  }
}
