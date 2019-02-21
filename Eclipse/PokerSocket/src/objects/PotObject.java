package objects;

import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class PotObject {
	private String id;
	private Chip chips;
	private double[] chipValues;
	
	public PotObject(double[] chipValues) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);
		
		id = "po" + DatatypeConverter.printHexBinary(b);
		
		chips = new Chip();
		this.chipValues = chipValues;
	}
	
	public PotObject(String id, Chip pot, double[] chipValues) {
		this.id = id;
		this.chips = pot;
		this.chipValues = chipValues;
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
	
	public void addChips(double red, double blue, double green, double black, double purple) {
		chips.addChips(red, blue, green, black, purple);
	}
	
	public void removeChips(double red, double blue, double green, double black, double purple) {
		chips.removeChips(red, blue, green, black, purple);
	}
	
	public void addChips(Chip chips) {
		this.chips.addChips(chips);
	}
	
	public void removeChips(Chip chips) {
		this.chips.removeChips(chips);
	}
	
	public double[] getChipValues() {
		return chipValues;
	}
}
