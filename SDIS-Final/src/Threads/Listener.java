package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Server.Server;
import Server.ServerInfo;


public abstract class Listener implements Runnable{
	
	public MulticastSocket socket;
	
	public InetAddress endereco;
	
	public Integer size = 64500;
	
	public int porta;
	
	byte buffer[];
	
	
	public Listener(InetAddress endereco, int porta)
	{
		this.endereco=endereco;
		this.porta=porta;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			socket = new MulticastSocket(porta);
			socket.setTimeToLive(1);
			socket.joinGroup(endereco);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		buffer = new byte[size];
		
		while(true)
		{
             DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			try {
				System.out.println("waiting " + endereco);
				socket.receive(packet);
				System.out.println("Recebeu algo");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			InetAddress packetAddress = packet.getAddress();
			int packetPort = packet.getPort();
			
			ServerInfo peerCompare = new ServerInfo(packetAddress, packetPort);
			
			System.out.println(peerCompare.equals(Server.serverInfo));
			
			if(!peerCompare.equals(Server.serverInfo)){
				handle(packet);
			}
			
		}
		
	}
	protected abstract void handle(DatagramPacket packet);
}
