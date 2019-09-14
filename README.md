# Android IOC架构和Annotation的用法  
&emsp;&emsp;控制反转（Inversion of Control，英文缩写为IoC）是一个重要的面向对象编程的法则来削减计算机程序的耦合问题，也是轻量级的Spring框架的核心。 控制反转一般分为两种类型，依赖注入（Dependency Injection，简称DI）和依赖查找（Dependency Lookup），依赖注入应用比较广泛，本工程主要运用的是依赖注入，支持注入布局，控件，事件，技术点主要是：***Annotation*** 和 ***反射***
### 注解有什么好处
&emsp;&emsp;比如说，给一个按钮添加onClick事件，传统方式：
```java
private Button mButton;
mButton = (Button) findViewById(R.id.button_view);
mButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //逻辑处理
    }
});
```
&emsp;&emsp;使用IOC和注解方式
```java
@InjectOnClick(R.id.button_view)
public void onButtonClick(View view) {//事件名可自定义
    //逻辑处理
}
```
&emsp;&emsp;对比发现，只需要一行注解，就可以替代传统方式的三四行代码，我们只需要关注具体的逻辑，如果控件多的话，我们可以少写很多重复代码，这样既提高了开发效率，也降低了开发难度
### 相关知识点
##### 注解
&emsp;&emsp;java Annotation 的组成中，有 3 个非常重要的主干类，它们分别是：
```java
public interface Annotation {

    boolean equals(Object obj);

    int hashCode();

    String toString();

    Class<? extends Annotation> annotationType();//返回注解本身的class对象
}
```
&emsp;&emsp;Annotation 通用定义
```java
@Documented
@Target(ElementType.TYPE)//指定注解修饰的对象类型
@Retention(RetentionPolicy.RUNTIME)//指定注解的保存策略
public @interface MyAnnotation1 {
    String[] value() default "unknown";//可加默认实现
}
```
&emsp;&emsp;ElementType的类型
```java
public enum ElementType {
    TYPE,               /* 类、接口（包括注释类型）或枚举声明  */

    FIELD,              /* 字段声明（包括枚举常量）  */

    METHOD,             /* 方法声明  */

    PARAMETER,          /* 参数声明  */

    CONSTRUCTOR,        /* 构造方法声明  */

    LOCAL_VARIABLE,     /* 局部变量声明  */

    ANNOTATION_TYPE,    /* 注释类型声明  */

    PACKAGE             /* 包声明  */
}
```
&emsp;&emsp;RetentionPolicy的类型
```java
public enum RetentionPolicy {
    SOURCE,    /* Annotation信息仅存在于编译器处理期间，编译器处理完之后就没有该Annotation信息了  */

    CLASS,     /* 编译器将Annotation存储于类对应的.class文件中。默认行为  */

    RUNTIME    /* 编译器将Annotation存储于class文件中，并且可由JVM读入 */
}
```
##### 反射
&emsp;&emsp;JAVA反射机制是在运行状态中，对于任意一个实体类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意方法和属性；这种动态获取信息以及动态调用对象方法的功能称为java语言的反射机制。  
&emsp;&emsp;获取Class类对象, 有三种方式:
```java
Class.forName("完整的包名+类名");
类名称.class;
类对象.getClass();
```
&emsp;&emsp;获取Field成员变量:
```java
Field getField(String fieldName); //获取权限为public的成员变量,包含从父类继承的成员变量
Field[] getFields();//获取所有权限为public的成员变量，包含从父类继承的成员变量
Field getDeclaredField(String fieldName); //根据指定的成员变量名，只获取本类的成员变量，与权限无关，
Field[] getDeclaredFields();//只获取本类的所有成员变量
field.setAccessible(true);//private变量默认不允许反射，通过该函数解除限制
field.set(Object obj,  Object… args);//将对象obj的成员变量field的值设置为args
```
&emsp;&emsp;获取Method成员方法:
```java
Method getMethod(String methodName, Class… parameterTypes);//获取包含从父类继承的权限为public的成员方法
Method[]getMethods();////获取包含从父类继承的权限为public的所有成员方法
Method getDeclaredMethod(String methodName, Class… parameterTypes);//获取本类的成员方法
Method[] getDeclaredMethods();//获取本类所有的成员方法
method.setAccessible(true);//private方法默认不允许反射，通过该函数解除限制
method.invoke(Object obj, Object… args)//调用obj的method成员方法
```
### 关键代码
&emsp;&emsp;注解关键代码主要分为三个功能，注入布局文件，注入控件（可设置Resource和Text），注入事件
##### 1. 新建一个Android Library类型的注入管理类，如：InjectManager
```java
public class InjectManager {
    public static void inject(Activity activity) {
        injectLayout(activity);
        injectControl(activity);
        injectEvent(activity);
    }
    
    private static void injectLayout(Activity activity) {
        //逻辑处理
    }
    
    private static void injectControl(Activity activity) {
        //逻辑处理
    }
    
    private static void injectEvent(Activity activity) {
        //逻辑处理
    }
}
```
##### 2. 新建一个annotations包，在该包下，建一个注解类
&emsp;&emsp;方法：选择包名－右键，New一个Java类－类的kind属性选择：Annotation-输入类名称－点击OK
```java
@Target({ElementType.TYPE})//注解作用于对象之上
@Retention(RetentionPolicy.RUNTIME)//运行时注解
public @interface InjectContentView {
    int value();//返回注解的值
}
```
使用方法(示例代码)：
```java
@InjectContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {
}
```
##### ３. 实现injectLayout方法
```java
  private static void injectLayout(Activity activity) {

      Class<? extends Activity> clazz = activity.getClass();
      InjectContentView contentView = clazz.getAnnotation(InjectContentView.class);//获取InjectContentView对象
      
      if(null != contentView) {
          int layout = contentView.value();//获取注解的值，示例中该值：　R.layout.activity_main
          try {
              Method method = clazz.getMethod("setContentView", int.class);//反射，获取父类的setContentView方法
              if(null != method) {
                  method.invoke(activity, layout);//调用该方法，设置布局
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  }
```
##### ４. 新建一个控件的注解类，名称（可自定义）：　injectControl，实现如下：
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectControl {

    public final int INVALID_VALUE = -1;

    int value();//控件的ResourceID
    String text() default "";//控件显示的文字，此属性暂时只支持：TextView，Button，默认值：　空字符串
    int image_resource() default INVALID_VALUE;//可以设置图片ResourceID，此属性暂时只支持：ImageView，默认值：　－１（无效）
}
```
使用方法(示例代码)：
```java
@InjectControl(value = R.id.text_view, text = "天生我材必有用")//InjectControl可设置text属性
private TextView mTextView;
```
##### 5. 实现injectControl方法
```java
private static void injectControl(Activity activity) {

    Class<? extends Activity> clazz = activity.getClass();
    Field[] fields = clazz.getDeclaredFields();//获取当前类的所有属性，不包括父类的属性

    for(Field field : fields) {
    
        InjectControl injectView = field.getAnnotation(InjectControl.class);//遍历，获取有注解InjectControl的属性
        
        if(null != injectView) {
            int control_id = injectView.value();//获取注解的值
            String text = injectView.text();//获取注解的值
            int img_id = injectView.image_resource();//获取注解的值

            try {
                Method method = clazz.getMethod("findViewById", int.class);//通过反射获取findViewById方法
                if(null != method) {
                    Object view = method.invoke(activity, control_id);
                    field.setAccessible(true);//属性可能是private，通过该方法，强行设置可访问
                    field.set(activity, view);//将findViewById查找到的view赋值给view
                    if(null != text) {
                        if(!text.isEmpty()) {//需要设置文言
                            Method method1 = view.getClass().getMethod("setText", CharSequence.class);
                            if(null != method1) {
                                method1.invoke(view, text);
                            }
                        }
                    }
                    if(InjectControl.INVALID_VALUE != img_id) {//需要设置图片
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
```
##### 6. 新建一个注入事件的注解类（onClick事件）  
&emsp;&emsp;点击事件的base注解类
```java
@Target({ElementType.ANNOTATION_TYPE})//作用于注解之上的注解
@Retention(RetentionPolicy.RUNTIME)
public @interface ClickBase {
    String listenerSetter();//设置点击事件的方法，如：setOnClickListener 或者 setOnLongClickListener
    Class<?> listenerType();//设置监听器的类型，如：View.OnClickListener.class 或者 View.OnLongClickListener.class
    String callBackListener();//设置回调方法，如：onClick 或者 onLongClick
}
```
&emsp;&emsp;ClickBase 注解类使用示例
```java
//示例一
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ClickBase(listenerSetter = "setOnClickListener", listenerType = View.OnClickListener.class, callBackListener = "onClick")
public @interface InjectOnClick {
    int[] value();
}

//示例二
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ClickBase(listenerSetter = "setOnLongClickListener", listenerType = View.OnLongClickListener.class, callBackListener = "onLongClick")
public @interface InjectOnLongClick {
    int[] value();
}
```
&emsp;&emsp;InjectOnClick 和 InjectOnLongClick注解类使用示例
```java
    @InjectOnClick(R.id.button_view)
    public void onButtonClick(View view) {
        Log.d(TAG, "onButtonClick: ");
    }

    @InjectOnLongClick(R.id.button_view)
    public boolean onButtonLongClick(View view) {
        Log.d(TAG, "onButtonLongClick: ");
        return true;
    }
```
##### 7. 实现injectEvent方法
```java
    private static void injectEvent(Activity activity) {
    
        Class<? extends Activity> clazz = activity.getClass();

        Method[] methods = clazz.getDeclaredMethods();//获取activity声明得所有方法

        for (Method method : methods) {//遍历所有方法
            Annotation[] annotations = method.getAnnotations();//方法可能有多个注解
            if(null != annotations) {//遍历方法的所有注解
                for (Annotation annotation :annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();//获取注解类
                    if(null != annotationType) {
                        ClickBase eventBase = annotationType.getAnnotation(ClickBase.class);//获取注解类的ClickBase（作用于注解之上）注解
                        if(null != eventBase) {//注解存在，即method有实现ClickBase的注解
                            String listenerSetter = eventBase.listenerSetter();
                            Class<?> listenerType = eventBase.listenerType();
                            String callBackListener = eventBase.callBackListener();

                            try {
                                Method methodValue = annotationType.getDeclaredMethod("value");//获取InjectOnClick 或者 InjectOnLongClick 的注解值
                                int[] view_ids = (int[]) methodValue.invoke(annotation);

                                ListenerInvocationHandler handler = new ListenerInvocationHandler(activity);//使用动态代理，ListenerInvocationHandler 的实现后续讲解
                                handler.addMethod(callBackListener, method);

                                Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[] {listenerType}, handler);

                                for(int view_id : view_ids) {
                                    View view = activity.findViewById(view_id);//获取注解ID值对应的view
                                    Method setter = view.getClass().getMethod(listenerSetter, listenerType);
                                    setter.invoke(view, listener);//设置监听器，参数是实现动态代理的listener 
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
```
&emsp;&emsp;动态代理的ListenerInvocationHandler实现如下
```java
public class ListenerInvocationHandler implements InvocationHandler {

    private Object mTarget = null;

    private HashMap<String, Method> methodHashMap = new HashMap<>();//HashMap存储key：监听器的回调，如onClick或者onLongClick，存储value:标注注解InjectOnClick 或者 InjectOnLongClick 的函数

    public ListenerInvocationHandler(Object object) {
        mTarget = object;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        String method_name = method.getName();//获取系统回调方法，如：onClick或者onLongClick
        method = methodHashMap.get(method_name);//获取用户自定义实现点击事件的方法，并赋值给method 
        if(null != method) {
            method.setAccessible(true);
            return method.invoke(mTarget, objects);//调用取用户自定义实现点击事件的方法
        } else {
            return null;
        }
    }

    public void addMethod(String key, Method value) {
        methodHashMap.put(key, value);
    }
}
```
##### 8. 调用InjectManager实现注入功能控制
```java
public class BaseActivity extends AppCompatActivity {//MainActivity继承BaseActivity
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InjectManager.inject(this);
    }
}
```
