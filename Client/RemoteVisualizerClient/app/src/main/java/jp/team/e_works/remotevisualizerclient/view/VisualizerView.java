package jp.team.e_works.remotevisualizerclient.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import jp.team.e_works.remotevisualizerclient.Const;

public class VisualizerView extends View {
    private int mOriginBitmapWidth = -1;
    private int mOriginBitmapHeight = -1;

    private Rect mDrawBitmapRect;

    private Paint mPaint;

    private Bitmap mDrawBitmap = null;

    private OnTouchEventListener mTouchListener = null;

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

        mDrawBitmapRect = new Rect();
    }

    public void setOnTouchEventListener(OnTouchEventListener listener) {
        mTouchListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);
        if (mDrawBitmap != null) {
            int left = (getWidth() / 2) - (mDrawBitmap.getWidth() / 2);
            int top = (getHeight() / 2) - (mDrawBitmap.getHeight() / 2);
            mDrawBitmapRect.set(left, top, left + mDrawBitmap.getWidth(), top + mDrawBitmap.getHeight());
            canvas.drawBitmap(mDrawBitmap, left, top, mPaint);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchListener != null) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                if (isTouchBitmap(event)) {
                    int touchType;
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            touchType = Const.TOUCH_TYPE_DOWN;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            touchType = Const.TOUCH_TYPE_MOVE;
                            break;

                        case MotionEvent.ACTION_UP:
                            touchType = Const.TOUCH_TYPE_UP;
                            break;

                        default:
                            return false;
                    }
                    int x = convertScreenXtoImageX((int) event.getX());
                    int y = convertScreenYtoImageY((int) event.getY());
                    mTouchListener.onEvent(touchType, x, y);
                    return true;
                }
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isTouchBitmap(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        return mDrawBitmapRect.contains(x, y);
    }

    private int convertScreenXtoImageX(int screenX) {
        double scale = ((double) (screenX - mDrawBitmapRect.left)) / mDrawBitmapRect.width();
        return (int) (mOriginBitmapWidth * scale);
    }

    private int convertScreenYtoImageY(int screenY) {
        double scale = ((double) (screenY - mDrawBitmapRect.top)) / mDrawBitmapRect.height();
        return (int) (mOriginBitmapHeight * scale);
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
            true);

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

    public interface OnTouchEventListener {
        void onEvent(int touchType, int x, int y);
    }
}
