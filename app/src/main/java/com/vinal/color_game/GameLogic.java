package com.vinal.color_game;

import android.graphics.Rect;

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
        gameState.getRectangleList().remove(getContainingRectIndex(toRemove));
    }

    public int determinePointValueOfCircle(int x, int y) {
        return getRadius(getContainingRect(x, y)) - 25;
    }

    public int determineDifficulty(int score) {
        if (score > 25000) {
            return 6;
        } else if (score > 10000) {
            return 5;
        } else if (score > 2500) {
            return 4;
        } else if (score > 1000) {
            return 3;
        } else if (score > 500) {
            return 2;
        }
        return 1;
    }
}