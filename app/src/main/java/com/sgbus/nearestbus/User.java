package com.sgbus.nearestbus;

import org.codehaus.jackson.annotate.JsonProperty;

public class User {
	@JsonProperty("Latitude")
	private String latitude;
	@JsonProperty("Longitude")
	private String longitude;
	private Double distance;
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
}
