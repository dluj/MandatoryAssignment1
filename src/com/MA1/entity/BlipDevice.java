package com.MA1.entity;

import dk.pervasive.jcaf.ContextEvent;
import dk.pervasive.jcaf.entity.Person;

public class BlipDevice extends Person{

	private String bluetooth;
	private String name;
	
	
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
	
	public String toString(){
		return "bluetooth->" + this.getBt();
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
