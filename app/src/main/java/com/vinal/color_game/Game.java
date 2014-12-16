package com.vinal.color_game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

    private Handler aHandler = new Handler();
    private Thread thread;

    private ImageView playScreen;
    private TextView score;

    private Canvas canvas;
    private Paint paint;
    private Random rn = new Random();
    private MediaPlayer media = new MediaPlayer();
    private AssetManager assetMan;
    private FileInputStream mp3Stream;

    private List<Rect> rect;
    private List<Integer> colors;
    private int difficulty = 1;
    //private int highScore = 0;
    private int prevScore;
    //private int life = 3;
    private boolean shouldRun = true;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout);
        Intent intent = getIntent();
        //String value = intent.getStringExtra("intercept this");

        playScreen = (ImageView) findViewById(R.id.playScreen);
        score = (TextView) findViewById(R.id.scoreValue);

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        int oldHighScore = prefs.getInt("key", 0);
        prevScore = oldHighScore;


        canvas = new Canvas();
        paint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(1080, 1600, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        playScreen.setImageBitmap(bitmap);
        playScreen.setBackgroundColor(getResources().getColor(R.color.black));
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
        rect = new ArrayList<Rect>();


        // Just for testing with circles
        final ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.red));
        colors.add(getResources().getColor(R.color.orange));
        colors.add(getResources().getColor(R.color.yellow));
        colors.add(getResources().getColor(R.color.green_apple));
        colors.add(getResources().getColor(R.color.blue));
        colors.add(getResources().getColor(R.color.violet));

        thread = new Thread() {
            public void run() {
                while (shouldRun) {
                    try {
                        Thread.sleep(1500 / difficulty);
                        int x = rn.nextInt(playScreen.getWidth());
                        int y = rn.nextInt(playScreen.getHeight());
                        int rad = rn.nextInt(100) + (25 - (3 * difficulty));
                        int color = colors.get(rn.nextInt(colors.size()));
                        int numTries = 0;
                        boolean checkOne = false;
                        boolean checkTwo = false;
                        boolean checkThree = false;

                        while (!(checkOne && checkTwo && checkThree)) {
                            x = rn.nextInt(playScreen.getWidth());
                            y = rn.nextInt(playScreen.getHeight());

                            checkOne = !(x < 120 || x > 960);
                            checkTwo = !(y < 120 || y > 1480);
                            checkThree = !(isInRectangle(x, y, rad));

                            if (numTries > 35) {
                                if (rad > 7) {
                                    rad--;
                                } else {
                                    //pointDecay();
                                    System.out.println("Inefficiency decay.");
                                    onPause();
                                }
                            }
                            numTries++;
                        }

                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(getResources().getColor(R.color.black));
                        //paint.setColor(getResources().getColor(R.color.white)); // For testing

                        Rect temp = new Rect(x - rad, y + rad, x + rad, y - rad);
                        canvas.drawRect(temp, paint);
                        rect.add(temp);

                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor(color);
                        canvas.drawCircle(x, y, rad, paint);

                        // For image instead
                        //Bitmap bt = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.color_wheel), 2*rad, 2*rad, false);
                        //canvas.drawBitmap(bt, x-rad, y-rad, paint);

                        // For target/sweet spot
                        /*
                                paint.setColor(getResources().getColor(R.color.white));//target depend on circle color?
                                canvas.drawCircle(x, y, rad/4, paint);
                                paint.setColor(getResources().getColor(R.color.red));
                                canvas.drawCircle(x, y, rad/6, paint);
                                paint.setColor(getResources().getColor(R.color.white));
                                canvas.drawCircle(x, y, rad/8, paint);
                                paint.setColor(getResources().getColor(R.color.red));
                                canvas.drawCircle(x, y, rad/16, paint);
                        */

                        runOnUiThread(new Runnable() {
                            public void run() {
                                playScreen.invalidate();
                            }
                        });

                    } catch (Exception e) {
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

            if (isInCircle(x, y)) {
                // right color check
                int points = getRadius(getContainingRect(x, y));

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
                addPoints(points);
                removeCircle(x, y);
            } else {
                removePoints();
                // change to remove life
                // play sound
            }
        }
        return true;
    }

    public boolean isInRectangle(int x, int y, int radius) {
        for (int i = 0; i < rect.size(); i++) {
            if ((x > rect.get(i).left - radius && x < rect.get(i).right + radius) && (y > rect.get(i).bottom - radius && y < rect.get(i).top + radius)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCircle(int x, int y) {

        Bitmap bitmap = ((BitmapDrawable) playScreen.getDrawable()).getBitmap();
        //System.out.println("Color of pixel: " + bitmap.getPixel(x, y));
        return bitmap.getPixel(x, y) != 0 && bitmap.getPixel(x, y) != -16777216; // Check if it's black
    }

    public Rect getContainingRect(int x, int y) {

        for (int i = 0; i < rect.size(); i++) {
            // contains doesn't seem to work..looks like y value is messed up
            if ((x > rect.get(i).left && x < rect.get(i).right) && (y > rect.get(i).bottom && y < rect.get(i).top)) {
                return rect.get(i);
            }
        }
        return new Rect();
    }

    public int getContainingRectIndex(Rect toFind) {
        for (int i = 0; i < rect.size(); i++) {
            if ((toFind.left == rect.get(i).left && toFind.right == rect.get(i).right) && (toFind.bottom == rect.get(i).bottom && toFind.top == rect.get(i).top)) {
                return i;
            }
        }
        return -1;
    }

    public void removeCircle(int x, int y) {

        Rect toRemove = getContainingRect(x, y);
        paint.setColor(getResources().getColor(R.color.black));
        canvas.drawRect(toRemove.left, toRemove.top + 1, toRemove.right + 1, toRemove.bottom, paint);
        rect.remove(getContainingRectIndex(toRemove));
        playScreen.invalidate();
    }

    public int getRadius(Rect rect) {
        return (rect.right - rect.left)/2;
    }

    public void addPoints(int points) {

        String pointScore = (String) score.getText();
        int scoreAsInt = Integer.parseInt(pointScore) + (difficulty*(120-points));
        pointScore = "" + scoreAsInt;
        score.setText(pointScore);

        if (scoreAsInt > 250 && scoreAsInt < 1000) {
            difficulty = 2;
        } else if (scoreAsInt > 1000 && scoreAsInt < 2500) {
            difficulty = 3;
        } else if (scoreAsInt > 2500 && scoreAsInt < 10000) {
            difficulty = 4;
        } else if (scoreAsInt > 10000 && scoreAsInt < 25000) {
            difficulty = 5;
        } else if (scoreAsInt > 25000) {
            difficulty = 6;
        }
    }

    public void removePoints() {

        /*
        life -= 1;
        switch(life ) { // clear background as black, that is now overlayed. will have to clear board and now make clearing this color
            case 0:
                System.out.println("GAME OVER");
                //playScreen.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case 1:
                //playScreen.setBackgroundColor(getResources().getColor(R.color.persian_pink));
                break;
            case 2:
                //playScreen.setBackgroundColor(getResources().getColor(R.color.cherry_blossom_pink));
                break;
            case 3:
                playScreen.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            default:
        }
        */

        String pointScore = (String) score.getText();
        int scoreAsInt = Integer.parseInt(pointScore) - (difficulty*100);
        pointScore = "" + scoreAsInt;
        score.setText(pointScore);
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
}
