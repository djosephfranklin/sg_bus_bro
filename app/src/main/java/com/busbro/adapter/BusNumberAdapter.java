package com.busbro.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.busbro.Item;
import com.busbro.R;
import com.sgbus.nearestbus.BusStop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusNumberAdapter extends ArrayAdapter<String> {
    ArrayList<BusStop> BusNumber= null;
    Context ctxt;
    ArrayList<String> BusTiming = new ArrayList<String>();
    private Typeface tf_h;
    private Typeface tf;
    private static LayoutInflater inflater=null;
    public BusNumberAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public BusNumberAdapter(Context context, int resource, ArrayList<BusStop> busNames) {
        super(context, resource);
        BusNumber=busNames;
        ctxt=context;
        tf_h = Typeface.createFromAsset(context.getAssets(), "fonts/Calibri Bold.ttf");
        //BusTiming=items;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return BusNumber.size();
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        TextView tv2;
        TextView tv3;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        //View ;
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            rowView = vi.inflate(R.layout.bus_nbr_list_view, null);
        }
        //rowView = inflater.inflate(R.layout.bus_list_view, null);
        holder.tv=(TextView) rowView.findViewById(R.id.content_4);
        holder.tv2=(TextView) rowView.findViewById(R.id.content_5);
        holder.tv3=(TextView) rowView.findViewById(R.id.content_6);

        holder.tv.setText(BusNumber.get(position).getBusStopCode());
        holder.tv.setTypeface(tf_h);
        holder.tv.setTextSize(16f);
        holder.tv2.setText(BusNumber.get(position).getDescription());
        holder.tv2.setTypeface(tf);
        holder.tv2.setTextSize(16f);
        holder.tv3.setText(BusNumber.get(position).getRoadName());
        holder.tv3.setTypeface(tf);
        holder.tv3.setTextSize(14f);
        //holder.tv2.setText(BusTiming.get(position) );
        /*rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
            }
        });*/
        return rowView;
    }

    public android.widget.Filter getFilter() {
        // TODO Auto-generated method stub
        return new android.widget.Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // TODO Auto-generated method stub

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

}