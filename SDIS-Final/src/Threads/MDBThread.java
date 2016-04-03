package Threads;

import java.net.DatagramPacket;
import java.net.InetAddress;

import File.Chunk;
import Messages.Handler;
import Server.Server;

public class MDBThread extends Listener {

	public MDBThread(InetAddress address, int port) {
		super(address, port);
	}

	@Override
	protected void handle(DatagramPacket packet) {
		Thread t = new Thread(new Handler(packet));
		t.start();
	}
	public synchronized Chunk getChunk(String fileName){
		Chunk chunk = null;
		
		while(chunk == null){
			try {
				System.out.println("waiting");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println(Server.gettoRestore());
			
			chunk = Server.gettoRestore().get(fileName).remove(0);
			
			System.out.println(chunk);
			
		}
		
		return chunk;
	}

}
