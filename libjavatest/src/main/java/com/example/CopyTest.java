package com.example;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //Pattern pattern = Pattern.compile("^\\d+[+\\-\\*/]\\d+$");
    Pattern pattern = Pattern.compile("^\\d+[[+][-][*][/]]\\d+$");
    Matcher matcher = pattern.matcher("123*123124");
    System.out.println(matcher.find());
    Thread tHread;

  }
}
