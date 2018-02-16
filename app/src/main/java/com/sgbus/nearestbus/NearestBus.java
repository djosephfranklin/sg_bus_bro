package com.sgbus.nearestbus;

import android.content.Context;
import android.os.StrictMode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import android.util.Log;
import android.widget.Toast;
import com.sgbus.utils.Utils;
import org.apache.commons.collections4.functors.ExceptionClosure;
import org.apache.commons.collections4.list.TreeList;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NearestBus{

    private static NearestBus instance = null;
    protected NearestBus() {
        // Exists only to defeat instantiation.
    }
    public static NearestBus getInstance() {
        if(instance == null) {
            instance = new NearestBus();
        }
        return instance;
    }
	ArrayList<BusStop> arrayList = null;
	ArrayList<SmartBusStop> smartArrayList = new ArrayList<SmartBusStop>();

	int countr=0;
	ArrayList<BusStop> buses = null;
	BusStop[] sortings=null;
	BusStop[] searchSortings=null;
	BusStop[] favSortings=null;
	ObjectMapper mapper = null;
	Properties props = null;
	InputStream inputStream = null;
	public final double R = 6372.8; // In kilometers
	String content="";
	public String favContent="";
	String searchContent="";

	public void loadSearchBusDetails(Context context) throws IOException {
		Utils.populateBusStopsList(context);
	}

	public ArrayList<BusStop> searchBuses(Context context,JSONArray args) throws JSONException {
        int countr=0;
		props=new Properties();
		ArrayList<String> busNumbers = new ArrayList<String>();
		try {


		try {
			busNumbers = (Utils.returnBusNumbersList(context, args.getString(0)));
		}catch(Exception e){

		}
		Log.d("NearestBus","Bus Number size: "+busNumbers.size());
		int busNumbersSize=0;
		String busNumber=null;
        String busNumber2= null;
        BusStop[] busNumberSorting=null;
        BusStop[] busNumberSorting2=null;
		try {
			Log.d("NearestBus","Length: "+args.getString(0).length());
			if(args.getString(0).length()<5 && busNumbers.size()>0) {
				//Utils.populateBusStopsList(context);
				busNumber = (Utils.returnBusStopsList1(context, args.getString(0)));
				Log.d("NearestBus","Bus Number : "+busNumber);
                busNumber2 = (Utils.returnBusStopsList2(context, args.getString(0)));
				Log.d("NearestBus","Bus Number 2 : "+busNumber2);
                mapper = new ObjectMapper();
                try {
						busNumberSorting = mapper.readValue(busNumber, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
                    if(busNumber2!=null) {
						busNumberSorting2 = mapper.readValue(busNumber2, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
					}
                } catch (JsonParseException e) {
                    //System.out.println("PARSE Exception");
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    //System.out.println("MAP Exception");
                    e.printStackTrace();
                } catch (IOException e) {
                    //System.out.println("IO Exception");
                    e.printStackTrace();
                }catch(Exception e){
                    //System.out.println("Other Exception");
                    e.printStackTrace();
                }
				if (busNumber != null) {
					if(busNumberSorting!=null) {
						busNumbersSize = busNumberSorting.length;
					}
					if(busNumber2!=null) {
						busNumbersSize = busNumbersSize + busNumberSorting2.length;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        arrayList = new ArrayList<BusStop>();
        int size=0;
        if(busNumber!=null && busNumbersSize!=0) {
            for (int i = 0; i < busNumbersSize; i++) {
                if(busNumber2!=null && i>=busNumberSorting.length){
                    arrayList.add(busNumberSorting2[size]);
                    size++;
                }else{
                    if(i<busNumberSorting.length){
                        arrayList.add(busNumberSorting[i]);
                    }
                }

            }
            //busNumber="";
            return arrayList;
        }
			if(searchContent.equalsIgnoreCase("")) {
				searchContent = (Utils.getProperty("content", context));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mapper = new ObjectMapper();
		try {
			searchSortings = mapper.readValue(searchContent, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
			////Log.d("Search String",""+sorting);
		} catch (JsonParseException e) {
			//System.out.println("PARSE Exception");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			//System.out.println("MAP Exception");
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("IO Exception");
			e.printStackTrace();
		}catch(Exception e){
			//System.out.println("Other Exception");
			e.printStackTrace();
		}
		//Log.d("NearestBus","Bus Number 2222 :::: "+busNumberSorting.length);
		if(args.getString(0).toLowerCase().contains("block")){
            args.put(0,args.getString(0).toLowerCase().replace("block","Blk"));
        }
		if(args.getString(0).toLowerCase().contains("street")){
			args.put(0,args.getString(0).toLowerCase().replace("street","St"));
		}
		if(args.getString(0).toLowerCase().contains("road")){
			args.put(0,args.getString(0).toLowerCase().replace("road","Rd"));
		}


		for(BusStop busStop :searchSortings){
                try{
						if (busStop.getBusStopCode().equals(args.getString(0))) {
							arrayList.add(busStop);
							return arrayList;
						}
						if (busStop.getDescription().toLowerCase().contains(args.getString(0).toLowerCase())) {
							arrayList.add(busStop);
						}
						if (args.getString(0).toLowerCase().contains(busStop.getRoadName().toLowerCase())) {
							arrayList.add(busStop);
						}

                }catch(Exception e) {

				}

        }

        return arrayList;
	}

	public ArrayList<BusStop> searchBusNbr(Context context,JSONArray args) throws JSONException {
		int countr=0;
		props=new Properties();
		ArrayList<String> busNumbers = new ArrayList<String>();
		try {


			try {
				busNumbers = (Utils.returnBusNumbersList(context, args.getString(0)));
			}catch(Exception e){

			}
			Log.d("NearestBus","Bus Number size: "+busNumbers.size());
			int busNumbersSize=0;
			String busNumber=null;
			String busNumber2= null;
			BusStop[] busNumberSorting=null;
			try {
				Log.d("NearestBus","Length: "+args.getString(0).length());
				if(args.getString(0).length()<5 && busNumbers.size()>0) {
					//Utils.populateBusStopsList(context);
					if(args.get(1).toString().equalsIgnoreCase("1")) {
						busNumber = (Utils.returnBusStopsList1(context, args.getString(0)));
						Log.d("NearestBus", "Bus Number : " + busNumber);
					}else {
						busNumber = (Utils.returnBusStopsList2(context, args.getString(0)));
						Log.d("NearestBus", "Bus Number 2 : " + busNumber);
					}

					if(null== busNumber)
					{
						return arrayList;
					}
					mapper = new ObjectMapper();
					try {
						busNumberSorting = mapper.readValue(busNumber, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
					} catch (JsonParseException e) {
						//System.out.println("PARSE Exception");
						e.printStackTrace();
					} catch (JsonMappingException e) {
						//System.out.println("MAP Exception");
						e.printStackTrace();
					} catch (IOException e) {
						//System.out.println("IO Exception");
						e.printStackTrace();
					}catch(Exception e){
						//System.out.println("Other Exception");
						e.printStackTrace();
					}
					if (busNumber != null) {
						if(busNumberSorting!=null) {
							busNumbersSize = busNumberSorting.length;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			arrayList = new ArrayList<BusStop>();
			int size=0;
			if(busNumber!=null && busNumbersSize!=0) {
				for (int i = 0; i < busNumbersSize; i++) {
					if(i<busNumberSorting.length){
						arrayList.add(busNumberSorting[i]);
					}
				}
				//busNumber="";
				return arrayList;
			}
			if(searchContent.equalsIgnoreCase("")) {
				searchContent = (Utils.getProperty("content", context));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mapper = new ObjectMapper();
		try {
			searchSortings = mapper.readValue(searchContent, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
			////Log.d("Search String",""+sorting);
		} catch (JsonParseException e) {
			//System.out.println("PARSE Exception");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			//System.out.println("MAP Exception");
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("IO Exception");
			e.printStackTrace();
		}catch(Exception e){
			//System.out.println("Other Exception");
			e.printStackTrace();
		}
		//Log.d("NearestBus","Bus Number 2222 :::: "+busNumberSorting.length);
		if(args.getString(0).toLowerCase().contains("block")){
			args.put(0,args.getString(0).toLowerCase().replace("block","Blk"));
		}
		if(args.getString(0).toLowerCase().contains("street")){
			args.put(0,args.getString(0).toLowerCase().replace("street","St"));
		}
		if(args.getString(0).toLowerCase().contains("road")){
			args.put(0,args.getString(0).toLowerCase().replace("road","Rd"));
		}


		for(BusStop busStop :searchSortings){
			try{
				if (busStop.getBusStopCode().equals(args.getString(0))) {
					arrayList.add(busStop);
					return arrayList;
				}
				if (busStop.getDescription().toLowerCase().contains(args.getString(0).toLowerCase())) {
					arrayList.add(busStop);
				}
				if (args.getString(0).toLowerCase().contains(busStop.getRoadName().toLowerCase())) {
					arrayList.add(busStop);
				}

			}catch(Exception e) {

			}

		}

		return arrayList;
	}

	public ArrayList<SmartBusStop> getSmartBusDetails(Context context) throws JSONException {
		int countr=0;
		props=new Properties();
		int busNumbersSize=0;
		String busNumber=null;
		if(smartArrayList.size()==0) {
			try {
				busNumber = (Utils.returnSmartBusStopsList(context));
			} catch (Exception e) {

			}

			SmartBusStop[] busNumberSorting = null;

			mapper = new ObjectMapper();
			try {
				busNumberSorting = mapper.readValue(busNumber, TypeFactory.defaultInstance().constructArrayType(SmartBusStop.class));
			} catch (JsonParseException e) {
				//System.out.println("PARSE Exception");
				e.printStackTrace();
			} catch (JsonMappingException e) {
				//System.out.println("MAP Exception");
				e.printStackTrace();
			} catch (IOException e) {
				//System.out.println("IO Exception");
				e.printStackTrace();
			} catch (Exception e) {
				//System.out.println("Other Exception");
				e.printStackTrace();
			}
			if (busNumber != null) {
				if (busNumberSorting != null) {
					busNumbersSize = busNumberSorting.length;
				}
			}

			int size = 0;
			if (busNumber != null && busNumbersSize != 0) {
				for (int i = 0; i < busNumbersSize; i++) {
					if (i < busNumberSorting.length) {
						smartArrayList.add(busNumberSorting[i]);
					}
				}
				//busNumber="";
			}
		}
		Log.d("NearestBus", "Length: " +smartArrayList);
		return smartArrayList;
	}

	public String getBusNbrDetails(Context context,String content,String busNbr){
		if(null==arrayList){
			JSONArray jsArray = new JSONArray();
			jsArray.put(busNbr);
			jsArray.put("1");
			try {
				arrayList = searchBusNbr(context,jsArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if(content.equalsIgnoreCase("from")) {
			return arrayList.get(0).getDescription();
		}else{
			return arrayList.get(arrayList.size()-1).getDescription();
		}
	}

	public ArrayList<BusStop> searchDestBuses(Context context,JSONArray args) throws JSONException {
		int countr=0;
		props=new Properties();
		ArrayList<String> busNumbers = new ArrayList<String>();
		try {

			Log.d("NearestBus","Bus Number size: "+busNumbers.size());

			if(searchContent.equalsIgnoreCase("")) {
				searchContent = (Utils.getProperty("content", context));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		mapper = new ObjectMapper();
		try {
			searchSortings = mapper.readValue(searchContent, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
			////Log.d("Search String",""+sorting);
		} catch (JsonParseException e) {
			//System.out.println("PARSE Exception");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			//System.out.println("MAP Exception");
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("IO Exception");
			e.printStackTrace();
		}catch(Exception e){
			//System.out.println("Other Exception");
			e.printStackTrace();
		}
		//Log.d("NearestBus","Bus Number 2222 :::: "+busNumberSorting.length);
		if(args.getString(0).toLowerCase().contains("block")){
			args.put(0,args.getString(0).toLowerCase().replace("block","Blk"));
		}
		if(args.getString(0).toLowerCase().contains("street")){
			args.put(0,args.getString(0).toLowerCase().replace("street","St"));
		}
		if(args.getString(0).toLowerCase().contains("road")){
			args.put(0,args.getString(0).toLowerCase().replace("road","Rd"));
		}


		for(BusStop busStop :searchSortings){
			try{
				if (busStop.getBusStopCode().equals(args.getString(0))) {
					arrayList.add(busStop);
					return arrayList;
				}
				if (busStop.getDescription().toLowerCase().contains(args.getString(0).toLowerCase())) {
					arrayList.add(busStop);
				}
				if (args.getString(0).toLowerCase().contains(busStop.getRoadName().toLowerCase())) {
					arrayList.add(busStop);
				}

			}catch(Exception e) {

			}

		}

		return arrayList;
	}

	public ArrayList<String> searchBusNumbers(Context context,JSONArray args) throws JSONException, IOException {
		ArrayList<String> busNumber=null;
		busNumber = (Utils.returnBusNumbersList(context, args.getString(0).toUpperCase()));
		Log.d("NearestBus","Bus Number : "+busNumber);
		return busNumber;
	}

	public JSONArray getBusStopDetails(JSONArray args) throws JSONException {
		String USER_AGENT = "Mozilla/5.0";
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy);
            Log.d("Nearest Bus","Bus Stop ID ::"+args.getString(0));
			//String url = "http://datamall2.mytransport.sg/ltaodataservice/BusArrival?SST=True&BusStopID="+ args.getString(0);
			String url = "http://datamall2.mytransport.sg/ltaodataservice/BusArrivalv2?BusStopCode="+ args.getString(0);

			////Log.d("BusStopDetails","URL : "+url);
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			con.setRequestProperty("AccountKey", "sxeLsqA5TYGPoOXo0P/jDg==");
			//con.setRequestProperty("UniqueUserID", "c267debb-83d6-4092-a4b9-da2f9fd13b81");
			con.setRequestProperty("accept", "application/json");

			int responseCode = con.getResponseCode();

			////Log.d("BusStopDetails","ResponseCode: "+responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();


			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

           // ////Log.d("BusStopDetails","Response: "+response.toString());
			JSONObject resJson = new JSONObject(response.toString());

			JSONArray busServices = resJson.getJSONArray("Services");
			JSONArray sortedJsonArray = new JSONArray();

			List<JSONObject> jsonValues = new ArrayList<JSONObject>();
			for (int i = 0; i < busServices.length(); i++) {
				jsonValues.add(busServices.getJSONObject(i));
			}
			Collections.sort( jsonValues, new Comparator<JSONObject>() {
				//You can change "Name" with "ID" if you want to sort by ID
				private static final String KEY_NAME = "ServiceNo";

				@Override
				public int compare(JSONObject a, JSONObject b) {
					String valA = new String();
					String valB = new String();

					try {
						valA = (String) a.get(KEY_NAME);
						valB = (String) b.get(KEY_NAME);
					}
					catch (JSONException e) {
						//do something
					}

					return valA.compareTo(valB);
					//if you want to change the sort order, simply use the following:
					//return -valA.compareTo(valB);
				}
			});

			for (int i = 0; i < busServices.length(); i++) {
				sortedJsonArray.put(jsonValues.get(i));
			}

            return sortedJsonArray;
			// print result
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e){
			JSONArray sortedJsonArray = new JSONArray();
			sortedJsonArray.put(0,"NO CONNECTION");
			return sortedJsonArray;
		} catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	}
	public ArrayList<BusStop> getAllBus(JSONArray args, Context context) throws JSONException{
		int arr = 0;
		countr=0;

		double userLat = Double.parseDouble(args.getString(0));
		double userLon = Double.parseDouble(args.getString(1));
		try {
			content=(Utils.getProperty("content",context,args.getString(1),args.getString(0)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		mapper = new ObjectMapper();
		try {
			sortings = mapper.readValue(content, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
		} catch (JsonParseException e) {
			//System.out.println("PARSE Exception");
			e.printStackTrace();
		} catch (JsonMappingException e) {
			//System.out.println("MAP Exception");
			e.printStackTrace();
		} catch (IOException e) {
			//System.out.println("IO Exception");
			e.printStackTrace();
		}catch(Exception e){
			//System.out.println("Other Exception");
			e.printStackTrace();
		}


		double key;
		HashMap<Integer, BusStop> hmap = new HashMap<Integer, BusStop>();
		if(null!=sortings) {
			for (BusStop busStop : sortings) {
				if (busStop.getLongitude() != 0.0) {
					busStop.setComDistance(haversine(userLat, userLon, busStop.getLatitude(), busStop.getLongitude()));
					if (busStop.getComDistance() <= 0.9) {
						key = busStop.getComDistance() * 100000;
						hmap.put((int) key, busStop);

					}
				}
			}
			Map<Integer, BusStop> map = new TreeMap<Integer, BusStop>(hmap);
			ArrayList<BusStop> busArray = new ArrayList<BusStop>(map.values());
			return busArray;
		}
		//arrayList.addAll(busArray.subList(0,25));

       return null;
	}

	public double haversine(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}
	public ArrayList<BusStop> getFav(JSONArray jsArray, Context context) throws JSONException {
        props=new Properties();
		try {
			Log.d("favContent",favContent);
			if(favContent.equalsIgnoreCase("")) {
				favContent = (Utils.getProperty("content", context));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        mapper = new ObjectMapper();
        try {
			if(favSortings!=null){
				Log.d("FavSortings",":::!:!:!:!:!:!:");
			}else {
				if(null!=favContent) {
					favSortings = mapper.readValue(favContent, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
				}
			}
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
		arrayList = new ArrayList<BusStop>();

        String busStop = jsArray.getString(0);
        List<String> busStopArray = Arrays.asList(busStop.split(","));
		int counter=0;
		if(null!=favSortings) {
			int favBusSize = busStopArray.size();
			for (BusStop favBusStop : favSortings) {
				if (busStopArray.contains(favBusStop.getBusStopCode())) {
					arrayList.add(favBusStop);
					counter++;
				}
				if (counter == favBusSize) {
					return arrayList;
				}
			}
		}
		return arrayList;
	}

	public static String retString(InputStream inputStream) throws IOException{
		StringBuilder sb=new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String read;

		while((read=br.readLine()) != null) {
		    //System.out.println(read);
		    sb.append(read);
		}

		br.close();
		return sb.toString();
	}

	public String getBusStopID(String position){
        ////Log.d("NearestBus",""+arrayList.size());
		int pos = Integer.parseInt(position);
        return arrayList.get(pos).getBusStopCode();
    }
	public String getBusStopDescription(String position){
		////Log.d("NearestBus",""+arrayList.size());
		int pos = Integer.parseInt(position);
		return arrayList.get(pos).getDescription();
	}

    public String getTimeStamp(String dateString) {
        Date testDate = null;
        Long millis = null;
        if (dateString == null || dateString.equalsIgnoreCase("")) {
			return "NO LTA";
		}else{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            try {
                testDate = sdf.parse(dateString);
				Long tsLong = System.currentTimeMillis();
				millis = testDate.getTime()- tsLong;
				return ""+String.format("%d",TimeUnit.MILLISECONDS.toMinutes(millis));
			} catch (ParseException e) {
				e.printStackTrace();
			}
        }
        return "NO LTA";
    }

	public ArrayList<BusStop> getBusService(JSONArray args, Context context, String to_from) throws JSONException{
		String[] strArr;
		BusStop[] sortingsArr=null;
		String busContent="";
		int arr = 0;
		countr=0;
		ArrayList<BusStop> busStops = new ArrayList<BusStop>();
		try {
			busContent= (Utils.returnBusStopsList1(context,args.getString(0).toUpperCase()+""+to_from));
			if(!busContent.equalsIgnoreCase("")) {
				sortingsArr = mapper.readValue(busContent, TypeFactory.defaultInstance().constructArrayType(BusStop.class));
				for (BusStop busServiceStop : sortingsArr) {
					busStops.add(busServiceStop);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return busStops;
	}
}
