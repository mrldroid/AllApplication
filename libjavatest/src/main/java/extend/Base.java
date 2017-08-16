package extend;

/**
 * Created by liujun on 17/8/3.
 */

public class Base {
  public static int s;
  private int a = 1000;
  static {
    System.out.println("基类静态代码块 s = "+s);
    s = 1;
  }
  {
    System.out.println("基类实例代码块 a = "+a);
    a = 1;
  }
  public Base(){
    System.out.println("基类构造方法 a = "+a);
    a = 2;
  }

  protected void step(int r){
    System.out.println("base s = "+s+", a = "+a);
  }
  public void action(int a){
    System.out.println("start");
    step(a);
    System.out.println("end");
  }
}
