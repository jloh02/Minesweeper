package com.example.jonathan.minesweeper2;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.os.Handler;

import java.util.Random;

class ButtonStatus extends Game {
    boolean clicked;
    boolean flagged;
    int value;

    public ButtonStatus() {
        clicked = false;
        flagged = false;
        value = 0;
    }
    
    public boolean getFlagged() {
        return this.flagged;
    }
}

public class Game extends Activity {
    int prohibitedNum;
    Button[][] mMineField;
    ButtonStatus[][] mineFieldStatus;
    int bombValue = 9;
    int numClicked = 0;
    int seconds = 0;
    private boolean startRun;
    boolean flaggedMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        final int mWidth = Integer.parseInt(extras.getString("NUM_OF_COLUMNS"));
        final int mHeight = Integer.parseInt(extras.getString("NUM_OF_ROWS"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);



        mMineField = new Button[mHeight + 1][mWidth + 1];
        mineFieldStatus = new ButtonStatus[mHeight + 1][mWidth + 1];
        for (int i = 0; i <= mHeight; i++) {
            for (int j = 0; j <= mWidth; j++) {
                mineFieldStatus[i][j] = new ButtonStatus();
            }
        }
        for (int i = 0; i <= mHeight; i++) {
            mineFieldStatus[i][0].clicked = true;
        }
        for (int j = 0; j <= mWidth; j++) {
            mineFieldStatus[0][j].clicked = true;
        }
        Timer();
        createBoard();
    }

    private void Timer() {

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int secs = seconds;
                // 023
                secs += 1000;
                String time = secs + "";
                time = time.substring(1);

                final TextView timeView = (TextView) findViewById(R.id.time_view);
                timeView.setText(time);

                if (startRun) {
                    seconds++;
                }

                handler.postDelayed(this, 1000);
            }
        });

    }

    public void createBoard() {
        Bundle extras = getIntent().getExtras();
        final int mWidth = Integer.parseInt(extras.getString("NUM_OF_COLUMNS"));
        final int mHeight = Integer.parseInt(extras.getString("NUM_OF_ROWS"));
        RelativeLayout layout = (RelativeLayout) View.inflate(this, R.layout.activity_game, null);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels - 560;
        int sizea;
        if (width / mWidth < height / mHeight) sizea = width / mWidth;
        else sizea = height / mHeight;
        final int size = sizea;
        int oldI = 1;
        for (int i = 1; i <= mHeight; i++) {
            for (int j = 1; j <= mWidth; j++) {
                mMineField[i][j] = new Button(this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
                if (i == 1 && j == 1) params.setMargins(0, 160, 0, 0);
                if (i > oldI) {
                    String prevRow = (i - 1) + "" + j + "";
                    int prevRowId = Integer.parseInt(prevRow);
                    params.addRule(RelativeLayout.BELOW, prevRowId);
                    oldI++;
                } else if (j > 0) {
                    String prevVal = i + "" + (j - 1) + "";
                    int prevValId = Integer.parseInt(prevVal);
                    params.addRule(RelativeLayout.RIGHT_OF, prevValId);
                    params.addRule(RelativeLayout.ALIGN_TOP, prevValId);
                }
                String Id = i + "" + j + "";
                final int iD = Integer.parseInt(Id);
                mMineField[i][j].setLayoutParams(params);
                mMineField[i][j].setId(iD);
                mMineField[i][j].setBackgroundResource(R.drawable.facingdown);
                mMineField[i][j].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        prohibitedNum = iD;
                        generateBoard();
                    }
                });
                layout.addView(mMineField[i][j]);
            }
        }
        setContentView(layout);
    }

    public void victory() {
        startRun = false;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        String time = String.format("%02d:%02d", minutes, secs);
        Intent intent = new Intent(this, victoryPage.class);
        intent.putExtra("TIME_TAKEN", time);
        startActivity(intent);
    }

    public void generateBoard() {
        Bundle extras = getIntent().getExtras();
        final int mWidth = Integer.parseInt(extras.getString("NUM_OF_COLUMNS"));
        final int mHeight = Integer.parseInt(extras.getString("NUM_OF_ROWS"));
        final int mMines = Integer.parseInt(extras.getString("NUM_OF_MINES"));
        startRun = true;
        int bombCount = 0;
        while (bombCount < mMines) {
            Random ra = new Random();
            final int ran1 = ra.nextInt(mHeight) + 1;
            Random rb = new Random();
            final int ran2 = rb.nextInt(mWidth) + 1;
            if (mineFieldStatus[ran1][ran2].value < bombValue && Integer.parseInt(ran1 + "" + ran2) != prohibitedNum) {
                mineFieldStatus[ran1][ran2].value = bombValue;
                bombCount++;
                int buttonID = Integer.parseInt(ran1 + "" + ran2);
                final Button addButton = findViewById(buttonID);
                addButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!mineFieldStatus[ran1][ran2].flagged()) gameOver();
                    }
                });
                if (ran2 + 1 <= mWidth) mineFieldStatus[ran1][ran2 + 1].value++;
                if (ran2 - 1 > 0) mineFieldStatus[ran1][ran2 - 1].value++;
                if (ran1 - 1 > 0) mineFieldStatus[ran1 - 1][ran2].value++;
                if (ran1 + 1 <= mHeight) mineFieldStatus[ran1 + 1][ran2].value++;
                if (ran1 - 1 > 0 && ran2 - 1 > 0)
                    mineFieldStatus[ran1 - 1][ran2 - 1].value++;
                if (ran1 - 1 > 0 && ran2 + 1 <= mWidth)
                    mineFieldStatus[ran1 - 1][ran2 + 1].value++;
                if (ran1 + 1 <= mHeight && ran2 - 1 > 0)
                    mineFieldStatus[ran1 + 1][ran2 - 1].value++;
                if (ran1 + 1 <= mHeight && ran2 + 1 <= mWidth)
                    mineFieldStatus[ran1 + 1][ran2 + 1].value++;
            }
        }
        for (int i = 1; i <= mHeight; i++) {
            for (int j = 1; j <= mWidth; j++) {
                String buttonIdString = i + "" + j;
                int buttonId = Integer.parseInt(buttonIdString);
                final Button buttonClicked = findViewById(buttonId);
                final int ran1 = i;
                final int ran2 = j;
                buttonClicked.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        if (!mineFieldStatus[ran1][ran2].flagged()) {
                            /*buttonClicked.setText("!");
                            buttonClicked.setTextColor(Color.RED);*/
                            buttonClicked.setBackgroundResource(R.drawable.flagged);
                            mineFieldStatus[ran1][ran2].flagged = true;
                        } else {
                            /*buttonClicked.setText("");
                            buttonClicked.setTextColor(Color.BLACK);*/
                            buttonClicked.setBackgroundResource(R.drawable.facingdown);
                            mineFieldStatus[ran1][ran2].flagged = false;
                        }
                        return true;
                    }
                });
                if (mineFieldStatus[i][j].value < bombValue && !mineFieldStatus[i][j].flagged()) {
                    buttonClicked.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            int text = mineFieldStatus[ran1][ran2].value;
                            //String textString = text + "";
                            mineFieldStatus[ran1][ran2].clicked = true;
                            numClicked++;
                            //buttonClicked.setText(textString);
                            switch (text) {
                                case 0:
                                    buttonClicked.setBackgroundResource(R.drawable.n0);
                                    break;

                                case 1:
                                    buttonClicked.setBackgroundResource(R.drawable.n1);
                                    break;

                                case 2:
                                    buttonClicked.setBackgroundResource(R.drawable.n2);
                                    break;

                                case 3:
                                    buttonClicked.setBackgroundResource(R.drawable.n3);
                                    break;

                                case 4:
                                    buttonClicked.setBackgroundResource(R.drawable.n4);
                                    break;

                                case 5:
                                    buttonClicked.setBackgroundResource(R.drawable.n5);
                                    break;

                                case 6:
                                    buttonClicked.setBackgroundResource(R.drawable.n6);
                                    break;

                                case 7:
                                    buttonClicked.setBackgroundResource(R.drawable.n7);
                                    break;

                                case 8:
                                    buttonClicked.setBackgroundResource(R.drawable.n8);
                                    break;

                            }
                            if (numClicked + mMines == mWidth * mHeight) victory();
                            if (text == 0) {
                                buttonClicked.setBackgroundResource(R.drawable.n0);
                                if (ran2 + 1 <= mWidth) {
                                    if (!mineFieldStatus[ran1][ran2 + 1].clicked) {
                                        String aString = ran1 + "" + (ran2 + 1);
                                        int aId = Integer.parseInt(aString);
                                        Button a = findViewById(aId);
                                        a.performClick();
                                    }
                                }
                                if (ran2 - 1 > 0) {
                                    if (!mineFieldStatus[ran1][ran2 - 1].clicked) {
                                        String aString = ran1 + "" + (ran2 - 1);
                                        int aId = Integer.parseInt(aString);
                                        Button a = findViewById(aId);
                                        a.performClick();
                                    }
                                }
                                if (ran1 - 1 > 0) {
                                    if (!mineFieldStatus[ran1 - 1][ran2].clicked) {
                                        String aString = (ran1 - 1) + "" + ran2;
                                        int aId = Integer.parseInt(aString);
                                        Button a = findViewById(aId);
                                        a.performClick();
                                    }
                                }
                                if (ran1 + 1 <= mHeight) {
                                    if (!mineFieldStatus[ran1 + 1][ran2].clicked) {
                                        String aString = (ran1 + 1) + "" + ran2;
                                        int aId = Integer.parseInt(aString);
                                        Button a = findViewById(aId);
                                        a.performClick();
                                    }
                                }
                                if (ran1 - 1 > 0 && ran2 - 1 > 0) {
                                    if (!mineFieldStatus[ran1 - 1][ran2 - 1].clicked) {
                                        String aString = (ran1 - 1) + "" + (ran2 - 1);
                                        int aId = Integer.parseInt(aString);
                                        Button a = findViewById(aId);
                                        a.performClick();
                                    }
                                }
                                if (ran1 - 1 > 0 && ran2 + 1 <= mWidth) {
                                    if (!mineFieldStatus[ran1 - 1][ran2 + 1].clicked) {
                                        String aString = (ran1 - 1) + "" + (ran2 + 1);
                                        int aId = Integer.parseInt(aString);
                                        Button a = findViewById(aId);
                                        a.performClick();
                                    }
                                }
                                if (ran1 + 1 <= mHeight && ran2 - 1 > 0) {
                                    if (!mineFieldStatus[ran1 + 1][ran2 - 1].clicked) {
                                        String aString = (ran1 + 1) + "" + (ran2 - 1);
                                        int aId = Integer.parseInt(aString);
                                        Button a = findViewById(aId);
                                        a.performClick();
                                    }
                                }
                                if (ran1 + 1 <= mHeight && ran2 + 1 <= mWidth) {
                                    if (!mineFieldStatus[ran1 + 1][ran2 + 1].clicked) {
                                        String aString = (ran1 + 1) + "" + (ran2 + 1);
                                        int aId = Integer.parseInt(aString);
                                        Button a = findViewById(aId);
                                        a.performClick();
                                    }
                                }
                            }
                            buttonClicked.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    int numFlagged = 0;
                                    if (ran1 - 1 > 0 && ran2 - 1 > 0)
                                        if (mineFieldStatus[ran1 - 1][ran2 - 1].flagged())
                                            numFlagged++;
                                    if (ran1 - 1 > 0)
                                        if (mineFieldStatus[ran1 - 1][ran2].flagged())
                                            numFlagged++;
                                    if (ran1 - 1 > 0 && ran2 + 1 < mWidth)
                                        if (mineFieldStatus[ran1 - 1][ran2 + 1].flagged())
                                            numFlagged++;

                                    if (ran2 - 1 > 0)
                                        if (mineFieldStatus[ran1][ran2 - 1].flagged())
                                            numFlagged++;
                                    if (ran2 + 1 < mWidth)
                                        if (mineFieldStatus[ran1][ran2 + 1].flagged())
                                            numFlagged++;
                                    if (ran1 + 1 < mHeight && ran2 - 1 > 0)
                                        if (mineFieldStatus[ran1 + 1][ran2 - 1].flagged())
                                            numFlagged++;
                                    if (ran1 + 1 < mHeight)
                                        if (mineFieldStatus[ran1 + 1][ran2].flagged())
                                            numFlagged++;
                                    if (ran1 + 1 < mHeight && ran2 + 1 < mWidth)
                                        if (mineFieldStatus[ran1 + 1][ran2 + 1].flagged())
                                            numFlagged++;

                                    if (numFlagged == mineFieldStatus[ran1][ran2].value) {
                                        if (ran2 + 1 <= mWidth) {
                                            if (!mineFieldStatus[ran1][ran2 + 1].clicked) {
                                                String aString = ran1 + "" + (ran2 + 1);
                                                int aId = Integer.parseInt(aString);
                                                Button a = findViewById(aId);
                                                a.performClick();
                                            }
                                        }
                                        if (ran2 - 1 > 0) {
                                            if (!mineFieldStatus[ran1][ran2 - 1].clicked) {
                                                String aString = ran1 + "" + (ran2 - 1);
                                                int aId = Integer.parseInt(aString);
                                                Button a = findViewById(aId);
                                                a.performClick();
                                            }
                                        }
                                        if (ran1 - 1 > 0) {
                                            if (!mineFieldStatus[ran1 - 1][ran2].clicked) {
                                                String aString = (ran1 - 1) + "" + ran2;
                                                int aId = Integer.parseInt(aString);
                                                Button a = findViewById(aId);
                                                a.performClick();
                                            }
                                        }
                                        if (ran1 + 1 <= mHeight) {
                                            if (!mineFieldStatus[ran1 + 1][ran2].clicked) {
                                                String aString = (ran1 + 1) + "" + ran2;
                                                int aId = Integer.parseInt(aString);
                                                Button a = findViewById(aId);
                                                a.performClick();
                                            }
                                        }
                                        if (ran1 - 1 > 0 && ran2 - 1 > 0) {
                                            if (!mineFieldStatus[ran1 - 1][ran2 - 1].clicked) {
                                                String aString = (ran1 - 1) + "" + (ran2 - 1);
                                                int aId = Integer.parseInt(aString);
                                                Button a = findViewById(aId);
                                                a.performClick();
                                            }
                                        }
                                        if (ran1 - 1 > 0 && ran2 + 1 <= mWidth) {
                                            if (!mineFieldStatus[ran1 - 1][ran2 + 1].clicked) {
                                                String aString = (ran1 - 1) + "" + (ran2 + 1);
                                                int aId = Integer.parseInt(aString);
                                                Button a = findViewById(aId);
                                                a.performClick();
                                            }
                                        }
                                        if (ran1 + 1 <= mHeight && ran2 - 1 > 0) {
                                            if (!mineFieldStatus[ran1 + 1][ran2 - 1].clicked) {
                                                String aString = (ran1 + 1) + "" + (ran2 - 1);
                                                int aId = Integer.parseInt(aString);
                                                Button a = findViewById(aId);
                                                a.performClick();
                                            }
                                        }
                                        if (ran1 + 1 <= mHeight && ran2 + 1 <= mWidth) {
                                            if (!mineFieldStatus[ran1 + 1][ran2 + 1].clicked) {
                                                String aString = (ran1 + 1) + "" + (ran2 + 1);
                                                int aId = Integer.parseInt(aString);
                                                Button a = findViewById(aId);
                                                a.performClick();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
        Button firstClick = findViewById(prohibitedNum);
        firstClick.performClick();

    }

    public void gameOver() {
        startRun = false;
        Bundle extras = getIntent().getExtras();
        final int mWidth = Integer.parseInt(extras.getString("NUM_OF_COLUMNS"));
        final int mHeight = Integer.parseInt(extras.getString("NUM_OF_ROWS"));
        final Button goHome = findViewById(R.id.goHomeButton);
        goHome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toHomePage();
            }
        });
        goHome.setVisibility(View.VISIBLE);
        for (int i = 1; i <= mHeight; i++) {
            for (int j = 1; j <= mWidth; j++) {
                String buttonIdString = i + "" + j;
                int buttonId = Integer.parseInt(buttonIdString);
                Button mineButton = findViewById(buttonId);
                if (mineFieldStatus[i][j].value >= bombValue) {
                    mineButton.setBackgroundResource(R.drawable.bomb);
                } else {
                    switch (mineFieldStatus[i][j].value) {
                        case 0:
                            mineButton.setBackgroundResource(R.drawable.n0);
                            break;

                        case 1:
                            mineButton.setBackgroundResource(R.drawable.n1);
                            break;

                        case 2:
                            mineButton.setBackgroundResource(R.drawable.n2);
                            break;

                        case 3:
                            mineButton.setBackgroundResource(R.drawable.n3);
                            break;

                        case 4:
                            mineButton.setBackgroundResource(R.drawable.n4);
                            break;

                        case 5:
                            mineButton.setBackgroundResource(R.drawable.n5);
                            break;

                        case 6:
                            mineButton.setBackgroundResource(R.drawable.n6);
                            break;

                        case 7:
                            mineButton.setBackgroundResource(R.drawable.n7);
                            break;

                        case 8:
                            mineButton.setBackgroundResource(R.drawable.n8);
                            break;

                    }
                }
            }
        }
        TextView endGame = (TextView) findViewById(R.id.gameOverText);
        endGame.setVisibility(View.VISIBLE);
    }

    private void toHomePage() {
        Intent intent = new Intent(this, homeScreen.class);
        startActivity(intent);
    }
}
