package com.example.labyball.controller;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.example.labyball.R;
import com.example.labyball.modele.Bean.Hurdle;
import com.example.labyball.view.LabyrinthView;
import java.util.ArrayList;

/**
 * Class name    : BallManager
 *
 * Description   : calculates the ball position in function of acceleration data provided by the
 *                 phone accelerometer and updates it every PERIOD_REFRESH_BALL_MS ms
 * @version 1.0
 *
 * @author Laurine Bailly
 */
public class LabyrinthManager {

    // Length and height of a labyrinth brick in dpi
    public final static int BRICK_HEIGHT = 30;
    public final static int BRICK_LENGTH = 60;
    // Screen space taken by the labyrinth in shapes of rectangles
    private final ArrayList<Hurdle> hurdles = new ArrayList<>();

    /**
     * LabyrinthManager constructor
     * Inflates the view layout_labyrinth, sends the list of bricks to be drawn to LabyrinthView,
     * defines the labyrinth hurdles
     *
     * @param       context context of the activity it is called from
     * @param       rootParent parent of the layont_ball
     */
    public LabyrinthManager(Context context, ViewGroup rootParent) {
        ArrayList<Rect> bricks = new ArrayList<>();
        int nbBricks = 0;
        // Number of pixels in 1dip to convert dip to pixels and  keep the same labyrinth
        // proportions on any screen
        float pixelsInOneDip = 1f;

        /// Display and getting the labyrinthView
        LayoutInflater layoutInflaterContext = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout rootLabyrinth = (FrameLayout)layoutInflaterContext.inflate(R.layout.layout_labyrinth, rootParent, true);
        LabyrinthView labyrinthView = rootLabyrinth.findViewById(R.id.id_labyrinthView);

        // Getting pixelsInOneDip
        double density = 160*context.getResources().getDisplayMetrics().density;
        if(density != 0) {
            pixelsInOneDip = (float)(density/160);
        }

        // Sending the list of bricks to be drawn by the labyrinthView
        // TODO
        nbBricks = 1;
        for(int i = 0; i < nbBricks; i++) {
            Rect brick = new Rect((int)(100*pixelsInOneDip), (int)(100*pixelsInOneDip), (int)((100 + BRICK_LENGTH)*pixelsInOneDip), (int) ((100 + BRICK_HEIGHT)*pixelsInOneDip));
            Rect brick1 = new Rect((int)(200*pixelsInOneDip), (int)(200*pixelsInOneDip), (int)((200 + BRICK_LENGTH)*pixelsInOneDip), (int) ((200 + BRICK_HEIGHT)*pixelsInOneDip));
            Rect brick2 = new Rect((int)(300*pixelsInOneDip), (int)(300*pixelsInOneDip), (int)((300 + BRICK_LENGTH)*pixelsInOneDip), (int) ((300 + BRICK_HEIGHT)*pixelsInOneDip));
            hurdles.add(new Hurdle(brick));
            hurdles.add(new Hurdle(brick1));
            hurdles.add(new Hurdle(brick2));
            bricks.add(brick);
            bricks.add(brick1);
            bricks.add(brick2);
        }
        labyrinthView.setBrickList(bricks);
    }

    /**
     * hurdles getter
     *
     * @return   labyrinth hurdles list
     */
    public ArrayList<Hurdle> getLabyrinthHurdles() {
        return hurdles;
    }
}
