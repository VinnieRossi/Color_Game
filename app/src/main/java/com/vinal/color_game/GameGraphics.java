package com.vinal.color_game;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Vinnie on 3/30/2016.
 */
public class GameGraphics {

    private TextView score;
    private ImageView lifeStatusImage1;
    private ImageView lifeStatusImage2;
    private ImageView lifeStatusImage3;
    private ImageView playScreen;

    private Canvas canvas;
    private Paint paint;
    private Activity activity;
    private ArrayList<Integer> colors;
    private Random rn;

    public GameGraphics(Activity activity) {
        this.activity = activity;
        colors = new ArrayList<>();
        rn = new Random();

        score = (TextView) activity.findViewById(R.id.scoreValue);
        playScreen = (ImageView) activity.findViewById(R.id.playScreen);
        lifeStatusImage1 = (ImageView) activity.findViewById(R.id.lifeStatusImage1);
        lifeStatusImage2 = (ImageView) activity.findViewById(R.id.lifeStatusImage2);
        lifeStatusImage3 = (ImageView) activity.findViewById(R.id.lifeStatusImage3);
    }

    public void setupGraphics() {
        canvas = new Canvas();
        paint = new Paint();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels - 425, Bitmap.Config.ARGB_8888);

        canvas.setBitmap(bitmap);
        playScreen.setImageBitmap(bitmap);
        playScreen.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
    }

    public void eraseCircle(Rect toRemove) {
        paint.setColor(ContextCompat.getColor(activity, R.color.black));
        canvas.drawRect(toRemove.left, toRemove.top + 1, toRemove.right + 1, toRemove.bottom, paint);
        playScreen.invalidate();
    }

    public Bitmap getPlayscreenBitmap() {
        return ((BitmapDrawable) playScreen.getDrawable()).getBitmap();
    }

    public void updateVisibleScore(int currentScore) {
        score.setText("" + currentScore);
    }

    public void drawOutlineRectangle(Rect newRect) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(ContextCompat.getColor(activity, R.color.black));
        //paint.setColor(ContextCompat.getColor(activity, R.color.white)); // debug
        canvas.drawRect(newRect, paint);
    }

    public void drawCircle(int x, int y, int radius) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colors.get(rn.nextInt(colors.size())));
        canvas.drawCircle(x, y, radius, paint);
    }

    public void setLifeStatusImages(int life) {
        switch (life) {
            case 0:
                lifeStatusImage1.setImageResource(R.drawable.red_x);
                lifeStatusImage2.setImageResource(R.drawable.red_x);
                lifeStatusImage3.setImageResource(R.drawable.red_x);
                break;
            case 1:
                lifeStatusImage1.setImageResource(R.drawable.red_x);
                lifeStatusImage2.setImageResource(R.drawable.red_x);
                lifeStatusImage3.setImageResource(android.R.color.transparent);
                break;
            case 2:
                lifeStatusImage1.setImageResource(R.drawable.red_x);
                lifeStatusImage2.setImageResource(android.R.color.transparent);
                lifeStatusImage3.setImageResource(android.R.color.transparent);
                break;
            case 3:
                lifeStatusImage1.setImageResource(android.R.color.transparent);
                lifeStatusImage2.setImageResource(android.R.color.transparent);
                lifeStatusImage3.setImageResource(android.R.color.transparent);
                break;
            case 4:
                lifeStatusImage1.setImageResource(R.drawable.green_check);
                lifeStatusImage2.setImageResource(android.R.color.transparent);
                lifeStatusImage3.setImageResource(android.R.color.transparent);
                break;
            case 5:
                lifeStatusImage1.setImageResource(R.drawable.green_check);
                lifeStatusImage2.setImageResource(R.drawable.green_check);
                lifeStatusImage3.setImageResource(android.R.color.transparent);
                break;
            default:
                break;
        }
    }

    public void initializeUsableColors(int theme) {
        if (theme == 0) {
            addHotColors();
            addCoolColors();
        } else if (theme == 1) {
            addGrayscaleColors();
        } else if (theme == 2) {
            addHotColors();
        } else if (theme == 3) {
            addCoolColors();
        }
    }

    private void addHotColors() {
        colors.add(ContextCompat.getColor(activity, R.color.red));
        colors.add(ContextCompat.getColor(activity, R.color.red_orange));
        colors.add(ContextCompat.getColor(activity, R.color.orange));
        colors.add(ContextCompat.getColor(activity, R.color.yellow));
    }

    private void addCoolColors() {
        colors.add(ContextCompat.getColor(activity, R.color.green));
        colors.add(ContextCompat.getColor(activity, R.color.turquoise));
        colors.add(ContextCompat.getColor(activity, R.color.blue));
        colors.add(ContextCompat.getColor(activity, R.color.violet_blue));
        colors.add(ContextCompat.getColor(activity, R.color.violet));
        colors.add(ContextCompat.getColor(activity, R.color.indigo));
    }

    private void addGrayscaleColors() {
        colors.add(ContextCompat.getColor(activity, R.color.white));
        colors.add(ContextCompat.getColor(activity, R.color.gray_cloud));
        colors.add(ContextCompat.getColor(activity, R.color.gray));
        colors.add(ContextCompat.getColor(activity, R.color.gray_dolphin));
        colors.add(ContextCompat.getColor(activity, R.color.black_cat));
    }
}
