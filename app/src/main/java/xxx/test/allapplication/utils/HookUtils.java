package xxx.test.allapplication.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import xxx.test.allapplication.EmptyActivity;

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

    public void hookHandler(){
        try {
            Class<?> activityThreadClazz = Class.forName("android.app.ActivityThread");
            Field activityThreadField = activityThreadClazz.getDeclaredField("sCurrentActivityThread");
            activityThreadField.setAccessible(true);
            Object activityThreadInstance = activityThreadField.get(null);//获取ActivityThread对象

            Field handlerField = activityThreadClazz.getDeclaredField("mH");
            handlerField.setAccessible(true);
            Handler handlerInstance = (Handler) handlerField.get(activityThreadInstance);//获取Handler实例
            Field callbackField = Handler.class.getDeclaredField("mCallback");
            callbackField.setAccessible(true);
            callbackField.set(handlerInstance,new MyCallback(handlerInstance));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static int LAUNCH_ACTIVITY = 0;
    private class MyCallback implements Handler.Callback {
        private Handler handlerSys;
        public MyCallback(Handler handlerInstance){
            handlerSys = handlerInstance;
            try {
                Class<?> clazz = Class.forName("android.app.ActivityThread$H");
                Field field = clazz.getDeclaredField("LAUNCH_ACTIVITY");
                field.setAccessible(true);
                LAUNCH_ACTIVITY =  field.getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == LAUNCH_ACTIVITY){
                Object obj = msg.obj;//ActivityClientRecord对象 里面含有intent字段
                try {
                    Field field = obj.getClass().getDeclaredField("intent");
                    field.setAccessible(true);
                    Intent intent = (Intent) field.get(obj);
                    Intent realIntent = intent.getParcelableExtra("real_intent");
                    if(realIntent.getComponent() != null)
                    intent.setComponent(realIntent.getComponent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            handlerSys.handleMessage(msg);
            return true;
        }
    }
    private class MyHandler implements InvocationHandler {
        private Object iActivityManagerObj;
        MyHandler(Object iActivityManagerObj){
            this.iActivityManagerObj = iActivityManagerObj;
        }
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            int index = -1;
            Intent raw;
            if("startActivity".equals(method.getName())) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }
                if(index == -1)return method.invoke(iActivityManagerObj,args);
                raw = (Intent) args[index];
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("xxx.test.allapplication", EmptyActivity.class.getName()));
                intent.putExtra("real_intent",raw);
                args[index] = intent;//替换intent 欺骗ams
                return method.invoke(iActivityManagerObj,args);
            }

            return method.invoke(iActivityManagerObj,args);
        }
    }
}
