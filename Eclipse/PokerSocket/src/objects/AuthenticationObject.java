package objects;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class AuthenticationObject {
	private String id, type;
	private PlayerObject mainPlayer;
	private Chip chipObject;
	private ArrayList<PlayerObject> players = new ArrayList<PlayerObject>();
	
	public AuthenticationObject(String type, PlayerObject mainPlayer) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);
		
		id = "au" + DatatypeConverter.printHexBinary(b);
		this.type = type;
		this.mainPlayer = mainPlayer;
		this.chipObject = new Chip();
	}
	
	public AuthenticationObject(String type, PlayerObject mainPlayer, Chip chipObject) {
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
	
	public PlayerObject getMainPlayer() {
		return mainPlayer;
	}
	
	public String getType() {
		return type;
	}
	
	public ArrayList<PlayerObject> getPlayers() {
		return players;
	}
	
	public void addPlayer(PlayerObject p) {
		players.add(p);
	}
	
	public void removePlayer(PlayerObject p) {
		players.remove(p);
	}
	
	public Chip getChipObject() {
		return chipObject;
	}
}
