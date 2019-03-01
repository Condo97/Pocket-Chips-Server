package objects;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class AuthenticationObject {
	private String id, type;
	private PokerPlayerObject mainPlayer;
	private Chip chipObject;
	private ArrayList<PokerPlayerObject> players = new ArrayList<PokerPlayerObject>();
	
	public AuthenticationObject(String type, PokerPlayerObject mainPlayer) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);
		
		id = "au" + DatatypeConverter.printHexBinary(b);
		this.type = type;
		this.mainPlayer = mainPlayer;
		this.chipObject = new Chip();
	}
	
	public AuthenticationObject(String type, PokerPlayerObject mainPlayer, Chip chipObject) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);
		
		id = "au" + DatatypeConverter.printHexBinary(b);
		this.type = type;
		this.mainPlayer = mainPlayer;
		this.chipObject = chipObject;
	}
	
	public String getId() {
		return id;
	}
	
	public PokerPlayerObject getMainPlayer() {
		return mainPlayer;
	}
	
	public String getType() {
		return type;
	}
	
	public ArrayList<PokerPlayerObject> getPlayers() {
		return players;
	}
	
	public void addPlayer(PokerPlayerObject p) {
		players.add(p);
	}
	
	public void removePlayer(PokerPlayerObject p) {
		players.remove(p);
	}
	
	public Chip getChipObject() {
		return chipObject;
	}
}
