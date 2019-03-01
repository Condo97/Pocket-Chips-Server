package objects;

import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class PlayerObject {
	private Chip chips;
	private String id, userId;
	
	public PlayerObject(String userId) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);
		
		id = "pl" + DatatypeConverter.printHexBinary(b);
		
		this.userId = userId;
		chips = new Chip();//(500, 500, 500, 500, 500);
	}
	
	public PlayerObject(Chip chips, String id, String userId) {
		this.chips = chips;
		this.id = id;
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Chip getChipObject() {
		return chips;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public void addChips(double red, double blue, double yellow, double green, double orange) {
		chips.addChips(red, blue, yellow, green, orange);
	}
	
	public void removeChips(double red, double blue, double yellow, double green, double orange) {
		chips.removeChips(red, blue, yellow, green, orange);
	}
	
	public void addChips(Chip chips) {
		this.chips.addChips(chips);
	}
	
	public void removeChips(Chip chips) {
		this.chips.removeChips(chips);
	}
}
