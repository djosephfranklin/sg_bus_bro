package com.busbro.tabs;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.busbro.BuildConfig;
import com.busbro.FoldingCellListAdapter;
import com.busbro.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.sgbus.nearestbus.BusStop;
import com.sgbus.nearestbus.NearestBus;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Nearby extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String[] INITIAL_PERMS = {Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int INITIAL_REQUEST = 1337;
    String goBackFavBusStops = "";
    String onLoadFavBusStops = "";
    String msg = "Android BusBro : ";
    ArrayList<String> buses;
    FoldingCellListAdapter adapter = null;
    ArrayList<BusStop> busList = new ArrayList<BusStop>();
    NearestBus nearestBus = NearestBus.getInstance();

    ExpandableListView theListView;
    public static final String PREFS_NAME = "BusBro";
    private int lastExpandedPosition = -1;
    private FrameLayout mRelativeLayout;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location latestLocation;
    PendingResult<LocationSettingsResult> result;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public String latitude = "";
    public String longitude = "";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 10 seconds
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10;

    String lat = "";
    String lonh = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkLocationPermission()) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkLocationPermission()) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        buildGoogleApiClient();


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 1);

        setContentView(R.layout.nearby_bus);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        onLoadFavBusStops = settings.getString("favBusStops", "");


        TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
        pageTitle.setText("Nearby");
        //pageTitle.setTextColor(Color.BLACK);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }


        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        //getWindow().setBackgroundDrawable(null);
        //getWindow().setWindowAnimations(R.style.PauseDialogAnimation);

        theListView = (ExpandableListView) findViewById(R.id.mainListView);
        mRelativeLayout = (FrameLayout) findViewById(R.id.nrl);


        SharedPreferences prefs = getApplicationContext().getSharedPreferences("BusBro", 0);

        lat = prefs.getString("latitude", "");
        lonh = prefs.getString("longitude", "");
       /* lat="1.33903";
        lonh="103.951";*/
//        ProgressDialog progress = null;
//        while ("".equalsIgnoreCase(lat)) {
//            progress = ProgressDialog.show(Nearby.this, "Finding Nearby Bus Stops!", "Please wait...");
//        }
//        if (null != progress) {
//            progress.dismiss();
//        }

        try {

            populateBuses();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ImageView backPress = (ImageView) findViewById(R.id.backPress);
        TextView homeTitle = (TextView) findViewById(R.id.appTitle);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                goBackFavBusStops = settings.getString("favBusStops", "");
                Log.d("OnEnd", "" + goBackFavBusStops);
                if (!goBackFavBusStops.equalsIgnoreCase(onLoadFavBusStops)) {
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
                Log.d("OnEnd", "" + goBackFavBusStops);
                if (!goBackFavBusStops.equalsIgnoreCase(onLoadFavBusStops)) {
                    Intent intent = new Intent(getApplicationContext(), Favourites.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                finish();
            }
        });

        theListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    theListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        theListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {

            }
        });
        theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                int newFav = 0;
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                String favBusStops = settings.getString("favBusStops", "");
                String newFavBusStops = "";
                String[] busStopArray = favBusStops.split(",");
                for (String busStop : busStopArray) {
                    if (busStop.equalsIgnoreCase(busList.get(pos).getBusStopCode())) {
                        newFav = 1;
                    } else {
                        if (!busStop.equals("")) {
                            newFavBusStops = newFavBusStops + "," + busStop;
                        }
                    }
                }
                if (newFav == 1) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("favBusStops", "");
                    editor.putString("favBusStops", newFavBusStops);
                    editor.commit();
                    AlertDialog.Builder alertFav = new AlertDialog.Builder(Nearby.this);
                    //alertFav.setTitle("Favourites");
                    alertFav.setMessage("Favorites Removed");
                    alertFav.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(getApplicationContext(), Favourites.class), 1);
                            dialog.dismiss();

                        }
                    });
                    alertFav.show();
                    Toast.makeText(getApplicationContext(), "Favorites Removed: " + busList.get(pos).getDescription(), Toast.LENGTH_LONG).show();
                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putString("favBusStops", favBusStops + "," + busList.get(pos).getBusStopCode());
                    editor.commit();
                    AlertDialog.Builder alertFav = new AlertDialog.Builder(Nearby.this);
                    //alertFav.setTitle("Favourites");
                    alertFav.setMessage("Favorites Added");
                    alertFav.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(getApplicationContext(), Favourites.class), 1);
                            dialog.dismiss();

                        }
                    });
                    alertFav.show();
                    Toast.makeText(getApplicationContext(), "Favorites Added: " + busList.get(pos).getDescription(), Toast.LENGTH_LONG).show();
                    Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                }
                Log.v("long clicked", "pos: " + pos);

                return true;
            }
        });
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if (!BuildConfig.APPLICATION_ID.toString().toLowerCase().equalsIgnoreCase("com.busbro.premium")) {
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-7776551572587843/2864002813");
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        //turnGPSOn();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(getApplicationContext(), "Unable to get your location. Please enable GPS and try again, else try Search", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    private void populateBuses() throws JSONException {
        if (busList.size() == 0) {
            //Log.d(msg, "populateBuses");
            //Log.d(msg,"Sortings :"+content);
            buses = new ArrayList<String>();
            HashMap<String, HashMap<String, ArrayList<String>>> busNumbersHM = new HashMap<String, HashMap<String, ArrayList<String>>>();
            HashMap<String, ArrayList<String>> busMap = new HashMap<String, ArrayList<String>>();
            HashMap<String, ArrayList<String>> busNamesMap = new HashMap<String, ArrayList<String>>();
            List<String> busCode = new ArrayList<String>();
            List<String> busRoadName = new ArrayList<String>();
            View emptyView = (TextView) findViewById(R.id.txtEmptyNearbyView);
            theListView.setEmptyView(emptyView);

            if(null!=lat && !"".equalsIgnoreCase(lat)) {
                ImageView imgView = (ImageView) findViewById(R.id.noNearbyImg);
                imgView.setVisibility(View.GONE);

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(lat);
                jsonArray.put(lonh);

                try {
                    busList = nearestBus.getAllBus(jsonArray, getApplicationContext());
                    //Log.d("BusList",""+busList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new FoldingCellListAdapter(getApplicationContext(), busList, busNumbersHM, busNamesMap, mRelativeLayout, "Nearby");

                // set elements to adapter
                theListView.setAdapter(adapter);
            }else{
                ImageView imgView = (ImageView) findViewById(R.id.noNearbyImg);
                imgView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        goBackFavBusStops = settings.getString("favBusStops", "");
        Log.d("OnEnd", "" + goBackFavBusStops);
        if (!goBackFavBusStops.equalsIgnoreCase(onLoadFavBusStops)) {
            Intent intent = new Intent(getApplicationContext(), Favourites.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);

            result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    //Log.d(msg,"Status "+ status.getStatusCode());
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            createPage();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });

            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,  this);

        } else {
            try {
                handleNewLocation(location);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences prefs = this.getApplicationContext().getSharedPreferences("BusBro", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("permitted", "true");
        editor.commit();
    }

    private void handleNewLocation(Location location) throws JSONException {
        latitude = "" + location.getLatitude();
        longitude = "" + location.getLongitude();
        SharedPreferences prefs = this.getApplicationContext().getSharedPreferences("BusBro", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("latitude", latitude);
        editor.putString("longitude", longitude);
        editor.commit();
    }


    public void createPage() {
        LocationManager lm = (LocationManager) this.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            Toast.makeText(this.getApplicationContext(), "Please Enable Location Services", Toast.LENGTH_SHORT).show();
        } else {
            Location location = getLastBestLocation(lm);
            try {
                handleNewLocation(location);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Location getLastBestLocation(LocationManager locationManager) {
        try {
            // Getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                ////Log.d(msg,"No Network or GPS available");
                // No network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    ////Log.d("Network", "Network");
                    if (locationManager != null) {
                        latestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (latestLocation != null) {
                            latitude = "" + latestLocation.getLatitude();
                            longitude = "" + latestLocation.getLongitude();
                            SharedPreferences prefs = this.getApplicationContext().getSharedPreferences("BusBro", 0);
                            SharedPreferences.Editor editor = prefs.edit();
                            Log.d("LatLong", latitude);
                            editor.putString("latitude", latitude);
                            editor.putString("longitude", longitude);
                            editor.commit();
                        }
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    if (latestLocation == null) {

                        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                            //Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                latestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (latestLocation != null) {
                                    latitude = "" + latestLocation.getLatitude();
                                    longitude = "" + latestLocation.getLongitude();
                                    SharedPreferences prefs = this.getApplicationContext().getSharedPreferences("BusBro", 0);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("latitude", latitude);
                                    editor.putString("longitude", longitude);
                                    editor.commit();
                                }
                            }
                        }

                    }
                }
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "User Location enabled by recently by user! Something went wrong while performing this action  SG Bus Bro...", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong while performing this action  SG Bus Bro...", Toast.LENGTH_LONG).show();
        }
        long GPSLocationTime = 0;
        if (null != latestLocation) {
            GPSLocationTime = latestLocation.getTime();
        }

        long NetLocationTime = 0;

        if (null != latestLocation) {
            NetLocationTime = latestLocation.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return latestLocation;
        } else {
            return latestLocation;
        }

    }

    @Override
    public void onLocationChanged(Location loc) {
        Toast.makeText(
                getBaseContext(),
                "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                        + loc.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + loc.getLongitude();
        //Log.d("LatLongNew", longitude);
        String latitude = "Latitude: " + loc.getLatitude();
        //Log.v("LatLongNew", latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean canAccessLocation() {
        return (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d("RequestCode", "" + requestCode);
        if (requestCode == 101) {
            if ((grantResults[0] == PackageManager.PERMISSION_DENIED) || (grantResults[1] == PackageManager.PERMISSION_DENIED)) {
                Toast.makeText(getApplicationContext(), "Unable to get permissions for ...", Toast.LENGTH_LONG).show();
            }
        }

        if (grantResults.length > 0 && permissions[0].equalsIgnoreCase("android.permission.ACCESS_FINE_LOCATION")) {
            Log.d("Permisson", "Granted");
            Intent intent = new Intent(getApplicationContext(), Favourites.class);
            startActivity(intent);
        } else {
            //redirect to settings page or ask permission again
        }
    }


    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        //checkPlayServices();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            //Log.d(msg, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
        Log.d("BUSBRO", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }
}

