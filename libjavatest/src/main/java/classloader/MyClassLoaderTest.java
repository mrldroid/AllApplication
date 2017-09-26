package classloader;

/**
 * Created by liujun on 17/9/25.
 */

public class MyClassLoaderTest {
    public static void main(String[] args) {
//        MyClassLoader cl1 = new MyClassLoader();
//        String className = "classloader.HelloService";
//        Class<?> class1 = null;
//        try {
//            class1 = cl1.loadClass(className);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        MyClassLoader cl2 = new MyClassLoader();
//        Class<?> class2 = null;
//        try {
//            class2 = cl2.loadClass(className);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        if (class1 != class2) {
//            System.out.println(" classes");
//        }

//        System.out.println(System.getProperty("sun.boot.class.path"));
        System.out.println(System.getProperty("java.class.path"));

    }
}
