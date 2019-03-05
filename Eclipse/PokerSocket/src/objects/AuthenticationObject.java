package objects;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class AuthenticationObject {
	private String id, gameID, type;
	private PokerPlayerObject mainPlayer;
	private Chip chipObject;
	private ArrayList<PokerPlayerObject> players = new ArrayList<PokerPlayerObject>();
	
	public AuthenticationObject(String type, PokerPlayerObject mainPlayer, String gameID) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);
		
		id = "au" + DatatypeConverter.printHexBinary(b);
		this.type = type;
		this.mainPlayer = mainPlayer;
		this.chipObject = new Chip();
		this.gameID = gameID;
	}
	
	public AuthenticationObject(String type, PokerPlayerObject mainPlayer, Chip chipObject, String gameID) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);
		
		id = "au" + DatatypeConverter.printHexBinary(b);
		this.type = type;
		this.mainPlayer = mainPlayer;
		this.chipObject = chipObject;
		this.gameID = gameID;
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

	public String getGameID() {
		return gameID;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AuthenticationObject)) return false;

		if(((AuthenticationObject)obj).getId().equals(id)) return true;
		return false;
	}
}
