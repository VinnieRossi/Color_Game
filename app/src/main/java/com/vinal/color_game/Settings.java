package com.vinal.color_game;



import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinnie on 12/16/2014.
 */
public class Settings extends Activity {

    private ImageView settingsScreen;
    private TextView back;
    private TextView themeText;
    private TextView soundText;
    private TextView powerUpText;
    private TextView themeValue;
    private TextView soundOnText;
    private TextView powerUpOnText;

    private String currentSettings;
    private int currentTheme = 0;
    private List<String> themes;
    private boolean soundOn = true;
    private boolean powerUpsOn = true;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_layout);

        settingsScreen = (ImageView) findViewById(R.id.settingsScreen);
        back = (TextView) findViewById(R.id.back);
        themeText = (TextView) findViewById(R.id.themeText);
        themeValue = (TextView) findViewById(R.id.themeValue);
        soundText = (TextView) findViewById(R.id.soundsText);
        soundOnText = (TextView) findViewById(R.id.soundValue);
        powerUpText = (TextView) findViewById(R.id.powerUpsText);
        powerUpOnText = (TextView) findViewById(R.id.powerUpValue);

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Bitmap bitmap = Bitmap.createBitmap(1080, 1600, Bitmap.Config.ARGB_8888);
        settingsScreen.setImageBitmap(bitmap);
        settingsScreen.setBackgroundColor(getResources().getColor(R.color.black));
        currentSettings = prefs.getString("settings", "011");

        themes = new ArrayList<String>();
        themes.add("Normal");
        themes.add("GrayScale");
        themes.add("Hot");
        themes.add("Cool");

        currentTheme = Integer.parseInt(currentSettings.substring(0, 1));
        soundOn = (currentSettings.substring(1, 2).equals("1"));
        powerUpsOn = (currentSettings.substring(2).equals("1"));

        if (soundOn) {
            soundOnText.setText("ON");
            soundOnText.setTextColor(getResources().getColor(R.color.green));
        } else {
            soundOnText.setText("OFF");
            soundOnText.setTextColor(getResources().getColor(R.color.red));
        }
        if (powerUpsOn) {
            powerUpOnText.setText("ON");
            powerUpOnText.setTextColor(getResources().getColor(R.color.green));
        } else {
            powerUpOnText.setText("OFF");
            powerUpOnText.setTextColor(getResources().getColor(R.color.red));
        }
        themeValue.setText(themes.get(currentTheme));
        setColoredText((String)themeValue.getText());

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onPause();
            }
        });

        themeText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                currentTheme = (currentTheme + 1) % themes.size();
                themeValue.setText(themes.get(currentTheme));
                setColoredText((String)themeValue.getText());
            }
        });

        themeValue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                currentTheme = (currentTheme + 1) % themes.size();
                themeValue.setText(themes.get(currentTheme));
                setColoredText((String)themeValue.getText());
            }
        });

        soundText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                soundOn = !soundOn;
                if (soundOn) {
                    soundOnText.setText("ON");
                    soundOnText.setTextColor(getResources().getColor(R.color.green));
                } else {
                    soundOnText.setText("OFF");
                    soundOnText.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        soundOnText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                soundOn = !soundOn;
                if (soundOn) {
                    soundOnText.setText("ON");
                    soundOnText.setTextColor(getResources().getColor(R.color.green));
                } else {
                    soundOnText.setText("OFF");
                    soundOnText.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        powerUpText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                powerUpsOn = !powerUpsOn;
                if (powerUpsOn) {
                    powerUpOnText.setText("ON");
                    powerUpOnText.setTextColor(getResources().getColor(R.color.green));
                } else {
                    powerUpOnText.setText("OFF");
                    powerUpOnText.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });

        powerUpOnText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                powerUpsOn = !powerUpsOn;
                if (powerUpsOn) {
                    powerUpOnText.setText("ON");
                    powerUpOnText.setTextColor(getResources().getColor(R.color.green));
                } else {
                    powerUpOnText.setText("OFF");
                    powerUpOnText.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        currentSettings = "" + currentTheme;
        currentSettings += soundOn ? "1" : "0";
        currentSettings += powerUpsOn ? "1" : "0";
        editor.putString("settings", currentSettings);
        editor.commit();
        finish();
    }

    public void setColoredText(String themeText) {

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.red));
        colors.add(getResources().getColor(R.color.red_orange));
        colors.add(getResources().getColor(R.color.orange));
        colors.add(getResources().getColor(R.color.yellow));
        colors.add(getResources().getColor(R.color.green));
        colors.add(getResources().getColor(R.color.green_apple));
        colors.add(getResources().getColor(R.color.turquoise));
        colors.add(getResources().getColor(R.color.blue));
        colors.add(getResources().getColor(R.color.violet_blue));
        colors.add(getResources().getColor(R.color.violet));
        colors.add(getResources().getColor(R.color.indigo));
        colors.add(getResources().getColor(R.color.white));
        colors.add(getResources().getColor(R.color.gray_cloud));
        colors.add(getResources().getColor(R.color.gray));
        colors.add(getResources().getColor(R.color.gray_dolphin));
        colors.add(getResources().getColor(R.color.black_cat));

        SpannableString text = new SpannableString(themeValue.getText());
        if (themeText.equals("Normal")) {
            text.setSpan(new ForegroundColorSpan(colors.get(0)), 0, 1, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(2)), 1, 2, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(3)), 2, 3, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(5)), 3, 4, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(7)), 4, 5, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(10)), 5, 6, 0);
        } else if (themeText.equals("GrayScale")) {
            text.setSpan(new ForegroundColorSpan(colors.get(11)), 0, 1, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(12)), 1, 2, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(13)), 2, 3, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(14)), 3, 4, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(15)), 4, 5, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(11)), 5, 6, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(12)), 6, 7, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(13)), 7, 8, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(14)), 8, 9, 0);
        }  else if (themeText.equals("Hot")) {
            text.setSpan(new ForegroundColorSpan(colors.get(0)), 0, 1, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(1)), 1, 2, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(2)), 2, 3, 0);
        } else if (themeText.equals("Cool")) {
            text.setSpan(new ForegroundColorSpan(colors.get(7)), 0, 1, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(8)), 1, 2, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(9)), 2, 3, 0);
            text.setSpan(new ForegroundColorSpan(colors.get(10)), 3, 4, 0);
        }
        themeValue.setText(text);
    }

}
