package com.example.LabyrinthBall.modele;

// A borderline object is a line representing the side of an object (graphic component or phone edge)
// BorderLine objects are horizontal or vertical lines
public class BorderLine {

    // Side type on the object perspective
    // 't' for top, 'b' for bottom, 'l' for left, 'r' for right
    private final char lineType;
    public final static char TOP = 't';
    public final static char BOTTOM = 'b';
    public final static char RIGHT = 'r';
    public final static char LEFT = 'l';
    
    // Coordinates of the line in X and Y
    // The screen is a 2D dimension
    // A line has 1 coordinate on a parallel axis, 2 coordinates on the perpendicular axis
    private final float valueAxisParallel;
    private final float valueAxisPerpendicular1;
    private final float valueAxisPerpendicular2;

    // Builds a borderLine from a line type and coordinates
    public BorderLine(char lineType, float valueAxisParallel, float valueAxisPerpendicular1, float valueAxisPerpendicular2) {
        this.lineType = lineType;
        this.valueAxisParallel = valueAxisParallel;
        this.valueAxisPerpendicular1 = valueAxisPerpendicular1;
        this.valueAxisPerpendicular2 = valueAxisPerpendicular2;
    }

    public char getLineType() {
        return lineType;
    }

    public float getValueAxisParallel() {
        return valueAxisParallel;
    }

    public float getValueAxisPerpendicular1() {
        return valueAxisPerpendicular1;
    }

    public float getValueAxisPerpendicular2() {
        return valueAxisPerpendicular2;
    }
}
