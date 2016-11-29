package com.vinal.color_game;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinnie on 3/29/2016.
 */
public class GameState {

    private int difficulty;
    private int life;
    private int previousScore;
    private int score;
    private List<Rect> rectangles;

    public GameState() {
        difficulty = 1;
        life = 3;
        score = 0;
        previousScore = 0;
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
        //finalAmount *= 20; // For debugging purposes
        previousScore = score;
        score += finalAmount;
        //if (shouldAddLife(score, finalAmount)) {
        //    addLife(1);
        //}
    }

    public boolean shouldAddLife() {
        // If you just hit 10k or 25k points
        return previousScore < 10000 && score >= 10000 || previousScore < 25000 && score >= 25000;
        //return ((score > 10000 && score - finalAmount < 10000) || (score > 25000 && score - finalAmount < 25000));
    }
}
