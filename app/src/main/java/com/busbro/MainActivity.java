package com.busbro;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.busbro.tabs.Favourites;




/**
 * Example of using Folding Cell with ListView and ListAdapter
 */
public class MainActivity extends AppCompatActivity {
    String msg = "Android BusBro : ";



    private final static String APP_TITLE = "SG Bus Bro";// App Name
    private final static String APP_PNAME = "com.busbro.premium";// Package Name
    private final static int LAUNCHES_UNTIL_PROMPT = 10;//Min number of launches
    private final static int DAYS_UNTIL_PROMPT = 10;//Min number of days


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Start home activity
        startActivity(new Intent(MainActivity.this, Favourites.class));
        // close splash activity
        finish();

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);


        //setSupportActionBar(toolbar);

        /*TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Favourites"));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Nearby"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

*/

        app_launched(getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop(){

        super.onStop();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume() {
        super.onResume();
       // mGoogleApiClient.connect();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    public void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("BusBro", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        Log.d(msg,"Launch Count "+launch_count);
        Log.d(msg,"Date First Launched "+date_firstLaunch);
        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(editor);
            }
        }

        editor.commit();
    }

    public void showRateDialog(final SharedPreferences.Editor editor) {
        Log.d(msg,"Show Rate DIALOG called");

        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Rate " + APP_TITLE);

        LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(getApplicationContext());
        tv.setTextColor(Color.parseColor("#222222"));
        tv.setText("Enjoying " + APP_TITLE + "!, Take a moment to rate it. Thanks for your support!");
        tv.setTextSize(25);
        tv.setPadding(20, 20, 20, 20);
        ll.addView(tv);

        Button b1 = new Button(getApplicationContext());
        b1.setText("Rate " + APP_TITLE);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }

            }
        });
        ll.addView(b1);

        Button b2 = new Button(getApplicationContext());
        b2.setText("Remind me later");
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ll.addView(b2);

        Button b3 = new Button(getApplicationContext());
        b3.setText("No, thanks");
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });
        ll.addView(b3);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.setContentView(ll);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


}
