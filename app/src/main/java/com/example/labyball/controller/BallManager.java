package com.example.labyball.controller;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
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

/**
 * Class name    : BallManager
 *
 * Description   : calculates the ball position in function of acceleration data provided by the
 *                 phone accelerometer and updates it every PERIOD_REFRESH_BALL_MS ms
 * @version 1.0
 *
 * @author Laurine Bailly
 */
public class BallManager implements SensorEventListener {

    // Period after which we wish the ball position to be updated
    public static final int PERIOD_REFRESH_BALL_MS = 40;
    // Factor to multiply the acceleration of the ball by
    public static final float FACTOR_ACCELERATION = 0.001f;
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
    // BallView properties
    private int ballDiameter = 0;
    private double posX = 0;
    private double posY = 0;
    private double xVelocity = 0;
    private double yVelocity = 0;
    private boolean ballViewLoaded = false;
    // List of areas where the ball can not go
    private final ArrayList<Hurdle> hurdles = new ArrayList<>();
    // How many pixels in 1mm to display the ball position values in mm. Initialized for a
    // 160 pix/inch. 25.4mm = 1 inch.
    private static double pixelsInOneMm = 160/25.4;
    // Message queue that will process a runnable.
    private final Handler handler = new Handler();
    // The runnable assigned to the handler : updates the ball View
    private final Runnable taskUpdateBallView;

    /**
     * BallManager constructor
     * Inflates the view layout_ball, instantiates the accelerometer, determines how many pixels
     * there are in 1mm, defines the task for updating the ball position,
     *
     * @param       context context of the activity it is called from
     * @param       rootParent parent of the layont_ball
     * @param       labyrinthHurdles areas of the screen in shapes of rectangles the ball can not
     *                               browse on
     * @exception   UnsupportedOperationException  thrown if there is no accelerometer on the phone
     */
    public BallManager(Context context, ViewGroup rootParent, ArrayList<Hurdle> labyrinthHurdles) throws UnsupportedOperationException {
        double density;

        // Display and getting the ballView and the textViews
        LayoutInflater layoutInflaterContext = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout rootBallTv = (FrameLayout)layoutInflaterContext.inflate(R.layout.layout_ball_tv, rootParent, true);
        ballView = rootBallTv.findViewById(R.id.id_ballView);
        tvXValue = rootBallTv.findViewById(R.id.id_tvXValue);
        tvYValue = rootBallTv.findViewById(R.id.id_tvYValue);

        // Setting up the accelerometer, throwing an exception if it does not exist
        sensorsOnDevice = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = this.sensorsOnDevice.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelerometer == null) {
            throw new UnsupportedOperationException("No accelerometer available on the device.");
        }

        // Defining pixelsInOneMm.
        density = 160*context.getResources().getDisplayMetrics().density;
        if(density != 0) {
            pixelsInOneMm = density/25.4;
        }

        // Getting the hurdles provided by the labyrinth
        hurdles.addAll(labyrinthHurdles);

        // When the ballView is fully loaded, getting some ball graphic properties, adding the
        // labyrinth hurdles to the hurdles list and displaying the coordinates of the ball
        ballView.addOnLayoutChangeListener((View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) -> {
            ballViewLoaded = true;
            ballDiameter = ballView.getBallDiameter();
            posX = ballView.getPosLeft();
            posY = ballView.getPosTop();
            displayBallCoordinates();
        });

        // Task for calculating the ball position and updating it
        taskUpdateBallView = new Runnable() {
            @Override
            public void run() {
                long taskStartsTimeMillis = System.currentTimeMillis();
                long durationTask;

                if(ballViewLoaded) {
                    calculateBallPosition();
                    ballView.setPosition(posX, posY);
                    ballView.performClick();
                    displayBallCoordinates();
                }

                handler.removeCallbacks(taskUpdateBallView);

                durationTask = System.currentTimeMillis() - taskStartsTimeMillis;
                if(durationTask <= PERIOD_REFRESH_BALL_MS) {
                    handler.post(taskUpdateBallView);
                }
                else {
                    handler.postDelayed(taskUpdateBallView, PERIOD_REFRESH_BALL_MS - durationTask);
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

    /**
     * Starts updating the view
     */
    public void startUpdatingBall() {

        sensorsOnDevice.registerListener(this, accelerometer, PERIOD_REFRESH_BALL_MS*1000);
        handler.post(taskUpdateBallView);
    }

    /**
     * Stops updating the view
     */
    public void stopUpdatingBall() {

        // sensorsOnDevice stops listening to the accelerometer
        sensorsOnDevice.unregisterListener(this);

        // Removing the callback for taskUpdateBallView
        handler.removeCallbacks(taskUpdateBallView);
    }

    /**
     * Setting the coordinates values of the ball to the textviews in mm
     */
    private void displayBallCoordinates() {
        tvXValue.setText(String.format(Locale.getDefault(),"%.1f", ballView.getPosLeft()/pixelsInOneMm));
        tvYValue.setText(String.format(Locale.getDefault(),"%.1f", ballView.getPosTop()/pixelsInOneMm));
    }

    /**
     * Calculating the ball position with the acceleration data ax and ay
     */
    private void calculateBallPosition() {
        double periodUpdatePosSec = PERIOD_REFRESH_BALL_MS*0.001;
        double oldPosX = posX;
        double oldPosY = posY;
        boolean xDetectedHurdle = false;
        boolean yDetectedHurdle = false;
        // Conversion of the acceleration data (meter/s2 --> pixels/s2), factorized by
        // FACTOR_ACCELERATION
        // The X position axis and X acceleration axis on the device are opposite. We put the
        // acceleration axis in the same direction of the position and therefore velocity ones.
        double yAcceleration = aY*1000*pixelsInOneMm*FACTOR_ACCELERATION;
        double xAcceleration = -aX*1000*pixelsInOneMm*FACTOR_ACCELERATION;
        // Acceleration components corresponding to the position delta due to acceleration only
        // (1/2)*A(t)*t^2
        // where t is time and A is the acceleration (pixels/s2)
        double xMoveAx = 0.5*xAcceleration*periodUpdatePosSec*periodUpdatePosSec;
        double yMoveAy = 0.5*yAcceleration*periodUpdatePosSec*periodUpdatePosSec;

        // Calculating a theoretical position
        // S = S(t-1) + acceleration component + U(t-1)*t
        // where S is position, U is initial velocity, t is time.
        posX = posX + xMoveAx + xVelocity*periodUpdatePosSec;
        posY = posY + yMoveAy + yVelocity*periodUpdatePosSec;

        // Defining the position of the ball on the edge if it gets out of the screen.
        if(posY < ballView.getTop()) {
            posY = ballView.getTop();
            yDetectedHurdle = true;
        }
        else if(posY > ballView.getBottom() - ballDiameter) {
            posY = ballView.getBottom() - ballDiameter;
            yDetectedHurdle = true;
        }
        if(posX < ballView.getLeft()) {
            posX = ballView.getLeft();
            xDetectedHurdle = true;
        }
        else if(posX > ballView.getRight() - ballDiameter) {
            posX = ballView.getRight() - ballDiameter;
            xDetectedHurdle = true;
        }

        // In this loop, for each labyrinth hurdle, we check if the ball position is found to be
        // on the hurdle. If yes, we define the position to be on the side of the hurdle it comes
        // towards.
        for(Hurdle hurdle : hurdles) {
            int bottom = hurdle.getBottom();
            float top = hurdle.getTop();
            float right = hurdle.getRight();
            int left = hurdle.getLeft();
            boolean ballPosOnHurdle = (posY < bottom) && (posY > top - ballDiameter) && (posX > left - ballDiameter) && (posX < right);

            // If a hurdle has been detected in x and y already, including before this loop, no
            // need to check anymore
            if(yDetectedHurdle && xDetectedHurdle) {
                break;
            }
            else if(ballPosOnHurdle) {
                if(!yDetectedHurdle) {
                    if((oldPosY > bottom) && hurdle.isCriticalBottomLine()) {
                        posY = bottom;
                        yDetectedHurdle = true;
                    }
                    else if((oldPosY < (top - ballDiameter)) && hurdle.isCriticalTopLine()) {
                        posY = top - ballDiameter;
                        yDetectedHurdle = true;
                    }
                }
                if(!xDetectedHurdle) {
                    if((oldPosX > right) && hurdle.isCriticalRightLine()) {
                        posX = right;
                        xDetectedHurdle = true;
                    }
                    else if((oldPosX < left - ballDiameter) && hurdle.isCriticalLeftLine()) {
                        posX = left - ballDiameter;
                        xDetectedHurdle = true;
                    }
                }
            }
        }

        // Velocity definition with bouncing management
        if(!yDetectedHurdle) {
            yVelocity = yAcceleration*periodUpdatePosSec + yVelocity;
        }
        else {
            yVelocity = -yVelocity;
        }
        if(!xDetectedHurdle) {
            xVelocity = xAcceleration*periodUpdatePosSec + xVelocity;
        }
        else {
            xVelocity = -xVelocity;
        }
    }
}
