package Protocols;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import File.FileHandler;
import Server.Server;

public class Reclaim implements Runnable{

	private long amount;
	
	public Reclaim(long l)
	{
		this.amount = l;
	}
	@Override
	public void run() {
		long spaceReclaimed = 0;
		while(spaceReclaimed < amount){
			@SuppressWarnings("unchecked")
			HashMap<String, Integer> chunksClone = (HashMap<String, Integer>) Server.getBackedChunks().clone();
			
			
			java.util.Iterator<Entry<String, Integer>> it = chunksClone.entrySet().iterator();
			Map.Entry<String, Integer> pair = it.next();
			String chunkName = pair.getKey();
			it.remove();
			System.out.println("Chunks" + chunksClone);
			
			spaceReclaimed += FileHandler.deleteChunk(chunkName);
			
			String chunkNo = chunkName.split("-")[1];
			
			Server.getMessageSender().sendREMOVED(chunkName, chunkNo);
		}
	}

}
