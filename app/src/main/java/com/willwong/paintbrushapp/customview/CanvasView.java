package com.willwong.paintbrushapp.customview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;

import android.graphics.PorterDuffXfermode;
import android.os.Vibrator;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.willwong.paintbrushapp.R;

import java.util.ArrayList;

/**
 * Created by WillWong 01/13/19
 * Drawing canvas that captures user touch motion events and displays it on the screen
 */
public class CanvasView extends View {

    // Holds color for paint brush, eraser, and background
    private Paint mPaint, mEraser, canvasPaint;
    // Records the path drawn
    private Path mPath;
    // Default brush color
    private int mDrawColor;
    // Holds the color for the background canvas
    private int mBackgroundColor;
    //
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private boolean eraserMode = false;
    private ArrayList<Path> undoPathList = new ArrayList<>();
    private ArrayList<Path> pathList = new ArrayList<>();

    private Vibrator mVibrator;
    private Context mContext;

    /*public CanvasView(Context context) {
        this(context, null);
    }*/

    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        mBackgroundColor = ResourcesCompat.getColor(getResources(),
                R.color.whitebackground, null);
        mDrawColor = ResourcesCompat.getColor(getResources(),
                R.color.Black, null);

        // Holds the path we are currently drawing.
        mPath = new Path();
        // Set up the paint with which to draw.
        mPaint = new Paint();
        mPaint.setColor(mDrawColor);
        // Smoothes out edges of what is drawn without affecting shape.
        mPaint.setAntiAlias(true);
        // Dithering affects how colors with higher-precision device
        // than the are down-sampled.
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(10);
        // Eraser attributes
        mEraser = new Paint();
        mEraser.setColor(Color.parseColor("#FFFFFF"));
        mEraser.setAntiAlias(true);
        mEraser.setDither(true);
        mEraser.setStyle(Paint.Style.STROKE);
        mEraser.setStrokeJoin(Paint.Join.ROUND);
        mEraser.setStrokeCap(Paint.Cap.ROUND);
        mEraser.setStrokeWidth(mPaint.getStrokeWidth()+10);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }
    @Override
    protected void onSizeChanged(int width, int height,
                                 int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
    // Create bitmap, create canvas with bitmap, fill canvas with color.
        mBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    // Fill the Bitmap with the background color.
        mCanvas.drawColor(mBackgroundColor);


    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(mBitmap, 0, 0, canvasPaint);

        if (!eraserMode) {
            mCanvas.drawPath(mPath, mPaint);
        } else {
            mCanvas.drawPath(mPath, mEraser);
        }


    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

    // Invalidate() is inside the case statements because there are many
    // other types of motion events passed into this listener,
    // and we don't want to invalidate the view for those.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);

                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();

                break;
            default:
                // Do nothing.
        }
        return true;
    }
    // Variables for the latest x,y values,
    // which are the starting point for the next path.
    private float mX, mY;
    // Don't draw every single pixel.
    // If the finger has has moved less than this distance, don't draw.
    private static final float TOUCH_TOLERANCE = 4;

    // The following methods factor out what happens for different touch events,
    // as determined by the onTouchEvent() switch statement.
    // This keeps the switch statement
    // concise and and easier to change what happens for each event.
    private void touchStart(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        // Reset the path so it doesn't get drawn again.
        mPath.reset();
    }

    // Get the width of the screen
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    // Get the height of the screen
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
    // Change color of paint for brush;
    public void setColor(int color) {
        mPaint.setColor(color);

    }
    // Enables the ability to draw.
    public void setBrushPaint() {
        eraserMode = false;
        invalidate();
    }
    // Enables the eraser.
    public void setEraserPaint() {
        eraserMode = true;
        invalidate();
    }
    // Clears canvas and starts a new canvas.
    public void newCanvas() {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }
    // Method for undoing a the most recent drawn path. Incomplete.
    public void undoPath() {
        if (pathList.size() > 0)
        {
            undoPathList.add(pathList.remove(pathList.size()-1));
            invalidate();
        }
    }
    // Method for redrawing the last undone path. Incomplete.
    public void redoPath() {
        if (undoPathList.size()> 0) {
            pathList.add(undoPathList.remove(undoPathList.size()-1));
            invalidate();
        }
    }
    // Sets Background of the bitmap
    public void setBitMap(Bitmap bg) {
        mBitmap = bg;
        invalidate();
    }

}
