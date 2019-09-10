package soft.wl.ioc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import soft.znmd.library.annotations.InjectContentView;
import soft.znmd.library.annotations.InjectControl;
import soft.znmd.library.annotations.InjectOnClick;
import soft.znmd.library.annotations.InjectOnLongClick;

@InjectContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    private static final String TAG = "InjectManager";

    @InjectControl(value = R.id.text_view, text = "天生我材必有用")//InjectControl可设置text属性
    private TextView mTextView;

    @InjectControl(R.id.button_view)//InjectControl也可只绑定控件
    private Button mButton;

    @InjectControl(value = R.id.image_view, image_resource = R.raw.img_inject)//InjectControl也可只绑定控件,并设置图片的Resource
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @InjectOnClick(R.id.button_view)
    public void onButtonClick(View view) {
        Log.d(TAG, "onButtonClick: ");
    }

    @InjectOnLongClick(R.id.button_view)
    public boolean onButtonLongClick(View view) {
        Log.d(TAG, "onButtonLongClick: ");
        return true;
    }
}
