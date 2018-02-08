package com.takusemba.rtmppublisher;

import android.Manifest;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RtmpPublisher implements Publisher, SurfaceTexture.OnFrameAvailableListener,
        CameraSurfaceRenderer.OnRendererStateChangedListener, LifecycleObserver {


    private GLSurfaceView glView;
    private CameraSurfaceRenderer renderer;
    private CameraClient camera;
    private Streamer streamer;

    private String url;
    private int width;
    private int height;
    private int audioBitrate;
    private int videoBitrate;
    private AppCompatActivity mActivity;
    private static final String TAG = "RtmpPublisher";

    RtmpPublisher(AppCompatActivity activity,
                  GLSurfaceView glView,
                  String url,
                  int width,
                  int height,
                  int audioBitrate,
                  int videoBitrate,
                  CameraMode mode,
                  PublisherListener listener) {

        activity.getLifecycle().addObserver(this);

        this.glView = glView;
        this.url = url;
        this.width = width;
        this.height = height;
        this.audioBitrate = audioBitrate;
        this.videoBitrate = videoBitrate;
        mActivity = activity;

        this.camera = new CameraClient(activity, mode);
        this.streamer = new Streamer();
        this.streamer.setMuxerListener(listener);

        glView.setEGLContextClientVersion(2);
        renderer = new CameraSurfaceRenderer();
        renderer.addOnRendererStateChangedLister(streamer.getVideoHandlerListener());
        renderer.addOnRendererStateChangedLister(this);

        glView.setRenderer(renderer);
        glView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

    }

    @Override
    public void switchCamera() {
        camera.swap();
    }

    @Override
    public void startPublishing() {
        streamer.open(url, width, height);
        glView.queueEvent(new Runnable() {
            @Override
            public void run() {
                // EGL14.eglGetCurrentContext() should be called from glView thread.
                final EGLContext context = EGL14.eglGetCurrentContext();
                glView.post(new Runnable() {
                    @Override
                    public void run() {
                        // back to main thread
                        streamer.startStreaming(context, width, height, audioBitrate, videoBitrate);
                    }
                });
            }
        });
    }

    @Override
    public void stopPublishing() {
        if (streamer.isStreaming()) {
            streamer.stopStreaming();
        }
    }

    @Override
    public boolean isPublishing() {
        return streamer.isStreaming();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume(LifecycleOwner owner) {
        new RxPermissions(mActivity).request(Manifest.permission.CAMERA)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.d(TAG, "onNext: ");
                        if (aBoolean) {
                            Camera.Parameters params = camera.open();
                            final Camera.Size size = params.getPreviewSize();
                            glView.onResume();
                            glView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    renderer.setCameraPreviewSize(size.width, size.height);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");

                    }
                });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause(LifecycleOwner owner) {
        if (camera != null) {
            camera.close();
        }
        glView.onPause();
        glView.queueEvent(new Runnable() {
            @Override
            public void run() {
                renderer.pause();
            }
        });
        if (streamer.isStreaming()) {
            streamer.stopStreaming();
        }
    }

    @Override
    public void onSurfaceCreated(SurfaceTexture surfaceTexture) {
        surfaceTexture.setOnFrameAvailableListener(this);
        camera.startPreview(surfaceTexture);
    }

    @Override
    public void onFrameDrawn(int textureId, float[] transform, long timestamp) {
        // no-op
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        glView.requestRender();
    }
}
