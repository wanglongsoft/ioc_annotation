package soft.znmd.library.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectControl {

    public final int INVALID_VALUE = -1;

    int value();
    String text() default "";
    int image_resource() default INVALID_VALUE;//可以设置图片ResourceID
}
