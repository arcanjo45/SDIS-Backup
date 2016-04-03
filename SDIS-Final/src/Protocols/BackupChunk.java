package Protocols;

import java.io.IOException;

import File.Chunk;
import Server.Server;

public class BackupChunk implements Runnable {
	private Chunk chunk;
	
	public long initialWaitingTime = 500;
	public int maxTries = 5;
	
	public BackupChunk(Chunk chunk){
		this.chunk = chunk;
	}

	@Override
	public void run() {
				
		int attempt = 0;
		long waitingTime = initialWaitingTime;
		
		boolean done = false;
		
		while(!done){
			
			try {
				Server.getMessageSender().sendPutChunk(chunk);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			int currentRepDeg = Server.getStored().get(chunk.getChunkName()).size();
			
			if(currentRepDeg < chunk.getReplicationDegree()){
				attempt++;
				
				if(attempt > maxTries){
					System.out.println("MAX TRIES");
					done = true;
				}else{
					System.out.println("Increasing replication degree");
					waitingTime *= 2;
				}
			}else{
				System.out.println("Replication degree reached");
				done = true;
			}
			
		}
	}
}
