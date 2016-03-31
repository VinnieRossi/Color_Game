package com.vinal.color_game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Activity implements View.OnTouchListener{

    private Thread thread;

    private ImageView playScreen;
    private TextView score;
    private ImageView lifeStatusImage1;
    private ImageView lifeStatusImage2;
    private ImageView lifeStatusImage3;

    //private Canvas canvas;
    //private Paint paint;
    private Random rn = new Random();
    private MediaPlayer media = new MediaPlayer();
    private AssetManager assetMan;
    private FileInputStream mp3Stream;

    private GameState gameState;
    private GameLogic gameLogic;
    private GameGraphics gameGraphics;
    private Activity activity;

    //private List<Rect> rect; // move
    private ArrayList<Integer> colors = new ArrayList<>();
    private String currentSettings;
    private int difficulty = 1; // move
    private int prevScore; // remove
    //int score
    private int life = 3; // move
    private volatile boolean shouldRun = true;// move

    protected void onCreate(Bundle savedInstanceState) {
    // **Have a game state object with important variables**
        // Game state should NOT know about the UI, but SHOULD know about all game/state based actions

        // UI will ask GS if it should continue drawing the game
        // UI can grab rectangle to draw circle
        // UI gets x,y coords of clicks
        // UI will get life total and show appropriate UI views

        // GS will continue logic until user loses the game
        // GS will know about the outline rectangles
        // GS knows what to do with x,y coords of clicks
        // GS will know life total and report it to UI

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout);

        playScreen = (ImageView) findViewById(R.id.playScreen);
        score = (TextView) findViewById(R.id.scoreValue);
        lifeStatusImage1 = (ImageView) findViewById(R.id.lifeStatusImage1);
        lifeStatusImage2 = (ImageView) findViewById(R.id.lifeStatusImage2);
        lifeStatusImage3 = (ImageView) findViewById(R.id.lifeStatusImage3);



        activity = this;
        gameState = new GameState();
        gameLogic = new GameLogic(gameState);
        gameGraphics = new GameGraphics(activity);

        loadSettings();
        gameGraphics.setupGraphics();
        setupAudio();
        initializeUsableColors(getCurrentTheme());

        playScreen.setOnTouchListener(this);

        //rect = new ArrayList<>();



        // startGame() = initializes thread and starts it
        // remove variable declaration within thread
        thread = new Thread() {
            public void run() {
                int x, y, rad, color, numTries;
                while (shouldRun) {
                    try {
                        Thread.sleep(1500 / difficulty);
                        x = rn.nextInt(playScreen.getWidth());
                        y = rn.nextInt(playScreen.getHeight());
                        rad = rn.nextInt(100) + (45 - (5 * difficulty));//100, 25, 3
                        color = colors.get(rn.nextInt(colors.size()));
                        numTries = 0;

                        while (gameLogic.xNotWithinBounds(x) || gameLogic.yNotWithinBounds(y) || gameLogic.haveOverlappingCircle(x, y, rad)) {
                            x = rn.nextInt(playScreen.getWidth());
                            y = rn.nextInt(playScreen.getHeight());

                            if (numTries > 35) {
                                if (rad > 20) {
                                    rad--;
                                } else {
                                    if (numTries > 100) {
                                        onPause();
                                    } else if (numTries > 75) {
                                        // warn user about ending game
                                        Log.e("SYSTEM INFO", "GAME ABOUT TO END");
                                    }
                                }
                            }
                            numTries++;
                        }

                        // drawOutlineRectangle()
                        gameGraphics.getPaint().setStyle(Paint.Style.STROKE);
                        gameGraphics.getPaint().setColor(getResources().getColor(R.color.black));

                        // have rectangle map with key and circle value
                        Rect temp = new Rect(x - rad, y + rad, x + rad, y - rad);
                        gameGraphics.getCanvas().drawRect(temp, gameGraphics.getPaint());
                        gameState.getRectangleList().add(temp);
                        //rect.add(temp);

                        //drawCircle
                        gameGraphics.getPaint().setStyle(Paint.Style.FILL);
                        gameGraphics.getPaint().setColor(color);
                        gameGraphics.getCanvas().drawCircle(x, y, rad, gameGraphics.getPaint());

                        runOnUiThread(new Runnable() {
                            public void run() {
                                playScreen.invalidate();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            int points = gameLogic.getRadius(gameLogic.getContainingRect(x, y)) - 25;
            if (isInCircle(x, y, gameGraphics.getPlayscreenBitmap())) {
                // right color check
                if (getSoundOn()) {
                    if (media.isPlaying()) { // still probably not perfect
                        media.stop();
                        media.reset();
                        String mp3File = "raw/popping.mp3";
                        assetMan = getAssets();
                        try {
                            mp3Stream = assetMan.openFd(mp3File).createInputStream();
                            media.setDataSource(mp3Stream.getFD());
                            media.prepare();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        media.start();
                    }
                    media.start();
                }
                addPoints(points);
                removeCircle(x, y);
            } else {
                removeLife();
                // change to remove life
                // play sound
            }
        }
        return true;
    }


    private boolean isInCircle(int x, int y, Bitmap bitmap) {
        return bitmap.getPixel(x, y) != 0 && bitmap.getPixel(x, y) != ContextCompat.getColor(this, R.color.black);
    }
/*
    private Rect getContainingRect(int x, int y) {
        for (int i = 0; i < rect.size(); i++) {
            // contains doesn't seem to work..looks like y value is messed up
            if ((x > rect.get(i).left && x < rect.get(i).right) && (y > rect.get(i).bottom && y < rect.get(i).top)) {
                return rect.get(i);
            }
        }
        return new Rect();
    }

    private int getContainingRectIndex(Rect toFind) {
        for (int i = 0; i < rect.size(); i++) {
            if ((toFind.left == rect.get(i).left && toFind.right == rect.get(i).right) && (toFind.bottom == rect.get(i).bottom && toFind.top == rect.get(i).top)) {
                return i;
            }
        }
        return -1;
    }
*/
    private void removeCircle(int x, int y) {
        Rect toRemove = gameLogic.getContainingRect(x, y);
        gameLogic.removeCircle(x, y, toRemove);
        gameGraphics.eraseCircle(x, y, toRemove);
    }

    private void addPoints(int points) {
        points *= -20;
        String pointScore = (String) score.getText();
        int scoreAsInt = Integer.parseInt(pointScore) + (difficulty*(120-points));
        pointScore = "" + scoreAsInt;
        score.setText(pointScore);

        // move this
        if (scoreAsInt > 500 && scoreAsInt < 1000) {
            difficulty = 2;
        } else if (scoreAsInt > 1000 && scoreAsInt < 2500) {
            difficulty = 3;
        } else if (scoreAsInt > 2500 && scoreAsInt < 10000) {
            difficulty = 4;
        } else if (scoreAsInt > 10000 && scoreAsInt < 25000) {
            if (difficulty != 5) {
                addLife();
            }
            difficulty = 5;
        } else if (scoreAsInt > 25000) {
            if (difficulty != 6) {
                addLife();
            }
            difficulty = 6;
        }
    }

    private void removeLife() {
        life -= 1; //change color of background?

        switch (life) {
            case 0:
                lifeStatusImage3.setImageResource(R.drawable.red_x);
                break;
            case 1:
                lifeStatusImage2.setImageResource(R.drawable.red_x);
                break;
            case 2:
                lifeStatusImage1.setImageResource(R.drawable.red_x);
                break;
            case 3:
                lifeStatusImage1.setImageResource(android.R.color.transparent);
                break;
            case 4:
                lifeStatusImage2.setImageResource(android.R.color.transparent);
                break;
            default:
                onPause();
                break;
        }
    }

    private void addLife() {
        life += 1;
        switch (life) {
            case 1:
                lifeStatusImage3.setImageResource(android.R.color.transparent);
                break;
            case 2:
                lifeStatusImage2.setImageResource(android.R.color.transparent);
                break;
            case 3:
                lifeStatusImage1.setImageResource(android.R.color.transparent);
                break;
            case 4:
                lifeStatusImage1.setImageResource(R.drawable.green_check);
                break;
            case 5:
                lifeStatusImage2.setImageResource(R.drawable.green_check);
                break;
            default:
                break;
        }
    }

    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String pointScore = (String) score.getText();

        int score = Integer.parseInt(pointScore);

        if (score > prevScore) {
            editor.putInt("key", score);
            editor.apply();
        }
        shouldRun = false;
        finish();
    }

    private void lostSequence() {
        //ask to play again? Y - reset thread. N - quit thread, goto main
        playScreen.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
    }

    private boolean getSoundOn() {
        return currentSettings.substring(1, 2).equals("1");
    }

    private boolean getPowerUpOn() {
        return currentSettings.substring(2).equals("1");
    }

    private int getCurrentTheme() {
        return Integer.parseInt(currentSettings.substring(0, 1));
    }

    private void setupAudio() {
        String mp3File = "raw/popping.mp3";

        assetMan = getAssets();
        try {
            mp3Stream = assetMan.openFd(mp3File).createInputStream();
            media.setDataSource(mp3Stream.getFD());//error after first try
            media.prepare();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void initializeUsableColors(int theme) {
        if (theme == 0) {
            addHotColors();
            addCoolColors();
        } else if (theme == 1) {
            addGrayscaleColors();
        } else if (theme == 2) {
            addHotColors();
        } else if (theme == 3) {
            addCoolColors();
        }
    }

    private void addHotColors() {
        colors.add(ContextCompat.getColor(this, R.color.red));
        colors.add(ContextCompat.getColor(this, R.color.red_orange));
        colors.add(ContextCompat.getColor(this, R.color.orange));
        colors.add(ContextCompat.getColor(this, R.color.yellow));
    }

    private void addCoolColors() {
        colors.add(ContextCompat.getColor(this, R.color.green));
        colors.add(ContextCompat.getColor(this, R.color.turquoise));
        colors.add(ContextCompat.getColor(this, R.color.blue));
        colors.add(ContextCompat.getColor(this, R.color.violet_blue));
        colors.add(ContextCompat.getColor(this, R.color.violet));
        colors.add(ContextCompat.getColor(this, R.color.indigo));
    }

    private void addGrayscaleColors() {
        colors.add(ContextCompat.getColor(this, R.color.white));
        colors.add(ContextCompat.getColor(this, R.color.gray_cloud));
        colors.add(ContextCompat.getColor(this, R.color.gray));
        colors.add(ContextCompat.getColor(this, R.color.gray_dolphin));
        colors.add(ContextCompat.getColor(this, R.color.black_cat));
    }

    private void loadSettings() {
        // create enum settings since there are so few
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        prevScore = prefs.getInt("key", 0);
        currentSettings = prefs.getString("settings", "011");
    }
}

/*
// For image instead
Bitmap bt = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.color_wheel), 2*rad, 2*rad, false);
canvas.drawBitmap(bt, x-rad, y-rad, paint);

// For target/sweet spot
paint.setColor(getResources().getColor(R.color.white));//target depend on circle color?
canvas.drawCircle(x, y, rad/4, paint);
paint.setColor(getResources().getColor(R.color.red));
canvas.drawCircle(x, y, rad/6, paint);
paint.setColor(getResources().getColor(R.color.white));
canvas.drawCircle(x, y, rad/8, paint);
paint.setColor(getResources().getColor(R.color.red));
canvas.drawCircle(x, y, rad/16, paint);

*/
