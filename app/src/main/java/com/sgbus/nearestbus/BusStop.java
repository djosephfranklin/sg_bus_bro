package com.sgbus.nearestbus;

import android.view.View;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BusStop {
	@JsonProperty("b")
	private String busStopCode;
	@JsonProperty("r")
	private String roadName;
	@JsonProperty("d")
	private String description;
	@JsonProperty("la")
	private Double latitude;
	@JsonProperty("lo")
	private Double longitude;
	@JsonProperty("distance")
	private Double distance;


	private Double comDist;

	private View.OnClickListener requestBtnClickListener;
	
	public String getBusStopCode() {
		return busStopCode;
	}
	public void setBusStopCode(String busStopCode) {
		this.busStopCode = busStopCode;
	}
	public String getRoadName() {
		return roadName;
	}
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}	
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public Double getComDistance() {
		return comDist;
	}
	public void setComDistance(Double comDist) {
		this.comDist = comDist;
	}

	public View.OnClickListener getRequestBtnClickListener() {
		return requestBtnClickListener;
	}

	public void setRequestBtnClickListener(View.OnClickListener requestBtnClickListener) {
		this.requestBtnClickListener = requestBtnClickListener;
	}
}
