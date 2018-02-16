package com.busbro.tabs;

import android.app.Activity;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.busbro.BuildConfig;
import com.busbro.BusNumberActivity;
import com.busbro.FoldingCellListAdapter;
import com.busbro.R;
import com.busbro.SmartBusActivity;
import com.busbro.adapter.DestSearchAdapter;
import com.busbro.adapter.SearchAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sgbus.nearestbus.BusStop;
import com.sgbus.nearestbus.NearestBus;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Favourites extends AppCompatActivity {
    private boolean isViewShown = false;
    String msg = "Android BusBro : ";
    ArrayList<BusStop> favBusList = new ArrayList<BusStop>();
    ArrayList<BusStop> searchBusList = new ArrayList<BusStop>();
    ArrayList<String> buses;
    NearestBus nearestBus = NearestBus.getInstance();
    JSONArray busStopDetails = new JSONArray();
    FoldingCellListAdapter adapter = null;
    ExpandableListView theListView = null;
    public static final String PREFS_NAME = "BusBro";
    private int lastExpandedPosition = -1;
    private FrameLayout mRelativeLayout;
    String createdFavBusStops="";
    final static int REQUEST_LOCATION = 199;

    public LocalActivityManager activityManager;
    public LinearLayout contentViewLayout;
    public LinearLayout.LayoutParams contentViewLayoutParams;
    private Context context;
    public Intent nextActivit;

    Timer myTimer;
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
        setContentView(R.layout.activity_main);

        SearchAdapter srcSearchAdapter = new SearchAdapter(this, android.R.layout.simple_dropdown_item_1line);
        final DestSearchAdapter destSearchAdapter = new DestSearchAdapter(this, android.R.layout.simple_dropdown_item_1line);

        TextView toolbar = (TextView) findViewById(R.id.appTitle);
        toolbar.setText("BusBro");
        toolbar.setTextColor(Color.BLACK);

        final TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
        pageTitle.setText("Favourites");
        //pageTitle.setTextColor(Color.BLACK);


        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
        editor.putString("Search","");
        editor.commit();

        LinearLayout smartBusLayout = (LinearLayout) findViewById(R.id.smartBusLayout);
        smartBusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SmartBusActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivityFromChild(getParent(),intent,1);
            }
        });

        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
        final String favBusStops = settings.getString("favBusStops", "");
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_to_right);
        fab.setAnimation(animation);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myTimer!=null) {
                    myTimer.cancel();
                    myTimer = null;
                }
                //Log.d("LatitudeClick",latitude);
                //Call your Location Activity
                LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                if(!gps_enabled && !network_enabled) {
                    AlertDialog.Builder notifyLocationServices = new AlertDialog.Builder(Favourites.this);
                    notifyLocationServices.setTitle("Switch on Location Services");
                    notifyLocationServices.setMessage("Location Services must be turned on to complete this action. ");
                    notifyLocationServices.setPositiveButton("Ok, Open Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent =new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS) ;
                            startActivity(intent);
                        }
                    });
                    notifyLocationServices.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    notifyLocationServices.show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), Nearby.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivityFromChild(getParent(),intent,1);
                }
            }
        });
        final AutoCompleteTextView editText = (AutoCompleteTextView) findViewById(R.id.searchView);
        final LinearLayout rl = (LinearLayout) findViewById(R.id.search_layout);
        final AutoCompleteTextView srcTxt = (AutoCompleteTextView) rl.findViewById(R.id.source);
        final AutoCompleteTextView destTxt = (AutoCompleteTextView) rl.findViewById(R.id.destination);

        final TextView tv1 = (TextView) findViewById(R.id.bus1);
        final TextView tv2 = (TextView) findViewById(R.id.bus2);
        final TextView tv3 = (TextView) findViewById(R.id.bus3);
        final TextView tv4 = (TextView) findViewById(R.id.bus4);
        final TextView tv5 = (TextView) findViewById(R.id.bus5);
        final TextView tv6 = (TextView) findViewById(R.id.bus6);
        final TextView tv7 = (TextView) findViewById(R.id.bus7);
        final TextView tv8 = (TextView) findViewById(R.id.bus8);

        final ImageView appSearch = (ImageView) findViewById(R.id.appSearch);
        final LinearLayout appSearchLayout = (LinearLayout) findViewById(R.id.appSearchLayout);
        final ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        appSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout rl = (LinearLayout) findViewById(R.id.search_layout);
                final AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.source);
                if(rl.getVisibility()==View.GONE) {
                    rl.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    rl.setVisibility(View.VISIBLE);
                    source.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT);
                }else{
                    source.clearFocus();
                    editText.clearFocus();
                    editText.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    rl.setVisibility(View.GONE);
                }
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout rl = (LinearLayout) findViewById(R.id.search_layout);
                final AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.source);
                if(rl.getVisibility()==View.GONE) {
                    rl.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    rl.setVisibility(View.VISIBLE);
                    source.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT);
                }else{
                    source.clearFocus();
                    editText.clearFocus();
                    editText.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    rl.setVisibility(View.GONE);
                }
            }
        });


        /*editText.setHint("\uD83D\uDCA1 Explore");
        editText.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout rl = (LinearLayout) findViewById(R.id.search_layout);
                final AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.source);
                if(rl.getVisibility()==View.GONE) {
                    rl.setVisibility(View.VISIBLE);
                    editText.setVisibility(View.GONE);
                    rl.setVisibility(View.VISIBLE);
                    source.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(source, InputMethodManager.SHOW_IMPLICIT);
                }else{
                    source.clearFocus();
                    editText.clearFocus();
                    editText.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    rl.setVisibility(View.GONE);
                }
            }
        });*/

        final LinearLayout busLayout = (LinearLayout) findViewById(R.id.bus_layout);
        busLayout.setOrientation(LinearLayout.HORIZONTAL);
        busLayout.setVisibility(View.VISIBLE);

        srcTxt.setHint("\uD83D\uDD0D Search Buses");
        srcTxt.setAdapter(srcSearchAdapter);
        srcTxt.setFocusable(true);
        srcTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id) {

                AsyncTask<String, Void, String> read = new AsyncTask<String, Void, String>() {
                    Dialog progress;
                    @Override
                    protected void onPreExecute() {
                        progress = ProgressDialog.show(Favourites.this, "Finding Bus Stops!", "Please wait...");
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        if (params[0].toString().equalsIgnoreCase("") || params[0].length() == 0) {
                            try {
                                getFavList(favBusStops);
                                Intent intent = new Intent(getApplicationContext(), Favourites.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                JSONArray jsArray = new JSONArray();
                                jsArray.put(params[0]);
                                searchBuses(jsArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        rl.setVisibility(View.GONE);
                        editText.setVisibility(View.GONE);
                        progress.dismiss();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                };
                read.execute(String.valueOf(srcTxt.getText()));
            }
        });
        srcTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LinearLayout rl2 = (LinearLayout) findViewById(R.id.destination_layout);
                ArrayList<String> busNumbers = new ArrayList<String>();
                JSONArray jsArray = new JSONArray();
                jsArray.put(srcTxt.getText());
                Log.d("Bus","Bus:"+srcTxt.getText());
                try {
                    busNumbers = nearestBus.searchBusNumbers(getApplicationContext(),jsArray);
                    if("".equalsIgnoreCase(srcTxt.getText().toString())){
                        busNumbers=null;
                    }
                    Log.d("BusNumbers",""+busNumbers);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
                }

                tv1.setText("");
                tv2.setText("");
                tv3.setText("");
                tv4.setText("");
                tv5.setText("");
                tv6.setText("");
                tv7.setText("");
                tv8.setText("");
                if(busNumbers!=null) {
                    if (busNumbers.size() > 0) tv1.setText(busNumbers.get(0));
                    if (busNumbers.size() > 1) tv2.setText(busNumbers.get(1));
                    if (busNumbers.size() > 2) tv3.setText(busNumbers.get(2));
                    if (busNumbers.size() > 3) tv4.setText(busNumbers.get(3));
                    if (busNumbers.size() > 4) tv5.setText(busNumbers.get(4));
                    if (busNumbers.size() > 5) tv6.setText(busNumbers.get(5));
                    if (busNumbers.size() > 6) tv7.setText(busNumbers.get(6));
                    if (busNumbers.size() > 7) tv8.setText(busNumbers.get(7));
                }

                Log.d("BusGroup",""+busNumbers);
                /*if(srcTxtSub.getText().length()>2 && tv1.getText().toString().equalsIgnoreCase("")) {
                    if(rl2.getVisibility()==View.GONE) {
                        TranslateAnimation trans = new TranslateAnimation(0, 0, -100, 50);
                        trans.setDuration(500);
                        trans.setInterpolator(new AccelerateInterpolator(1.0f));
                        rl2.startAnimation(trans);
                    }
                    rl2.setVisibility(View.VISIBLE);
                }else{
                    rl2.setVisibility(View.GONE);
                }
                destTxt.setText(srcTxt.getText());*/
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        destTxt.setAdapter(destSearchAdapter);
        destTxt.setFocusable(true);

        tv1.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=tv1.getText() && !tv1.getText().toString().equalsIgnoreCase("")) {
                    pageTitle.setText("Search");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("busNumber", "" + tv1.getText());
                    editor.putString("busDirection", "1");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
                //pageTitle.setTextColor(Color.BLACK);
                /*AsyncTask<String, Void, String> read = new AsyncTask<String, Void, String>() {
                    Dialog progress;
                    @Override
                    protected void onPreExecute() {
                        progress = ProgressDialog.show(Favourites.this, "Finding Bus Stops!", "Please wait...");
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        if (params[0].toString().equalsIgnoreCase("") || params[0].length() == 0) {
                            try {
                                getFavList(favBusStops);
                                Intent intent = new Intent(getApplicationContext(), Favourites.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                JSONArray jsArray = new JSONArray();
                                jsArray.put(params[0]);
                                searchBuses(jsArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        rl.setVisibility(View.GONE);
                        editText.setVisibility(View.GONE);
                        progress.dismiss();
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        if(myTimer!=null) {
                            myTimer.cancel();
                            myTimer = null;
                        }
                    }
                };
                read.execute(String.valueOf(tv1.getText()));*/
            }
        });

        tv2.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=tv2.getText() && !tv2.getText().toString().equalsIgnoreCase("")) {
                    pageTitle.setText("Search");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("busNumber", "" + tv2.getText());
                    editor.putString("busDirection", "1");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });
        tv3.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=tv3.getText() && !tv3.getText().toString().equalsIgnoreCase("")) {
                    pageTitle.setText("Search");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("busNumber", "" + tv3.getText());
                    editor.putString("busDirection", "1");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }

            }
        });


        tv4.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=tv4.getText() && !tv4.getText().toString().equalsIgnoreCase("")) {
                    pageTitle.setText("Search");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("busNumber", "" + tv4.getText());
                    editor.putString("busDirection", "1");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });


        tv5.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=tv5.getText() && !tv5.getText().toString().equalsIgnoreCase("")) {
                    pageTitle.setText("Search");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("busNumber", "" + tv5.getText());
                    editor.putString("busDirection", "1");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });

        tv6.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=tv6.getText() && !tv6.getText().toString().equalsIgnoreCase("")) {
                    pageTitle.setText("Search");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("busNumber", "" + tv6.getText());
                    editor.putString("busDirection", "1");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });


        tv7.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=tv7.getText() && !tv7.getText().toString().equalsIgnoreCase("")) {
                    pageTitle.setText("Search");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("busNumber", "" + tv7.getText());
                    editor.putString("busDirection", "1");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });

        tv8.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=tv8.getText() && !tv8.getText().toString().equalsIgnoreCase("")) {
                    pageTitle.setText("Search");
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("busNumber", "" + tv8.getText());
                    editor.putString("busDirection", "1");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
            }
        });

        srcTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
                srcTxt.setEnabled(false);
                srcTxt.setEnabled(true);
                AsyncTask<String, Void, String> read = new AsyncTask<String, Void, String>() {
                    Dialog progress;

                    @Override
                    protected void onPreExecute() {
                        progress = ProgressDialog.show(Favourites.this,
                                "Finding Bus Stops!", "Please wait...");
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... params) {

                        JSONArray jsArray = new JSONArray();
                        jsArray.put(params[0]);

                        if (params[0].toString().equalsIgnoreCase("") || params[0].length() == 0) {
                            try {
                                getFavList(favBusStops);
                                Intent intent = new Intent(getApplicationContext(), Favourites.class);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                searchBuses(jsArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }catch (Exception e) {
                                Toast.makeText(getApplicationContext(),
                                        "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        rl.setVisibility(View.GONE);
                        editText.setVisibility(View.VISIBLE);
                        progress.dismiss();
                        v.clearFocus();
                        if(myTimer!=null) {
                            myTimer.cancel();
                            myTimer = null;
                        }
                        /*getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/
                    }
                };
                read.execute(String.valueOf(srcTxt.getText()));
                return true;
            }
        });


        if(!isViewShown){
            try {
                getFavList(favBusStops);
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong while performing this action", Toast.LENGTH_SHORT).show();
            }

        }
        theListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false;
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
                // TODO Auto-generated method stub
                int newFav=0;
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                createdFavBusStops = settings.getString("favBusStops", "");
                String newFavBusStops="";
                String[] busStopArray = createdFavBusStops.split(",");
                String content ="";
                for(int i=0;i<busStopArray.length;i++) {

                    if(!"".equalsIgnoreCase(settings.getString("Search",""))){
                        Log.d("BusStopDetail","Search Bus List"+settings.getString("Search","NULL"));
                        if (searchBusList.get(pos).getBusStopCode().equalsIgnoreCase(busStopArray[i])){
                            newFav=1;
                        }else {
                            if(!busStopArray[i].equals("")) {
                                newFavBusStops = newFavBusStops + "," + busStopArray[i];
                            }
                            content =searchBusList.get(pos).getBusStopCode();
                        }

                    }else {
                        if (busStopArray[i].equalsIgnoreCase(favBusList.get(pos).getBusStopCode())) {
                            newFav = 1;
                        } else {
                            if (!busStopArray[i].equals("")) {
                                newFavBusStops = newFavBusStops + "," + busStopArray[i];
                            }
                            content=favBusList.get(pos).getBusStopCode();
                        }

                       // finish();
                    }
                }
                if(newFav==1) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("favBusStops","");
                    editor.putString("favBusStops", newFavBusStops);
                    editor.commit();
                    AlertDialog.Builder alertFav = new AlertDialog.Builder(Favourites.this);
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
                }else{
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putString("favBusStops", createdFavBusStops + "," +content );
                    editor.commit();

                    AlertDialog.Builder alertFav1 = new AlertDialog.Builder(Favourites.this);
                    //alertFav.setTitle("Favourites");
                    alertFav1.setMessage("Favorites Added");
                    alertFav1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(getApplicationContext(), Favourites.class),1);
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
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if(!BuildConfig.APPLICATION_ID.toString().toLowerCase().equalsIgnoreCase("com.busbro.premium")) {
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-7776551572587843/2864002813");
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }else{
            mAdView.setVisibility(View.GONE);
        }
        if(myTimer==null) {
            myTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    try {
                        populateFav();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Some error while loading Details", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            myTimer.schedule(timerTask, 5000, 10000);
        }
    }


    private void getFavList(String busStops) throws JSONException {
        theListView = (ExpandableListView) findViewById(R.id.favListView);
        View emptyView = (TextView) findViewById(R.id.txtEmptyView);
        mRelativeLayout = (FrameLayout) findViewById(R.id.frl);
        theListView.setEmptyView(emptyView);
        //Log.d(msg, "get Fav Buses" + busStops);
        //Log.d(msg,"Sortings :"+content);
        buses = new ArrayList<String>();
        HashMap<String, HashMap<String, ArrayList<String>>> busNumbersHM = new HashMap<String, HashMap<String, ArrayList<String>>>();
        HashMap<String, ArrayList<String>> busNamesMap = new HashMap<String, ArrayList<String>>();
        int countr = 0;
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(busStops);
        if (favBusList.size() == 0) {
            favBusList = nearestBus.getFav(jsonArray, this.getApplicationContext());

            adapter = new FoldingCellListAdapter(this.getApplicationContext(), favBusList, busNumbersHM, busNamesMap,mRelativeLayout,"Fav");

            adapter.refreshList();
            // set elements to adapter
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    theListView.setAdapter(adapter);
                }
            });

        }
    }

    public ArrayList<String> getBusStopInitDetails(String busStopId){
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(busStopId);
        try {
            busStopDetails = nearestBus.getBusStopDetails(jsonArray);
        } catch (JSONException e) {
            Toast.makeText(this.getApplicationContext(),"Some error while loading Details", Toast.LENGTH_SHORT).show();
        }

        ArrayList<String> busNames= new ArrayList<String>();
        for (int i = 0; i < busStopDetails.length(); i++) {
            try {
                busNames.add(busStopDetails.getJSONObject(i).getString("ServiceNo"));

            } catch (JSONException e) {
                Toast.makeText(this.getApplicationContext(),"Some error while loading Details", Toast.LENGTH_SHORT).show();
                //e.printStackTrace();
            }
        }
        return busNames;
    }

    private boolean isFragmentLoaded=false;






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Toast.makeText(this, "Location enabled by user! Please click Refresh button to populate list", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case 1:
                        Toast.makeText(this, "WELCOME BACK", Toast.LENGTH_LONG).show();
                        break;
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    public void populateFav() throws JSONException {
        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, 0);
        final String favBusStops = settings.getString("favBusStops", "");
        getFavList(favBusStops);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        if(myTimer==null){
            myTimer = new Timer();
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    try {
                        populateFav();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Some error while loading Details", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            myTimer.schedule(timerTask, 5000, 10000);
        }
        //Log.d("RESUME","***RESUME***");

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)   {
            //Log.d("BackPressed", "Back Pressed");

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            //Log.d("BackPressed", ""+settings);
            if(null==settings){
               return true;
            }else {
                //Log.d("BackPressed", "Search" + settings.getString("Search", ""));

                if ("true".equalsIgnoreCase(settings.getString("Search", ""))) {
                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("BusBro", 0);
                    final SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("Search", "");
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), Favourites.class);
                    startActivity(intent);
                    finish();
                }else{
                    finish();
                }
                finish();
            }
        }
        return true;
    }


    private void searchBuses(JSONArray jsArray) throws JSONException {
        buses = new ArrayList<String>();
        ArrayList<String> items = new ArrayList<String>();
        HashMap<String, HashMap<String,ArrayList<String>>> busNumbersHM = new HashMap<String, HashMap<String,ArrayList<String>>>();
        HashMap<String, ArrayList<String>> busMap= new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> busNamesMap= new HashMap<String, ArrayList<String>>();
        List<String> busCode = new ArrayList<String>();
        List<String> busRoadName = new ArrayList<String>();
        int countr = 0;
        Log.d("Search",jsArray.toString());
        searchBusList = nearestBus.searchBuses(getApplicationContext(),jsArray);

        while (countr < searchBusList.size()) {
            String busStopCode = searchBusList.get(countr).getBusStopCode();
            buses.add(searchBusList.get(countr).getDescription());
            busCode.add(busStopCode);
            busRoadName.add(searchBusList.get(countr).getRoadName());
            //Log.d("BusStopDetails", "::Bus Name::: " + busList.get(countr).getDescription());
            JSONArray busArray = new JSONArray();
            busArray.put(busStopCode);
            //Log.d(msg,"Bus Code::: "+busStopCode );
            busStopDetails = nearestBus.getBusStopDetails(busArray);
            //Log.d(msg,"Bus Code::: "+busList.get(countr).getBusStopCode()+"\n Bus Stop Details :::"+busStopDetails.toString());

            ArrayList<String> busNames = new ArrayList<String>();

            String firstBusETA = null;
            String secondBusETA = null;
            String thirdBusETA = null;

            for (int i = 0; i < busStopDetails.length(); i++) {
                ArrayList<String> busTiming = new ArrayList<String>();
                try {
                    //Log.d(msg,"Bus Details View ::: "+busStopDetails.getJSONObject(i));

                    firstBusETA = busStopDetails.getJSONObject(i).getJSONObject("NextBus").getString("EstimatedArrival");
                    secondBusETA = busStopDetails.getJSONObject(i).getJSONObject("NextBus2").getString("EstimatedArrival");
                    thirdBusETA = busStopDetails.getJSONObject(i).getJSONObject("NextBus3").getString("EstimatedArrival");
                    firstBusETA = nearestBus.getTimeStamp(firstBusETA);
                    secondBusETA = nearestBus.getTimeStamp(secondBusETA);
                    thirdBusETA = nearestBus.getTimeStamp(thirdBusETA);
                    busTiming.add(firstBusETA);
                    busTiming.add(secondBusETA);
                    busTiming.add(thirdBusETA);
                } catch (JSONException e) {
                    Toast.makeText(Favourites.this, "Some error while loading Details", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                busMap.put(searchBusList.get(countr).getBusStopCode() + "_" + busStopDetails.getJSONObject(i).getString("ServiceNo"), busTiming);
                busNames.add(busStopDetails.getJSONObject(i).getString("ServiceNo"));
            }

            busNamesMap.put(searchBusList.get(countr).getBusStopCode(),busNames);

            //Log.d("BusStopDetails", "::BusStop Code:::"+busList.get(countr).getBusStopCode()+ " Items::: "+busMap.toString());
            busNumbersHM.put(searchBusList.get(countr).getBusStopCode(), busMap);
            countr++;
        }

        //Log.d(msg,"Items :"+busNumbersHM);
        // prepare elements to display

        adapter = new FoldingCellListAdapter(getApplicationContext(), searchBusList, busNumbersHM,busNamesMap,mRelativeLayout,"Search");

        // add default btn handler for each request btn on each item if custom handler not founds
        // set elements to adapter
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("BusBro", 0);
                final SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Search","true");
                editor.commit();
                theListView.setAdapter(adapter);
                //Log.d("Searched",prefs.getString("Search",""));
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(myTimer!=null) {
            myTimer.cancel();
            myTimer = null;
        }
        //getWindow().setWindowAnimations(R.style.PauseDialog);
        finish();
    }
}