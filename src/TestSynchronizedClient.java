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
import java.util.Scanner;


/**
 * Client class will make the client socket and communicate with ClientHandler
 * extends Thread
 */
public class TestSynchronizedClient extends Thread{

	private String ID;
	private int port;
	private Socket client;
	private BufferedReader in;
	private BufferedWriter out;
	
	
	/**
	 * Constructors for Client
	 */
	public TestSynchronizedClient() throws IOException{
		this.ID = "DEFAULT";
		this.port = 8080;
		this.client = new Socket("localhost", port);
		this.in = new BufferedReader(new InputStreamReader(client.getInputStream())); 
		this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		
	}
	
	public TestSynchronizedClient(int port) throws IOException {
		this.ID = "DEFAULT";
		this.port = port;
		this.client = new Socket("localhost", port);
		this.in = new BufferedReader(new InputStreamReader(client.getInputStream())); 
		this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream())); 
	}
	
	public TestSynchronizedClient(int port, String ID) throws IOException {
		this.ID = ID;
		this.port = port;
		this.client = new Socket("localhost", port);
		this.in = new BufferedReader(new InputStreamReader(client.getInputStream())); 
		this.out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream())); 
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
	public String getID() {return this.ID;}
	
	
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
	 * Setter for client
	 * @param client new client
	 */
	public void setClient(Socket client) {this.client = client;}
	/**
	 * Getter for client
	 * @return client
	 */
	public Socket getClient() {return client;}
	
	
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
	 * Send a message to the ClientHandler
	 * @param msg message to be send
	 */
	public void sendMsg(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {shutdown();}
	}
	
	
	/**
	 * Shutdowns socket and communications
	 */
	public void shutdown() {
		try {
			if (in != null) {in.close();}
			if (out != null) {out.close();}
			if (client != null) {client.close();}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * Main communication loop for the client
	 */
	public void run() {
		
		
		new Thread(new Runnable() {
			public void run() {
				String incomingMsg;
				while(client.isConnected()) {
					try {
						
						incomingMsg = in.readLine();
						System.out.println(incomingMsg);
						if (incomingMsg.equals("You left the lobby.")) {shutdown();}
						
					}catch (IOException e) {shutdown();}
				}
			}
		}).start();		
		
		
		try {
			
			sendMsg(ID);
			Scanner scanner = new Scanner(System.in);
			
			while (client.isConnected()) {
				
				String msg = scanner.nextLine();
				sendMsg(msg);
				
				
				}
		}catch (Exception e) {shutdown();}
		
		
	}
	
	
	/**
	 * Will make the client with the port number after getting a ID input from the user
	 * @param args command line arguments
	 */
	public static void main(String[] args) throws IOException {
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter a ID: ");
		
		int port = 8080;
		
		TestSynchronizedClient client = new TestSynchronizedClient(port, scan.nextLine());
		client.start();
		
	
	}
	
}
