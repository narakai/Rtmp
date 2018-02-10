package www.clem.com.rtmp;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.takusemba.rtmppublisher.Publisher;
import com.takusemba.rtmppublisher.PublisherListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by laileon on 2018/2/10.
 */

public class PushActivity extends AppCompatActivity implements PublisherListener {

    GLSurfaceView glView;
    Button mButton;
    ImageView mImageView;
    String url;
    private static final String TAG = "PushActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        Intent intent = getIntent();
        url = intent.getStringExtra("rtmp");
        Log.d(TAG, "onCreate: " + url);

        glView = findViewById(R.id.surface_view);
        mButton = findViewById(R.id.toggle_publish);
        mImageView = findViewById(R.id.toggle_camera);

        final Publisher publisher = new Publisher.Builder(PushActivity.this)
                .setGlView(glView)
                .setUrl(url)
                .setSize(Publisher.Builder.DEFAULT_WIDTH, Publisher.Builder.DEFAULT_HEIGHT)
                .setAudioBitrate(Publisher.Builder.DEFAULT_AUDIO_BITRATE)
                .setVideoBitrate(Publisher.Builder.DEFAULT_VIDEO_BITRATE)
                .setCameraMode(Publisher.Builder.DEFAULT_MODE)
                .setListener(PushActivity.this)
                .build();

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publisher.switchCamera();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RxPermissions(PushActivity.this).request(Manifest.permission.RECORD_AUDIO)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "onSubscribe: ");
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    if (!publisher.isPublishing()) {
                                        publisher.startPublishing();
                                        mButton.setText(R.string.stop_publishing);
                                        Toast.makeText(PushActivity.this, "start publishing", Toast.LENGTH_LONG).show();
                                    } else {
                                        finish();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: ");

                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: ");
//                                publisher.startPublishing();
                            }
                        });
            }
        });


    }


    @Override
    public void onStarted() {

    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onFailedToConnect() {

    }
}
