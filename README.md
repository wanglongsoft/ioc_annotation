# Android IOC架构和Annotation的用法  
&ensp;&ensp;&ensp;&ensp;控制反转（Inversion of Control，英文缩写为IoC）是一个重要的面向对象编程的法则来削减计算机程序的耦合问题，也是轻量级的
Spring框架的核心。 控制反转一般分为两种类型，依赖注入（Dependency Injection，简称DI）和依赖查找（Dependency Lookup），依赖注入应用比较广泛，
本工程主要运用的是依赖注入，支持注入布局，控件，事件，技术点主要是：***Annotation***和***反射***
### 注解有什么好处
比如说，给一个按钮添加onClick事件，传统方式：
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
使用IOC和注解方式
```java
@InjectOnClick(R.id.button_view)
public void onButtonClick(View view) {//事件名可自定义
    //逻辑处理
}
```
对比发现，只需要一行注解，就可以替代传统方式的三四行代码，我们只需要关注具体的逻辑，如果控件多的话，我们可以少写很多重复代码，这样既提高了开发效率，也
降低了开发难度
### 相关知识点
##### 注解

##### 反射

### 关键代码
&ensp;&ensp;&ensp;&ensp;注解关键代码主要分为三个功能，注入布局文件，注入控件（可设置Resource和Text），注入事件
##### 1. 新建一个Module,类型是：Android Library,在该Module中实现一个注入管理类，如：InjectManager
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
&ensp;&ensp;&ensp;&ensp;方法：选择包名－右键，New一个Java类－类的kind属性选择：Annotation-输入类名称－点击OK
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
##### 5. 实现injectControl方法
