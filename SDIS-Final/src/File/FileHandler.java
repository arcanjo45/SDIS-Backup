package File;

import java.io.*;
import Server.Server;

public class FileHandler {
	public static String Files = "Files/" + Server.serverID + "/";

	public static String Chunks = "Chunks/" + Server.serverID + "/";

	private static String Restores = "Restores/" + Server.serverID + "/";

	public static boolean fileExists(String fileName){
		File file = new File(fileName);

		return (file.exists() && file.isFile());
	}

	public static boolean folderExists(String folderPath){
		File file = new File(folderPath);

		return (file.exists() && file.isDirectory());
	}

	public static void createFolder(String folderPath){
		File file = new File(folderPath);

		file.mkdir();
	}

	public static void saveRestoredFile(String fileName, byte[] data) throws FileNotFoundException{
		if(!folderExists(Restores)){
			createFolder(Restores);
		}

		FileOutputStream outputStream = new FileOutputStream(Restores + fileName);
		try {
			outputStream.write(data);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void deleteFile(String fileName){
		if(fileExists(fileName)){
			System.out.println("deleting file " + fileName);
			File file = new File(fileName);
			file.delete();
		}
	}

	public static void saveChunk(String chunkName, byte[] data) throws IOException{
		if(!folderExists(Chunks)){
			createFolder(Chunks);
		}
		System.out.println(data.length);
		FileOutputStream outputStream = new FileOutputStream(Chunks + chunkName);
		outputStream.write(data);
		outputStream.close();
	}
	
	public static byte[] loadChunkData(String chunkName) throws IOException{
		
		File file = new File(Chunks + chunkName);
		FileInputStream inputStream = new FileInputStream(file);
		
		int fileSize = (int) file.length();
		
		byte[] data = new byte[fileSize];
		
		inputStream.read(data);
		inputStream.close();
		
		return data;
		
	}

	public static long deleteChunk(String chunkName){
		
		File file = new File(Chunks + chunkName);
		
       long size = file.length();
		
		file.delete();
		
		return size;
	}
}
