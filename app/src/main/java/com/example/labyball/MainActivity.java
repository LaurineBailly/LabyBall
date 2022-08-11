package com.example.labyball;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.labyball.controller.BallManager;
import com.example.labyball.controller.LabyrinthManager;
import com.example.labyball.modele.Bean.Hurdle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BallManager ballController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Forbidding phone from sleeping
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Getting the root layout of this activity
        ViewGroup rootMainActivity = findViewById(R.id.main_frame_layout);

        // Instantiating the LabyrinthManager
        LabyrinthManager labyController = new LabyrinthManager(this, rootMainActivity);

        // Getting the hurdles calculated by labyController
        ArrayList<Hurdle> labyHurdles = labyController.getLabyrinthHurdles();

        // Instantiating the BallManager
        // Throws an UnsupportedOperationException if the accelerometer does not exist on the phone
        try{

            // Instantiating the ball controller
            ballController = new BallManager(this, rootMainActivity, labyHurdles);
        }
        catch(UnsupportedOperationException e){
            e.printStackTrace();

            // A window pops up displaying the error and invites the user to close the application.
            // When the user presses close, onDestroy() is triggered.
            AlertDialog.Builder errorPopUp = new AlertDialog.Builder(this);
            errorPopUp.setTitle("Error encountered");
            errorPopUp.setMessage("The following error has been encountered: " + e);
            errorPopUp.setPositiveButton("Close", (DialogInterface dialog, int which) -> finish());
            errorPopUp.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ballController.startUpdatingBall();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ballController.stopUpdatingBall();
    }
}