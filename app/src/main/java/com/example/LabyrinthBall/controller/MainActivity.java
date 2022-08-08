package com.example.LabyrinthBall.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.LabyrinthBall.R;
import com.example.LabyrinthBall.modele.Accelerometer;
import com.example.LabyrinthBall.view.BallView;
import com.example.LabyrinthBall.view.LabyrinthView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Period after which we wish the ball position to be updated
    public static final int PERIOD_REFRESH_BALL_MS = 40;

    // True if the ball bitmap is loaded
    private boolean ballPictureLoaded = false;

    // ballView is the view including the ball and the area it can move in
    private BallView ballView;

    // An accelerometer that provides acceleration data on Y and X axis
    Accelerometer accelerometer;

    // Textviews that display the coordinates in mm
    private TextView tvXValue;
    private TextView tvYValue;

    // A handler is like a message queue that will process a runnable
    // Differences between a handler and a timer :
    // https://medium.com/@f2016826/timers-vs-handlers-aeae5d3cb5a
    private final Handler handler = new Handler();

    // The runnable assigned to the handler : updates the ball View
    private Runnable taskUpdateBallView;

    // onCreate is called when the activity is being open
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Forbidding phone from sleeping
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // labyrinthView in which is the labyrinth
        LabyrinthView labyrinthView;

        // Getting the graphic components
        ballView = findViewById(R.id.id_ballView);
        labyrinthView = findViewById(R.id.id_labyrinthView);
        tvXValue = findViewById(R.id.id_tvXValue);
        tvYValue = findViewById(R.id.id_tvYValue);

        // Sending the ballView to the labyrinth
        labyrinthView.setBallView(ballView);

        // Indicating to the ballView the period with which it is updated
        try {
            ballView.setPeriodUpdatePosSec(PERIOD_REFRESH_BALL_MS);
        }

        // The period given is wrong
        catch(Exception e) {
            e.printStackTrace();
        }

        // Listening when the onSizeChanged method is called on ballView, it allows to be sure that
        // the bipmap file is loaded.
        ballView.addOnLayoutChangeListener((View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) -> {

            // The ball picture is loaded
            ballPictureLoaded = true;

            // Display the coordinates of the ballView
            displayBallCoordinates();
        });

        // sensorsOnDevice is the manager of the sensors on the device.
        SensorManager sensorsOnDevice = (SensorManager)getSystemService(SENSOR_SERVICE);

        // Try to instantiate the accelerometer
        try {

            // Giving the check values period to the Accelerometer
            accelerometer = new Accelerometer(sensorsOnDevice, PERIOD_REFRESH_BALL_MS);
        }

        // In case of a failure of the accelerometer initialization.
        catch(Exception e) {

            // The period given to the function is wrong
            if(e instanceof IllegalArgumentException) {
                e.printStackTrace();
            }

            // Exception due to the device
            else {

                // A window pops up displaying the error and invites the user to close the application.
                // When the user presses close, onDestroy() is triggered.
                AlertDialog.Builder errorPopUp = new AlertDialog.Builder(this);
                errorPopUp.setTitle("Error encountered");
                errorPopUp.setMessage("The following error has been encountered: " + e);
                errorPopUp.setPositiveButton("Close", (DialogInterface dialog, int which) -> finish());
                errorPopUp.show();
            }
        }

        // Defining the task for updating the ballView
        taskUpdateBallView = new Runnable() {
            @Override
            public void run() {

                // Getting the current time in ms
                long currentTimeMillis = System.currentTimeMillis();

                // If the ball picture is loaded
                if(ballPictureLoaded) {

                    // Setting the new position of the ball according to the acceleration noticed
                    // during the timer tick.
                    try {
                        ballView.setPosition(accelerometer.getAx(), accelerometer.getAy());
                    }

                    // The period for updating the view has not been called
                    // Or the labyrinth is not loaded
                    catch(Exception e) {
                        e.printStackTrace();
                    }

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

    // The activity is running
    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.startListener();

        // taskUpdateBallView starts
        handler.post(taskUpdateBallView);
    }

    // Something interrupted the activity
    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.cancelListener();

        // Removing the callback for taskUpdateBallView
        handler.removeCallbacks(taskUpdateBallView);
    }

    // Setting the coordinates values of the ball to the textviews in mm
    public void displayBallCoordinates() {
        tvXValue.setText(String.format(Locale.getDefault(),"%.1f", ballView.getPosLeftMm()));
        tvYValue.setText(String.format(Locale.getDefault(),"%.1f", ballView.getPosTopMm()));
    }
}