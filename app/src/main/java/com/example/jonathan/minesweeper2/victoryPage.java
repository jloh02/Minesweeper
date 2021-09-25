package com.example.jonathan.minesweeper2;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class victoryPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory_page);
        String time = getIntent().getStringExtra("TIME_TAKEN");
        String text = "Time Taken: "+time;
        TextView timer = findViewById(R.id.iTime);
        timer.setText(text);
    }

    public void toHomePage(View view) {
        Intent intent = new Intent(this, homeScreen.class);
        startActivity(intent);
    }
}
