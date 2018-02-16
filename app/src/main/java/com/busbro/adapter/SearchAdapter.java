package com.busbro.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.sgbus.nearestbus.BusStop;
import com.sgbus.nearestbus.NearestBus;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseph on 19/8/17.
 */

public class SearchAdapter extends ArrayAdapter<String> {
    private Filter mFilter;

    private List<String> mSubData = new ArrayList<String>();
    static int counter=0;
    ArrayList<BusStop> sortings;
    public SearchAdapter(final Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        setNotifyOnChange(false);

        mFilter = new Filter() {
            private int c = ++counter;
            private List<String> mData = new ArrayList<String>();

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // This method is called in a worker thread
                mData.clear();

                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    try {
                        NearestBus nearestBus = NearestBus.getInstance();
                        Log.d("SearchString",constraint.toString());
                        JSONArray jsArray = new JSONArray();
                        jsArray.put(constraint.toString());
                        sortings = nearestBus.searchBuses(context, jsArray);
                        Log.d("SearchString",""+sortings);
                        for(BusStop busStop :sortings){
                            try{
                                if (busStop.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                    mData.add(busStop.getDescription());
                                }
                            }catch(Exception e) {

                            }

                        }
                    }
                    catch(Exception e) {
                    }

                    filterResults.values = mData;
                    filterResults.count = mData.size();
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(c == counter) {
                    mSubData.clear();
                    if(results != null && results.count > 0) {
                        ArrayList<String> objects = (ArrayList<String>)results.values;
                        for (String v : objects)
                            mSubData.add(v);

                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }
            }
        };
    }

    @Override
    public int getCount() {
        return mSubData.size();
    }

    @Override
    public String getItem(int index) {
        return mSubData.get(index);
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
