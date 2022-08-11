package com.example.labyball.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.labyball.R;


public class BallView extends View {

    // Pencil that will allow the picture to be drawn
    private final Paint picturePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Bitmap that will be drawn
    private Bitmap ballPicture;

    // X and Y position in pixels, initialized for a ball at the left top of the screen.
    private double posTop = 0;
    private double posLeft = 0;

    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    // onSizeChanged is called each time the size view changes, here only once because the activity
    // in which the view is displayed has been stacked in portrait mode (see manifest file).
    // onSizeChanged is called before onDraw.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // Loading picture into bitmap
        ballPicture = BitmapFactory.decodeResource(getResources(), R.drawable.bille);

        // Setting left and top position for a picture at the left and top of the view
        posLeft = 0;
        posTop = 0;
    }

    // onDraw is called by the system each time the view component is displayed or updated
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Drawing the picture in the middle of the view component
        canvas.drawBitmap(ballPicture, (int)posLeft, (int)posTop, picturePainter);


    }

    // performClick method for a better accessibility
    @Override
    public boolean performClick() {
        super.performClick();

        // The old view redraws with the new view (onDraw method is called)
        invalidate();
        return true;
    }

    public void setPosition(double posLeft, double posTop) {
        this.posLeft = posLeft;
        this.posTop = posTop;
    }

    public double getPosTop() {
        return posTop;
    }

    public double getPosLeft() {
        return posLeft;
    }

    public int getBallDiameter() {
        return ballPicture.getWidth();
    }
}
