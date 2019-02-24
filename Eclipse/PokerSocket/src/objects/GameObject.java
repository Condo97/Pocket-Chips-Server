package objects;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class GameObject {
	private ArrayList<PlayerObject> players;
	private String id, name;
	private PotObject pot;
	private Chip defaultChips;
	private double[] chipValues;
	
	public GameObject(String name, Chip defaultChips, double[] chipValues) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);

		id = "ga" + DatatypeConverter.printHexBinary(b);
		
		players = new ArrayList<PlayerObject>();
		
		this.name = name;
		this.defaultChips = defaultChips;
		this.chipValues = chipValues;
	}
	
	public GameObject(String id, ArrayList<PlayerObject> players, PotObject pot, String name, Chip defaultChips, double[] chipValues) {
		this.id = id;
		this.players = players;
		this.pot = pot;
		this.name = name;
		this.defaultChips = defaultChips;
		this.chipValues = chipValues;
	}
	
	public void addPlayer(PlayerObject p) {
		players.add(p);
	}
	
	public void removePlayer(PlayerObject p) {
		players.remove(p);
	}
	
	public void addToPot(Chip chips) {
		pot.addChips(chips);
	}
	
	public void removeFromPot(Chip chips) {
		pot.removeChips(chips);
	}
	
	public Chip getPotChipObject() {
		return pot.getChipObject();
	}
	
	public ArrayList<PlayerObject> getPlayers() {
		return players;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPot(PotObject pot) {
		this.pot = pot;
	}
	
	public PotObject getPotObject() {
		return pot;
	}
	
	public void setDefaultChips(Chip defaultChips) {
		this.defaultChips = defaultChips;
	}
	
	public Chip getDefaultChips() {
		return defaultChips;
	}

	public double[] getChipValues() {
		return chipValues;
	}
}
