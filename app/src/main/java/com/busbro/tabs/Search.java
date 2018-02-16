package com.busbro.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.busbro.FoldingCellListAdapter;
import com.busbro.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sgbus.nearestbus.BusStop;
import com.sgbus.nearestbus.NearestBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.*;


public class Search extends Fragment {
    ArrayList<BusStop> searchBusList = new ArrayList<BusStop>();
    ArrayList<String> buses;
    NearestBus nearestBus = NearestBus.getInstance();
    JSONArray busStopDetails = new JSONArray();
    FoldingCellListAdapter adapter = null;
    ExpandableListView theListView = null;
    public static final String PREFS_NAME = "BusBro";
    private int lastExpandedPosition = -1;
    private FrameLayout mRelativeLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View searchView = inflater.inflate(R.layout.search_bus, container, false);
        final EditText editText = (EditText) searchView.findViewById(R.id.searchView);
        theListView = (ExpandableListView) searchView.findViewById(R.id.searchListView);
        View emptyView = (TextView) searchView.findViewById(R.id.txtSearchView);
        mRelativeLayout = (FrameLayout) searchView.findViewById(R.id.srl);
        theListView.setEmptyView(emptyView);
        try {
            nearestBus.loadSearchBusDetails(getActivity().getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                editText.setEnabled(false);
                editText.setEnabled(true);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    JSONArray jsArray = new JSONArray();
                    jsArray.put(editText.getText());

                    if(editText.getText().toString().equalsIgnoreCase("") || editText.getText().length()==0){
                        Toast.makeText(getActivity(), "Please key in Bus stop code / name", Toast.LENGTH_LONG).show();
                    }else {
                        try {
                            searchBuses(jsArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                }
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
                int newFav=0;
                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                String favBusStops = settings.getString("favBusStops", "");
                String newFavBusStops="";
                String[] busStopArray = favBusStops.split(",");
                for(String busStop: busStopArray) {
                    if (busStop.equalsIgnoreCase(searchBusList.get(pos).getBusStopCode())){
                        newFav=1;
                    }else {
                        if(!busStop.equals("")) {
                            newFavBusStops = newFavBusStops + "," + busStop;
                        }
                    }
                }
                if(newFav==1) {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE).edit();
                    editor.putString("favBusStops","");
                    editor.putString("favBusStops", newFavBusStops);
                    editor.commit();
                    Toast.makeText(getActivity(), "Favorites Removed: "+searchBusList.get(pos).getDescription(), Toast.LENGTH_LONG).show();
                    Vibrator v = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);

                }else {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE).edit();
                    editor.putString("favBusStops", favBusStops + "," + searchBusList.get(pos).getBusStopCode());
                    editor.commit();
                    Toast.makeText(getActivity(), "Favorites Added: "+searchBusList.get(pos).getDescription(), Toast.LENGTH_LONG).show();
                    Vibrator v = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                }
                Log.v("long clicked","pos: " + pos);

                return true;
            }
        });
        return searchView;
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
        searchBusList = nearestBus.searchBuses(getActivity().getApplicationContext(),jsArray);

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
                        secondBusETA = busStopDetails.getJSONObject(i).getJSONObject("SubsequentBus").getString("EstimatedArrival");
                        thirdBusETA = busStopDetails.getJSONObject(i).getJSONObject("SubsequentBus3").getString("EstimatedArrival");
                        firstBusETA = nearestBus.getTimeStamp(firstBusETA);
                        secondBusETA = nearestBus.getTimeStamp(secondBusETA);
                        thirdBusETA = nearestBus.getTimeStamp(thirdBusETA);
                        busTiming.add(firstBusETA);
                        busTiming.add(secondBusETA);
                        busTiming.add(thirdBusETA);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Some error while loading Details", Toast.LENGTH_SHORT).show();
                        //e.printStackTrace();
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

        adapter = new FoldingCellListAdapter(getActivity().getApplicationContext(), searchBusList, busNumbersHM,busNamesMap,mRelativeLayout,"Search");

        // add default btn handler for each request btn on each item if custom handler not founds
        // set elements to adapter
        theListView.setAdapter(adapter);

    }

    private boolean isFragmentLoaded=false;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded ) {
            // Load your data here or do network operations here
            MobileAds.initialize(getActivity().getApplicationContext(), "ca-app-pub-7776551572587843/2864002813");
            //String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
            ////Log.d(msg,"Device ID: " + android_id);
            AdView mAdView = (AdView) getActivity().findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            isFragmentLoaded = true;
        }
    }
}

