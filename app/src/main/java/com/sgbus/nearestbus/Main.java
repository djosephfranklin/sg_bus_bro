package com.sgbus.nearestbus;

import com.sgbus.utils.Utils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class Main {
	
	private final String USER_AGENT = "Mozilla/5.0";
	int R = 6371;
	public static void main(String[] args) throws IOException, JSONException {
		Main main = new Main();
//		System.out.println(main.readProperty("key"));
		//main.checkCloseLatLong();
		//main.callBusService();
		main.distanceCalculation();
	}


	public void distanceCalculation() throws JSONException, IOException {
		int arr = 0;
		int countr=0;
		ArrayList<BusStop> arrayList = null;
		String content = "";
		int tracker=25;
		BusStop[] busStops = null;
		BusStop[] buses = null;
		ObjectMapper mapper = null;
		Properties props = null;
		InputStream inputStream = null;
		double centerLat = 1.352057;
		double centerLon = 103.819843;
		BusStop[] sortings = null;
		props=new Properties();
		Properties prop = new Properties();
		InputStream input = null;
		//inputStream = this.getClass().getClassLoader().getResourceAsStream("CArray.properties");
		input = new FileInputStream("BusProperties.properties");

		// load a properties file

		try {
			prop.load(input);
			//props.load(inputStream);
		} catch (Exception e){
		}

		content=prop.getProperty("content");

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


		//double userDistance = haversine(Double.parseDouble(args.getString(0)),Double.parseDouble(args.getString(1)),centerLat,centerLon);

		while(countr<sortings.length){
			sortings[countr].setComDistance(haversine(centerLat ,centerLon ,sortings[countr].getLatitude(),sortings[countr].getLongitude()));
			countr++;
		}
		JSONArray array = new JSONArray();
		for (BusStop gi : sortings)
		{
			JSONObject obj = new JSONObject();
			obj.put("b", gi.getBusStopCode());
			obj.put("d", gi.getDescription());
			obj.put("distance", gi.getComDistance());
			obj.put("la", gi.getLatitude());
			obj.put("lo", gi.getLongitude());
			obj.put("r", gi.getRoadName());
			array.put(obj);
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("BusProperties1.properties"), "utf-8"))) {
			writer.write(""+array);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void checkCloseLatLong(){
		double lat1=1.36319303798878;	//User
		double lon1=103.82783467220469;	//User
		double lat2=1.352057;
		double lon2=103.819843;

		double dist = haversine(lat1,lon1,lat2,lon2);
		System.out.println(dist + "Km");
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
	private void callBusService() {

		String url = "http://datamall2.mytransport.sg/ltaodataservice/BusStops";
		
		//url = url + "?BusStopID=96159&SST=True";
		int counter=0;

		try {
			while(counter<10000){
				String url1 = url + "?$skip="+counter;
				
				URL obj = new URL(url1);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				// optional default is GET
				con.setRequestMethod("GET");
				// add request header
				con.setRequestProperty("User-Agent", USER_AGENT);
				
				con.setRequestProperty("AccountKey", "sxeLsqA5TYGPoOXo0P/jDg==");
				//con.setRequestProperty("UniqueUserID", "c267debb-83d6-4092-a4b9-da2f9fd13b81");
				con.setRequestProperty("accept", "application/json");
	
				
				
				int responseCode = con.getResponseCode();
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
	
				// print result
				System.out.println(response.toString());
				counter = counter + 50;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String readProperty(String property) {
		String result = "";
		InputStream inputStream = null;
		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
			// get the property value and print it out
			result = prop.getProperty(property);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static void binarySearch(int[ ] array, int lowerbound, int upperbound, int key)
	   {
	       int position;
	       int comparisonCount = 1;    // counting the number of comparisons (optional)

	     // To start, find the subscript of the middle position.
	     position = ( lowerbound + upperbound) / 2;

	     while((array[position] != key) && (lowerbound <= upperbound))
	     {
	         comparisonCount++;
	         if (array[position] > key)             // If the number is > key, ..
	         {                                              // decrease position by one. 
	              upperbound = position - 1;   
	         }                                                             
	              else                                                   
	        {                                                        
	              lowerbound = position + 1;    // Else, increase position by one. 
	        }
	       position = (lowerbound + upperbound) / 2;
	     }
	     if (lowerbound <= upperbound)
	     {
	           System.out.println("The number was found in array subscript" + position);
	           System.out.println("The binary search found the number after " + comparisonCount +
	                 "comparisons.");
	           // printing the number of comparisons is optional
	     }
	     else
	          System.out.println("Sorry, the number is not in this array.  The binary search made "
	                 +comparisonCount  + " comparisons.");
	  }
	
	
}
