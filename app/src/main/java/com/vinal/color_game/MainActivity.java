package com.vinal.color_game;


import java.lang.reflect.Array;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ImageView playScreen;
	private TextView score;
    private TextView gameTitle;
    private TextView quickPlay;
    private TextView challenges;
    private TextView settings;

    private Canvas canvas;

    private int highScore;
    private int oldScore;
    private ArrayList<Integer> colors = new ArrayList<Integer>();;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		playScreen = (ImageView) findViewById(R.id.playScreen);
		score = (TextView) findViewById(R.id.scoreValue);
        gameTitle = (TextView) findViewById(R.id.gameTitle);
        quickPlay = (TextView) findViewById(R.id.quickPlay);
        challenges = (TextView) findViewById(R.id.challenges);
        settings = (TextView) findViewById(R.id.settings);



        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
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

        quickPlay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Game.class);
                //intent.putExtra("intercept this", variable);
                MainActivity.this.startActivity(intent);
            }
        });

        challenges.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

            }
        });
	}

    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        oldScore = prefs.getInt("key", 0);
        String oS = oldScore + "";
        score.setText(oS);
    }

    protected void onPause() {
        super.onPause();
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
Power ups? Explosive hit, laser rain, etc
A colorwheel that determines the right color to hit?
Misclick/clicking when wrong color grays it out and requires 2 clicks now?
Flash red/white then fade out
Loading screen discs that fan out and line up
Dont let circles get too small

MOVE THIS ALL OUT

*/