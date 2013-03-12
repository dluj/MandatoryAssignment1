package com.MA1.entity;

import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.entity.Person;

public class BlipDevice extends Person{

	private String bluetooth;
	private String name;
	
	
	public BlipDevice(){
		super("000ea50050b4","blip tracker");
//		setLocation();
	}
	
	public BlipDevice(String bluetooth, String name){
		super(bluetooth, name);
		this.bluetooth = bluetooth;
		this.name = name;
	}

	public String getBt(){
		return bluetooth;
	}
		
	public String getName(){
		return name;
	}
	
	@Override
	public void contextChanged(ContextEvent event) {
		System.out.println("Something changed --> BLIP Entity: " + name);
	}
	
	@Override
	public String getEntityInfo() {
		return "BlipEntity entity";
	}

}
