package com.MA1.contextserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.RemoteException;

import com.MA1.entity.BlipDevice;
import com.MA1.entity.BlipLocation;
import com.MA1.relationship.Arrived;
import com.MA1.relationship.Left;

import dk.pervasive.jcaf.util.AbstractMonitor;


public class ContextMonitor extends AbstractMonitor{

	private String bluetooth;
	private String name;
	private BlipLocation blip_location;
	private BlipDevice blip_device;
	private final Socket socket;
	final Arrived arrived = new Arrived(this.getClass().getName());
	final Left left = new Left(this.getClass().getName());

	public ContextMonitor(String service_uri, Socket socket) throws RemoteException{
		super(service_uri);
		this.socket = socket;
	}

	@Override public void run(){
		try{
			//read INSIDE ITU message -> bluetooth_name_inside
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String bluetooth_name_position = inFromClient.readLine();
			String[] bn_array = bluetooth_name_position.split("_");
			this.bluetooth = bn_array[0];
			this.name = bn_array[1];
			String in = bn_array[2];
			while(!in.equals("inside")){
				bluetooth_name_position = inFromClient.readLine();
				bn_array = bluetooth_name_position.split("_");
				this.bluetooth = bn_array[0];
				this.name = bn_array[1];
				in = bn_array[2];
			}
			blip_device = new BlipDevice(bluetooth, name);
			System.out.println("ContextMonitor listening for -> " + blip_device.getBt() + " with name-> "+ blip_device.getName());
			String location = "ITU";//generic location, does not exists
			blip_location = new BlipLocation(location);
			while(!location.equals("null")){
				location = BlipLocation.getFromBluetooth(blip_device.getBt());
				String old_location = blip_location.getId();
				if(blip_location.locationChanged(location)){
					getContextService().addContextItem(old_location, left, blip_device);
					//device moved to another location
					blip_location = new BlipLocation(location);
//					getContextService().addEntity(blip_location);
					getContextService().addContextItem(blip_location.getId(), arrived, blip_device);					
				}
				Thread.currentThread();
				Thread.sleep(5000);
			}
			//tell Context Service device left
			getContextService().addContextItem(blip_location.getId(), left, blip_location);
			//read OUTSIDE ITU message -> bluetooth_name_outside
			inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			bluetooth_name_position = inFromClient.readLine();
			bn_array = bluetooth_name_position.split("_");
			this.bluetooth = bn_array[0];
			this.name = bn_array[1];
			System.out.println("ContextMonitor -> Device "+blip_device.getBt()+" left ITU");
		}catch(IOException e){
			System.out.println("ContextMonitor -> Device "+blip_device.getBt()+" left ITU");
		}catch(Exception e){
			System.out.println("ContextMonitor -> Device "+blip_device.getBt()+" disconnected");
		}
	}

	@Override
	public void monitor(String arg0) throws RemoteException {
		// TODO Auto-generated method stub

	}
}
