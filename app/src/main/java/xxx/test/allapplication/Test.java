package xxx.test.allapplication;

/**
 * Created by neo on 17/3/23.
 */

public class Test extends ClassLoader{
    public static void main(String[] args) {
        String str = "http://v.admaster.com.cn/i/a88178,b1791978,c3821,i0,m202,8a2,8b2,h";
        System.out.println(str.substring(0,str.length()-1));
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

}
