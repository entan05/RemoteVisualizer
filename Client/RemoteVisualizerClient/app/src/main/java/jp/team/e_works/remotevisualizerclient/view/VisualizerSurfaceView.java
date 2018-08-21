package jp.team.e_works.remotevisualizerclient.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VisualizerSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;

    public VisualizerSurfaceView(Context context) {
        super(context);

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    public void drawBitmap(Bitmap bitmap) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        {
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);

            canvas.drawColor(Color.BLACK);
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = mSurfaceHolder.lockCanvas();
        {
            canvas.drawColor(Color.BLACK);
        }
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mScreenWidth = width;
        mScreenHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
