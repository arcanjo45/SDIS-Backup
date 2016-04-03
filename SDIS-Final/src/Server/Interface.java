package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Interface {
	
	public static DatagramSocket socket;
	
	public static void main(String args[]) throws SocketException, UnknownHostException {
		
		String[] IPandPort = args[0].split(":");
		InetAddress Ip = InetAddress.getByName(IPandPort[0]);
		int port = Integer.parseInt(IPandPort[1]);
		
		socket = new DatagramSocket();
		
		byte[] request = new byte[256];
		String requestString = null;
		switch(args[1]){
			case "BACKUP":
				requestString = args[1] + " " + args[2] + " " + args[3];
				break;
			case "RESTORE":
				requestString = args[1] + " " + args[2];
				break;
			case "DELETE":
				requestString = args[1] + " " + args[2];
			case "RECLAIM":
				requestString = args[1] + " " + args[2];
				break;
		}
		
		System.out.println(requestString);
		
		request = requestString.getBytes();
		
		DatagramPacket requestPacket = new DatagramPacket(request, request.length, Ip, port);
		try {
			socket.send(requestPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}