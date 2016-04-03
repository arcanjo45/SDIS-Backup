package Protocols;


import Server.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Vector;

import File.*;

public class Backup implements Runnable{
	private String fileName;
	private int replicationDegree;
	private Vector<byte[]> chunks;
	private String FileId;
	private int nchunks;
	File file;
	FileInputStream inputStream;
	
	public Backup(String fileName, int replicationDegree)  throws IOException{
		this.fileName=fileName;
		this.replicationDegree=replicationDegree;
		
		file = new File("Files/" + Server.serverID + "/" + fileName);
		
		
		inputStream = new FileInputStream(file);
		
		
	}
	
	@Override
	public void run(){
		
		nchunks = (int) (file.length() / Chunk.MAX_SIZE + 1);
		
		int actualSize=0;
		
		for(int i=0; i < nchunks ; i++){
			byte[] chunkData;
			
			if (i == nchunks - 1 && file.length() % Chunk.MAX_SIZE == 0) {
				chunkData = new byte[actualSize];
			}else{
				chunkData = new byte[Chunk.MAX_SIZE];
				
				try {
					actualSize = this.inputStream.read(chunkData);
					chunkData = Arrays.copyOf(chunkData, actualSize);
					System.out.println(chunkData.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
			try {
				
				Chunk chunk = new Chunk(fileName, i, replicationDegree, chunkData);
				Thread t = new Thread(new BackupChunk(chunk)); 
				t.start();
				
				t.join();
				
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}
		
		Server.add_backup_file(fileName, nchunks);
		try {
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}