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
    private int score;

    private List<Rect> rectangles;


    public GameState() {
        difficulty = 1;
        life = 3;
        score = 0;
        rectangles = new ArrayList<>();
    }

    public int getLife() { return life; }

    public int getDifficulty() { return difficulty; }

    public int getScore() { return score; }

    public List<Rect> getRectangleList() { return rectangles; }

}
