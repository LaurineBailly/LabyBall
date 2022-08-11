package com.example.labyball.controller;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.labyball.R;
import com.example.labyball.modele.Bean.Hurdle;
import com.example.labyball.view.LabyrinthView;

import java.util.ArrayList;

public class LabyrinthManager {

    // Length and hight of a labyrith brick in dpi
    public final static int BRICK_HEIGHT = 30;
    public final static int BRICK_LENGTH = 60;

    // List of labyrinth hurdles. The borders of the labyrinth could be drawn with hurdles.
    private final ArrayList<Hurdle> hurdles = new ArrayList<>();

    // Constructor that inflates the view layout_labyrinth, sends the list of the labyrinth hurdles
    // to the ballView and the bricks (rectangles) to be drawn to the labyrinthView.
    // It needs the context of the activity it is called from to get the displayMatrics for getting
    // the number of pixels there are in 1dip and to inflate the view.
    // It also needs the root layout of the activity, which is the parent of the layout_labyrinth.
    public LabyrinthManager(Context context, ViewGroup rootParent) {

        // DISPLAY THE LABYRINTHVIEW

        // Inflating the view described in layout_ball.xml file (ball, area it moves in, text views
        // displaying the coordinates)
        LayoutInflater layoutInflaterContext = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout rootLabyrinth = (FrameLayout)layoutInflaterContext.inflate(R.layout.layout_labyrinth, rootParent, true);

        // Getting the labyrinthView
        LabyrinthView labyrinthView = rootLabyrinth.findViewById(R.id.id_labyrinthView);

        // DEFINING THE NUMBER OF PIXELS IN 1DIP
        // To keep the same labyrinth proportions on any type screen

        // Number of pixels in 1 dip
        // 1 dip = 1 pixel for a density of 160
        // Initialised at 1 which is the value for a 160 density screen
        float pixelsInOneDip = 1f;

        // DisplayMetrics structure describing general information about a display, such as its size,
        // density, and font scaling.
        DisplayMetrics screenMetrics = context.getResources().getDisplayMetrics();

        // getResources().getDisplayMetrics().density is the scaling factor for the Density
        // Independent Pixel unit, where one DIP is one pixel on an approximately 160 dpi screen
        // density of the screen (pixels per inch). DENSITY_DEFAULT = 160.
        // getting the number of pixels in 1 inch
        double density = 160*screenMetrics.density;

        // How many pixels in 1 density independent pixels
        if(density != 0) {
            pixelsInOneDip = (float)(density/160);
        }

        // SENDING THE LIST OF BRICKS TO BE DRAWN BY THE LABYRINTHVIEW

        // Each brick is a hurdle, hurdles will be assembled later
        ArrayList<Rect> bricks = new ArrayList<>();

        // Getting the number of parts in the labyrinth
        int nbParts = 1;
        for(int i = 0; i < nbParts; i++) {

            // Just for test
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

        // Assembly of the hurdles
        // Empty for now

        // Sending the bricks list to labyrinthView
        labyrinthView.setBrickList(bricks);
    }

    public ArrayList<Hurdle> getLabyrinthHurdles() {
        return hurdles;
    }
}
