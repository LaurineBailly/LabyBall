/*
 * Class name    : MainActivity
 *
 * Description   : manages the lifecycles of the app
 */

package com.example.labyball;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.example.labyball.controller.BallManager;
import com.example.labyball.controller.LabyrinthManager;
import com.example.labyball.bean.ScreenArea;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LabyrinthManager.LabyrinthListener {

    // Manager of the ball position
    private BallManager ballController;

    // True is the labyrinth is already loaded
    private boolean labyrinthLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        FrameLayout rootMainActivity = findViewById(R.id.main_frame_layout);

        new LabyrinthManager(this, rootMainActivity, this);

        try {
            ballController = new BallManager(this, rootMainActivity);
        }
        catch(UnsupportedOperationException e) {
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
    protected void onPause() {
        super.onPause();
        ballController.stopUpdatingBall();
    }

    @Override
    public void onLabyrinthLoaded(int pathWidth, ArrayList<ScreenArea> labyrinthScreenAreas) {
        if(!labyrinthLoaded) {
            ballController.setBallManagerSettings(labyrinthScreenAreas, pathWidth);
            labyrinthLoaded = true;
        }
    }
}