package Messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import File.Chunk;
import Server.Server;

public class Sender {
	
	
	public static final String CRLF = "\r\n";
	
	public static final String VERSION = "1.0";
	
	public void sendPutChunk(Chunk chunk) throws IOException{
		String header = "PUTCHUNK" + " " + VERSION + " " + chunk.getFileId() + " " + chunk.getNo() + " " + chunk.getReplicationDegree() + CRLF + CRLF;
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write( header.getBytes() );
		outputStream.write( chunk.getData() );
		

		byte toSend[] = outputStream.toByteArray( );
		
		
		
		sendToMDB(toSend);
	}
	
	public void sendSTORED(String FileId, int ChunkNo){
		String header = "STORED" + " " + VERSION + " " + FileId + " " + Integer.toString(ChunkNo) + CRLF + CRLF;
		sendToMC(header.getBytes());
	}
	
	public void sendGetChunk(String FileId, int ChunkNo){
		String header = "GETCHUNK" + " " + VERSION + " " + FileId + " " + Integer.toString(ChunkNo) + CRLF + CRLF;
		sendToMDR(header.getBytes());
		
	}
	
	public void sendChunk(Chunk chunk) throws IOException{
		String header = "CHUNK" + " " + VERSION + " " + chunk.getFileId() + " " + Integer.toString(chunk.getNo()) + CRLF + CRLF;
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write( header.getBytes() );
		outputStream.write( chunk.getData() );
		

		byte toSend[] = outputStream.toByteArray( );
		
		sendToMDR(toSend);
	}
	
	public void sendDELETE(String fileName){
		String header = "DELETE" + " " + VERSION + " " + fileName  + CRLF + CRLF;
		sendToMC(header.getBytes());
	}
	
	public void sendREMOVED(String fileName, String chunkNo){
		String header = "REMOVED" + " " + VERSION + " " + fileName + " " + chunkNo + CRLF + CRLF;
		sendToMC(header.getBytes());
	}
	
	public synchronized void sendToMDR(byte[] data){
		DatagramPacket packet = new DatagramPacket(data, data.length, Server.getMdrListener().endereco, Server.getMdrListener().porta);
		
		try {
			Server.get_PeerSocket().send(packet);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void sendToMC(byte[] data){
		DatagramPacket packet = new DatagramPacket(data, data.length, Server.getMcListener().endereco, Server.getMcListener().porta);
		try {
			Server.get_PeerSocket().send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public synchronized void sendToMDB(byte[] data){
		DatagramPacket packet = new DatagramPacket(data, data.length, Server.getMdbListener().endereco, Server.getMdbListener().porta);
		try {
			Server.get_PeerSocket().send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
