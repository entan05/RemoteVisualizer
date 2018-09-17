package jp.team.e_works.remotevisualizerclient.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class ControlButton extends View {
    private Bitmap mImage = null;

    private Paint mPaint;

    public ControlButton(Context context) {
        super(context);

        initialize();
    }

    public ControlButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initialize();
    }

    public ControlButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initialize();
    }

    private void initialize() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
    }

    public void setImage(@DrawableRes int drawable) {
        mImage = BitmapFactory.decodeResource(getResources(), drawable);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);
        if (mImage != null) {
            int left = (getWidth() / 2) - (mImage.getWidth() / 2);
            int top = (getHeight() / 2) - (mImage.getHeight() / 2);
            canvas.drawBitmap(mImage, left, top, mPaint);
        }
    }
}
