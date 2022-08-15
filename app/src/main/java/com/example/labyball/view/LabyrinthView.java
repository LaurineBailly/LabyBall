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
    // Bricks in a shape of a rectangle to be drawn
    private ArrayList<Rect> bricks;

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

        // Drawing the labyrinth
        for(Rect brick : bricks) {
            canvas.drawRect(brick, brickPainter);
            canvas.drawRect(brick, brickStrokePainter);
        }
    }

    public void setBrickList(ArrayList<Rect> bricksList) {
        bricks = new ArrayList<>(bricksList);
    }
}
