package com.MA1.contextserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;

import com.MA1.entity.BlipLocation;

import dk.pervasive.jcaf.util.AbstractMonitor;

public class ContextServer extends AbstractMonitor{

	private final static String service_uri = "rmi://192.168.1.3/info@itu";

	public ContextServer(String serviceUri) throws RemoteException {
		super(serviceUri);
	}

	@Override
	public void run() {		
		try {
			System.out.println("Server-> \n   " + getContextService().getServerInfo());

			getContextService().addEntity(new BlipLocation("itu.zone0.zoneaud1"));
			getContextService().addEntity(new BlipLocation("itu.zone0.zonedorsyd"));
			System.out.println("ContextServer-> Added locations zoneaud1 and zonedorsyd");
			
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

	@Override
	public void monitor(String arg0) throws RemoteException {
		// TODO Auto-generated method stub
	}
}


