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
        Field gDefaultField = clazz.getDeclaredField("gDefault");//拿到ActivityManagerNative  gDefault 变量（类型为Singleton）
        Method gDefaultMethod = clazz.getDeclaredMethod("getDefault");//拿到 ActivityManagerNative.getDefault()静态方法
        gDefaultField.setAccessible(true);
        gDefaultMethod.setAccessible(true);
        Object sysIActivityManager = gDefaultMethod.invoke(clazz);//调用方法得到系统的IActivityManager
        Class<?> singleTonClazz = Class.forName("android.util.Singleton");//gDefault代理对象

        Field mInstanceField = singleTonClazz.getDeclaredField("mInstance");
        Object iActivityManagerObj = Proxy.newProxyInstance(sysIActivityManager.getClass().getClassLoader(),sysIActivityManager.getClass().getInterfaces(), new MyHandler(sysIActivityManager));
        mInstanceField.setAccessible(true);
        mInstanceField.set(gDefaultField,iActivityManagerObj);//设置代理的IActivityManager
        gDefaultField.set(clazz,singleTonClazz);

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
