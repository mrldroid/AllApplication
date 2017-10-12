package xxx.test.allapplication.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by neo on 17/10/12.
 */

public class HookUtils {
    public void hookAms() throws Exception{
        Class<?> clazz = Class.forName("android.app.ActivityManagerNative");//ActivityManagerNative class 对象
        Field gDefaultField = clazz.getDeclaredField("gDefault");//拿到ActivityManagerNative  gDefault 变量
        gDefaultField.setAccessible(true);
        Object gDefaultFieldObj = gDefaultField.get(null);//静态所属的是类不是对象所以为null，得到gDefault的值（类型为Singleton）

        Class<?> singleTonClazz = Class.forName("android.util.Singleton");
        Field mInstanceField = singleTonClazz.getDeclaredField("mInstance");
        mInstanceField.setAccessible(true);
        Object sysIActivityManager = mInstanceField.get(gDefaultFieldObj);//系统的IActivityManager，mInstanceField所属的对象是gDefaultFieldObj（类型为Singleton）

        Class<?> iActivityManagerProxy = Class.forName("android.app.IActivityManager");//代理对象
        Object proxy = Proxy.newProxyInstance(sysIActivityManager.getClass().getClassLoader(),new Class[]{iActivityManagerProxy}, new MyHandler(sysIActivityManager));
        mInstanceField.set(gDefaultFieldObj,proxy);//设置代理的IActivityManager
    }


    private class MyHandler implements InvocationHandler {
        private Object iActivityManagerObj;
        MyHandler(Object iActivityManagerObj){
            this.iActivityManagerObj = iActivityManagerObj;
            Log.i("neo","MyHandler");
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.i("neo","method "+method.getName());
            return method.invoke(iActivityManagerObj,args);
        }
    }
}
