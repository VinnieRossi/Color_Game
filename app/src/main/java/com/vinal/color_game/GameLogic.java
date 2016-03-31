package com.vinal.color_game;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

import java.util.List;

/**
 * Created by Vinnie on 3/30/2016.
 */
public class GameLogic {

    private GameState gameState;

    public GameLogic(GameState gameState) {
        this.gameState = gameState;
    }

    public boolean xNotWithinBounds(int x) {
        return x < 120 || x > 1320;
    }

    public boolean yNotWithinBounds(int y) {
        return y < 120 || y > 2015;
    }

    public int getRadius(Rect rect) {
        return (rect.right - rect.left)/2;
    }

    public Rect getContainingRect(int x, int y) {
        for (int i = 0; i < gameState.getRectangleList().size(); i++) {
            // contains might work now? look into later
            if ((x > gameState.getRectangleList().get(i).left && x < gameState.getRectangleList().get(i).right) && (y > gameState.getRectangleList().get(i).bottom && y < gameState.getRectangleList().get(i).top)) {
                return gameState.getRectangleList().get(i);
            }
        }
        return new Rect();
    }

    public int getContainingRectIndex(Rect toFind) {
        for (int i = 0; i < gameState.getRectangleList().size(); i++) {
            if ((toFind.left == gameState.getRectangleList().get(i).left && toFind.right == gameState.getRectangleList().get(i).right) && (toFind.bottom == gameState.getRectangleList().get(i).bottom && toFind.top == gameState.getRectangleList().get(i).top)) {
                return i;
            }
        }
        return -1;
    }

    public boolean haveOverlappingCircle(int x, int y, int radius) {
        for (int i = 0; i < gameState.getRectangleList().size(); i++) {
            if ((x > gameState.getRectangleList().get(i).left - radius && x < gameState.getRectangleList().get(i).right + radius) && (y > gameState.getRectangleList().get(i).bottom - radius && y < gameState.getRectangleList().get(i).top + radius)) {
                return true;
            }
        }
        return false;
    }

    public void removeCircle(int x, int y, Rect toRemove) {
        gameState.getRectangleList().remove(getContainingRectIndex(toRemove)); // index out of bounds
    }
}
