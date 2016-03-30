package com.vinal.color_game;

/**
 * Created by Vinnie on 3/29/2016.
 */
public class GameState {

    private int difficulty;
    private int life;
    private int score;
    private volatile boolean shouldRun;
    private final GameState instance = new GameState();

    private GameState() {
        difficulty = 1;
        life = 3;
        score = 0;
        shouldRun = true;
    }

    public GameState getInstance() { return instance; }

    private int getLife() { return life; }

    private int getDifficulty() { return difficulty; }

    private int getScore() { return score; }

    private boolean shouldRun() { return shouldRun; }

    private void doGame() {

    }

}
