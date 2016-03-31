package com.vinal.color_game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class Game extends Activity implements View.OnTouchListener{
    private Rect newRect;
    private Thread thread;
    private Activity activity;
    private ImageView playScreen;

    private GameState gameState;
    private GameLogic gameLogic;
    private GameGraphics gameGraphics;

    private AssetManager assetMan;
    private FileInputStream mp3Stream;
    private MediaPlayer media = new MediaPlayer();

    private int prevScore;
    private String currentSettings;
    private Random rn = new Random();
    private volatile boolean shouldRun = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout);

        playScreen = (ImageView) findViewById(R.id.playScreen);
        activity = this;
        gameState = new GameState();
        gameLogic = new GameLogic(gameState);
        gameGraphics = new GameGraphics(activity);

        loadSettings();
        setupAudio();
        gameGraphics.setupGraphics();
        gameGraphics.initializeUsableColors(getCurrentTheme());
        playScreen.setOnTouchListener(this);

        startGame();
    }

    private void startGame() {
        thread = new Thread() {
            public void run() {
                int x, y, radius, numTries;
                while (shouldRun) {
                    try {
                        Thread.sleep(1500 / gameState.getDifficulty());
                        x = rn.nextInt(playScreen.getWidth());
                        y = rn.nextInt(playScreen.getHeight());
                        radius = rn.nextInt(100) + (45 - (5 * gameState.getDifficulty()));//100, 25, 3
                        numTries = 0;

                        while (gameLogic.xNotWithinBounds(x) || gameLogic.yNotWithinBounds(y) || gameLogic.haveOverlappingCircle(x, y, radius)) {
                            x = rn.nextInt(playScreen.getWidth());
                            y = rn.nextInt(playScreen.getHeight());

                            if (numTries > 35) {
                                if (radius > 20) {
                                    radius--;
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
                        createNewCircle(x, y, radius);

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

            if (isInCircle(x, y, gameGraphics.getPlayscreenBitmap())) {
                if (getSoundOn()) {
                    playPoppingSound();
                }
                gameState.addPoints(gameLogic.determinePointValueOfCircle(x, y));
                removeCircle(x, y);
                gameGraphics.updateVisibleScore(gameState.getScore());
                gameGraphics.setLifeStatusImages(gameState.getLife());
                gameState.setDifficulty(gameLogic.determineDifficulty(gameState.getScore()));
            } else {
                removeLife();
            }
        }
        return true;
    }

    public void createNewCircle(int x, int y, int radius) {
        newRect = new Rect(x - radius, y + radius, x + radius, y - radius);
        gameGraphics.drawOutlineRectangle(newRect);
        gameState.getRectangleList().add(newRect);
        gameGraphics.drawCircle(x, y, radius);
    }

    private boolean isInCircle(int x, int y, Bitmap bitmap) {
        return bitmap.getPixel(x, y) != 0 && bitmap.getPixel(x, y) != ContextCompat.getColor(this, R.color.black);
    }

    private void playPoppingSound() {
        if (media.isPlaying()) {
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

    private void removeCircle(int x, int y) {
        Rect toRemove = gameLogic.getContainingRect(x, y);
        gameLogic.removeCircle(x, y, toRemove);
        gameGraphics.eraseCircle(toRemove);
    }

    private void removeLife() {
        gameState.removeLife(1);
        gameGraphics.setLifeStatusImages(gameState.getLife());
        if (gameState.getLife() < 0) {
            onPause();
        }
    }

    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (gameState.getScore() > prevScore) {
            editor.putInt("key", gameState.getScore());
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

    private void loadSettings() {
        // create enum settings since there are so few
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        prevScore = prefs.getInt("key", 0);
        currentSettings = prefs.getString("settings", "011");
    }
}