package com.vinal.color_game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import java.io.FileInputStream;
import java.util.Random;

public class Game extends Activity implements View.OnTouchListener{
    private Rect newRect;
    private Thread thread;
    private Activity activity;
    private ImageView playScreen;

    private GameState gameState;
    private GameLogic gameLogic;
    private GameGraphics gameGraphics;

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
        gameGraphics.setupGraphics();
        gameGraphics.initializeUsableColors(getCurrentTheme());
        playScreen.setOnTouchListener(this);

        startGame();
    }

    // TODO:
    // 1. Rewrite radius calculation
    // 2. Rewrite game ending logic
    // 3. Rewrite point from radius (currently 120 - radius - 25 multiplied by difficulty)
    // 4. Move UI screen invalidate to graphics?
    // 5. Add touch hitbox for circles (3x3?)
    // 6. Refactor more code out of Game class (should probably just be thread loop and on touch
    // 7. **Fix random bug where sometimes circle won't erase**
    // 8. Fix audio interruption - maybe never interrupt new life or loss of life, but popping has lowest priority?
    // 9. Add in bonus multiplier for <5 circles
    // 10. Add in bonus for how many in a row (X.Y, where x is 1 + (how many in a row/20) y is ((how many in a row/2) % 20 )
    // 11. Add in toggle for hitbox?
    // 12. Add in hard mode? it includes toggle?
    // 13. Is there a small graphical glitch? debug mode shows some circles outside the box

    private void startGame() {
        thread = new Thread() {
            public void run() {
                int x, y, radius, numTries;
                while (shouldRun) {
                    try {
                        Thread.sleep(1500 / gameState.getDifficulty());
                        x = rn.nextInt(playScreen.getWidth());
                        y = rn.nextInt(playScreen.getHeight());
                        // Rewrite radius logic
                        radius = rn.nextInt(100) + (45 - (5 * gameState.getDifficulty()));
                        numTries = 0;

                        while (gameLogic.xNotWithinBounds(x) || gameLogic.yNotWithinBounds(y) || gameLogic.haveOverlappingCircle(x, y, radius)) {
                            x = rn.nextInt(playScreen.getWidth());
                            y = rn.nextInt(playScreen.getHeight());
                            //Rewrite this entire section to determine game end (use difficulty as well?)

                            // Currently:
                            // 1. Randomly generate x and y for a circle
                            // 2. Verify it's a legal spot
                            // 3. If it's not and you've tried a few times, start to lower the radius
                            // 4. If you've lowered the radius multiple times and tried a lot of times, game ends

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

                        // Move this to graphics?
                        runOnUiThread(new Runnable() {
                            public void run() {
                                playScreen.invalidate();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Does this have any impact? Requires testing
                return;
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
                if (isSoundOn()) {
                    playSound("popping");
                }
                gameState.addPoints(gameLogic.determinePointValueOfCircle(x, y));
                removeCircle(x, y);
                gameGraphics.updateVisibleScore(gameState.getScore());

                if(gameState.shouldAddLife()) {
                    gameState.addLife(1);
                    playSound("life");
                    gameGraphics.setLifeStatusImages(gameState.getLife());
                }
                gameState.setDifficulty(gameLogic.determineDifficulty(gameState.getScore()));
            } else {
                removeLife();
            }
        }
        return true;
    }

    public void createNewCircle(int x, int y, int radius) {
        newRect = new Rect(x - radius, y + radius, x + radius, y - radius);
        gameGraphics.drawOutlineRectangle(newRect, isPowerUpOn());
        gameState.getRectangleList().add(newRect);
        gameGraphics.drawCircle(x, y, radius);
    }

    private boolean isInCircle(int x, int y, Bitmap bitmap) {
        return bitmap.getPixel(x, y) != 0 && bitmap.getPixel(x, y) != ContextCompat.getColor(this, R.color.black);
    }

    private void playSound(String sound) {
        // It's a bit hacky, but hey it works!
        media.release();
        switch(sound) {
            case "popping":
                media = MediaPlayer.create(this, R.raw.popping);
                break;
            case "life" :
                media = MediaPlayer.create(this, R.raw.life);
                break;
            case "lifeloss":
                media = MediaPlayer.create(this, R.raw.lifeloss);
                break;
            default:
                Log.e("EXCEPTION", "Could not find audio case of word: " + sound);
        }
        media.start();
    }

    private void removeCircle(int x, int y) {
        Rect toRemove = gameLogic.getContainingRect(x, y);
        gameLogic.removeCircle(x, y, toRemove);
        gameGraphics.eraseCircle(toRemove);
    }

    private void removeLife() {
        if(isSoundOn()) {
            playSound("lifeloss");
        }
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

    private boolean isSoundOn() {
        return currentSettings.substring(1, 2).equals("1");
    }

    private boolean isPowerUpOn() {
        return currentSettings.substring(2).equals("1");
    }

    private int getCurrentTheme() {
        return Integer.parseInt(currentSettings.substring(0, 1));
    }

    private void loadSettings() {
        // create enum settings since there are so few
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        prevScore = prefs.getInt("key", 0);
        currentSettings = prefs.getString("settings", "011");
    }
}