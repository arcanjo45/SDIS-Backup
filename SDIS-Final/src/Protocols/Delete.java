package Protocols;

import java.util.ArrayList;
import java.util.HashMap;

import File.Chunk;
import File.FileHandler;
import Server.Server;

public class Delete implements Runnable {
	
	String filename;
	
	

	public Delete(String filename) {
		this.filename=filename;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
     FileHandler.deleteFile("Files/"+ Server.serverID + "/" + filename);
		
		if(Server.getBackedFiles().containsKey(filename)){
			Server.getMessageSender().sendDELETE(filename);
		}
		
		FileHandler.deleteFile("Restores/" + filename);

	}

}
