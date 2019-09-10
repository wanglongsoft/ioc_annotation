package soft.znmd.library.annotations;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ClickBase(listenerSetter = "setOnLongClickListener", listenerType = View.OnLongClickListener.class, callBackListener = "onLongClick")
public @interface InjectOnLongClick {
    int[] value();
}
