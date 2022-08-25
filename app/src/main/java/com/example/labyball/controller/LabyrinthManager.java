/*
 * Class name    : LabyrinthManager
 *
 * Description   : draws the labyrinth from a file and sends the screen areas filled by the
 *                 labyrinth
 */

package com.example.labyball.controller;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import com.example.labyball.R;
import com.example.labyball.bean.ScreenArea;
import com.example.labyball.view.LabyrinthView;
import java.util.ArrayList;

public class LabyrinthManager implements View.OnClickListener {

    // Length of a labyrinth brick in dpi
    public int brickLength = 0;
    public int brickHeight = 0;

    // A model of the labyrinth, '.' meaning space and '-' meaning brick
    public final static char[][] DATA_ENTRY = {
            {'.', '.', '.', '.', '.', '.', '.', '.', '-', '.', '.', '.', '.', '.', '.'},
            {'.', '-', '-', '-', '-', '-', '-', '.', '.', '.', '-', '-', '-', '-', '-'},
            {'.', '.', '-', '.', '.', '.', '-', '.', '.', '.', '-', '.', '.', '.', '.'},
            {'.', '-', '-', '.', '-', '.', '-', '-', '-', '-', '-', '.', '-', '-', '-'},
            {'.', '.', '.', '.', '-', '.', '.', '.', '.', '.', '-', '.', '-', '.', '-'},
            {'.', '-', '-', '-', '-', '-', '-', '-', '-', '-', '-', '.', '-', '.', '.'},
            {'.', '.', '.', '.', '-', '.', '-', '.', '.', '.', '.', '.', '-', '.', '.'},
            {'.', '-', '.', '.', '-', '.', '.', '.', '-', '-', '-', '-', '-', '.', '.'},
            {'-', '-', '-', '.', '-', '.', '-', '-', '-', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '-', '.', '-', '.', '-', '.', '-', '.', '-', '-', '-', '-', '-'},
            {'-', '.', '.', '.', '.', '.', '-', '.', '-', '.', '.', '-', '.', '.', '.'},
            {'-', '.', '-', '-', '-', '-', '-', '.', '-', '-', '.', '.', '.', '-', '.'},
            {'-', '.', '-', '.', '.', '.', '.', '.', '.', '-', '-', '-', '-', '-', '.'},
            {'-', '.', '.', '.', '-', '-', '-', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'-', '.', '-', '-', '-', '.', '-', '-', '-', '-', '-', '-', '-', '-', '.'},
            {'-', '.', '.', '.', '-', '.', '.', '.', '.', '.', '.', '-', '.', '.', '.'},
            {'-', '-', '-', '.', '-', '-', '-', '-', '-', '-', '.', '-', '.', '.', '-'},
            {'.', '.', '-', '.', '-', '.', '.', '.', '.', '.', '.', '-', '.', '.', '-'},
            {'-', '.', '-', '.', '.', '.', '.', '.', '.', '.', '.', '-', '.', '.', '-'},
            {'.', '.', '-', '.', '.', '-', '-', '-', '-', '-', '-', '-', '.', '.', '-'},
            {'.', '-', '-', '-', '-', '-', '.', '.', '.', '.', '.', '.', '.', '.', '-'},
            {'.', '-', '.', '.', '.', '.', '.', '-', '-', '-', '-', '-', '-', '-', '-'},
            {'.', '.', '.', '-', '-', '-', '.', '.', '-', '-', '-', '.', '.', '.', '.'},
            {'.', '-', '-', '-', '.', '.', '.', '.', '.', '.', '.', '.', '.', '-', '.'},
            {'.', '.', '.', '-', '.', '-', '-', '-', '-', '.', '-', '-', '.', '-', '-'},
            {'-', '.', '.', '-', '-', '-', '.', '-', '-', '-', '-', '.', '.', '.', '-'},
            {'-', '.', '.', '.', '.', '.', '.', '.', '.', '-', '.', '.', '-', '.', '-'},
            {'-', '-', '-', '-', '-', '.', '-', '.', '.', '-', '-', '-', '-', '-', '-'},
            {'.', '.', '-', '.', '.', '.', '-', '.', '.', '.', '.', '.', '.', '.', '.'},
            {'.', '.', '.', '.', '-', '-', '-', '-', '-', '-', '-', '-', '.', '-', '-'},
    };
    private final int NB_LINES_DATA_ENTRY = DATA_ENTRY.length;
    private final int NB_COLUMNS_DATA_ENTRY = DATA_ENTRY[0].length;

    // A callback
    private final LabyrinthListener labyrinthListener;

    // Labyrinth view
    private final LabyrinthView labyrinthView;

    /**
     * LabyrinthManager constructor
     * Inflates the view layout_labyrinth, sends the list of bricks to be drawn to LabyrinthView,
     * defines the screen Area taken by the labyrinth
     * @param       context context of the activity it is called from
     * @param       rootParent parent of the layout_ball
     * @param       labyrinthListener receiver subscribing to an object from this class
     */
    public LabyrinthManager(Context context, FrameLayout rootParent, LabyrinthListener labyrinthListener) {

        // For debug purposes : button that allows to show the labyrinth areas calculated
        final Button bt_test;

        this.labyrinthListener = labyrinthListener;

        /// Display and getting the labyrinthView
        LayoutInflater layoutInflaterContext = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflaterContext.inflate(R.layout.layout_labyrinth, rootParent, true);
        labyrinthView = rootParent.findViewById(R.id.id_labyrinthView);

        // For debug purposes
        bt_test = rootParent.findViewById(R.id.id_bt_test);
        bt_test.setOnClickListener(this);

        // When the labyrinth is fully loaded
        labyrinthView.addOnLayoutChangeListener((View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) -> {

            // Variables used to draw the labyrinth
            final ArrayList<Rect> bricks = new ArrayList<>();
            int posX = 0;

            // Lists used to define the areas of the screen taken by the labyrinth
            ArrayList<ScreenArea> labyRawAreas = new ArrayList<>();
            ArrayList<ScreenArea> labyVerticalAreas = new ArrayList<>();
            ArrayList<ScreenArea> labyAreas = new ArrayList<>();
            ArrayList<Integer> addedAreaIndexes = new ArrayList<>();

            // Calculating the length and height of a brick in pixels (right and bottom are the
            // right and bottom of labyrinthView in pixels)
            brickLength = right/NB_COLUMNS_DATA_ENTRY;
            brickHeight = bottom/NB_LINES_DATA_ENTRY;

            // Reading the content of dataEntry tab and building the content of bricks
            for(int j = 0; j < NB_COLUMNS_DATA_ENTRY; j++) {
                int posY = 0;

                // At j = 0, the poX of the brick is 0 (brick of the first column)
                if(j > 0) {
                    posX = posX + brickLength;
                }
                for(int k = 0; k < NB_LINES_DATA_ENTRY; k++) {

                    // At k = 0, the poY of the brick is 0 (brick of the first line)
                    if(k > 0) {
                        posY = posY + brickHeight;
                    }
                    if(DATA_ENTRY[k][j] == '-') {
                        Rect brick = new Rect(posX, posY, posX + brickLength, posY + brickHeight);
                        bricks.add(brick);

                        // Building an area for each brick and putting it into rawAreas list
                        // Indicating where there is a space around the brick (when a . char or
                        // the extremity of the labyrinth is found around it)
                        ScreenArea rawArea = new ScreenArea(brick);
                        if(k == 0 || DATA_ENTRY[k-1][j] == '.') {
                            rawArea.setSpaceTopLine(true);
                        }
                        if(k == NB_LINES_DATA_ENTRY-1 || DATA_ENTRY[k+1][j] == '.') {
                            rawArea.setSpaceBottomLine(true);
                        }
                        if(j == 0 || DATA_ENTRY[k][j-1] == '.') {
                            rawArea.setSpaceRightLine(true);
                        }
                        if(j == NB_COLUMNS_DATA_ENTRY-1 || DATA_ENTRY[k][j+1] == '.') {
                            rawArea.setSpaceLeftLine(true);
                        }
                        labyRawAreas.add(rawArea);
                    }
                }
            }

            // Labyrinth bricks ready to be displayed
            labyrinthView.setBrickList(bricks);

            // Building the vertical areas which consists in concatenating the bricks on top of each
            // other
            for(int i = 0; i < labyRawAreas.size(); i++) {
                ScreenArea tempArea = labyRawAreas.get(i);
                boolean concatenatedAreas = false;

                // If the area I is not already included in the areas concatenated
                if(!addedAreaIndexes.contains(i)) {

                    // This loop is broken when at least two areas are found to be concatenated or
                    // if the i and i+1 areas are not concatenable :
                    // we know that the areas are tidied up vertically, so no use to browse the list
                    // again.
                    for(int j = i; j < labyRawAreas.size(); j++) {
                        ScreenArea screenAreaJ = labyRawAreas.get(j);
                        if((j != i)
                                && (tempArea.getLeft() == screenAreaJ.getLeft())
                                && (tempArea.getRight() == screenAreaJ.getRight())
                                && (tempArea.getBottom() == screenAreaJ.getTop())
                                && (tempArea.isSpaceLeftLine() == screenAreaJ.isSpaceLeftLine())
                                && (tempArea.isSpaceRightLine() == screenAreaJ.isSpaceRightLine())) {
                            tempArea = ScreenArea.concatenate(tempArea, screenAreaJ);
                            addedAreaIndexes.add(j);
                            concatenatedAreas = true;
                        }
                        else if(concatenatedAreas || (j == i + 1)) {
                            break;
                        }
                    }
                    labyVerticalAreas.add(tempArea);
                }
            }

            // Building the final areas from the vertical areas, which consists in concatenating the
            // areas at the left of each other
            addedAreaIndexes.clear();
            for(int i = 0; i < labyVerticalAreas.size(); i++) {
                ScreenArea tempArea = labyVerticalAreas.get(i);

                // If the area I is not already included in the areas concatenated
                if(!addedAreaIndexes.contains(i)) {
                    for(int j = i; j < labyVerticalAreas.size(); j++) {
                        ScreenArea screenAreaJ = labyVerticalAreas.get(j);
                        if((j != i)
                                &&(tempArea.getTop() == screenAreaJ.getTop())
                                && (tempArea.getBottom() == screenAreaJ.getBottom())
                                && (tempArea.getRight() == screenAreaJ.getLeft())
                                && (tempArea.isSpaceTopLine() == screenAreaJ.isSpaceTopLine())
                                && (tempArea.isSpaceBottomLine() == screenAreaJ.isSpaceBottomLine())) {
                            tempArea = ScreenArea.concatenate(tempArea, screenAreaJ);
                            addedAreaIndexes.add(j);
                        }
                    }
                    labyAreas.add(tempArea);
                }
            }

            // For debug purposes : getting the rectangles defining the areas
            ArrayList<Rect> hurdleRectList = new ArrayList<>();
            for(ScreenArea screenArea : labyAreas) {
                hurdleRectList.add(screenArea.getRectangle());
            }

            // Ignoring the sides of the areas that touch each other
            for(int i = 0; i < labyAreas.size() - 1; i++) {
                for(int j = i + 1; j < labyAreas.size(); j++) {
                    ScreenArea screenAreaI = labyAreas.get(i);
                    ScreenArea screenAreaJ = labyAreas.get(j);
                    int iSaTop = screenAreaI.getTop();
                    int jSaTop = screenAreaJ.getTop();
                    int iSaBot =screenAreaI.getBottom();
                    int jSaBot = screenAreaJ.getBottom();
                    int iSaLeft = screenAreaI.getLeft();
                    int jSaLeft = screenAreaJ.getLeft();
                    int iSaRight = screenAreaI.getRight();
                    int jSaRight = screenAreaJ.getRight();
                    if((iSaBot == jSaTop)
                            && ((iSaLeft >= jSaLeft && iSaRight <= jSaRight)
                            || (jSaLeft >= iSaLeft && jSaRight <= iSaRight))) {
                        screenAreaI.ignoreBottomLine();
                        screenAreaJ.ignoreTopLine();

                        // For debug purposes : drawing a little rectangle at the level of the
                        // ignored side
                        hurdleRectList.add(new Rect(screenAreaI.getLeft(), screenAreaI.getBottom() - 5, screenAreaI.getRight(), screenAreaJ.getTop() + 5));
                    }
                    else if((iSaRight == jSaLeft)
                            && ((iSaTop >= jSaTop && iSaBot <= jSaBot)
                            || (jSaTop >= iSaTop && jSaBot <= iSaBot))) {

                        screenAreaI.ignoreRightLine();
                        screenAreaJ.ignoreLeftLine();

                        // For debug purposes : drawing a little rectangle at the level of the
                        // ignored side
                        hurdleRectList.add(new Rect(screenAreaI.getRight() - 5, screenAreaI.getTop(), screenAreaJ.getLeft() + 5, screenAreaI.getBottom()));
                    }
                }
            }

            // For debug purposes : sending to labyrinthView
            labyrinthView.setScreenAreasList(hurdleRectList);

            // borders sent to the receiver
            if(this.labyrinthListener != null) {
                this.labyrinthListener.onLabyrinthLoaded(brickHeight, labyAreas);
            }
        });
    }

    // For debug purposes : refreshing the view when bt_test is pressed
    @Override
    public void onClick(View v) {
        labyrinthView.performClick();
    }

    /**
     * Interface name : LabyrinthListener
     *
     * Description    : allows to communicate the pathWidth and the borders
     */
    public interface LabyrinthListener {
        void onLabyrinthLoaded(int pathWidth, ArrayList<ScreenArea> labyrinthScreenAreas);
    }
}
