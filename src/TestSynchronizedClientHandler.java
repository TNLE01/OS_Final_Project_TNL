/**
 * 
 * @author Truong Le
 *
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;


/**
 * ClientHandler class will handle communications between the client and the server
 * implements Runnable
 */
public class TestSynchronizedClientHandler implements Runnable{
	
	
	private String ID;
	private Socket clientSocket;
	private BufferedReader in;
	private BufferedWriter out;

	private static final Object lock = new Object();
	
	
	/**
	 * ArrayLists for communications with its clients
	 */
    public static ArrayList<TestSynchronizedClientHandler> lobby = new ArrayList<TestSynchronizedClientHandler>();
 
    
	/**
	 * Constructors for ClientHandler
	 * @throws IOException
	 */
	public TestSynchronizedClientHandler(Socket clientSocket) throws IOException{
		this.clientSocket = clientSocket;
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); 
		this.ID = in.readLine();
	}
	
	
	/**
	 * Setter for ID
	 * @param ID new ID
	 */
	public void setID(String ID) {this.ID = ID;}
	/**
	 * Getter for ID
	 * @return ID
	 */
	public String getID() {return ID;}
	
	
	/**
	 * Setter for client socket
	 * @param clientSocket new clientSocket
	 */
	public void setClientSocket(Socket clientSocket) {this.clientSocket = clientSocket;}
	/**
	 * Getter for client socket
	 * @return client socket
	 */
	public Socket getClientSocket() {return clientSocket;}
	
	
	/**
	 * Getter for BufferedReader
	 * @return BufferedReader
	 */
	public BufferedReader getBR() {return this.in;}
	/**
	 * Getter for BufferedWriter
	 * @return BufferedWriter
	 */
	public BufferedWriter getBW() {return this.out;}
	
	
	/**
	 * For testing with and without synchronized
	 */
	// public void testSynchronized() {lobby.add(this);System.out.println(ID + " joined the server. " + lobby.size());}
	public void testSynchronized() {synchronized(lock) {lobby.add(this);System.out.println(ID + " joined the server. " + lobby.size());}}
	
	
	/**
	 * Main communication loop for the client
	 */
	@Override
	public void run() {
		
		testSynchronized();
        
	
		String clientMsg;
		
		while (clientSocket.isConnected()) {
			try {
				clientMsg = in.readLine();
				System.out.println(ID + ": " + clientMsg);
			} catch(Exception e) {
				shutdown();
				break;
			}
		}
	}
	
	
	/**
	 * Remove the client from ArrayList lobby and send a message to others about it in the lobby
	 */
	public void removeClient() {
		lobby.remove(this);
        System.out.println(ID + " left the server.");
	}
	
	
	/**
	 * Shutdowns socket and communications
	 */
	public void shutdown() {
		removeClient();
		try {
			if(in != null) {in.close();}
			if(out != null) {out.close();}
			if(clientSocket != null) {clientSocket.close();}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
