package soft.znmd.library;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ListenerInvocationHandler implements InvocationHandler {

    private Object mTarget = null;

    private HashMap<String, Method> methodHashMap = new HashMap<>();

    public ListenerInvocationHandler(Object object) {
        mTarget = object;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        String method_name = method.getName();
        method = methodHashMap.get(method_name);
        if(null != method) {
            method.setAccessible(true);
            return method.invoke(mTarget, objects);
        } else {
            return null;
        }
    }

    public void addMethod(String key, Method value) {
        methodHashMap.put(key, value);
    }
}
