package com.example.LabyrinthBall.modele;

// The labyrinth is made of paths and bricks.
// A brick has a defined size, and two coordinates X and Y for its position
public class Brick {

    // Brick properties in dpi
    public static final int LENGTH = 60;
    public static final int HEIGHT = 30;

    // Brick properties in pixels
    private static float length = 60;
    private static float height = 20;

    // Coordinated in pixels
    private final float xCoordinate;
    private final float yCoordinate;

    public Brick(float xCoordinate, float yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public float getTop() {
        return yCoordinate;
    }

    public float getBottom() {
        return yCoordinate + height;
    }

    public float getLeft() {
        return xCoordinate;
    }

    public float getRight() {
        return xCoordinate + length;
    }

    // Getting HEIGHT and LENGTH in pixels, which depends on the screen density
    public static void setBricksProperties(float l, float h) {
        height = h;
        length = l;
    }

    // Getting the brick's top side
    public BorderLine getTopBorder() {
        return new BorderLine(BorderLine.TOP, yCoordinate, xCoordinate, xCoordinate+length);
    }

    // Getting the brick's bottom side
    public BorderLine getBottomBorder() {
        return new BorderLine(BorderLine.BOTTOM, yCoordinate + height, xCoordinate, xCoordinate + length);
    }

    // Getting the brick's left side
    public BorderLine getLeftBorder() {
        return new BorderLine(BorderLine.LEFT, xCoordinate, yCoordinate, yCoordinate + height);
    }

    // Getting the brick's right side
    public BorderLine getRightBorder() {
        return new BorderLine(BorderLine.RIGHT, xCoordinate + length, yCoordinate, yCoordinate + height);
    }

}
