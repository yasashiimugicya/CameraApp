package com.example.cameraapp02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera.Face;
import android.view.View;

@SuppressWarnings("deprecation")
public class CameraOverlayView extends View {
    private Paint paint;
    private Face[] faces;

    public CameraOverlayView(Context context) {
        super(context);

        initPaint(); //initPaintメソッドの呼び出し
    }

    private void initPaint() {
        //Paintクラスのオブジェクトを生成
        paint = new Paint();

        //アンチエイリアスを設定
        paint.setAntiAlias(true);

        //ディザをかける
        paint.setDither(true);

        //色は緑に設定
        paint.setColor(Color.GREEN);

        //アルファ値（半透過指定）を設定
        paint.setAlpha(128);

        //文字サイズを設定
        paint.setTextSize(50);

        //スタイルは図形の内部を塗りつぶし、かつ輪郭線を描画するように設定
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //メンバ変数facesがnullならば処理をせず返す。
        if (faces == null) {
            return;
        }

        for (Face face : faces) {
            //配列から取り出したデータがnullならば、何もせずに反復処理を継続する。
            if (face == null) {
                continue;
            }
            //Matrixクラスのオブジェクトを生成する。
            Matrix matrix = new Matrix();

            //拡大率を設定する。
            matrix.postScale(getWidth() / 2000f, getHeight() / 2000f);

            //タッチ操作で画像を動かすように設定する。
            matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);

            //キャンバスの現在の設定を保存する。
            int saveCount = canvas.save();

            //Matrixを操作して、キャンバスの移動、拡大縮小、回転などを設定する。
            canvas.concat(matrix);

            //検出した顔の範囲（矩形）を元に四角形を描画する。
            canvas.drawRect(face.rect, paint);

            //保存した設定を指定して復元する。
            canvas.restoreToCount(saveCount);
        }
    }

    public void setFaces(Face[] faces) {
        //引数のfacesをメンバ変数のfacesに設定する。
        this.faces = faces;

        //再描画する。
        invalidate();
    }

}
