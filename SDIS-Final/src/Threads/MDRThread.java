package Threads;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import File.Chunk;
import Messages.Handler;
import Server.Server;

public class MDRThread extends Listener {
	private static volatile ArrayList<String> chunksRestored;

	public MDRThread (InetAddress address, int port) {
		super(address, port);
	}

	@Override
	protected void handle(DatagramPacket packet) {
		Thread t = new Thread(new Handler(packet));
		t.start();
		
	}
	
	public synchronized void addChunkRestored(Chunk chunk){
		if(!chunksRestored.contains(chunk.getChunkName())){
			chunksRestored.add(chunk.getChunkName());
		}
	}
	
	public synchronized void addChunk(Chunk chunk){
		String fileId = chunk.getFileId();
		if(!Server.gettoRestore().get(fileId).contains(chunk.getChunkName())){
			Server.add_chunk_toRestore(chunk.getFileId(), chunk);
		}
		notifyAll();
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
