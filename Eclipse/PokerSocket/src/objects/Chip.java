package objects;

public class Chip implements Cloneable {
	private double red, blue, green, black, purple; //1, 5, 10, 25, 100

	public Chip() {
		red = 0;
		blue = 0;
		green = 0;
		black = 0;
		purple = 0;
	}

	public Chip(double red, double blue, double green, double black, double purple) {
		this.red = red;
		this.blue = blue;
		this.green = green;
		this.black = black;
		this.purple = purple;
	}
	
	public void addChips(double red, double blue, double green, double black, double purple) {
		this.red += red;
		this.blue += blue;
		this.green += green;
		this.black += black;
		this.purple += purple;
	}
	
	public void addChips(Chip chips) {
		red += chips.getRed();
		blue += chips.getBlue();
		green += chips.getGreen();
		black += chips.getBlack();
		purple += chips.getPurple();
	}
	
	public void removeChips(double red, double blue, double green, double black, double purple) {
		this.red -= red;
		this.blue -= blue;
		this.green -= green;
		this.black -= black;
		this.purple -= purple;
	}
	
	public void removeChips(Chip chips) {
		red -= chips.getRed();
		blue -= chips.getBlue();
		green -= chips.getGreen();
		black -= chips.getBlack();
		purple -= chips.getPurple();
	}
	
	public double getRed() {
		return red;
	}

	public double getBlue() {
		return blue;
	}

	public double getGreen() {
		return green;
	}

	public double getBlack() {
		return black;
	}

	public double getPurple() {
		return purple;
	}
	
	public void setChips(double[] chips) {
		red = chips[0];
		blue = chips[1];
		green = chips[2];
		black = chips[3];
		purple = chips[4];
	}
	
	public double[] getChips() {
		return new double[] {red, blue, green, black, purple};
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
