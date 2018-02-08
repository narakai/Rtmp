package www.clem.com.rtmp;

import android.Manifest;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.takusemba.rtmppublisher.Publisher;
import com.takusemba.rtmppublisher.PublisherListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by laileon on 2018/2/7.
 */

public class MainActivity extends AppCompatActivity implements PublisherListener {
    GLSurfaceView glView;
    Button mButton;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glView = findViewById(R.id.surface_view);
        mButton = findViewById(R.id.toggle_publish);

        final String url = "rtmp://119.23.19.90/live/livestream";

        final Publisher publisher = new Publisher.Builder(MainActivity.this)
                .setGlView(glView)
                .setUrl(url)
                .setSize(Publisher.Builder.DEFAULT_WIDTH, Publisher.Builder.DEFAULT_HEIGHT)
                .setAudioBitrate(Publisher.Builder.DEFAULT_AUDIO_BITRATE)
                .setVideoBitrate(Publisher.Builder.DEFAULT_VIDEO_BITRATE)
                .setCameraMode(Publisher.Builder.DEFAULT_MODE)
                .setListener(MainActivity.this)
                .build();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RxPermissions(MainActivity.this).request(Manifest.permission.RECORD_AUDIO)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "onSubscribe: ");
                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    Log.d(TAG, "start: ");
                                    Toast.makeText(MainActivity.this, "start", Toast.LENGTH_LONG).show();
                                    publisher.startPublishing();
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
