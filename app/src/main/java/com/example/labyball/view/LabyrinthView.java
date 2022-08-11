package com.example.labyball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.labyball.R;

import java.util.ArrayList;

public class LabyrinthView extends View {

    // Bricks in a shape of a rectangle to be drawn
    private ArrayList<Rect> bricks;

    // Stroke of the brick in dip
    public static final int STROKE_WIDTH = 2;

    // Pencil for the filling of the brick
    private final Paint brickPainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    // Pencil for the brick stroke
    private final Paint brickStrokePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LabyrinthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

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
        brickStrokePainter.setColor(getResources().getColor(R.color.secondaryDarkColor));

        // Drawing the labyrinth from the parts list
        // Soon to be for (Part part : partsList)
        for(Rect brick : bricks) {

            // Drawing the filling
            canvas.drawRect(brick, brickPainter);

            // Drawing the stroke
            canvas.drawRect(brick, brickStrokePainter);
        }
    }

    public void setBrickList(ArrayList<Rect> bricksList) {
        bricks = new ArrayList<>(bricksList);
    }
}
