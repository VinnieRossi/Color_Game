package com.vinal.color_game;

import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinnie on 3/29/2016.
 */
public class GameState {

    private int difficulty;
    private int life;
    private int score;
    private List<Rect> rectangles;

    public GameState() {
        difficulty = 1;
        life = 3;
        score = 0;
        rectangles = new ArrayList<>();
    }

    public int getLife() { return life; }

    public void addLife(int amount) {
        life += amount;
    }

    public void removeLife(int amount) {
        life -= amount;
    }

    public int getDifficulty() { return difficulty; }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getScore() { return score; }

    public List<Rect> getRectangleList() { return rectangles; }

    public void addPoints(int amount) {
        int finalAmount = getDifficulty()*(120-amount);
        finalAmount *= 20; //debug
        score += finalAmount;
        if (shouldAddLife(score, finalAmount)) {
            addLife(1);
        }
    }

    private boolean shouldAddLife(int score, int finalAmount) {
        // If you just hit 10k or 25k points
        return ((score > 10000 && score - finalAmount < 10000) || (score > 25000 && score - finalAmount < 25000));
    }
}
