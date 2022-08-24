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

/**
 * Class name    : LabyrinthView
 *
 * Description   : draws the labyrinth
 *
 * @version 1.0
 *
 * @author Laurine Bailly
 */
public class LabyrinthView extends View {

    // Stroke of the brick in dip
    public static final int STROKE_WIDTH = 2;
    // Pencil for the filling of the brick
    private final Paint brickPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
    // Pencil for the brick stroke
    private final Paint brickStrokePainter = new Paint(Paint.ANTI_ALIAS_FLAG);
    // Pencil for debug purposes
    private final Paint areaPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint areaStrokePainter = new Paint(Paint.ANTI_ALIAS_FLAG);
    // Bricks in a shape of a rectangle to be drawn
    private ArrayList<Rect> bricks;
    // For debug
    private ArrayList<Rect> screenAreasRect;
    // Debug purposes : true if the user wants to see the areas
    private boolean showAreas = false;

    public LabyrinthView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Brick (filling: color secondaryColor / stroke : BRICK_STROKE dpi, color "borders")
        brickPainter.setStyle(Paint.Style.FILL);
        brickPainter.setStrokeWidth(0);
        brickPainter.setColor(getResources().getColor(R.color.secondaryColor));
        brickStrokePainter.setStyle(Paint.Style.STROKE);
        brickStrokePainter.setStrokeWidth(STROKE_WIDTH);
        brickStrokePainter.setColor(getResources().getColor(R.color.secondaryDarkColor));

        // Debug purposes
        areaPainter.setStyle(Paint.Style.FILL);
        areaPainter.setStrokeWidth(0);
        areaPainter.setColor(getResources().getColor(R.color.primaryLightColor));
        areaStrokePainter.setStyle(Paint.Style.STROKE);
        areaStrokePainter.setStrokeWidth(5);
        areaStrokePainter.setColor(getResources().getColor(R.color.secondaryDarkColor));

        // Debug purposes
        if(showAreas) {

            // Drawing the areas
            for(Rect hurdle : screenAreasRect) {
                canvas.drawRect(hurdle, areaPainter);
                canvas.drawRect(hurdle, areaStrokePainter);
            }
        }
        else {

            // Drawing the labyrinth
            for(Rect brick : bricks) {
                canvas.drawRect(brick, brickPainter);
                canvas.drawRect(brick, brickStrokePainter);
            }
        }
    }

    public void setBrickList(ArrayList<Rect> bricksList) {
        bricks = new ArrayList<>(bricksList);
    }

    public void setScreenAreasList(ArrayList<Rect> screenAreasRectList) {
        screenAreasRect = new ArrayList<>(screenAreasRectList);
    }

    // Debug purposes
    @Override
    public boolean performClick() {
        super.performClick();
        showAreas = !showAreas;
        invalidate();
        return true;
    }
}
