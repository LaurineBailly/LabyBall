package com.example.LabyrinthBall.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.LabyrinthBall.R;
import com.example.LabyrinthBall.modele.BorderLine;

import java.util.ArrayList;

public class BallView extends View {

    // Pencil that will allow the picture to be drawn
    private final Paint picturePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Bitmap that will be drawn
    private Bitmap ballPicture;

    // Picture position from left and top of the view component
    private double posTop = 0;
    private double posLeft = 0;

    // Variables stocking view and ballview graphic properties
    private int ballHeight = 0;
    private int ballWidth = 0;

    // Velocity of the ball in pixels per second
    private double xVelocity = 0;
    private double yVelocity = 0;

    // Factor to multiply the acceleration of the ball by
    private static final float FACTOR_ACCELERATION = 0.005f;

    // Period after which the ball position is updated in seconds
    private double periodUpdatePosSec = 0;

    // Number of pixels in 1 mm
    private static double pixelsInOneMm = 160/25.4;

    // The list of hurdles the ball will hit
    ArrayList<BorderLine> hurdles = new ArrayList<>();

    // Component view constructor with the physical layout
    // Getting the screenMetrics of the activity that instantiated a BallView object
    public BallView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // DisplayMetrics structure describing general information about a display, such as its size,
        // density, and font scaling.
        DisplayMetrics screenMetrics = context.getResources().getDisplayMetrics();

        // getResources().getDisplayMetrics().density is the scaling factor for the Density
        // Independent Pixel unit, where one DIP is one pixel on an approximately 160 dpi screen
        // density of the screen (pixels per inch). DENSITY_DEFAULT = 160.
        // getting the number of pixels in 1 inch
        double density = 160*screenMetrics.density;

        if(density != 0) {
            // 25.4mm = 1 inch.
            // Getting the number of pixels in 1mm.
            pixelsInOneMm = density/25.4;
        }
    }

    // Set new position values of the ball
    public void setPosition(double aX, double aY) throws IllegalArgumentException {

        // periodUpdatePosSec not set
        if(periodUpdatePosSec == 0) {
            throw new IllegalArgumentException("The update period of the view has not been set.");
        }

        // Labyrinth not set
        if(hurdles.size() <= 4) {
            throw new IllegalArgumentException("Their is a problem on the hurdles list.");
        }

        // Conversion of the acceleration data (meter/s2 --> pixels/s2)
        // factor_acceleration set to the acceleration
        // The X position axis and X acceleration axis on the device are opposite. We put the
        // acceleration axis in the same direction of the position and therefore velocity ones.
        double xAcceleration = -aX*1000*pixelsInOneMm*FACTOR_ACCELERATION;
        double yAcceleration = aY*1000*pixelsInOneMm*FACTOR_ACCELERATION;

        // Acceleration components corresponding to the position delta due to acceleration only

        // (1/2)*Ax(t)*t^2
        // where t is time and Ax is the accelerations on X axis (pixels/s2)
        double xMoveAx = 0.5*xAcceleration*periodUpdatePosSec*periodUpdatePosSec;

        // (1/2)*Ay(t)*t^2
        // where t is time and Ay is the accelerations on Y axis (pixels/s2)
        double yMoveAy = 0.5*yAcceleration*periodUpdatePosSec*periodUpdatePosSec;

        // Stocking the position of the ball before it is updated
        double oldPosLeft = posLeft;
        double oldPosTop = posTop;

        // Calculating a theoretical position in X
        // Sx = Sx(t-1) + xMoveAx + Ux(t-1)*t
        // where Sx is position, Ux is initial velocity, t is time.
        posLeft = posLeft + xMoveAx + xVelocity*periodUpdatePosSec;

        // Calculating a theoretical position in Y
        // Sy = Sy(t-1) + yMoveAy + Uy(t-1)*t
        // where Sy is position, Uy is initial velocity, t is time.
        posTop = posTop + yMoveAy + yVelocity*periodUpdatePosSec;

        // hurdles not detected yet in x or y
        boolean xDetectedHurdle = false;
        boolean yDetectedHurdle = false;

        // For each borderline in the hurdles tab
        for(BorderLine hurdle : hurdles) {

            // If a hurdle has been detected in x and y (corner of the screen), no need to check
            // anymore
            if(yDetectedHurdle && xDetectedHurdle) {
                break;
            }
            else {
                char hurdleType = hurdle.getLineType();

                // A hurdle is a line with one coordinate on the axis parallel to this line
                float valueAxisParallel = hurdle.getValueAxisParallel();

                // And two coordinates on the other
                float valueAxisPerpendicular1 = hurdle.getValueAxisPerpendicular1();
                float valueAxisPerpendicular2 = hurdle.getValueAxisPerpendicular2();

                // Sorting valueAxisPerpendicular1 and valueAxisPerpendicular2 for valueAxisPerpendicular1
                // smaller than valueAxisPerpendicular2
                float val = valueAxisPerpendicular1;
                if(valueAxisPerpendicular1 > valueAxisPerpendicular2) {
                    valueAxisPerpendicular1 = valueAxisPerpendicular2;
                    valueAxisPerpendicular2 = val;
                }

                // If a hurdle has not been detected in Y yet
                if(!yDetectedHurdle) {

                    // If the line is a bottom side of the object (phone edge or brick)
                    if(hurdleType == BorderLine.BOTTOM) {

                        // If the Y pos of the ball is calculated above the line
                        // And if the old Y pos of the ball was underneath the line (the ball comes from
                        // somewhere on the bottom of the object)
                        // And if the X pos of the ball is between the X coordinates of the line
                        if((posTop < valueAxisParallel) && (oldPosTop >= valueAxisParallel) && ((posLeft > valueAxisPerpendicular1 - ballWidth) && (posLeft < valueAxisPerpendicular2))) {

                            // The ball touches the line
                            posTop = valueAxisParallel;
                            yDetectedHurdle = true;
                        }
                    }

                    // else if the line is a top side of the object (phone edge or brick)
                    else if(hurdleType == BorderLine.TOP) {

                        // If the Y pos of the ball is calculated underneath the line
                        // And if the old Y pos of the ball was above the line (the ball comes from
                        // somewhere on the top of the object)
                        // And if the X pos of the ball is between the X coordinates of the line
                        if((posTop > valueAxisParallel - ballHeight) && (oldPosTop <= valueAxisParallel - ballHeight) && ((posLeft > valueAxisPerpendicular1 - ballWidth) && (posLeft < valueAxisPerpendicular2))) {

                            // The ball touches the line
                            posTop = valueAxisParallel - ballHeight;
                            yDetectedHurdle = true;
                        }
                    }
                }

                // If a hurdle has not been detected in X yet
                if(!xDetectedHurdle) {

                    // If the line is a right side of the object (phone edge or brick)
                    if(hurdleType == BorderLine.RIGHT) {

                        // If the X pos of the ball is calculated at the right of the line
                        // And if the old X pos of the ball was at the left of the line (the ball comes from
                        // somewhere on the left of the object)
                        // And if the Y pos of the ball is between the Y coordinates of the line
                        if((posLeft < valueAxisParallel) && (oldPosLeft >= valueAxisParallel) && ((posTop > valueAxisPerpendicular1 - ballHeight) && (posTop < valueAxisPerpendicular2))) {

                            // The ball touches the line
                            posLeft = valueAxisParallel;
                            xDetectedHurdle = true;
                        }
                    }

                    // If the line is a left side of the object (phone edge or brick)
                    else if(hurdleType == BorderLine.LEFT) {

                        // If the X pos of the ball is calculated at the left of the line
                        // And if the old X pos of the ball was at the right of the line (the ball comes from
                        // somewhere on the right of the object)
                        // And if the Y pos of the ball is between the Y coordinates of the line
                        if((posLeft > valueAxisParallel - ballWidth) && (oldPosLeft <= valueAxisParallel - ballWidth) && ((posTop > valueAxisPerpendicular1 - ballHeight) && (posTop < valueAxisPerpendicular2))) {

                            // The ball touches the line
                            posLeft = valueAxisParallel - ballWidth;
                            xDetectedHurdle = true;
                        }
                    }
                }
            }
        }

        // If no hurdle detected in Y, y velocity calculation
        if(!yDetectedHurdle) {
            yVelocity = yAcceleration*periodUpdatePosSec + yVelocity;
        }

        // Else the ball will bounce in Y
        else {
            yVelocity = -yVelocity;
        }

        // If no hurdle detected in X, x velocity calculation
        if(!xDetectedHurdle) {
            xVelocity = xAcceleration*periodUpdatePosSec + xVelocity;
        }

        // Else the ball will bounce in X
        else {
            xVelocity = -xVelocity;
        }
    }

    // onSizeChanged is called each time the size view changes, here only once because the activity
    // in which the view is displayed has been stacked in portrait mode (see manifest file).
    // onSizeChanged is called before onDraw.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        // Loading picture into bitmap
        ballPicture = BitmapFactory.decodeResource(getResources(), R.drawable.bille);

        // Updating the values of ballview graphic properties
        ballHeight = ballPicture.getHeight();
        ballWidth = ballPicture.getWidth();
        float viewTop = getTop();
        float viewBottom = getBottom();
        float viewLeft = getLeft();
        float viewRight = getRight();

        // Adding the screen edges to the hurdle list
        // A top screen border becomes a top type line because it is at the top of the phone edge
        // Indeed, the lines are relative to objects like bricks and phone edges, not to the view
        hurdles.add(new BorderLine(BorderLine.TOP, viewBottom, viewRight, viewLeft));
        hurdles.add(new BorderLine(BorderLine.BOTTOM, viewTop, viewRight, viewLeft));
        hurdles.add(new BorderLine(BorderLine.LEFT, viewRight, viewTop,  viewBottom));
        hurdles.add(new BorderLine(BorderLine.RIGHT, viewLeft, viewTop, viewBottom));

        // Getting left and top position for a picture at the left and top of the view
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

    public double getPosTopMm() {
        return posTop/pixelsInOneMm;
    }

    public double getPosLeftMm() {
        return posLeft/pixelsInOneMm;
    }

    public void setPeriodUpdatePosSec(double periodUpdatePosMs) throws IllegalArgumentException {
        this.periodUpdatePosSec = periodUpdatePosMs*0.001;
        if(periodUpdatePosMs <= 0) {
            throw new IllegalArgumentException("The period to update the view can not be nul or negative");
        }
    }

    // The labyrinth borders are added to the obstacles
    public void setLabyrinthBorders(ArrayList<BorderLine> labyrinthBorders) {
        hurdles.addAll(labyrinthBorders);
    }
}