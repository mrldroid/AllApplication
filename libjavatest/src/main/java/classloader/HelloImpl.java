package classloader;

/**
 * Created by liujun on 17/9/25.
 */

public class HelloImpl implements IHelloService {
    @Override
    public void sayHello() {
        System.out.println("HelloImpl hello");
    }
}
