package Protocols;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import File.Chunk;
import File.FileHandler;
import Server.Server;

public class Restore implements Runnable{
	
	
	String name;
	
	public Restore(String name)
	{
		this.name = name;
	}

	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		
		int numberChunks = Server.getBackedFiles().get(name);
		
		System.out.println(numberChunks);
		
		Server.add_fileName_toRestore(name);
		
		ArrayList<Chunk>chunks = new ArrayList<Chunk>();
		
		
		for(int j=0; j < numberChunks;j++)
		{
			Server.getMessageSender().sendGetChunk(name,j);
			
			Chunk chunk = Server.getMdrListener().getChunk(name);
			
			chunks.add(chunk);
			
			System.out.println("added chunk to array");
		}
		
		
		byte[] data = new byte[0];
		
		for(int i=0 ; i < numberChunks;i++)
		{
			for(Chunk chunk : chunks)
			{
				if(chunk.getNo() == i){
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					try {
						outputStream.write( data );
						outputStream.write( chunk.getData() );
						data = outputStream.toByteArray( );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		
		
		
	}
		try {
			FileHandler.saveRestoredFile(name, data);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
