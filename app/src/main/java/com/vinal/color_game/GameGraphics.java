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

/**
 * Created by Vinnie on 3/30/2016.
 */
public class GameGraphics {

    private ImageView playScreen;
    private Canvas canvas;
    private Paint paint;
    private Activity activity;

    public GameGraphics(Activity activity) {
        this.activity = activity;
        playScreen = (ImageView) activity.findViewById(R.id.playScreen);
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

    public Paint getPaint() { return paint; }

    public Canvas getCanvas() { return canvas; }

    public void eraseCircle(int x, int y, Rect toRemove) {
        //paint.setColor(activity.getResources().getColor(R.color.black));
        paint.setColor(ContextCompat.getColor(activity, R.color.black));
        canvas.drawRect(toRemove.left, toRemove.top + 1, toRemove.right + 1, toRemove.bottom, paint);
        playScreen.invalidate();
    }

    public Bitmap getPlayscreenBitmap() {
        return ((BitmapDrawable) playScreen.getDrawable()).getBitmap();
    }

    public int getScreenBackgroundColor() {
        return ContextCompat.getColor(activity, R.color.black);
    }


}
