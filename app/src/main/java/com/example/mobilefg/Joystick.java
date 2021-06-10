package com.example.mobilefg;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import static android.graphics.Color.GRAY;
import static android.graphics.Color.GREEN;
import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class Joystick extends View {

    private Paint circlePaint;
    private Paint bgPaint;
    private Paint borderPaint;

    private OnJoystickValueChange valueChangeListener;

    public Joystick(Context context) {
        super(context);
        initPaints();
    }

    public Joystick(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initPaints();
    }

    private void initPaints() {
        /* initialize objects */
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint();

        /* initialize each paint individually */
        circlePaint.setColor(GREEN);
        circlePaint.setStyle(Paint.Style.FILL);

        bgPaint.setColor(GRAY);
        bgPaint.setStyle(Paint.Style.FILL);

        borderPaint.setColor(Color.rgb(100, 100, 100));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(10f);
    }

    private int height;
    private int width;

    private int circleRadius;
    private float basecx, basecy;
    private float cx, cy;

    private float borderWidth;
    private float halvedBorderWidth;

    // the maximal radius on the x,y axes
    private float xMaxRadius, yMaxRadius;

    // whether the circle is currently being used
    private boolean onFocus;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int xpad = getPaddingLeft() + getPaddingRight();
        int ypad = getPaddingTop() + getPaddingBottom();

        width = w - xpad;
        height = h - ypad;

        int minDim = Math.min(width, height);

        // the border size
        borderWidth = Math.min(10f, minDim / 10f);
        borderPaint.setStrokeWidth(borderWidth);
        halvedBorderWidth = borderWidth / 2;

        /*
         * make sure there is a space from where the inner circle is
         * to the borders
         */
        circleRadius = Math.min(120, minDim / 4);

        // position circle in middle
        basecx = cx = width / 2f;
        basecy = cy = height / 2f;

        // maximal radius of the control circle
        xMaxRadius = width / 2f - circleRadius;
        yMaxRadius = height / 2f - circleRadius;

        // reset circle being used
        onFocus = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw the background
        canvas.drawOval(0, 0, width, height, bgPaint);

        // draw the borders
        canvas.drawOval(halvedBorderWidth, halvedBorderWidth,
                width - halvedBorderWidth, height - halvedBorderWidth, borderPaint);

        // draw circle on top of all
        canvas.drawCircle(cx, cy, circleRadius, circlePaint);
    }

    public void setValueChangeListener(OnJoystickValueChange valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();

        switch (action) {
            case ACTION_DOWN: {
                // if not in middle, leave
                float diffx = Math.abs(x - basecx);
                float diffy = Math.abs(y - basecy);

                if (diffx > circleRadius || diffy > circleRadius)
                    break;

                // if in middle
                onFocus = true;
                break;
            }
            case ACTION_MOVE: {
                // if not moving, do nothing
                if (!onFocus)
                    break;

                // don't allow leaving the ellipse
                if (!isWithinEllipse(x, y))
                    break;


                // set position if all is good
                setPosition(x, y);
                break;
            }

            case ACTION_CANCEL:
            case ACTION_UP: {
                if (onFocus) {
                    /* return the circle to the middle */
                    onFocus = false;
                    setPosition(basecx, basecy);
                }
                break;
            }
        }

        return true;
    }

    /**
     * Tests whether a point is within the main ellipse
     * @param x the x of the point
     * @param y the y of the point
     * @return true if within, false otherwise
     */
    public boolean isWithinEllipse(float x, float y) {
        float dx = x - basecx;
        float dy = y - basecy;

        int a = width / 2 - circleRadius;
        int b = height / 2 - circleRadius;
        float diff = (dx * dx) / (a * a) + (dy * dy) / (b * b);
        return diff <= 1f;
    }


    /**
     * Sets the position of the circle and notifies the event listener if there is
     * @param cx the x of the circle
     * @param cy the y of the circle
     */
    private void setPosition(float cx, float cy) {
        // set position
        this.cx = cx;
        this.cy = cy;

        // redraw
        invalidate();

        /* adjustments because of circle size */
        int widthRange = width - 2 * circleRadius;
        int heightRange = height - 2 * circleRadius;

        cx -= circleRadius;
        cy -= circleRadius;

        // get difference normalized to [-1, 1]
        float diffx = 2*cx / widthRange - 1;
        float diffy = 2*cy / heightRange - 1;

        // notify events
        if (valueChangeListener != null)
            valueChangeListener.onChanged(diffx, diffy);
    }
}
