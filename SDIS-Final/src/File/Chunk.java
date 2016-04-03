package File;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Chunk {
	
	public static final int MAX_SIZE = 64000;
	
	private int ChunkNo;
	
	private String FileId;
	
	private String ChunkName;
	
	
	private byte[] data;
	
	private int replicationDegree;
	
	public Chunk(String FileId, int ChunkNo, int replicationDegree, byte[] data) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		this.FileId = FileId;
		this.ChunkNo = ChunkNo;
		
		
		
		ChunkName = FileId + "-" + ChunkNo;
		
		//ChunkName2 = id + "-" + ChunkNo;
		
		this.replicationDegree = replicationDegree;
		this.data = data;
	}
	

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public byte[] getData() {
		return data;
	}

	public String getFileId() {
		return FileId;
	}

	public String getChunkName() {
		return ChunkName;
	}
	
	public int getNo() {
		return ChunkNo;
	}


}
