package com.sgbus.nearestbus;

import android.view.View;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartBusStop {
	@JsonProperty("b")
	private String busStopCode;
	@JsonProperty("r")
	private String roadName;
	@JsonProperty("d")
	private String description;
	@JsonProperty("t")
	private String timing;
	@JsonProperty("e")
	private String estimation;
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
	public String getTiming() {
		return timing;
	}
	public void setLatitude(String timing) {
		this.timing = timing;
	}
	public String getEstimation() {
		return estimation;
	}
	public void setLongitude(String estimation) {
		this.estimation = estimation;
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
