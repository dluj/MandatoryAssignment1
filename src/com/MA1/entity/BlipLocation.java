package com.MA1.entity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.MA1.entity.BlipDevice;
import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.entity.GenericEntity;

public class BlipLocation extends GenericEntity {

	private String location = "";
	private BlipDevice device;
	
	public BlipLocation(String bluetooth, String name){
		super(bluetooth);
//		this.location = "itu.zone0.zonedorsyd";
		this.device = new BlipDevice(bluetooth, name);
	}
		
	public void updateLocation(String location){
		this.location = location;
	}
	
	public Boolean locationChanged(String actual_location){
		if(location.equals(actual_location)){
			return false;
		}
		this.location = actual_location;
		return true;
	}
	
	public static String getFromBluetooth(String bluetooth){		
		String location = "error";
		URL url;
		HttpURLConnection conn;
		InputStreamReader isr;
		BufferedReader rd;
		String url_string = "http://pit.itu.dk:7331/location-of/";
		JSONObject device_info;
		try {
			url = new URL(url_string + bluetooth);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			isr = new InputStreamReader(conn.getInputStream());
			rd = new BufferedReader(isr);
			device_info = (JSONObject)JSONValue.parse(isr);
			location = device_info.get("location").toString();
			rd.close();
		}catch(NullPointerException e){
				location = "null";
		} catch (Exception e) {
			System.out.println("getFromBluetooth "+ e.toString() + "\n ************** \n");
			e.printStackTrace();
		}
		return location;
	}
	
	public String getBT(){
		return this.device.getBt();
	}
	
	public String getName(){
		return this.device.getName();
	}
	
	@Override
	public void contextChanged(ContextEvent event) {
		BlipLocation aux = ((BlipLocation)event.getItem());
		System.out.println("Something changed --> BLIP Location: "+ aux.getId() + " device:" + aux.getBT() + " with name:" + aux.getName());
	}
	@Override
	public String getEntityInfo() {
		return "BlipLocation location -> "+location;
	}
	
}
