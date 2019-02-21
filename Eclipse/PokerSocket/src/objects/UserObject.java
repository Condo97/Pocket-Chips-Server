package objects;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class UserObject {
	private String id, name;
	private ArrayList<GameObject> hostedGames;
	private PrintWriter writer;
	private GameObject currentGame = new GameObject("", new Chip());
	
	public UserObject(PrintWriter writer, String name) {
		this.writer = writer;
		this.name = name;
		
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);
		
		hostedGames = new ArrayList<GameObject>();
		
		id = "us" + DatatypeConverter.printHexBinary(b);
	}

	public UserObject(String id, ArrayList<GameObject> hostedGames, PrintWriter writer, String name) {
		this.id = id;
		this.hostedGames = hostedGames;
		this.writer = writer;
		this.name = name;
	}

	public void addGameToUser(GameObject g) {
		hostedGames.add(g);
	}
	
	public void removeHostedGame(GameObject g) {
		hostedGames.remove(g);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public PrintWriter getWriter() {
		return writer;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ArrayList<GameObject> getGames() {
		return hostedGames;
	}
	
	public void setCurrentGame(GameObject g) {
		currentGame = g;
	}
	
	public GameObject getCurrentGame() {
		return currentGame;
	}
}
