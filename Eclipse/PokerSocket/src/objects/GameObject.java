package objects;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class GameObject {
	private String id, name;
	private Chip defaultChips;
	private double[] chipValues;
	
	public GameObject(String name, Chip defaultChips, double[] chipValues) {
		Random rd = new Random();
		byte[] b = new byte[30];
		rd.nextBytes(b);

		id = "ga" + DatatypeConverter.printHexBinary(b);

		this.name = name;
		this.defaultChips = defaultChips;
		this.chipValues = chipValues;
	}
	
	public GameObject(String id, String name, Chip defaultChips, double[] chipValues) {
		this.id = id;
		this.name = name;
		this.defaultChips = defaultChips;
		this.chipValues = chipValues;
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
