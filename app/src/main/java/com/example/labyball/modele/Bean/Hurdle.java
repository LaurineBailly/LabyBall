package com.example.labyball.modele.Bean;

import android.graphics.Rect;

// A Hurdle object is a screen space. It has the shape of a rectangle.
public class Hurdle {

    // A rectangle describing the hurdle
    private final Rect rectangle;

    // The critical hurdle lines to consider, can be set to false if a Hurdle object touches another
    // one.
    private boolean criticalTopLine = true;
    private boolean criticalBottomLine = true;
    private boolean criticalRightLine = true;
    private boolean criticalLeftLine = true;

    public Hurdle(Rect rectangle) {
        this.rectangle = rectangle;
    }

    public boolean isCriticalTopLine() {
        return criticalTopLine;
    }

    public void ignoreTopLine() {
        this.criticalTopLine = false;
    }

    public boolean isCriticalBottomLine() {
        return criticalBottomLine;
    }

    public void ignoreBottomLine() {
        this.criticalBottomLine = false;
    }

    public boolean isCriticalRightLine() {
        return criticalRightLine;
    }

    public void ignoreRightLine() {
        this.criticalRightLine = false;
    }

    public boolean isCriticalLeftLine() {
        return criticalLeftLine;
    }

    public void ignoreLeftLine() {
        this.criticalLeftLine = false;
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
}
