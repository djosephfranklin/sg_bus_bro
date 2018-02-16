package com.busbro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.busbro.tabs.Favourites;

/**
 * Created by joseph on 14/9/17.
 */

public class TempActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "BusBro";
    String goBackFavBusStops ="";
    String onLoadFavBusStops ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_bus);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        onLoadFavBusStops = settings.getString("favBusStops", "");
        Log.d("OnStart",""+onLoadFavBusStops);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 1);
        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
        }


        TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
        pageTitle.setText("Nearby");
        pageTitle.setTextColor(Color.BLACK);
        final ImageView backPress = (ImageView) findViewById(R.id.backPress);
        TextView homeTitle = (TextView) findViewById(R.id.appTitle);
        homeTitle.setTextColor(Color.BLACK);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                goBackFavBusStops = settings.getString("favBusStops", "");
                Log.d("OnEnd",""+goBackFavBusStops);
                if(!goBackFavBusStops.equalsIgnoreCase(onLoadFavBusStops)) {
                    Intent intent = new Intent(getApplicationContext(), Favourites.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                finish();
            }
        });
        homeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                goBackFavBusStops = settings.getString("favBusStops", "");
                Log.d("OnEnd",""+goBackFavBusStops);
                if(!goBackFavBusStops.equalsIgnoreCase(onLoadFavBusStops)) {
                    Intent intent = new Intent(getApplicationContext(), Favourites.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                finish();
            }
        });

        //getWindow().setWindowAnimations(R.style.PauseDialogAnimation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        goBackFavBusStops = settings.getString("favBusStops", "");
        Log.d("OnEnd",""+goBackFavBusStops);
        if(!goBackFavBusStops.equalsIgnoreCase(onLoadFavBusStops)){
            Intent intent = new Intent(getApplicationContext(), Favourites.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        finish();
    }
}
