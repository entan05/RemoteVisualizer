package jp.team.e_works.remotevisualizerclient.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerView extends View {
    private int mOriginBitmapWidth = -1;
    private int mOriginBitmapHeight = -1;

    private Paint mPaint;

    private Bitmap mDrawBitmap = null;

    public VisualizerView(Context context) {
        super(context);

        initialize();
    }

    public VisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize();
    }

    public VisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize();
    }

    // 初期化処理
    private void initialize() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);
        if (mDrawBitmap != null) {
            canvas.drawBitmap(mDrawBitmap,
                    (getWidth() / 2) - (mDrawBitmap.getWidth() / 2),
                    (getHeight() / 2) - (mDrawBitmap.getHeight() / 2),
                    mPaint);
        }
    }

    /**
     * 描画するBitmapを更新する
     *
     * @param bitmap 描画するBitmap
     */
    public void updateDrawBitmap(Bitmap bitmap) {
        mOriginBitmapWidth = bitmap.getWidth();
        mOriginBitmapHeight = bitmap.getHeight();

        double rScale;
        // 横長
        if (mOriginBitmapWidth >= mOriginBitmapHeight) {
            rScale = (double) getWidth() / mOriginBitmapWidth;
        }
        // 縦長
        else {
            rScale = (double) getHeight() / mOriginBitmapHeight;
        }
        mDrawBitmap = Bitmap.createScaledBitmap(bitmap,
                (int) (mOriginBitmapWidth * rScale),
                (int) (mOriginBitmapHeight * rScale),
                false);

        invalidate();
    }

    /**
     * 描画しているBitmapを削除する
     */
    public void clearBitmap() {
        mOriginBitmapWidth = -1;
        mOriginBitmapHeight = -1;
        mDrawBitmap = null;

        invalidate();
    }
}
