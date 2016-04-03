package Server;

import java.io.*;
import java.net.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Map;
import Threads.MCThread;
import Threads.MDBThread;
import Threads.MDRThread;
import Messages.Sender;
import Protocols.Backup;
import Protocols.Delete;
import Protocols.Reclaim;
import Protocols.Restore;

import File.Chunk;

public class Server {

	public static ServerInfo serverInfo;

	private static Sender messageSender;
	
	public static String serverID;

	

	private static MDBThread mdbListener;
	private static MCThread mcListener;
	private static MDRThread mdrListener;
	private static SocketServer socket;
	
	private volatile static HashMap<String, Integer> backedFiles; //FileId e nchunks
	private volatile static HashMap<String, ArrayList<Chunk>> toRestore; //FileId e chunks
	private volatile static HashMap<String,Integer> backedChunks; //chunkName
	private volatile static HashMap<String, ArrayList<ServerInfo>> stored;

	public static void main(String args[]) throws IOException{
		
		serverID = args[0];
		
		socket = new SocketServer(Integer.parseInt(args[1]));
		new Thread(socket).start();
		
		System.out.println(InetAddress.getLocalHost());
		
		backedFiles = new HashMap<String, Integer>();
		toRestore = new HashMap<String, ArrayList<Chunk>>();
		backedChunks = new HashMap<String,Integer>();
		stored = new HashMap<String, ArrayList<ServerInfo>>();
		
		
		mcListener = new MCThread(InetAddress.getByName(args[2]), Integer.parseInt(args[3]));
		mdbListener = new MDBThread(InetAddress.getByName(args[4]), Integer.parseInt(args[5]));
		mdrListener = new MDRThread(InetAddress.getByName(args[6]), Integer.parseInt(args[7]));
		
		messageSender = new Sender();
		
		new Thread(mcListener).start();
		new Thread(mdbListener).start();
		new Thread(mdrListener).start();
		
		serverInfo = new ServerInfo(InetAddress.getLocalHost(), Integer.parseInt(args[1]));

		
	}

	
	public static void handle_request(DatagramPacket request) throws NumberFormatException, IOException{
		byte[] data = request.getData();
		String requestString = new String(data, 0, request.getLength());
		
		String[] requestTokens = requestString.split("\\s+");
		System.out.println(requestString);
		
		switch(requestTokens[0]){
			case "BACKUP":
				new Thread(new Backup(requestTokens[1], Integer.parseInt(requestTokens[2]))).start();
				break;
			case "RESTORE":
				new Thread(new Restore(requestTokens[1])).start();
				break;
			case "DELETE":
				new Thread(new Delete(requestTokens[1])).start();
				break;
			case "RECLAIM":
				new Thread(new Reclaim(Long.parseLong(requestTokens[1]))).start();
				break;
		}
	}
	
	public static void add_backup_file(String fileName, Integer nchunks){
		backedFiles.put(fileName, nchunks);
		
		System.out.println("Chunks: " + backedFiles.get(fileName));
	}
	
	public static boolean already_in_toRestore(String fileId, int chunkNo){
		ArrayList<Chunk> chunks = toRestore.get(fileId);
		for(Chunk chunk : chunks){
			if(chunk.getNo() == chunkNo){
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean gettingChunksOfFile(String fileID) {
		return toRestore.containsKey(fileID);
	}
	
	public static boolean hasStoredChunk(String chunkName){
		if(backedChunks.containsKey(chunkName)){
			return true;
		}
		return false;
	}
	
	public static void add_fileName_toRestore(String fileName){
		toRestore.put(fileName, new ArrayList<Chunk>());
	}
	
	public static void add_PeerInfo_stored(String chunkName, ServerInfo peerInfo){
		stored.get(chunkName).add(peerInfo);
	}
	
	public static void remove_Server_stored(String chunkName, ServerInfo peerInfo){
		stored.get(chunkName).remove(peerInfo);
	}
	
	public static void add_chunk_toRestore(String fileName, Chunk chunk){
		toRestore.get(fileName).add(chunk);
	}
	
	public static void add_info_backedChunks(String fileName,int repDegree){
		backedChunks.put(fileName,repDegree);
	}


	public static DatagramSocket getSocket() {
		return socket.socket;
	}

	public static MulticastSocket get_PeerSocket(){
		return socket.socket;
	}

	public static Sender getMessageSender() {
		return messageSender;
	}

	public static void add_chunkName_stored(String chunkName){
		stored.put(chunkName, new ArrayList<ServerInfo>());
	}

	public static MDBThread getMdbListener() {
		return mdbListener;
	}

	public static ArrayList<String> get_chunksOfFileName(String fileName){
		ArrayList<String> chunks = new ArrayList<String>();

		java.util.Iterator<Entry<String, Integer>> it = backedChunks.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Integer> pair = it.next();
			String chunkName = pair.getKey();
			it.remove();
			String toCompare = chunkName.split("-")[0];
			if(fileName.equals(toCompare)){
				chunks.add(chunkName);
			}
		}

		return chunks;
	}


	public static MCThread getMcListener() {
		return mcListener;
	}


	public static MDRThread getMdrListener() {
		return mdrListener;
	}


	
	public static void setMdrListener(MDRThread mdrThread) {
		Server.mdrListener = mdrThread;
	}


	public static HashMap<String, Integer> getBackedFiles() {
		return backedFiles;
	}


	public void setBackedFiles(HashMap<String, Integer> backedFiles) {
		this.backedFiles = backedFiles;
	}


	public static HashMap<String, ArrayList<Chunk>> gettoRestore() {
		return toRestore;
	}


	public static HashMap<String,Integer> getBackedChunks() {
		return backedChunks;
	}
	
	public static HashMap<String, ArrayList<ServerInfo>> getStored() {
		return stored;
	}




}
