package www.clem.com.rtmp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by laileon on 2018/2/7.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    EditText mEditText;
    Button mPush;
    Button mPull;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_select);

        mEditText = findViewById(R.id.et_rtmp_uri);
        mPush = findViewById(R.id.btn_push);
        mPull = findViewById(R.id.btn_pull);
        mPush.setTag(1);
        mPull.setTag(2);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch ((Integer) view.getTag()){
                    case 1:
                        Intent intent = new Intent(MainActivity.this, PushActivity.class);
                        intent.putExtra("rtmp", mEditText.getText().toString());
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent2 = new Intent(MainActivity.this, PullActivity.class);
                        intent2.putExtra("rtmp", mEditText.getText().toString());
                        startActivity(intent2);                        break;
                }
            }
        };

        mPush.setOnClickListener(listener);
        mPull.setOnClickListener(listener);

    }

}
