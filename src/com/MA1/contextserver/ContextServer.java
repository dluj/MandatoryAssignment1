package com.MA1.contextserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.rmi.RemoteException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.MA1.entity.BlipLocation;

import dk.pervasive.jcaf.util.AbstractMonitor;

public class ContextServer extends AbstractMonitor{

	private final static String service_uri = "rmi://10.25.231.246/info@itu";

	public ContextServer(String serviceUri) throws RemoteException {
		super(serviceUri);
	}

	@Override
	public void run() {		
		try {
			System.out.println("Server-> \n   " + getContextService().getServerInfo());

			addLocations();			
			ServerSocket welcomeSocket = new ServerSocket(6789);
			while(true){
				System.out.println("ContextServer-> Listening on port 6789");
				Socket connectionSocket = welcomeSocket.accept();
				ContextMonitor monitor = new ContextMonitor(service_uri, connectionSocket);
				Thread thread = new Thread(monitor);
				System.out.println("ContextServer-> new connection... starting thread");
				thread.start();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e){
			System.err.println("Server->Thread error --> " + e.toString());
			e.printStackTrace();
		}
		System.out.println("ContextServer-> GoodBye!!");
	}

	public static void main(String[] args) {
		try{
			ContextServer server = new ContextServer(service_uri);
			Thread t = new Thread(server);
			t.start();
			//			}
		}catch(RemoteException e){
			System.err.println(e.toString() + "**************");
			e.printStackTrace();
		}
	}

	private void addLocations(){
		try {
			URL url;
			HttpURLConnection conn;
			InputStreamReader isr;
			BufferedReader rd;
			JSONArray locations;
			url = new URL("http://pit.itu.dk:7331/locations");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			isr = new InputStreamReader(conn.getInputStream());
			rd = new BufferedReader(isr);
			locations = (JSONArray)JSONValue.parse(isr);
			for(int i=0; i<locations.size();i++){
				JSONObject aux = (JSONObject)locations.get(i);
				String location = aux.get("location-name").toString();
				getContextService().addEntity(new BlipLocation(location));
				System.out.println("ContextServer->Added location: "+location);	
			}
			rd.close();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void monitor(String arg0) throws RemoteException {
		// TODO Auto-generated method stub
	}
}


