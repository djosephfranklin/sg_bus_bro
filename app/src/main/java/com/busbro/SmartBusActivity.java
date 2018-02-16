package com.busbro;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.busbro.adapter.BusNumberAdapter;
import com.busbro.adapter.SmartCellListAdapter;
import com.busbro.tabs.Favourites;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sgbus.nearestbus.BusStop;
import com.sgbus.nearestbus.NearestBus;
import com.sgbus.nearestbus.SmartBusStop;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by joseph on 26/9/17.
 */

public class SmartBusActivity extends AppCompatActivity {

    SmartCellListAdapter busNumberAdapter;
    NearestBus nearestBus = NearestBus.getInstance();
    ArrayList<SmartBusStop> busNumberDetails;
    public static final String PREFS_NAME = "BusBro";
    ListView theListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE );
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
        }
        setContentView(R.layout.smart_bus_details);

        Log.d("CONTENT","ENTRANCE");

        theListView = (ListView) findViewById(R.id.smartBusDetails);


        try {
            busNumberDetails = nearestBus.getSmartBusDetails(getApplicationContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        busNumberAdapter = new SmartCellListAdapter(getApplicationContext(),1,busNumberDetails);
        theListView.setAdapter(busNumberAdapter);


        ImageView appSearch = (ImageView) findViewById(R.id.appSearch);
        appSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Favourites.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });
        TextView appTitle = (TextView) findViewById(R.id.appTitle);
        appTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Favourites.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        AdView mAdView = (AdView) findViewById(R.id.adView);
        if(!BuildConfig.APPLICATION_ID.toString().toLowerCase().equalsIgnoreCase("com.busbro.premium")) {
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-7776551572587843/2864002813");
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
        }
    }

}
