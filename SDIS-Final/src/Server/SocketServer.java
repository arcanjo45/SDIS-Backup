package Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class SocketServer implements Runnable {

	public MulticastSocket socket;

	byte[] data;

	public SocketServer(int port) throws IOException{
		socket = new MulticastSocket(port);
		InetAddress address = InetAddress.getByName("224.0.0.3");

		socket.joinGroup(address);
	}

	
	@Override
	public void run() {
		data = new byte[256];

		boolean done = false;

		while(!done){
			DatagramPacket packet = new DatagramPacket(data, data.length);

			try {

				socket.receive(packet);
				Server.handle_request(packet);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
