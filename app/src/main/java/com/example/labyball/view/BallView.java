/*
 * Class name    : BallView
 *
 * Description   : draws the ball
 */

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

    // Ball diameter
    private int ballDiameter = 60;

    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        ballPicture = BitmapFactory.decodeResource(getResources(), R.drawable.bille);
        ballPicture = Bitmap.createScaledBitmap(ballPicture, ballDiameter, ballDiameter, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(ballPicture, (int)posLeft, (int)posTop, picturePainter);
    }

    @Override
    public boolean performClick() {
        super.performClick();
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

    public void setBallDiameter(int ballDiameter) {
        this.ballDiameter = ballDiameter;
    }
}
