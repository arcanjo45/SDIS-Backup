package Server;

import java.io.Serializable;
import java.net.InetAddress;

public class ServerInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private InetAddress address;
	private int port;
	
	public ServerInfo(InetAddress address, int port){
		this.address = address;
		this.port = port;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
public boolean equals(Object obj){
		

		ServerInfo other = (ServerInfo) obj;

		if (address == null) {
			if (other.address != null){
				return false;
			}
		} else if (!address.equals(other.address)){
			return false;
		}

		if (port != other.port){
			return false;
		}
		

		return true;
	}
	
}
