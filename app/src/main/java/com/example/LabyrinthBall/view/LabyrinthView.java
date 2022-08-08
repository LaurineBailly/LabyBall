package com.example.LabyrinthBall.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.LabyrinthBall.R;
import com.example.LabyrinthBall.modele.BorderLine;
import com.example.LabyrinthBall.modele.Brick;

import java.util.ArrayList;


public class LabyrinthView extends View {

    // Pencil for the filling of the brick
    private final Paint brickPainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Pencil for the brick stroke
    private final Paint brickStrokePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // The list of bricks
    // Soon to become a double entry tab of labyrinth parts (bricks and paths)
    private final ArrayList<Brick> partsList = new ArrayList<>();

    // The list of the borders of the labyrinth
    private final ArrayList<BorderLine> labyrinthBorders = new ArrayList<>();

    // Stroke of the brick in dip
    private static final int STROKE_WIDTH = 2;

    // BallView that will receive the labytinth borders
    // Soon thanks to which we will get the paths' widths
    private BallView ballView;

    public LabyrinthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // DisplayMetrics structure describing general information about a display, such as its size,
        // density, and font scaling.
        DisplayMetrics screenMetrics = context.getResources().getDisplayMetrics();

        // getResources().getDisplayMetrics().density is the scaling factor for the Density
        // Independent Pixel unit, where one DIP is one pixel on an approximately 160 dpi screen
        // density of the screen (pixels per inch). DENSITY_DEFAULT = 160.
        // getting the number of pixels in 1 inch
        double density = 160*screenMetrics.density;

        // Number of pixels in 1 dip
        // 1 dip = 1 pixel for a density of 160
        // Initialised at 1 which is the value for a 160 density screen
        float pixelsInOneDip = 1f;

        // How many pixels in 1 density independent pixels
        if(density != 0) {
            pixelsInOneDip = (float)(density/160);
        }

        // Setting to the Brick class the length and height of 1 brick in pixels
        Brick.setBricksProperties(Brick.LENGTH*pixelsInOneDip, Brick.HEIGHT*pixelsInOneDip);

        // Building the list of labyrinth parts
        // Soon from a file and paths will be included
        // Getting the number of parts in the labyrinth
        int nbParts = 1;
        for(int i = 0; i < nbParts; i++) {

            // Just for test
            // Soon to be : read 1 char, add 1 part
            partsList.add(new Brick(300, 300));
            partsList.add(new Brick(200, 200));
            partsList.add(new Brick(400, 400));
        }

        // Building the list of the labyrinth borders
        // At this stage, no calculation cause their is a space between the bricks
        // In the future, labyrinthBorders should be, in most cases, smaller than bricksBordersList
        for(Brick brick : partsList) {
            labyrinthBorders.add(brick.getLeftBorder());
            labyrinthBorders.add(brick.getRightBorder());
            labyrinthBorders.add(brick.getTopBorder());
            labyrinthBorders.add(brick.getBottomBorder());
        }
    }

    // onSizeChanged is called each time the size view changes, here only once because the activity
    // in which the view is displayed has been stacked in portrait mode (see manifest file).
    // onSizeChanged is called before onDraw.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Giving the labyrinth borders to the ballView
        ballView.setLabyrinthBorders(labyrinthBorders);
    }

    // onDraw is called by the system each time the view component is displayed or updated
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Drawing characteristics of the brick's filling

        // Brick filling, color secondaryColor
        brickPainter.setStyle(Paint.Style.FILL);
        brickPainter.setStrokeWidth(0);
        brickPainter.setColor(getResources().getColor(R.color.secondaryColor));

        // Drawing characteristics of the brick's stroke
        // A brick stroke with a width of BRICK_STROKE dpi, color "borders"
        brickStrokePainter.setStyle(Paint.Style.STROKE);
        brickStrokePainter.setStrokeWidth(STROKE_WIDTH);
        brickStrokePainter.setColor(getResources().getColor(R.color.borders));

        // Drawing the labyrinth from the parts list
        // Soon to be for (Part part : partsList)
        for(Brick brick : partsList) {

            // Soon for each part in the labyrinth, if part instanceof brick

            // Getting brick properties
            float brickLeftPx = brick.getLeft();
            float brickRightPx = brick.getRight();
            float brickTopPx = brick.getTop();
            float brickBottomPx = brick.getBottom();

            // Drawing the filling
            canvas.drawRect(brickLeftPx, brickTopPx, brickRightPx, brickBottomPx, brickPainter);

            // Drawing the stroke
            canvas.drawRect(brickLeftPx, brickTopPx, brickRightPx, brickBottomPx, brickStrokePainter);
        }
    }

    // performClick method for a better accessibility
    @Override
    public boolean performClick() {
        super.performClick();

        // The old view redraws with the new view (onDraw method is called)
        invalidate();
        return true;
    }

    public void setBallView(BallView ballView){
        this.ballView = ballView;
    }
}
