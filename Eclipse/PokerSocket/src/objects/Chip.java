package objects;

public class Chip implements Cloneable {
	private double red, blue, yellow, green, orange; //1, 5, 10, 25, 100

	public Chip() {
		red = 0;
		blue = 0;
		yellow = 0;
		green = 0;
		orange = 0;
	}

	public Chip(double red, double blue, double yellow, double green, double orange) {
		this.red = red;
		this.blue = blue;
		this.yellow = yellow;
		this.green = green;
		this.orange = orange;
	}
	
	public void addChips(double red, double blue, double green, double black, double purple) {
		this.red += red;
		this.blue += blue;
		this.yellow += green;
		this.green += black;
		this.orange += purple;
	}
	
	public void addChips(Chip chips) {
		red += chips.getRed();
		blue += chips.getBlue();
		yellow += chips.getYellow();
		green += chips.getGreen();
		orange += chips.getOrange();
	}
	
	public void removeChips(double red, double blue, double green, double black, double purple) {
		this.red -= red;
		this.blue -= blue;
		this.yellow -= green;
		this.green -= black;
		this.orange -= purple;
	}
	
	public void removeChips(Chip chips) {
		red -= chips.getRed();
		blue -= chips.getBlue();
		yellow -= chips.getYellow();
		green -= chips.getGreen();
		orange -= chips.getOrange();
	}
	
	public double getRed() {
		return red;
	}

	public double getBlue() {
		return blue;
	}

	public double getYellow() {
		return yellow;
	}

	public double getGreen() {
		return green;
	}

	public double getOrange() {
		return orange;
	}
	
	public void setChips(double[] chips) {
		red = chips[0];
		blue = chips[1];
		yellow = chips[2];
		green = chips[3];
		orange = chips[4];
	}
	
	public double[] getChips() {
		return new double[] {red, blue, yellow, green, orange};
	}
	
	public Chip clone() {
		try {
			return (Chip)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
