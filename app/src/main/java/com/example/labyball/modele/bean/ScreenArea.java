package com.example.labyball.modele.bean;

import android.graphics.Rect;

/**
 * Class name    : ScreenArea
 *
 * Description   : defines a screen space in a shape of a rectangle and if some sides have to be
 *                 ignored.
 *
 * @version 1.0
 *
 * @author Laurine Bailly
 */


public class ScreenArea {

    private final Rect rectangle;
    private boolean criticalTopLine = true;
    private boolean criticalBottomLine = true;
    private boolean criticalRightLine = true;
    private boolean criticalLeftLine = true;
    private boolean spaceLeftLine = false;
    private boolean spaceRightLine = false;
    private boolean spaceTopLine = false;
    private boolean spaceBottomLine = false;


    /**
     * Hurdle constructor
     *
     * @param       rectangle represents a screen space
     */
    public ScreenArea(Rect rectangle) {
        this.rectangle = rectangle;
    }

    public boolean isCriticalTopLine() {
        return criticalTopLine;
    }

    public boolean isCriticalBottomLine() {
        return criticalBottomLine;
    }

    public boolean isCriticalRightLine() {
        return criticalRightLine;
    }

    public boolean isCriticalLeftLine() {
        return criticalLeftLine;
    }

    public int getLeft() {
        return rectangle.left;
    }

    public int getRight() {
        return rectangle.right;
    }

    public int getTop() {
        return rectangle.top;
    }

    public int getBottom() {
        return rectangle.bottom;
    }

    public void ignoreTopLine() {
        this.criticalTopLine = false;
    }

    public void ignoreBottomLine() {
        this.criticalBottomLine = false;
    }

    public void ignoreRightLine() {
        this.criticalRightLine = false;
    }

    public void ignoreLeftLine() {
        this.criticalLeftLine = false;
    }

    public boolean isSpaceLeftLine() {
        return spaceLeftLine;
    }

    public boolean isSpaceRightLine() {
        return spaceRightLine;
    }

    public void setSpaceLeftLine(boolean spaceLeftLine) {
        this.spaceLeftLine = spaceLeftLine;
    }

    public void setSpaceRightLine(boolean spaceRightLine) {
        this.spaceRightLine = spaceRightLine;
    }

    public void setSpaceTopLine(boolean spaceTopLine) {
        this.spaceTopLine = spaceTopLine;
    }

    public void setSpaceBottomLine(boolean spaceBottomLine) {
        this.spaceBottomLine = spaceBottomLine;
    }

    public boolean isSpaceTopLine() {
        return spaceTopLine;
    }

    public boolean isSpaceBottomLine() {
        return spaceBottomLine;
    }

    public Rect getRectangle() {
        return rectangle;
    }

    /**
     * Concatenates two hurdles with either same bottom and top coordinates or left and right ones
     *
     * @param       screenArea1 hurdle 1 to concatenate, the coordinates of the hurdle 1 will be
     *                      used to build the new hurdle in top and left
     * @param       screenArea2 hurdle 2 to concatenate, the coordinates of the hurdle 2 will be
     *      *                      used to build the new hurdle in bottom and right
     *
     * @return      the concatenated hurdle
     */
    public static ScreenArea concatenate(ScreenArea screenArea1, ScreenArea screenArea2) {
        ScreenArea screenArea;
        screenArea = new ScreenArea(new Rect(screenArea1.getLeft(), screenArea1.getTop(), screenArea2.getRight(), screenArea2.getBottom()));
        screenArea.setSpaceLeftLine(screenArea1.isSpaceLeftLine());
        screenArea.setSpaceRightLine(screenArea2.isSpaceRightLine());
        screenArea.setSpaceTopLine(screenArea1.isSpaceTopLine());
        screenArea.setSpaceBottomLine(screenArea2.isSpaceBottomLine());
        return screenArea;
    }
}
