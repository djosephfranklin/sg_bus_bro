package com.sgbus.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Nirmal Dhara on 12-07-2015.
 */
public class Utils {
    public static InputStream inputStream = null;
    public static InputStream inputStream1 = null;
    public static InputStream inputStream2 = null;
    public static Properties properties = new Properties();
    public static Properties properties1 = new Properties();
    public static Properties properties2 = new Properties();
    public static AssetManager assetManager = null;
    public static String getProperty(String key,Context context,String longitude,String latitude) throws IOException {
        Log.d("Longitude",longitude);
        Log.d("Latitude",latitude);
        double sectorHorRange0=1.404396;
        double sectorHorRange1=1.349823;
        double sectorHorRange2=1.320048;

        double sectorRange0=103.675305;
        double sectorRange1=103.711011;
        double sectorRange2=103.743627;
        double sectorRange3=103.776414;
        double sectorRange4=103.820229;
        double sectorRange5=103.849798;
        double sectorRange6=103.879667;
        double sectorRange7=103.905073;
        double sectorRange8=103.937517;
        double sectorRange9=103.967214;
        String fileName="";

        if(Double.parseDouble(longitude)<=sectorRange0) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector0.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector1.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector2.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange0 && Double.parseDouble(longitude)<sectorRange1) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector3.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector4.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector5.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange1 && Double.parseDouble(longitude)<sectorRange2) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector6.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector7.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector8.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange2 && Double.parseDouble(longitude)<sectorRange3) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector9.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector10.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector11.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange3 && Double.parseDouble(longitude)<sectorRange4) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector12.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector13.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector14.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange4 && Double.parseDouble(longitude)<sectorRange5) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector15.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector16.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector17.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange5 && Double.parseDouble(longitude)<sectorRange6) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector18.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector19.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector20.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange6 && Double.parseDouble(longitude)<sectorRange7) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector21.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector22.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector23.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange7 && Double.parseDouble(longitude)<sectorRange8) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector24.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector25.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector26.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange8 && Double.parseDouble(longitude)<sectorRange9) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector27.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector28.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector29.properties";
            }
        }
        if(Double.parseDouble(longitude)>sectorRange9) {
            if (Double.parseDouble(latitude) <= sectorHorRange0 || (Double.parseDouble(latitude) > sectorHorRange0 && (sectorHorRange0 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector30.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange1 || (Double.parseDouble(latitude) > sectorHorRange1 && (sectorHorRange1 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector31.properties";
            } else if (Double.parseDouble(latitude) <= sectorHorRange2 || (Double.parseDouble(latitude) > sectorHorRange2 && (sectorHorRange2 - Double.parseDouble(latitude)) < 1.5)) {
                fileName="BusSector32.properties";
            }
        }

        Log.d("Utils",""+fileName);
        Properties properties = new Properties();;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(fileName);
        properties.load(inputStream);


        return properties.getProperty(key);

    }

    public static void populateBusStopsList(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();

        try {
            Log.d("Utils","InputStream : "+inputStream);
            if(inputStream==null) {
                inputStream = assetManager.open("BusDetailsProp.properties");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        properties.load(inputStream);
    }
    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    public static ArrayList<String> returnBusNumbersList(Context context, String key) throws IOException {
        ArrayList<String> contentArray = new ArrayList<String>();
        if(assetManager==null) {
            assetManager = context.getAssets();
        }
        try {
            Log.d("Utils","InputStream : "+inputStream1);
            if(inputStream1==null) {
                inputStream1 = assetManager.open("BusNumberProp.properties");
                properties1.load(inputStream1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content =properties1.getProperty("content");

        if(content==null){
            content="";
        }

        Log.d("ContentArray","Key "+key);
        String[] strArray = content.split("_");
        for(int i=0;i<strArray.length;i++) {
            if (strArray[i].equalsIgnoreCase(key.toLowerCase())) {
                contentArray.add(strArray[i]);
            }
        }
        for(int i=0;i<strArray.length;i++){
            if(!strArray[i].equalsIgnoreCase(key.toLowerCase()) && strArray[i].contains(key.toLowerCase())) {
                contentArray.add(strArray[i]);
            }
        }
        Log.d("ContentArray",""+contentArray);
        return contentArray;
    }
    public static String returnBusStopsList1(Context context, String key) throws IOException {
        if(assetManager==null) {
            assetManager = context.getAssets();
        }
        try {
            Log.d("Utils","InputStream : "+inputStream);
            if(inputStream==null) {
                inputStream = assetManager.open("BusDetailsProp.properties");
                properties.load(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = properties.getProperty(key+"_1");
        if(content==null){
            content="";
        }
        return content;
    }

    public static String returnBusStopsList2(Context context, String key) throws IOException {
        if(assetManager==null) {
            assetManager = context.getAssets();
        }
        try {
            if(inputStream==null) {
                inputStream = assetManager.open("BusDetailsProp.properties");
                properties.load(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = properties.getProperty(key+"_2");
        return content;
    }
    public static String returnSmartBusStopsList(Context context) throws IOException {
        if(assetManager==null) {
            assetManager = context.getAssets();
        }
        try {
            if(inputStream2==null) {
                inputStream2 = assetManager.open("SmartBus.properties");
                properties2.load(inputStream2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String content = properties2.getProperty("content");
        return content;
    }

    public static String getProperty(String key,Context context) throws IOException {
        String fileName="BusProperties.properties";
        //Log.d("Utils",""+fileName);
        Properties properties = new Properties();;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(fileName);
        properties.load(inputStream);
        return properties.getProperty(key);

    }
}
