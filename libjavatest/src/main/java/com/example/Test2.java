package com.example;

import java.util.Random;

/**
 * Created by liujun on 17/8/3.
 */

public class Test2 {
  public static void main(String ar[]){
    //Child c = new Child();
    //c.action(1);
    //
    //Base b = c;
    //b.action(1);
    //
    //System.out.println("b.s = "+b.s+" c.s = "+c.s);
    //TimeZone tz = TimeZone.getDefault();
    //TimeZone tz = TimeZone.getTimeZone("GMT+08:00");
    //
    //System.out.println(tz.getID());
    //Locale locale = Locale.getDefault();
    //System.out.println(locale.toString());

    //Integer[] a = {1,2,3};
    //List<Integer> list = Arrays.asList(a);
    //list.remove(0);
    for(int i =0; i <10;i++){
      System.out.print(" "+new Random(100).nextInt(100));
    }
    System.out.println();
    for(int i =0; i <10;i++){
      System.out.print(" "+new Random(100).nextInt(100));
    }

    System.out.println();
    Random random = new Random(20);
    for(int i =0; i <10;i++){
      System.out.print(" "+random.nextInt(100));
    }
    System.out.println();
    Random random2 = new Random(20);
    for(int i =0; i <10;i++){
      System.out.print(" "+random2.nextInt(100));
    }
    System.out.println();
     int h = 1123,length = 16;
    System.out.println(h&(length-1));
    System.out.println(h%length);

    System.out.println(~~0);

  }
}
