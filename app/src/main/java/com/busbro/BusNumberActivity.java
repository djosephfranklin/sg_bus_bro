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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.busbro.adapter.BusNumberAdapter;
import com.busbro.tabs.Favourites;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sgbus.nearestbus.BusStop;
import com.sgbus.nearestbus.NearestBus;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by joseph on 26/9/17.
 */

public class BusNumberActivity extends AppCompatActivity {

    BusNumberAdapter busNumberAdapter;
    NearestBus nearestBus = NearestBus.getInstance();
    ArrayList<BusStop> busNumberDetails;
    public static final String PREFS_NAME = "BusBro";
    ListView theListView;
    String createdFavBusStops;

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
        setContentView(R.layout.bus_number_details);

        Log.d("CONTENT","ENTRANCE");

        theListView = (ListView) findViewById(R.id.busNbrDetails);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String busNumber = settings.getString("busNumber", "");
        String busDirection = settings.getString("busDirection", "1");

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(busNumber);
        jsonArray.put(busDirection);

        try {
            busNumberDetails = nearestBus.searchBusNbr(getApplicationContext(),jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        busNumberAdapter = new BusNumberAdapter(getApplicationContext(),1,busNumberDetails);
        theListView.setAdapter(busNumberAdapter);

        TextView from = (TextView) findViewById(R.id.from);
        from.setText(nearestBus.getBusNbrDetails(getApplicationContext(),"from",busNumber));
        TextView to = (TextView) findViewById(R.id.to);
        to.setText(nearestBus.getBusNbrDetails(getApplicationContext(),"to",busNumber));

        LinearLayout directionLayout = (LinearLayout) findViewById(R.id.directionLayout);
        directionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                String busNumber = settings.getString("busNumber", "");
                String busDirection = settings.getString("busDirection", "");
                Log.d("BUSNUMBER",""+busNumber);
                Log.d("BUSDIRECTION",""+busDirection);
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                if(busDirection.equalsIgnoreCase("1")){
                    busDirection="2";
                    editor.putString("busDirection",""+busDirection);
                    editor.commit();
                }else{
                    busDirection="1";
                    editor.putString("busDirection",""+busDirection);
                    editor.commit();
                }
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(busNumber);
                jsonArray.put(busDirection);

                try {
                    busNumberDetails = nearestBus.searchBusNbr(getApplicationContext(),jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                busNumberAdapter = new BusNumberAdapter(getApplicationContext(),1,busNumberDetails);
                theListView.setAdapter(busNumberAdapter);
                busNumberAdapter.notifyDataSetChanged();
                TextView from = (TextView) findViewById(R.id.from);
                from.setText(nearestBus.getBusNbrDetails(getApplicationContext(),"from",busNumber));
                TextView to = (TextView) findViewById(R.id.to);
                to.setText(nearestBus.getBusNbrDetails(getApplicationContext(),"to",busNumber));
            }
        });

        theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                int newFav=0;
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                createdFavBusStops = settings.getString("favBusStops", "");
                String newFavBusStops="";
                String[] busStopArray = createdFavBusStops.split(",");
                String content =busNumberDetails.get(pos).getBusStopCode();
                for(int i=0;i<busStopArray.length;i++) {
                    if(!"".equalsIgnoreCase(settings.getString("Search",""))){
                        Log.d("BusStopDetail","Search Bus List"+settings.getString("Search","NULL"));
                        if (busNumberDetails.get(pos).getBusStopCode().equalsIgnoreCase(busStopArray[i])){
                            newFav=1;
                        }else {
                            if(!busStopArray[i].equals("")) {
                                newFavBusStops = newFavBusStops + "," + busStopArray[i];
                            }
                            content =busNumberDetails.get(pos).getBusStopCode();
                        }

                    }else {
                        if (busStopArray[i].equalsIgnoreCase(busNumberDetails.get(pos).getBusStopCode())) {
                            newFav = 1;
                        } else {
                            if (!busStopArray[i].equals("")) {
                                newFavBusStops = newFavBusStops + "," + busStopArray[i];
                            }
                            content=busNumberDetails.get(pos).getBusStopCode();
                        }

                        // finish();
                    }
                }
                if(newFav==1) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("favBusStops","");
                    editor.putString("favBusStops", newFavBusStops);
                    editor.commit();
                    AlertDialog.Builder alertFav = new AlertDialog.Builder(BusNumberActivity.this);
                    //alertFav.setTitle("Favourites");
                    alertFav.setMessage("Favorites Removed");
                    alertFav.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(getApplicationContext(), Favourites.class),1);
                            dialog.dismiss();

                        }
                    });
                    alertFav.show();
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(500);
                }else {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("favBusStops", createdFavBusStops + "," + content);
                    editor.commit();

                    AlertDialog.Builder alertFav1 = new AlertDialog.Builder(BusNumberActivity.this);
                    //alertFav.setTitle("Favourites");
                    alertFav1.setMessage("Favorites Added");
                    alertFav1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(getApplicationContext(), Favourites.class), 1);
                            dialog.dismiss();

                        }
                    });
                    alertFav1.show();
                    //Toast.makeText(getApplicationContext(), "Favorites Added", Toast.LENGTH_LONG).show();
                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                }
                //Log.v("long clicked","pos: " + pos);
                return true;
            }
        });

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
