package com.busbro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.busbro.adapter.BusViewAdapter;
import com.busbro.tabs.Nearby;
import com.sgbus.nearestbus.BusStop;
import com.sgbus.nearestbus.NearestBus;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by laptop on 10/7/2016.
 */
public class FoldingCellListAdapter extends BaseExpandableListAdapter {
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    public int _grpPosition;
    public int _touch=0;
    private Context _context;
    private ArrayList<BusStop> _busList; // header titles
    private ArrayList<BusStop> _originBusList; // header titles
    private HashMap<String, ArrayList<String>> _busMap = new HashMap<String, ArrayList<String>>();

    private HashMap<String, HashMap<String,ArrayList<String>>> _listDataDisplayChild ;
    private HashMap<String, HashMap<String,ArrayList<String>>> _listDataFavDisplayChild =new HashMap<String, HashMap<String,ArrayList<String>>>();
    private HashMap<String,ArrayList<String>> _busName;
    private Typeface tf;
    private Typeface tf_h;
    Timer myTimer;
    Timer txtTimer;
    TextView timerView;
    private PopupWindow mPopupWindow;
    private FrameLayout mRelativeLayout;
    String createdFavBuses="";
    String _loaderPage="";
    Timer myTimer1 = new Timer();

    private HashMap<String,ArrayList<String>> _favBusMap = new HashMap<String,ArrayList<String>>();

    public FoldingCellListAdapter(Context context, ArrayList<BusStop> busList,
                                  HashMap<String, HashMap<String,ArrayList<String>>> listChildData,HashMap<String,ArrayList<String>> busName, FrameLayout relativeLayout,String loader) {
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/Calibri.ttf");
        tf_h = Typeface.createFromAsset(context.getAssets(), "fonts/Calibri Bold.ttf");
        this._context = context;
        this._busName = busName;
        this._busList = busList;
        this._originBusList = busList;
        this._listDataDisplayChild= listChildData;
        this.mRelativeLayout = relativeLayout;
        this._loaderPage=loader;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {

        return this._listDataDisplayChild.get(this._busList.get(groupPosition).getBusStopCode()).get(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(childPosititon));
    }

    public Object getBusTiming(int groupPosition, String childText) {


        return this._listDataDisplayChild.get(this._busList.get(groupPosition).getBusStopCode()).get(childText);
    }

    public Object getFavBusTiming(int groupPosition) {

        Log.d("FavBusTiming",""+this._listDataFavDisplayChild.get(this._busList.get(groupPosition).getBusStopCode()));
        return this._listDataFavDisplayChild.get(this._busList.get(groupPosition).getBusStopCode());
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(LAYOUT_INFLATER_SERVICE);

            convertView = infalInflater.inflate(R.layout.bus_list_view,null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.content_4);
        //txtListChild.setText(childText);
        txtListChild.setTypeface(tf_h);
        txtListChild.setText(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(childPosition));

        //Log.d("BusTiming","Bus Data:"+this._busName);

        final List<String> childTextList = (List<String>) getBusTiming(groupPosition,this._busList.get(groupPosition).getBusStopCode()+"_"+this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(childPosition));

        //final List<String> childColorList = (List<String>) getBusColor(groupPosition,this._busList.get(groupPosition).getBusStopCode()+"_"+this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(childPosition));

        if(childTextList!=null) {
            TextView txtListChild1 = (TextView) convertView.findViewById(R.id.content_5);
            TextView txtListChild2 = (TextView) convertView.findViewById(R.id.content_6);
            TextView txtListChild3 = (TextView) convertView.findViewById(R.id.content_7);

            if (childTextList.size() > 0) {
                txtListChild1.setTypeface(tf);
                txtListChild2.setTypeface(tf);
                txtListChild3.setTypeface(tf);

                txtListChild1.setText(childTextList.get(0));
                txtListChild2.setText(childTextList.get(1));
                txtListChild3.setText(childTextList.get(2));

            } else {
                txtListChild1.setText("");
                txtListChild2.setText("");
                txtListChild3.setText("");
            }



            SharedPreferences settings = _context.getApplicationContext().getSharedPreferences( "BusBro", 0);
            createdFavBuses = settings.getString("favBuses", "");
            String newFavBusStops="";
            String[] busStopArray = createdFavBuses.split(",");
            for(int i=0;i<busStopArray.length;i++) {
                if (busStopArray[i].contains(txtListChild.getText()) && busStopArray[i].contains(_busList.get(_grpPosition).getBusStopCode())){
                    ArrayList<String> favBusDetails = new ArrayList<String>();
                    favBusDetails.add(1,txtListChild1.getText().toString());
                    favBusDetails.add(2,txtListChild2.getText().toString());
                    favBusDetails.add(3,txtListChild3.getText().toString());
                    _favBusMap.put(_busList.get(_grpPosition).getBusStopCode()+txtListChild.getText().toString(),favBusDetails);
                }
            }
            txtListChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("ONCLICK","TRUE");
                    Intent intent = new Intent(_context, BusNumberActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(intent);
                }
            });

        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(this._busName.get(this._busList.get(groupPosition).getBusStopCode())!=null) {
            return this._busName.get(this._busList.get(groupPosition).getBusStopCode()).size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._busList.get(groupPosition).getBusStopCode();
    }

    @Override
    public int getGroupCount() {
        return this._busList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.cell, null);
        }

//START OF FAV
        LinearLayout cellHangLayout = (LinearLayout) convertView.findViewById(R.id.cellHangLayout);

        if(null!=_loaderPage && _loaderPage.equalsIgnoreCase("Fav")) {

            displayFavData(groupPosition);

            HashMap<String,List<String>> childTextList = (HashMap<String,List<String>>) getFavBusTiming(groupPosition);

            List<String> childList = new ArrayList<String>();
            for(int counter=0;counter<childTextList.size();counter++) {
                if(counter<this._busName.get(this._busList.get(groupPosition).getBusStopCode()).size()) {
                    //Log.d("ChildTextList","Content "+this._busList.get(groupPosition).getBusStopCode() + "_" + this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(counter));
                    List<String> superChild = childTextList.get(this._busList.get(groupPosition).getBusStopCode() + "_" + this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(counter));

                    childList.add(superChild.get(0));
                }
            }

            /*BusViewAdapter adapter;

            LinearLayoutManager layoutManager= new LinearLayoutManager(_context,LinearLayoutManager.HORIZONTAL, false);

            adapter = new BusViewAdapter(_context, android.R.layout.select_dialog_item, this._busList);

            cellHangListView.setAdapter(adapter);

            adapter.notifyDataSetChanged();*/

            if (childList != null) {
                final LinearLayout layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                    final LinearLayout layout2 = (LinearLayout) convertView.findViewById(R.id.layout2);
                    final LinearLayout layout3 = (LinearLayout) convertView.findViewById(R.id.layout3);
                    final LinearLayout layout4 = (LinearLayout) convertView.findViewById(R.id.layout4);
                    final LinearLayout layout5 = (LinearLayout) convertView.findViewById(R.id.layout5);
                    final LinearLayout layout6 = (LinearLayout) convertView.findViewById(R.id.layout6);
                    final TextView firstBus = (TextView) convertView.findViewById(R.id.firstBus);
                    final TextView firstBusTiming = (TextView) convertView.findViewById(R.id.firstBusTiming);
                    final TextView secondBus = (TextView) convertView.findViewById(R.id.secondBus);
                    final TextView secondBusTiming = (TextView) convertView.findViewById(R.id.secondBusTiming);
                    final TextView thirdBus = (TextView) convertView.findViewById(R.id.thirdBus);
                    final TextView thirdBusTiming = (TextView) convertView.findViewById(R.id.thirdBusTiming);
                    final TextView fourthBus = (TextView) convertView.findViewById(R.id.fourthBus);
                    final TextView fourthBusTiming = (TextView) convertView.findViewById(R.id.fourthBusTiming);
                    final TextView fifthBus = (TextView) convertView.findViewById(R.id.fifthBus);
                    final TextView fifthBusTiming = (TextView) convertView.findViewById(R.id.fifthBusTiming);
                    final TextView sixthBus = (TextView) convertView.findViewById(R.id.sixthBus);
                    final TextView sixthBusTiming = (TextView) convertView.findViewById(R.id.sixthBusTiming);
                     if (childList.size() > 0) {
                         firstBus.setTypeface(tf_h);
                            firstBus.setTextSize(16f);
                            secondBus.setTypeface(tf_h);
                            secondBus.setTextSize(16f);
                            thirdBus.setTypeface(tf_h);
                            thirdBus.setTextSize(16f);
                            fourthBus.setTypeface(tf_h);
                            fourthBus.setTextSize(16f);
                            fifthBus.setTypeface(tf_h);
                            fifthBus.setTextSize(16f);
                            sixthBus.setTypeface(tf_h);
                            sixthBus.setTextSize(16f);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    firstBus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    secondBus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    thirdBus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    fourthBus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    fifthBus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    sixthBus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                }
                                firstBusTiming.setTypeface(tf);
                            firstBusTiming.setTextSize(13f);
                            secondBusTiming.setTypeface(tf);
                            secondBusTiming.setTextSize(13f);
                            thirdBusTiming.setTypeface(tf);
                            thirdBusTiming.setTextSize(13f);
                            fourthBusTiming.setTypeface(tf);
                            fourthBusTiming.setTextSize(13f);
                            fifthBusTiming.setTypeface(tf);
                            fifthBusTiming.setTextSize(13f);
                            sixthBusTiming.setTypeface(tf);
                            sixthBusTiming.setTextSize(13f);
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                               firstBusTiming.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    secondBusTiming.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    thirdBusTiming.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    fourthBusTiming.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    fifthBusTiming.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    sixthBusTiming.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                }        try {            layout1.setVisibility(View.INVISIBLE);
                                    layout2.setVisibility(View.INVISIBLE);
                                    layout3.setVisibility(View.INVISIBLE);
                                    layout4.setVisibility(View.INVISIBLE);
                                    layout5.setVisibility(View.INVISIBLE);
                                    layout6.setVisibility(View.INVISIBLE);
                                   firstBus.setText("");
                                    firstBusTiming.setText("");
                                    secondBus.setText("");
                                    secondBusTiming.setText("");
                                    thirdBus.setText("");
                                    thirdBusTiming.setText("");
                                    fourthBus.setText("");
                                    fourthBusTiming.setText("");
                                    fifthBus.setText("");
                                    fifthBusTiming.setText("");
                                    sixthBus.setText("");
                                    sixthBusTiming.setText("");
                                }catch(Exception e){      }
                         firstBus.setText(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(0));
                            layout1.setVisibility(View.VISIBLE);
                            if(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).size()>1) {
                                secondBus.setText(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(1));
                                    layout2.setVisibility(View.VISIBLE);
                                }        if(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).size()>2) {
                             thirdBus.setText(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(2));
                                    layout3.setVisibility(View.VISIBLE);
                                }        if(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).size()>3) {
                             fourthBus.setText(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(3));
                                    layout4.setVisibility(View.VISIBLE);
                                }        if(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).size()>4) {
                             fifthBus.setText(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(4));
                                    layout5.setVisibility(View.VISIBLE);
                                }        if(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).size()==6) {
                             sixthBus.setText(this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(5));
                                    layout6.setVisibility(View.VISIBLE);
                                }        firstBusTiming.setText(childList.get(0));
                           if(childList.size()>1) {            secondBusTiming.setText(childList.get(1));
                                }        if(childList.size()>2) {            thirdBusTiming.setText(childList.get(2));
                                }        if(childList.size()>3) {            fourthBusTiming.setText(childList.get(3));
                                }        if(childList.size()>4) {            fifthBusTiming.setText(childList.get(4));
                                }        if(childList.size()==6) {            sixthBusTiming.setText(childList.get(5));
                            }
                     } else {
                         firstBusTiming.setText("");
                         secondBusTiming.setText("");
                         thirdBusTiming.setText("");

                     }    cellHangLayout.setVisibility(View.VISIBLE);
                }


            if(childList.size()==0){
                cellHangLayout.setVisibility(View.GONE);

            }
        }else{
            cellHangLayout.setVisibility(View.GONE);
        }

        //END OF FAV

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.title_price);
        lblListHeader.setTypeface(tf);
        lblListHeader.setText(headerTitle);


        TextView txtListChild1 = (TextView) convertView.findViewById(R.id.title_from_address);
        txtListChild1.setTypeface(tf_h);
        txtListChild1.setText(this._busList.get(groupPosition).getDescription());

        TextView txtListChild2 = (TextView) convertView.findViewById(R.id.title_to_address);
        txtListChild2.setTypeface(tf);
        txtListChild2.setText(this._busList.get(groupPosition).getRoadName());


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

   public void filterData(String query){

        query = query.toLowerCase();
        _busList.clear();

        if(query.isEmpty()){
            _busList.addAll(_originBusList);
        }

        notifyDataSetChanged();

    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        Log.d("GroupExpand","Group Clicked"+groupPosition);
        final int[] countr = {15};
        /*txtTimer = new Timer();
        txtTimer.schedule(new TimerTask(){
            @Override
            public void run() {
                synchronized (this) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(countr[0]!=0){
                                textView.setText(""+countr[0]--+"sec to Refresh");
                            }else{
                                countr[0]=15;
                                textView.setText("Refreshing...");
                            }
                        }
                    });
                }

            }
        },0,1000);*/
        myTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                updateList();
            }
        };

        myTimer.schedule(timerTask, 5000, 10000);

                // Add children as an array to our HashMap
        _grpPosition = groupPosition;
        displayFavData(groupPosition);
        refreshData(groupPosition);
        notifyDataSetChanged();
    }

    @Override
    public void onGroupCollapsed(int groupPosition){
        super.onGroupCollapsed(groupPosition);
        if(myTimer!=null) {
            myTimer.cancel();
            myTimer = null;
        }
        if(txtTimer!=null){
            txtTimer.cancel();
            txtTimer=null;
            timerView.setText("");
        }
        notifyDataSetChanged();
    }

    public void refreshData(int groupPosition){
        NearestBus nearestBus = NearestBus.getInstance();
        JSONArray busStopDetailArray = new JSONArray();
        boolean freshFlow=false;
        String firstBusETA = null;
        String secondBusETA = null;
        String thirdBusETA = null;

        String firstBusColor= null;
        String secondBusColor= null;
        String thirdBusColor= null;
        // toggle clicked cell state

        String busStopId = this._busList.get(groupPosition).getBusStopCode();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(busStopId);
        try {
            busStopDetailArray = nearestBus.getBusStopDetails(jsonArray);


            //this._listDataDisplayChild.get(this._listDataCodeHeader.get(groupPosition)).clear();

            HashMap<String,ArrayList<String>> busDetails = new HashMap<String,ArrayList<String>>();
            HashMap<String,ArrayList<String>> busDetailsColor = new HashMap<String,ArrayList<String>>();
            ArrayList<String> busName = new ArrayList<String>();
            if(busStopDetailArray !=null) {
                if(this._listDataDisplayChild.get(this._busList.get(groupPosition).getBusStopCode())==null){
                    freshFlow=true;
                }
                for (int i = 0; i < busStopDetailArray.length(); i++) {

                    firstBusETA = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus").getString("EstimatedArrival");
                    secondBusETA = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus2").getString("EstimatedArrival");
                    thirdBusETA = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus3").getString("EstimatedArrival");

                    firstBusColor = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus").getString("Load");
                    secondBusColor = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus2").getString("Load");
                    thirdBusColor = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus3").getString("Load");

                    firstBusETA = nearestBus.getTimeStamp(firstBusETA);
                    secondBusETA = nearestBus.getTimeStamp(secondBusETA);
                    thirdBusETA = nearestBus.getTimeStamp(thirdBusETA);
                    //View child = getLayoutInflater().inflate(R.layout.bus_list_view,layout,true);
                    //LinearLayout layout = (LinearLayout)vi.getResources().getLayout(pos);
                    //*FoldingCell cell = (FoldingCell) view;
                    //TextView busName = (TextView) view.findViewById(R.id.content_4);

                    //busName.setText(busStopDetailArray.getJSONObject(i).getString("ServiceNo"));
                    if (firstBusETA.equalsIgnoreCase("No LTA")) {
                        firstBusETA = "No LTAs";
                        secondBusETA = "";
                        thirdBusETA = "";
                    } else if (secondBusETA.equalsIgnoreCase("No LTA")) {
                        if (Integer.parseInt(firstBusETA) < 2) {
                            firstBusETA = "Arr";
                        } else {
                            firstBusETA = firstBusETA + "min";
                        }
                        secondBusETA = "No LTAs";
                        thirdBusETA = "";
                    } else if (thirdBusETA.equalsIgnoreCase("No LTA")) {
                        if (Integer.parseInt(firstBusETA) < 2) {
                            firstBusETA = "Arr";
                        } else {
                            firstBusETA = firstBusETA + "min";
                        }
                        if (Integer.parseInt(secondBusETA) < 2) {
                            secondBusETA = "Arr";
                        } else {
                            secondBusETA = secondBusETA + "min";
                        }
                        thirdBusETA = "No ETA";
                    } else {
                        if (Integer.parseInt(firstBusETA) < 2) {
                            firstBusETA = "Arr";
                        } else {
                            firstBusETA = firstBusETA + "min";
                        }
                        if (Integer.parseInt(secondBusETA) < 2) {
                            secondBusETA = "Arr";
                        } else {
                            secondBusETA = secondBusETA + "min";
                        }
                        if (Integer.parseInt(thirdBusETA) < 2) {
                            thirdBusETA = "Arr";
                        } else {
                            thirdBusETA = thirdBusETA + "min";
                        }
                    }

                    ////Log.d("",firstBusETA+secondBusETA+thirdBusETA);
                    //layout.animate();
                    ArrayList<String> busTiming = new ArrayList<String>();
                    busTiming.add(firstBusETA);
                    busTiming.add(secondBusETA);
                    busTiming.add(thirdBusETA);

                    ArrayList<String> busColor = new ArrayList<String>();
                    busColor.add(firstBusColor);
                    busColor.add(secondBusColor);
                    busColor.add(thirdBusColor);
                    if(_busMap.get(busStopId + "_" +busStopDetailArray.getJSONObject(i).getString("ServiceNo"))==null) {
                        _busMap.put(busStopId + "_" + busStopDetailArray.getJSONObject(i).getString("ServiceNo"), busTiming);
                    }
                    busName.add(busStopDetailArray.getJSONObject(i).getString("ServiceNo"));
                    busDetails.put("" + i, busTiming);
                    busDetailsColor.put(""+i,busColor);
                }
                if(this._listDataDisplayChild.get(this._busList.get(groupPosition).getBusStopCode())==null) {
                    this._busName.put(this._busList.get(groupPosition).getBusStopCode(), busName);
                    this._listDataDisplayChild.put(this._busList.get(groupPosition).getBusStopCode(), _busMap);
                }

                if (this._listDataDisplayChild.get(this._busList.get(groupPosition).getBusStopCode()) != null) {
                    for (int i = 0; i < busStopDetailArray.length(); i++) {
                        this._listDataDisplayChild.get(this._busList.get(groupPosition).getBusStopCode()).put(this._busList.get(groupPosition).getBusStopCode() + "_" + this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(i), busDetails.get("" + i));
                       // this._listDataDisplayChild.get(this._busList.get(groupPosition).getBusStopCode()).put(this._busList.get(groupPosition).getBusStopCode() + "_" + this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(i)+"_Color", busDetailsColor.get("" + i));
                    }
                }
            }


        } catch (JSONException e) {
            //Toast.makeText(_context, "Details not available now. Please try in sometime", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }
    }

    public void displayFavData(int groupPosition){
        NearestBus nearestBus = NearestBus.getInstance();
        JSONArray busStopDetailArray = new JSONArray();
        boolean freshFlow=false;
        String firstBusETA = null;
        String secondBusETA = null;
        String thirdBusETA = null;

        String firstBusColor= null;
        String secondBusColor= null;
        String thirdBusColor= null;
        // toggle clicked cell state

        String busStopId = this._busList.get(groupPosition).getBusStopCode();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(busStopId);
        try {
            busStopDetailArray = nearestBus.getBusStopDetails(jsonArray);


            //this._listDataDisplayChild.get(this._listDataCodeHeader.get(groupPosition)).clear();

            HashMap<String,ArrayList<String>> busDetails = new HashMap<String,ArrayList<String>>();
            HashMap<String,ArrayList<String>> busDetailsColor = new HashMap<String,ArrayList<String>>();
            ArrayList<String> busName = new ArrayList<String>();
            if(busStopDetailArray !=null) {
                if(null!=_listDataFavDisplayChild && this._listDataFavDisplayChild.get(this._busList.get(groupPosition).getBusStopCode())==null){
                    freshFlow=true;
                }
                for (int i = 0; i < busStopDetailArray.length(); i++) {

                    firstBusETA = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus").getString("EstimatedArrival");
                    secondBusETA = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus2").getString("EstimatedArrival");
                    thirdBusETA = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus3").getString("EstimatedArrival");

                    firstBusColor = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus").getString("Load");
                    secondBusColor = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus2").getString("Load");
                    thirdBusColor = busStopDetailArray.getJSONObject(i).getJSONObject("NextBus3").getString("Load");

                    firstBusETA = nearestBus.getTimeStamp(firstBusETA);
                    secondBusETA = nearestBus.getTimeStamp(secondBusETA);
                    thirdBusETA = nearestBus.getTimeStamp(thirdBusETA);
                    //View child = getLayoutInflater().inflate(R.layout.bus_list_view,layout,true);
                    //LinearLayout layout = (LinearLayout)vi.getResources().getLayout(pos);
                    //*FoldingCell cell = (FoldingCell) view;
                    //TextView busName = (TextView) view.findViewById(R.id.content_4);

                    //busName.setText(busStopDetailArray.getJSONObject(i).getString("ServiceNo"));
                    if (firstBusETA.equalsIgnoreCase("No LTA")) {
                        firstBusETA = "No LTAs";
                        secondBusETA = "";
                        thirdBusETA = "";
                    } else if (secondBusETA.equalsIgnoreCase("No LTA")) {
                        if (Integer.parseInt(firstBusETA) < 2) {
                            firstBusETA = "Arr";
                        } else {
                            firstBusETA = firstBusETA + "min";
                        }
                        secondBusETA = "No LTAs";
                        thirdBusETA = "";
                    } else if (thirdBusETA.equalsIgnoreCase("No LTA")) {
                        if (Integer.parseInt(firstBusETA) < 2) {
                            firstBusETA = "Arr";
                        } else {
                            firstBusETA = firstBusETA + "min";
                        }
                        if (Integer.parseInt(secondBusETA) < 2) {
                            secondBusETA = "Arr";
                        } else {
                            secondBusETA = secondBusETA + "min";
                        }
                        thirdBusETA = "No ETA";
                    } else {
                        if (Integer.parseInt(firstBusETA) < 2) {
                            firstBusETA = "Arr";
                        } else {
                            firstBusETA = firstBusETA + "min";
                        }
                        if (Integer.parseInt(secondBusETA) < 2) {
                            secondBusETA = "Arr";
                        } else {
                            secondBusETA = secondBusETA + "min";
                        }
                        if (Integer.parseInt(thirdBusETA) < 2) {
                            thirdBusETA = "Arr";
                        } else {
                            thirdBusETA = thirdBusETA + "min";
                        }
                    }

                    ////Log.d("",firstBusETA+secondBusETA+thirdBusETA);
                    //layout.animate();
                    ArrayList<String> busTiming = new ArrayList<String>();
                    busTiming.add(firstBusETA);
                    busTiming.add(secondBusETA);
                    busTiming.add(thirdBusETA);

                    ArrayList<String> busColor = new ArrayList<String>();
                    busColor.add(firstBusColor);
                    busColor.add(secondBusColor);
                    busColor.add(thirdBusColor);
                    if(_busMap.get(busStopId + "_" +busStopDetailArray.getJSONObject(i).getString("ServiceNo"))==null) {
                        _busMap.put(busStopId + "_" + busStopDetailArray.getJSONObject(i).getString("ServiceNo"), busTiming);
                    }
                    busName.add(busStopDetailArray.getJSONObject(i).getString("ServiceNo"));
                    busDetails.put("" + i, busTiming);
                    busDetailsColor.put(""+i,busColor);
                }
                if(null==this._listDataFavDisplayChild.get(this._busList.get(groupPosition).getBusStopCode())) {
                    this._busName.put(this._busList.get(groupPosition).getBusStopCode(), busName);
                    this._listDataFavDisplayChild.put(this._busList.get(groupPosition).getBusStopCode(), _busMap);
                }

                if (this._listDataFavDisplayChild.get(this._busList.get(groupPosition).getBusStopCode()) != null) {
                    for (int i = 0; i < busStopDetailArray.length(); i++) {
                        this._listDataFavDisplayChild.get(this._busList.get(groupPosition).getBusStopCode()).put(this._busList.get(groupPosition).getBusStopCode() + "_" + this._busName.get(this._busList.get(groupPosition).getBusStopCode()).get(i), busDetails.get("" + i));
                    }
                }
            }


        } catch (JSONException e) {
            //Toast.makeText(_context, "Details not available now. Please try in sometime", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            //Toast.makeText(_context, "Some Error. Please try in sometime", Toast.LENGTH_SHORT).show();
        }
    }
    public void updateList(){
        //final ProgressDialog loading = ProgressDialog.show(_context, "Loading", "Wait while loading...");
        refreshData(_grpPosition);
        //notifyDataSetChanged();
    }
    public void refreshList(){
        notifyDataSetChanged();
    }
}
