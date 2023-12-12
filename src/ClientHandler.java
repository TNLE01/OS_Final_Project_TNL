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
public class ClientHandler implements Runnable{
	
	
	private String ID;
	private Socket clientSocket;
	private BufferedReader in;
	private BufferedWriter out;
	
	
	/**
	 * ArrayLists for communications with its clients
	 */
    public static ArrayList<ClientHandler> lobby = new ArrayList<ClientHandler>();
    public static ArrayList<ClientHandler> gameRoom = new ArrayList<ClientHandler>();
    
    
    /**
	 * Game Logic for its clients
	 */
    private String RPSChoice;
    private static boolean activeGame;
	
    
	/**
	 * Constructors for ClientHandler
	 * @throws IOException
	 */
	public ClientHandler(Socket clientSocket) throws IOException{
		this.clientSocket = clientSocket;
		this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
		this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); 
		this.ID = in.readLine();
		lobby.add(this);
	    RPSChoice = "blank";
	    activeGame = false;
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
	 * Setter for RPSChoice
	 * @param RPSChoice new RPSChoice
	 */
	public void setRPSChoice(String RPSChoice) {this.RPSChoice = RPSChoice;}
	/**
	 * Getter for RPSChoice
	 * @return RPSChoice
	 */
	public String getRPSChoice() {return RPSChoice;}
	
	
	// Broadcast methods

	
	/**
	 * Send a message to everyone in Arraylist lobby
	 * @param msg message to be send
	 */
	public void broadcastToAll(String msg) {
		for (ClientHandler ch : lobby) {
			try {
				ch.getBW().write(msg);
				ch.getBW().newLine();
				ch.getBW().flush();
			} catch(IOException e) {
				shutdown();
			}
		}
	}
	
	
	/**
	 * Send a message to everyone in Arraylist lobby except the sender
	 * @param msg message to be send
	 */
	public void broadcastToOthers(String msg) {
		for (ClientHandler ch : lobby) {
			try {
				if (!ch.getID().equals(this.getID())) {
					ch.getBW().write(msg);
					ch.getBW().newLine();
					ch.getBW().flush();
				}
			} catch(IOException e) {
				shutdown();
			}
		}
	}
	
	
	/**
	 * Send a message to the sender
	 * @param msg message to be send
	 */
	public void broadcastToSelf(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}
	
	
	/**
	 * Send a message to everyone in ArrayList gameRoom
	 * @param msg message to be send
	 */
	public void broadcastToGameRoom(String msg) {
		for (ClientHandler ch : gameRoom) {
			try {
				ch.getBW().write(msg);
				ch.getBW().newLine();
				ch.getBW().flush();
			} catch(IOException e) {
				shutdown();
			}
		}
	}
	
	
	// Game logic
	
	
	/**
	 * Add to Arraylist gameRoom
	 * synchronized to make sure only 2 people can be in the list
	 */
	public synchronized void addToGameRoom() {
		if (gameRoom.contains(this)) {broadcastToSelf("Already in game room");}
		else {
			if (gameRoom.size() < 2) {gameRoom.add(this);}
			else {broadcastToSelf("Game room full");}
		}
	}
	
	
	/**
	 * Starts the game, remove everyone in ArrayList gameRoom from ArrayList lobby
	 * This is so the game can only talk to the players
	 */
	public void startGame() {
		if (gameRoom.contains(this)) {
			broadcastToAll("A game has started");
			for (ClientHandler ch : gameRoom) {lobby.remove(ch);}
			broadcastToGameRoom("Rock Paper Scissors! (R/P/S) *invaild will default to Rock.");
			activeGame = true;}
		else {broadcastToSelf("A game is in session");}
	}
	
	
	/**
	 * Ends the game, calculate who wins
	 */
	public void endGame() {
		
		String winnerMsg;

		if (gameRoom.get(0).getRPSChoice().equalsIgnoreCase("P")) {
			if (gameRoom.get(1).getRPSChoice().equalsIgnoreCase("P")) {
				winnerMsg = gameRoom.get(0).getID() + " and " + gameRoom.get(1).getID() + " drew.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
			else if (gameRoom.get(1).getRPSChoice().equalsIgnoreCase("S")) {
				winnerMsg = gameRoom.get(0).getID() + " lost. " + gameRoom.get(1).getID() + " won.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
			else {
				winnerMsg = gameRoom.get(0).getID() + " won. " + gameRoom.get(1).getID() + " lost.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
			
		}
		
		else if (gameRoom.get(0).getRPSChoice().equalsIgnoreCase("S")) {
			if (gameRoom.get(1).getRPSChoice().equalsIgnoreCase("P")) {
				winnerMsg = gameRoom.get(0).getID() + " won. " + gameRoom.get(1).getID() + " lost.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
			else if (gameRoom.get(1).getRPSChoice().equalsIgnoreCase("S")) {
				winnerMsg = gameRoom.get(0).getID() + " and " + gameRoom.get(1).getID() + " drew.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
			else {
				winnerMsg = gameRoom.get(0).getID() + " lost. " + gameRoom.get(1).getID() + " won.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
			
		}
		
		else {
			if (gameRoom.get(1).getRPSChoice().equalsIgnoreCase("P")) {
				winnerMsg = gameRoom.get(0).getID() + " lost. " + gameRoom.get(1).getID() + " won.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
			else if (gameRoom.get(1).getRPSChoice().equalsIgnoreCase("S")) {
				winnerMsg = gameRoom.get(0).getID() + " won. " + gameRoom.get(1).getID() + " lost.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
			else {
				winnerMsg = gameRoom.get(0).getID() + " and " + gameRoom.get(1).getID() + " drew.";
				broadcastToGameRoom(winnerMsg);
				broadcastToAll(winnerMsg);
			}
		}
		
		activeGame = false;
		for (ClientHandler ch : gameRoom) {lobby.add(ch);ch.setRPSChoice("blank");}
		gameRoom.clear();
		
	}
	
	
	/**
	 * Main communication loop for the client
	 */
	@Override
	public void run() {
		
        System.out.println(ID + " joined the server.");
		broadcastToSelf("You have entered the lobby.\n"
				+ "Type \"/play\" to enter game queue.\n"
				+ "Type \"/quit\" to leave the lobby.");
		broadcastToOthers(ID + " has entered the lobby.");
		
		String clientMsg;
		
		while (clientSocket.isConnected()) {
			try {
				clientMsg = in.readLine();
				if (lobby.contains(this)) {
					if (clientMsg.equals("/play")) {addToGameRoom();
					if (gameRoom.size() == 2) {startGame();}}
					else if (clientMsg.equals("/quit")) {broadcastToSelf("You left the lobby.");}
					else {broadcastToOthers(ID + ": " + clientMsg);}
				}
				
				else {
					
					if (this.getRPSChoice().equals("blank")) {this.setRPSChoice(clientMsg);}
					else {broadcastToSelf("wait for other player");}
					
					if ((gameRoom.get(0).getRPSChoice() != "blank") && (gameRoom.get(1).getRPSChoice() != "blank")){
						endGame();}
				}
				
				
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
        broadcastToOthers(ID + " left the lobby.");
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
