/**
 * 
 * @author Truong Le
 *
 */


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * TestSynchronizedServer class will make the server socket and open it for incoming connections
 * extends Thread
 */

public class TestSynchronizedServer extends Thread{
	
	
	private int port;
	private ServerSocket server;
	
	
	/**
	 * Constructors for Server
	 */
	public TestSynchronizedServer() throws IOException{port = 8080; server = new ServerSocket(port);}
	public TestSynchronizedServer(int port) throws IOException{this.port = port; this.server = new ServerSocket(this.port);}
	
	
	/**
	 * Setter for port
	 * @param port new port
	 */
	public void setPort(int port) {this.port = port;}
	/**
	 * Getter for port
	 * @return port
	 */
	public int getPort() {return port;}
	
	
	/**
	 * Setter for server socket
	 * @param server new server socket
	 */
	public void setServer(ServerSocket server) {this.server = server;}
	/**
	 * Getter for server socket
	 * @return server socket
	 */
	public ServerSocket getServer() {return server;}
	
	
	/**
	 * Shutdowns the server socket
	 */
	public void shutdown() {
		try {
			if (this.getServer() != null) {this.getServer().close();}
		} catch (IOException e) {e.printStackTrace();}
	}
	
	
	/**
	 * Will listen for connections and make a ClientHandler Thread for it
	 */
	@Override
	public void run() {
		
		System.out.println(String.format("Listening for connection on port %d ....", port));
		
		try {
			
			while(!this.getServer().isClosed()) {
			
				Socket client = this.getServer().accept();
								
				TestSynchronizedClientHandler ch = new TestSynchronizedClientHandler(client);
				Thread thread = new Thread(ch);
				thread.start();
				
			}
		} catch (IOException e) {
			shutdown();
		}
	}
	
	
	/**
	 * Will make the server with the port number
	 * @param args command line arguments
	 */
	public static void main(String[] args) throws IOException {
		
		int portNumber = 8080;
		/***/
		TestSynchronizedServer server = new TestSynchronizedServer(portNumber);
		server.start();
		
	}
	
	
}
