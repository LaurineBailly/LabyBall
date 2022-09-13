package com.example.labyball.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.labyball.R;
import java.util.ArrayList;

public class BoardGameView extends View {

    // Stroke of the labyrinth bricks in dip
    public static final int STROKE_WIDTH = 2;

    // Set to true when the labyrinth has to be drawn again (only when the size of the view changes)
    private boolean labyrinthToDraw = true;

    // Pencil for the filling of the labyrinth brick
    private final Paint brickPainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Pencil for the labyrinth brick stroke
    private final Paint brickStrokePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Labyrinth bricks in a shape of a rectangle to be drawn
    private ArrayList<Rect> bricks;

    // Pencil that will allow the ball picture to be drawn
    private final Paint picturePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Ball bitmap that will be drawn
    private Bitmap ballPicture;

    // Ball X and Y position in pixels, initialized for a ball at the left top of the screen.
    // The point at the coordinates(posLeft, posTop) is the one of the left top corner of the ball
    // picture.
    private double posTop = 0;
    private double posLeft = 0;

    // Ball diameter initialized at 60dpi
    private int ballDiameter = 60;

    public BoardGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // Labyrinth brick (filling: color secondaryColor / stroke : BRICK_STROKE dpi, color "borders")
        brickPainter.setStyle(Paint.Style.FILL);
        brickPainter.setStrokeWidth(0);
        brickPainter.setColor(getResources().getColor(R.color.secondaryColor));
        brickStrokePainter.setStyle(Paint.Style.STROKE);
        brickStrokePainter.setStrokeWidth(STROKE_WIDTH);
        brickStrokePainter.setColor(getResources().getColor(R.color.secondaryDarkColor));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ballPicture = BitmapFactory.decodeResource(getResources(), R.drawable.bille);
        ballPicture = Bitmap.createScaledBitmap(ballPicture, ballDiameter, ballDiameter, true);
        labyrinthToDraw = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(labyrinthToDraw) {

            // Drawing the labyrinth
            for(Rect brick : bricks) {
                canvas.drawRect(brick, brickPainter);
                canvas.drawRect(brick, brickStrokePainter);
            }
            labyrinthToDraw = false;
        }
        canvas.drawBitmap(ballPicture, (int)posLeft, (int)posTop, picturePainter);

    }

    @Override
    public boolean performClick() {
        super.performClick();
        invalidate();
        return true;
    }

    /**
     * Calculates and sets posLeft and posTop from the center coordinates of the ball.
     *
     * @param   posX position in pixels from the right of the view of the center of the ball
     *
     * @param   posY position in pixels from the top of the view of the center of the ball
     */
    public void setPosition(double posX, double posY) {
        posLeft = posX - ballDiameter/2;
        posTop = posY - ballDiameter/2;
    }

    public void setLabyrinthBricksList(ArrayList<Rect> bricksList) {
        bricks = new ArrayList<>(bricksList);
    }

    /**
     * Calculates and returns the position in pixels, from the right of the view, of the center of
     * the ball
     *
     * @return  The position on pixels from the right of the view of the center of the ball
     */
    public double getPosX() {
        return posLeft + ballDiameter/2;
    }

    /**
     * Calculates and returns the position in pixels, from the top of the view, of the center of
     * the ball
     *
     * @return  The position on pixels from the top of the view of the center of the ball
     */
    public double getPosY() {
        return posTop + ballDiameter/2;
    }

    public void setBallDiameter(int ballDiameter) {
        this.ballDiameter = ballDiameter;
    }
}
