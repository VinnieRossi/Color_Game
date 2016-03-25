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
import android.os.Handler;
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

/**
 * Created by Vinnie on 12/16/2014.
 */
public class Game extends Activity implements View.OnTouchListener{

    private Thread thread;

    private ImageView playScreen;
    private TextView score;
    private ImageView x1;
    private ImageView x2;
    private ImageView x3;
    private ImageView check1;
    private ImageView check2;

    private Canvas canvas;
    private Paint paint;
    private Random rn = new Random();
    private MediaPlayer media = new MediaPlayer();
    private AssetManager assetMan;
    private FileInputStream mp3Stream;

    private List<Rect> rect;
    private ArrayList<Integer> colors = new ArrayList<>();
    private String currentSettings;
    private int difficulty = 1;
    private int prevScore;
    private int life = 3;
    private volatile boolean shouldRun = true;

    protected void onCreate(Bundle savedInstanceState) {
    // **Have a game state object with important variables**

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout);

        playScreen = (ImageView) findViewById(R.id.playScreen);
        score = (TextView) findViewById(R.id.scoreValue);
        x1 = (ImageView) findViewById(R.id.x1);
        x2 = (ImageView) findViewById(R.id.x2);
        x3 = (ImageView) findViewById(R.id.x3);
        check1 = (ImageView) findViewById(R.id.check1);
        check2 = (ImageView) findViewById(R.id.check2);
        // fix unnecessary image views

        //create setup function?
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        prevScore = prefs.getInt("key", 0);
        currentSettings = prefs.getString("settings", "011");

        canvas = new Canvas();
        paint = new Paint();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels - 425, Bitmap.Config.ARGB_8888);

        canvas.setBitmap(bitmap);
        playScreen.setImageBitmap(bitmap);
        playScreen.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        playScreen.setOnTouchListener(this);


        String mp3File = "raw/popping.mp3";
        assetMan = getAssets();
        try {

            mp3Stream = assetMan.openFd(mp3File).createInputStream();
            media.setDataSource(mp3Stream.getFD());//error after first try
            media.prepare();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        rect = new ArrayList<>();

        // create method that initialize list, easier testing
        colors.add(ContextCompat.getColor(this, R.color.red));
        colors.add(ContextCompat.getColor(this, R.color.red_orange));
        colors.add(ContextCompat.getColor(this, R.color.orange));
        colors.add(ContextCompat.getColor(this, R.color.yellow));
        colors.add(ContextCompat.getColor(this, R.color.green));
        colors.add(ContextCompat.getColor(this, R.color.turquoise));
        colors.add(ContextCompat.getColor(this, R.color.blue));
        colors.add(ContextCompat.getColor(this, R.color.violet_blue));
        colors.add(ContextCompat.getColor(this, R.color.violet));
        colors.add(ContextCompat.getColor(this, R.color.indigo));
        colors.add(ContextCompat.getColor(this, R.color.white));
        colors.add(ContextCompat.getColor(this, R.color.gray_cloud));
        colors.add(ContextCompat.getColor(this, R.color.gray));
        colors.add(ContextCompat.getColor(this, R.color.gray_dolphin));
        colors.add(ContextCompat.getColor(this, R.color.black_cat));

        // startGame()
        thread = new Thread() {
            public void run() {
                int x, y, rad, color, numTries;
                while (shouldRun) {
                    try {
                        Thread.sleep(1500 / difficulty);
                        x = rn.nextInt(playScreen.getWidth());
                        y = rn.nextInt(playScreen.getHeight());
                        rad = rn.nextInt(100) + (45 - (5 * difficulty));//100, 25, 3
                        color = colors.get(rn.nextInt(colors.size()-5));
                        numTries = 0;

                        while (xNotWithinBounds(x) || yNotWithinBounds(y) || haveOverlappingCircle(x, y, rad)) {
                            x = rn.nextInt(playScreen.getWidth());
                            y = rn.nextInt(playScreen.getHeight());

                            // make decision based on number of tries
                             checkGameState(numTries, rad);
/*
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
                            */
                        }

                        // drawOutlineRectangle()
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(getResources().getColor(R.color.black));
                        //paint.setColor(getResources().getColor(R.color.white)); // For testing

                        Rect temp = new Rect(x - rad, y + rad, x + rad, y - rad);
                        canvas.drawRect(temp, paint);
                        rect.add(temp);

                        // setThemeColor()
                        paint.setStyle(Paint.Style.FILL);
                        if (getCurrentTheme() == 0) {
                            paint.setColor(color);
                        } else if (getCurrentTheme() == 1) {
                            paint.setColor(colors.get(rn.nextInt(5) + 10));
                        } else if (getCurrentTheme() == 2) {
                            paint.setColor(colors.get(rn.nextInt(4)));
                        } else if (getCurrentTheme() == 3) {
                            paint.setColor(colors.get(rn.nextInt(5) + 5));
                        }

                        canvas.drawCircle(x, y, rad, paint);

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

            int points = getRadius(getContainingRect(x, y)) - 25;
            if (isInCircle(x, y)) {
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


    private boolean isInCircle(int x, int y) {
        Bitmap bitmap = ((BitmapDrawable) playScreen.getDrawable()).getBitmap();
        return bitmap.getPixel(x, y) != 0 && bitmap.getPixel(x, y) != ContextCompat.getColor(this, R.color.black);
    }

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

    private void removeCircle(int x, int y) {

        Rect toRemove = getContainingRect(x, y);
        paint.setColor(getResources().getColor(R.color.black));
        canvas.drawRect(toRemove.left, toRemove.top + 1, toRemove.right + 1, toRemove.bottom, paint);
        rect.remove(getContainingRectIndex(toRemove)); // index out of bounds
        playScreen.invalidate();
    }

    private int getRadius(Rect rect) {
        return (rect.right - rect.left)/2;
    }

    private void addPoints(int points) {

        String pointScore = (String) score.getText();
        int scoreAsInt = Integer.parseInt(pointScore) + (difficulty*(120-points));
        pointScore = "" + scoreAsInt;
        score.setText(pointScore);

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
                x3.setVisibility(View.VISIBLE);
                break;
            case 1:
                x2.setVisibility(View.VISIBLE);
                break;
            case 2:
                x1.setVisibility(View.VISIBLE);
                break;
            case 3:
                check1.setVisibility(View.INVISIBLE);
                break;
            case 4:
                check2.setVisibility(View.INVISIBLE);
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
                x3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                x2.setVisibility(View.INVISIBLE);
                break;
            case 3:
                x1.setVisibility(View.INVISIBLE);
                break;
            case 4:
                check1.setVisibility(View.VISIBLE);
                break;
            case 5:
                check2.setVisibility(View.VISIBLE);
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
            editor.commit();
        }
        shouldRun = false;
        finish();
    }

    private void lostSequence() {
        //use invisible fragment, turn visible and prompt/turn on buttons/etc
        playScreen.setBackgroundColor(getResources().getColor(R.color.black)); //ask to play again
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

    private boolean xNotWithinBounds(int x) {
        return x < 120 || x > 1320;
    }

    private boolean yNotWithinBounds(int y) {
        return y < 120 || y > 2015;
    }

    private boolean haveOverlappingCircle(int x, int y, int radius) {
        for (int i = 0; i < rect.size(); i++) {
            if ((x > rect.get(i).left - radius && x < rect.get(i).right + radius) && (y > rect.get(i).bottom - radius && y < rect.get(i).top + radius)) {
                return true;
            }
        }
        return false;
    }

    private void checkGameState(int numTries, int rad) {
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
