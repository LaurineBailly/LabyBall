package com.example.labyball.modele.Bean;

import android.graphics.Rect;

/**
 * Class name    : Hurdle
 *
 * Description   : defines a screen space in a shape of a rectangle and if some sides have to be
 *                 ignored.
 *
 * @version 1.0
 *
 * @author Laurine Bailly
 */


public class Hurdle {

    private final Rect rectangle;
    private boolean criticalTopLine = true;
    private boolean criticalBottomLine = true;
    private boolean criticalRightLine = true;
    private boolean criticalLeftLine = true;

    /**
     * Hurdle constructor
     *
     * @param       rectangle represents a screen space
     */
    public Hurdle(Rect rectangle) {
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
}
