package com.example.labyball.controller;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.labyball.R;
import com.example.labyball.modele.Bean.Hurdle;
import com.example.labyball.view.BallView;

import java.util.ArrayList;
import java.util.Locale;

public class BallManager implements SensorEventListener {

    // Period after which we wish the ball position to be updated
    public static final int PERIOD_REFRESH_BALL_MS = 40;

    // SensorManager represents the list of sensors on the device and allows to manage them,
    // including the sensor accelerometer
    private final SensorManager sensorsOnDevice;
    private final Sensor accelerometer;

    // Acceleration data in m/s2
    private double aX= 0;
    private double aY = 0;

    // View including the ball and the area it browses in
    private final BallView ballView;

    // TextViews to display the coordinates of the ball in mm
    private final TextView tvXValue;
    private final TextView tvYValue;

    // Ball diameter
    private int ballDiameter = 0;

    // Ball position
    private double posX = 0;
    private double posY = 0;

    // Ball speed
    private double xVelocity = 0;
    private double yVelocity = 0;

    // Factor to multiply the acceleration of the ball by
    public static final float FACTOR_ACCELERATION = 0.005f;

    // The ball view is not fully loaded yet
    private boolean ballViewLoaded = false;

    // Hurdles list
    private final ArrayList<Hurdle> hurdles = new ArrayList<>();

    // How many pixels in 1mm
    private static double pixelsInOneMm = 160/25.4;

    // A handler is like a message queue that will process a runnable
    // Differences between a handler and a timer :
    // https://medium.com/@f2016826/timers-vs-handlers-aeae5d3cb5a
    private final Handler handler = new Handler();

    // The runnable assigned to the handler : updates the ball View
    private final Runnable taskUpdateBallView;


    // Constructor that inflates the view layout_ball, instantiates the accelerometer, determines
    // how many pixels there are in 1mm.
    // It needs the context of the activity it is called from to get the displayMatrics and to
    // inflate the view.
    // It needs the labyrinth hurdles.
    // It also needs the root layout of the activity, which is the parent of the layout_ball.
    public BallManager(Context context, ViewGroup rootParent, ArrayList<Hurdle> labyrinthHurdles) throws UnsupportedOperationException {

        // DISPLAY THE TEXTVIEWS AND THE BALLVIEW

        // Inflating the view described in layout_ball.xml file (ball, area it moves in, text views
        // displaying the coordinates)
        LayoutInflater layoutInflaterContext = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout rootBallTv = (FrameLayout)layoutInflaterContext.inflate(R.layout.layout_ball_tv, rootParent, true);

        // Getting the ballView and the text views
        ballView = rootBallTv.findViewById(R.id.id_ballView);
        tvXValue = rootBallTv.findViewById(R.id.id_tvXValue);
        tvYValue = rootBallTv.findViewById(R.id.id_tvYValue);

        // SETTING UP THE ACCELEROMETER

        // Getting the sensor manager
        sensorsOnDevice = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);

        // Getting the accelerometer available by default on the phone, throws an exception if it
        // does not exist
        accelerometer = this.sensorsOnDevice.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer == null) {
            throw new UnsupportedOperationException("No accelerometer available on the device.");
        }

        // DEFINING THE NUMBER OF PIXELS IN ONE MM
        // To display the position in mm in the textviews

        // DisplayMetrics structure describing general information about a display, such as its size,
        // density, and font scaling.
        DisplayMetrics screenMetrics = context.getResources().getDisplayMetrics();

        // getResources().getDisplayMetrics().density is the scaling factor for the Density
        // Independent Pixel unit, where one DIP is one pixel on an approximately 160 dpi screen
        // density of the screen (pixels per inch). DENSITY_DEFAULT = 160.
        // getting the number of pixels in 1 inch
        double density = 160*screenMetrics.density;

        if(density != 0) {
            // 25.4mm = 1 inch.
            // Getting the number of pixels in 1mm.
            pixelsInOneMm = density/25.4;
        }

        // GETTING LABYRINTH HURDLES

        hurdles.addAll(labyrinthHurdles);

        // WHEN THE BALLVIEW IS FULLY LOADED

        ballView.addOnLayoutChangeListener((View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) -> {

            // The ball picture is loaded
            ballViewLoaded = true;

            // Getting the ball diameter
            ballDiameter = ballView.getBallDiameter();

            // Getting the ball position
            posX = ballView.getPosLeft();
            posY = ballView.getPosTop();

            // Adding the phone edges as obstacles
            // 2 hurdles of a shape of a rectangle :
            // -- 1 hurdle from right to left, top to bottom
            // -- 1 hurdle from bottom to top, left to right
            hurdles.add(new Hurdle(new Rect(ballView.getLeft(), ballView.getBottom(), ballView.getRight(), ballView.getTop())));
            hurdles.add(new Hurdle(new Rect(ballView.getRight(), ballView.getTop(), ballView.getLeft(), ballView.getBottom())));

            // Display the coordinates of the ballView
            displayBallCoordinates();
        });

        // DEFINING THE TASK FOR UPDATING THE BALLVIEW

        taskUpdateBallView = new Runnable() {
            @Override
            public void run() {

                // Getting the current time in ms
                long currentTimeMillis = System.currentTimeMillis();

                // If the ball picture is loaded
                if(ballViewLoaded) {

                    // Calculating the ball position
                    calculateBallPosition();

                    // Setting the new position of the ball according to the acceleration noticed
                    // during the timer tick.
                    ballView.setPosition(posX, posY);

                    // Updating the view
                    ballView.performClick();

                    // Setting the coordinates values to the textviews in mm
                    displayBallCoordinates();
                }

                // Removing the callback for this task of the handler
                handler.removeCallbacks(taskUpdateBallView);

                // Time that took the previous code in this runnable
                long durationTaskUpdateBallView = currentTimeMillis - System.currentTimeMillis();

                // If we are already running out of time, this same task is started immediately
                if(durationTaskUpdateBallView <= PERIOD_REFRESH_BALL_MS) {
                    handler.post(taskUpdateBallView);
                }

                // Otherwise this same task is delayed
                else {
                    handler.postDelayed(taskUpdateBallView, PERIOD_REFRESH_BALL_MS - durationTaskUpdateBallView);
                }
            }
        };
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        aX = event.values[0];
        aY = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startUpdatingBall() {

        // sensorsOnDevice listens to the accelerometer values every PERIOD_REFRESH_BALL_MS ms
        sensorsOnDevice.registerListener(this, accelerometer, PERIOD_REFRESH_BALL_MS*1000);

        // taskUpdateBallView starts
        handler.post(taskUpdateBallView);
    }

    public void stopUpdatingBall() {

        // sensorsOnDevice stops listening to the accelerometer
        sensorsOnDevice.unregisterListener(this);

        // Removing the callback for taskUpdateBallView
        handler.removeCallbacks(taskUpdateBallView);
    }

    // Setting the coordinates values of the ball to the textviews in mm
    private void displayBallCoordinates() {
        tvXValue.setText(String.format(Locale.getDefault(),"%.1f", ballView.getPosLeft()/pixelsInOneMm));
        tvYValue.setText(String.format(Locale.getDefault(),"%.1f", ballView.getPosTop()/pixelsInOneMm));
    }

    // Calculating the ball position with the acceleration data ax and ay
    private void calculateBallPosition() {

        // Conversion of the acceleration data (meter/s2 --> pixels/s2)
        // factor_acceleration set to the acceleration
        // The X position axis and X acceleration axis on the device are opposite. We put the
        // acceleration axis in the same direction of the position and therefore velocity ones.
        double xAcceleration = -aX*1000*pixelsInOneMm*FACTOR_ACCELERATION;
        double yAcceleration = aY*1000*pixelsInOneMm*FACTOR_ACCELERATION;

        // Period update ball in seconds
        double periodUpdatePosSec = PERIOD_REFRESH_BALL_MS*0.001;

        // Acceleration components corresponding to the position delta due to acceleration only

        // (1/2)*Ax(t)*t^2
        // where t is time and Ax is the accelerations on X axis (pixels/s2)
        double xMoveAx = 0.5*xAcceleration*periodUpdatePosSec*periodUpdatePosSec;

        // (1/2)*Ay(t)*t^2
        // where t is time and Ay is the accelerations on Y axis (pixels/s2)
        double yMoveAy = 0.5*yAcceleration*periodUpdatePosSec*periodUpdatePosSec;

        // Stocking the position of the ball before it is updated
        double oldPosX = posX;
        double oldPosY = posY;

        // Calculating a theoretical position in X
        // Sx = Sx(t-1) + xMoveAx + Ux(t-1)*t
        // where Sx is position, Ux is initial velocity, t is time.
        posX = posX + xMoveAx + xVelocity*periodUpdatePosSec;

        // Calculating a theoretical position in Y
        // Sy = Sy(t-1) + yMoveAy + Uy(t-1)*t
        // where Sy is position, Uy is initial velocity, t is time.
        posY = posY + yMoveAy + yVelocity*periodUpdatePosSec;

        // hurdles not detected yet in x or y
        boolean xDetectedHurdle = false;
        boolean yDetectedHurdle = false;

        // For each hurdle in the hurdles tab
        for(Hurdle hurdle : hurdles) {

            int bottom = hurdle.getBottom();
            float top = hurdle.getTop();
            float right = hurdle.getRight();
            int left = hurdle.getLeft();

            // If a hurdle has been detected in x and y, no need to check anymore
            if(yDetectedHurdle && xDetectedHurdle) {
                break;
            }
            else {

                // If a hurdle has not been detected in Y yet
                if(!yDetectedHurdle) {

                    // If the the bottom line of the hurdle is to be considered
                    // And if the Y pos of the ball calculated exceeds the bottom line of the hurdle
                    // And if the balls comes from the bottom of the hurdle
                    // And if the pos X of the ball calculated is between the left and the right of
                    // the hurdle.
                    if((hurdle.isCriticalBottomLine()) && (posY < bottom) && (oldPosY >= bottom) && (posX > left - ballDiameter) && (posX < right)) {

                        // The ball touches the bottom line of the hurdle
                        posY = bottom;
                        yDetectedHurdle = true;
                    }

                    // Else if the the top line of the hurdle is to be considered
                    // And if the Y pos of the ball calculated exceeds the top line of the hurdle
                    // And if the balls comes from the top of the hurdle
                    // And if the pos X of the ball calculated is between the left and the right of
                    // the hurdle.
                    else if((hurdle.isCriticalTopLine()) && (posY > top - ballDiameter) && (oldPosY <= top - ballDiameter) && (posX > left - ballDiameter) && (posX < right)) {

                        // The ball touches the top line of the hurdle
                        posY = top - ballDiameter;
                        yDetectedHurdle = true;
                    }
                }
                if(!xDetectedHurdle) {

                    // If the the right line of the hurdle is to be considered
                    // And if the X pos of the ball calculated exceeds the right line of the hurdle
                    // And if the balls comes from the right of the hurdle
                    // And if the pos Y of the ball calculated is between the top and the bottom of
                    // the hurdle.
                    if((hurdle.isCriticalRightLine()) && (posX < right) && (oldPosX >= right) && (posY > top - ballDiameter) && (posY < bottom)) {

                        // The ball touches the right line of the hurdle
                        posX = right;
                        xDetectedHurdle = true;
                    }

                    // Else if the the left line of the hurdle is to be considered
                    // And if the X pos of the ball calculated exceeds the left line of the hurdle
                    // And if the balls comes from the left of the hurdle
                    // And if the pos Y of the ball calculated is between the top and the bottom of
                    // the hurdle.
                    else if((hurdle.isCriticalLeftLine()) && (posX > left - ballDiameter) && (oldPosX <= left - ballDiameter) && (posY > top - ballDiameter) && (posY < bottom)) {

                        // The ball touches the left line of the hurdle
                        posX = left - ballDiameter;
                        xDetectedHurdle = true;
                    }
                }
            }
        }

        // If no hurdle detected in Y, y velocity calculation
        if(!yDetectedHurdle) {
            yVelocity = yAcceleration*periodUpdatePosSec + yVelocity;
        }

        // Else the ball will bounce in Y
        else {
            yVelocity = -yVelocity;
        }

        // If no hurdle detected in X, x velocity calculation
        if(!xDetectedHurdle) {
            xVelocity = xAcceleration*periodUpdatePosSec + xVelocity;
        }

        // Else the ball will bounce in X
        else {
            xVelocity = -xVelocity;
        }
    }
}
