package soft.znmd.library;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import soft.znmd.library.annotations.InjectContentView;
import soft.znmd.library.annotations.ClickBase;
import soft.znmd.library.annotations.InjectControl;

public class InjectManager {

    private static final String TAG = "InjectManager";

    public static void inject(Activity activity) {
        injectLayout(activity);
        injectControl(activity);
        injectEvent(activity);
    }

    private static void injectEvent(Activity activity) {

        Log.d(TAG, "injectEvent: ");

        Class<? extends Activity> clazz = activity.getClass();

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            if(null != annotations) {
                for (Annotation annotation :annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if(null != annotationType) {
                        ClickBase eventBase = annotationType.getAnnotation(ClickBase.class);
                        if(null != eventBase) {
                            String listenerSetter = eventBase.listenerSetter();
                            Class<?> listenerType = eventBase.listenerType();
                            String callBackListener = eventBase.callBackListener();

                            try {
                                Method methodValue = annotationType.getDeclaredMethod("value");
                                int[] view_ids = (int[]) methodValue.invoke(annotation);

                                ListenerInvocationHandler handler = new ListenerInvocationHandler(activity);
                                handler.addMethod(callBackListener, method);

                                Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[] {listenerType}, handler);

                                for(int view_id : view_ids) {
                                    View view = activity.findViewById(view_id);
                                    Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                                    setter.invoke(view, listener);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private static void injectControl(Activity activity) {

        Log.d(TAG, "injectControl: ");

        Class<? extends Activity> clazz = activity.getClass();

        Field[] fields = clazz.getDeclaredFields();

        for(Field field : fields) {
            InjectControl injectView = field.getAnnotation(InjectControl.class);
            if(null != injectView) {
                int control_id = injectView.value();
                String text = injectView.text();
                int img_id = injectView.image_resource();

                try {
                    Method method = clazz.getMethod("findViewById", int.class);
                    if(null != method) {
                        Object view = method.invoke(activity, control_id);
                        field.setAccessible(true);
                        field.set(activity, view);
                        if(null != text) {
                            if(!text.isEmpty()) {
                                Method method1 = view.getClass().getMethod("setText", CharSequence.class);
                                if(null != method1) {
                                    method1.invoke(view, text);
                                }
                            }
                        }
                        if(InjectControl.INVALID_VALUE != img_id) {
                            Method method1 = view.getClass().getMethod("setImageResource", int.class);
                            if(null != method1) {
                                method1.invoke(view, img_id);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void injectLayout(Activity activity) {

        Log.d(TAG, "injectLayout: ");

        Class<? extends Activity> clazz = activity.getClass();

        InjectContentView contentView = clazz.getAnnotation(InjectContentView.class);
        if(null != contentView) {
            int layout = contentView.value();
            try {
                Method method = clazz.getMethod("setContentView", int.class);
                if(null != method) {
                    method.invoke(activity, layout);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
