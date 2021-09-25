package com.example.jonathan.minesweeper2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class homeScreen extends AppCompatActivity {
    /*private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        /*int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }*/
    }

    public void startGame(View view) {
        final EditText ro = (EditText) findViewById(R.id.rows);
        String rowNum = ro.getText().toString();
        final EditText mi = (EditText) findViewById(R.id.mines);
        String mineNum = mi.getText().toString();
        final EditText cl = (EditText) findViewById(R.id.columns);
        String columnNum = cl.getText().toString();

        if (Integer.parseInt(rowNum) > 2 && Integer.parseInt(rowNum) < 9 && Integer.parseInt(columnNum) > 2 && Integer.parseInt(columnNum) < 9  && Integer.parseInt(mineNum) > 2 && Integer.parseInt(mineNum) <= (Integer.parseInt(columnNum)*Integer.parseInt(rowNum)-1)) {
            Bundle extras = new Bundle();
            extras.putString("NUM_OF_ROWS", rowNum);
            extras.putString("NUM_OF_COLUMNS", columnNum);
            extras.putString("NUM_OF_MINES", mineNum);
            Intent intent = new Intent(this, Game.class);
            intent.putExtras(extras);
            startActivity(intent);
        } else {
            //RelativeLayout layout = (RelativeLayout) View.inflate(this, R.layout.activity_home_screen, null);
            TextView errMsg = (TextView) findViewById(R.id.errorText);
            errMsg.setVisibility(View.VISIBLE);
        }

    }
}
