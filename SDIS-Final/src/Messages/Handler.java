package Messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import File.Chunk;
import File.FileHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Server.Server;
import Server.ServerInfo;

import Protocols.BackupChunk;


public class Handler implements Runnable {

	DatagramPacket packet;

	private String header; //Header da mensagem

	private byte[] body; // Corpo da mensagem
	
	private int header_range; // Tamanho do header
	
	String[] headerParts; // Separação da mensagem
	
	public Handler(DatagramPacket packet) {
		this.packet = packet;

		header = null;

		body = null;
		
		header_range=0;
	}

	@Override
	public void run(){
		byte[] packetData = packet.getData(); // Informação do pacote
		
		packetData = Arrays.copyOf(packetData, packet.getLength()); // Inicialização do tamanho do array e copia da informação

		header = get_header(packetData); // Obtenção do header da mensagem
		
		headerParts = header.split("\\s+"); // Separação do header por partes 
		
		
		switch(headerParts[0]){
			
			case "PUTCHUNK":
			try {
				PUTCHUNK_handler(packetData); // Handler para quando a mensagem indica um PUTCHUNK
			} catch (NoSuchAlgorithmException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
				break;
			case "STORED":
				STORED_handler(); // Handler para quando a mensagem indica que se vai fazer um STORE
				break;
			case "GETCHUNK":
			try {
				GETCHUNK_handler(); // Handler para quando a mensagem indica que vai ocorrer um GETCHUNK
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
				break;
			case "CHUNK":
			try {
				CHUNK_handler(packetData); // Handler para quando a mensagem indica um CHUNK
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
				break;
				
			case "DELETE":
				DELETE_handler(); // Handler para quando a mensagem indica que vai ocorrer um DELETE
				break;
			case "REMOVED":
			try {
				REMOVED_handler(); // Handler para quando a mensgaem indica que vai ocorrer um REMOVE
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				break;
		}
	}


	public String get_header(byte[] packetData){
		
		// Função para extrair o header da mensagem  do pacote de informação enviado pelo socket
		String pattern = "\r\n\r\n";

		byte[] patternBytes = pattern.getBytes(); // Transformação da String pattern num array de bytes para comparação d etamanhos
		
		
		for(int i=0; i <= packetData.length - 4; i++ ){
			if(Arrays.equals(patternBytes, Arrays.copyOfRange(packetData, i, i+patternBytes.length))){
				header_range = i;
				break;
			}
		} // Ciclo for para definir o tamanho do Header da mensagem para construir a string do Header

		byte[] headerBytes = Arrays.copyOfRange(packetData, 0, header_range);
		
		String ret = new String(headerBytes, 0 , header_range); // Transformação do Array de bytes em header

		return ret; // Retorno da string que contém o header da mensagem 
	}
	
	public byte[] get_body(byte[] packetData){
		return Arrays.copyOfRange(packetData, header_range+4, packetData.length); // Retorna um Array de bytes com o corpo da mensagem
	}
	
	public void PUTCHUNK_handler(byte[] packetData) throws NoSuchAlgorithmException{
	
		String FileId = headerParts[2]; // File ID
		
		String id;
		MessageDigest md;
		
		 md = MessageDigest.getInstance("SHA-256");
		 
		 id = FileId;
		 
		 md.update(id.getBytes());
         byte[] digest = md.digest();
         
         id = digest.toString(); // Hash do FILEID
		
		int ChunkNo = Integer.parseInt(headerParts[3]); // Número do Chunk
		
		int replicationDegree = Integer.parseInt(headerParts[4]); // File Replication Degree
		
		body = get_body(packetData); // Message Body
		
		Random rand = new Random(); /// Espera da Thread
		
		try {
			Thread.sleep(rand.nextInt(400));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			FileHandler.saveChunk(FileId + "-" + ChunkNo, body); // Chunk gravado na base de dados de chunks
			
			Server.add_info_backedChunks(FileId + "-" + ChunkNo, replicationDegree); // Chunk gravado no HashMap de Chunks do Servidor 
			System.out.println(Server.getBackedChunks()); // Verificação para ver se o chunk foi realmente guardado
			Server.getMessageSender().sendSTORED(FileId, ChunkNo); // Envio da mensagem a confirmar que o Chubk foi guardado
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void STORED_handler(){
		String FileId = headerParts[2];
		
		String ChunkNo = headerParts[3];
		
		String chunkName = FileId + "-" + ChunkNo; // Construção do nome do chunk
		
		Server.add_chunkName_stored(chunkName); // Adição do chunk ao Array de chunks stored
		
		ServerInfo peerInfo = new ServerInfo(packet.getAddress(), packet.getPort()); // Informação do servidor
		
		Server.add_PeerInfo_stored(chunkName, peerInfo); // Adicionar Chunks ao Peer correspondente
		
		System.out.println("Stored Chunk No " + headerParts[3]); // Verificação
	}
	
	public void GETCHUNK_handler() throws IOException, NumberFormatException, NoSuchAlgorithmException{
		String FileId = headerParts[2];
		
		String ChunkNo = headerParts[3];
		
		String chunkName = FileId + "-" + ChunkNo;
		
		byte[] chunkData = FileHandler.loadChunkData(chunkName);
		try {
			Random rand = new Random();
			Thread.sleep(rand.nextInt(400));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Sending CHUNK");
		
		Chunk chunk = new Chunk(FileId, Integer.parseInt(ChunkNo), 0, chunkData);
		Server.getMessageSender().sendChunk(chunk);
		
		
	}
	
	public synchronized void CHUNK_handler(byte[] packetData) throws NoSuchAlgorithmException, UnsupportedEncodingException{
		String FileId = headerParts[2];
		int ChunkNo = Integer.parseInt(headerParts[3]);

		if(Server.gettingChunksOfFile(FileId) && !Server.already_in_toRestore(FileId, ChunkNo)){

			

			body = get_body(packetData);

			Chunk chunk = new Chunk(FileId, ChunkNo, 0, body);

			Server.getMdrListener().addChunk(chunk);
			System.out.println("Added chunk of " + FileId);
		}
	}
	
	public void DELETE_handler(){
		String fileName = headerParts[2];
		ArrayList<String> chunks = Server.get_chunksOfFileName(fileName);

		while(!chunks.isEmpty()){
			String chunkName = chunks.remove(0);
			FileHandler.deleteChunk(chunkName);
		}
	}
	
	public void REMOVED_handler() throws IOException, NoSuchAlgorithmException{
		String chunkName = headerParts[2];

		if(Server.hasStoredChunk(chunkName)){

			int ChunkNo = Integer.parseInt(headerParts[3]);

			ServerInfo peerInfo = new ServerInfo(packet.getAddress(),packet.getPort());

			System.out.println(Server.getBackedChunks());

			Server.remove_Server_stored(chunkName, peerInfo);

			int currentDegree = Server.getStored().get(chunkName).size() + 1;
			System.out.println(Server.getBackedChunks());
			int degreeToHave = Server.getBackedChunks().get(chunkName);

			System.out.println(currentDegree + " " + degreeToHave);

			if(currentDegree < degreeToHave){
				try {
					Random rand = new Random();
					Thread.sleep(rand.nextInt(400));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				byte[] chunkData = FileHandler.loadChunkData(chunkName);

				Chunk chunk= new Chunk(chunkName.split("-")[0], ChunkNo, degreeToHave, chunkData);

				new Thread(new BackupChunk(chunk)).start();
			}
		}

	}





}
