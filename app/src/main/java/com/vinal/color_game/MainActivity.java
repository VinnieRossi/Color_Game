package com.vinal.color_game;



import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ImageView playScreen;
	private TextView score;
    private TextView gameTitle;
    private TextView quickPlayText;
    private TextView challengesText;
    private TextView settingsText;

    private Canvas canvas;

    private int highScore;
    private ArrayList<Integer> colors = new ArrayList<Integer>();


	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		playScreen = (ImageView) findViewById(R.id.playScreen);
		score = (TextView) findViewById(R.id.scoreValue);
        gameTitle = (TextView) findViewById(R.id.gameTitle);
        quickPlayText = (TextView) findViewById(R.id.quickPlayText);
        challengesText = (TextView) findViewById(R.id.challengesText);
        settingsText = (TextView) findViewById(R.id.settingsText);


        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        highScore = prefs.getInt("key", 0);
        String oS = highScore + "";
        score.setText(oS);

		canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(1080, 1600, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        playScreen.setImageBitmap(bitmap);
        playScreen.setBackgroundColor(getResources().getColor(R.color.black));

        colors.add(getResources().getColor(R.color.red));
        colors.add(getResources().getColor(R.color.orange));
        colors.add(getResources().getColor(R.color.yellow));
        colors.add(getResources().getColor(R.color.green));
        colors.add(getResources().getColor(R.color.blue));
        colors.add(getResources().getColor(R.color.violet));

        setTitle();

        quickPlayText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //intent.putExtra("intercept this", variable);
                Intent intent = new Intent(MainActivity.this, Game.class);
                MainActivity.this.startActivity(intent);
            }
        });

        challengesText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


            }
        });

        settingsText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Settings.class);
                MainActivity.this.startActivity(intent);
            }
        });
	}

    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        highScore = prefs.getInt("key", 0);
        String oS = highScore + "";
        score.setText(oS);
    }

    public void setTitle() {

        SpannableString text = new SpannableString(gameTitle.getText());
        text.setSpan(new ForegroundColorSpan(colors.get(0)), 0, 1, 0);
        text.setSpan(new ForegroundColorSpan(colors.get(1)), 1, 2, 0);
        text.setSpan(new ForegroundColorSpan(colors.get(2)), 2, 3, 0);
        text.setSpan(new ForegroundColorSpan(colors.get(3)), 3, 4, 0);
        text.setSpan(new ForegroundColorSpan(colors.get(4)), 4, 5, 0);
        text.setSpan(new ForegroundColorSpan(colors.get(5)), 6, 7, 0);
        text.setSpan(new ForegroundColorSpan(colors.get(0)), 7, 8, 0);
        text.setSpan(new ForegroundColorSpan(colors.get(1)), 8, 9, 0);
        text.setSpan(new ForegroundColorSpan(colors.get(2)), 9, 10, 0);
        gameTitle.setText(text);
    }

}

/*

IDEAS:
I like the look of blank squares with white background
Probably remove negative points for a life system, if you have full life at 10k, 25, 50k then you get a bonus. Or negative points scale?
Possible sweet spot in circle/different noise
Power ups? Explosive hit, laser rain, every good hit pops another bubble, etc
A colorwheel that determines the right color to hit?
Misclick/clicking when wrong color grays it out and requires 2 clicks now?
Flash red/white then fade out
Loading screen discs that fan out and line up
Dont let circles get too small
Background color settings?
achievements: double tap required, double speed, start with x circles, 1 life, etc


MOVE THIS ALL OUT

*/