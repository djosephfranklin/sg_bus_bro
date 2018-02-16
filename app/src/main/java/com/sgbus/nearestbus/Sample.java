package com.sgbus.nearestbus;

import org.json.JSONArray;

/**
 * Created by laptop on 11/19/2016.
 */
public class Sample {

    public static void main(String args[]){
        String lat="1.3399";
        String lon="103.9495";
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(lat);
        jsonArray.put(lon);
        NearestBus nearestBus= new NearestBus();

    }
}
