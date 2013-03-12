package com.MA1.contextserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

import com.MA1.entity.BlipLocation;
import com.MA1.relationship.Arrived;
import com.MA1.relationship.Left;

import dk.pervasive.jcaf.util.AbstractMonitor;

public class ContextServer extends AbstractMonitor{

	//	final BlipLocation blip_location = new BlipLocation();
	//
	final Arrived arrived = new Arrived(this.getClass().getName());
	final Left left = new Left(this.getClass().getName());
	//	final Attending attending = new Attending();
	private String bluetooth;
	private String name;
	private BlipLocation blip_location;
	private Boolean detected = false;
	private final Socket socket;

	public ContextServer(String serviceUri,Socket socket) throws RemoteException {
		super(serviceUri);
		this.socket = socket;
		try{
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String bluetooth_name_position = inFromClient.readLine();
			String[] bn_array = bluetooth_name_position.split("_");
			this.bluetooth = bn_array[0];
			this.name = bn_array[1];
			blip_location = new BlipLocation(bluetooth, name);
			String inside = bn_array[2];
			
//			System.out.println("TCP message received --> " + bluetooth_name_position);
//			System.out.println("MAIN --> waiting for devices2");
//			bluetooth_name_position = inFromClient.readLine();
//			System.out.println("TCP message received --> " + bluetooth_name_position);
//			//get TCP package with bluetooth_name
//			//			String bluetooth_name = "0C:71:5D:C9:44:F9_daniel_inside";
//			bn_array = bluetooth_name_position.split("_");
//			bluetooth = bn_array[0];
//			name = bn_array[1];
//			inside = bn_array[2];
		}catch(IOException e){
			System.err.print(e.getStackTrace());
		}
	}

	@Override
	public void run() {		
		try {
			System.out.println("Server info: \n   " + getContextService().getServerInfo());
			System.out.println("Tracking for device: " + bluetooth + " with name :" + name);
			String location = "ITU";

			while(!location.equals("null")){
				location = BlipLocation.getFromBluetooth(bluetooth);
				if(blip_location.locationChanged(location)){
					getContextService().addEntity(blip_location);
					getContextService().addContextItem(blip_location.getId(), arrived, blip_location);					
				}
				Thread.currentThread();
				Thread.sleep(5000);
			}
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String bluetooth_name_position = inFromClient.readLine();
			String[] bn_array = bluetooth_name_position.split("_");
			this.bluetooth = bn_array[0];
			this.name = bn_array[1];
			blip_location = new BlipLocation(bluetooth, name);
			String outside = bn_array[2];
			System.out.println("Device left ITU");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e){
			System.err.println("Thread error --> " + e.toString());
			e.printStackTrace();
		}
		System.out.println("Device gone!");
		System.exit(0);
	}

	public static void main(String[] args) {
		try{
			ServerSocket welcomeSocket = new ServerSocket(6789);
			System.out.println("MAIN --> waiting for devices1");
			while(true){
				Socket connectionSocket = welcomeSocket.accept();
				//get TCP package with bluetooth_name
				//			String bluetooth_name = "0C:71:5D:C9:44:F9_daniel_inside";
				ContextServer server = new ContextServer("rmi://10.25.231.246/spct", connectionSocket);
				Thread t = new Thread(server);
				t.start();
			}
		}catch(RemoteException e){
			System.err.println(e.toString() + "**************");
			e.printStackTrace();
		}catch(IOException e){
			System.err.println(e.toString() + "**************");
			e.printStackTrace();
		}
	}

	@Override
	public void monitor(String arg0) throws RemoteException {
		// TODO Auto-generated method stub
	}
}


