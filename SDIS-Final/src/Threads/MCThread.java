package Threads;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;

import Messages.Handler;
import Server.ServerInfo;

import java.util.ArrayList;


public class MCThread extends Listener{

	public MCThread(InetAddress endereco, int porta) {
		super(endereco, porta);
		// TODO Auto-generated constructor stub
		
		new HashMap<String,ArrayList<ServerInfo>>();
	}

	@Override
	protected void handle(DatagramPacket packet) {
		// TODO Auto-generated method stub
		Thread t = new Thread(new Handler(packet));
		t.start();
	}

}
