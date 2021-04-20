package com.example.cameraapp02;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity
        implements Callback, PictureCallback, FaceDetectionListener, OnClickListener {
    private CameraOverlayView coView;
    private SurfaceView cameraView;
    private Camera camera;
    private Button shutter;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CameraOverlayViewのオブジェクトを生成する。
        coView = new CameraOverlayView(this);

        //LinearLayout.LayoutParamsのオブジェクトを生成する。
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //activity_main.xmlとCameraOverlayViewを重ねて表示する。
        addContentView(coView, params);

        //SurfaceViewのオブジェクトを取得する。
        cameraView = (SurfaceView)findViewById(R.id.surfaceView1);

        //シャッター用ボタンのオブジェクトを取得しイベントリスナ登録する。
        shutter = (Button)findViewById(R.id.button1);
        shutter.setOnClickListener(this);

        //シャッター用ボタンを非表示に設定する。
        shutter.setVisibility(View.INVISIBLE);

        //メッセージ表示用TextViewのオブジェクトを取得する。
        message = (TextView)findViewById(R.id.textView1);

        //メッセージ表示用TextViewを非表示に設定する。
        message.setVisibility(View.INVISIBLE);

        //SurfaceHolderを取得する。
        SurfaceHolder holder = cameraView.getHolder();

        //MainActivityをコールバック・オブジェクトとして登録する。
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //Cameraのオブジェクトを生成する。
        camera = Camera.open();

        //CameraクラスにFaceDetectionListenerのリスナ登録をする。
        camera.setFaceDetectionListener(this);

        //顔認識操作を開始する。
        camera.startFaceDetection();

        //CameraのプレビューにSurfaceHolderを設定する。
        try {
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //カメラのプレビューを停止
        camera.stopPreview();

        //カメラのパラメータ（設定値）を取得
        Camera.Parameters params = camera.getParameters();

        //パラメータにプレビューサイズ（width、height）を設定
        params.setPreviewSize(width, height);

        //カメラにパラメータを設定
        camera.setParameters(params);

        //カメラのプレビューを開始
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //顔認識操作を終了
        camera.stopFaceDetection();

        //カメラのプレビューを停止
        camera.stopPreview();

        //カメラを解放
        camera.release();

        //カメラをnullにする
        camera = null;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        //byte配列として撮影された画像データをBitmapに変換して取得
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        //Bitmap画像をSDカードに保存
        ContentResolver resolver = getContentResolver();
        MediaStore.Images.Media.insertImage(resolver, bitmap, "", null);

        //Cameraのプレビューを開始
        camera.startPreview();
    }

    @Override
    public void onFaceDetection(Face[] faces, Camera camera) {
        //CameraOverlayViewクラスに配列facesを設定する。
        coView.setFaces(faces);

        if(faces.length == 0) {
            shutter.setVisibility(View.INVISIBLE); //ボタン非表示
            message.setVisibility(View.INVISIBLE); //テキスト非表示
        } else {
            shutter.setVisibility(View.VISIBLE); //ボタン表示
            message.setVisibility(View.VISIBLE); //テキスト表示
        }
    }

    @Override
    public void onClick(View v) {
        //カメラで撮影
        camera.takePicture(null, null, this);
    }
}
