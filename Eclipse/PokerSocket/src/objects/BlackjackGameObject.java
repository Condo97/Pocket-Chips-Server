package objects;

import java.util.ArrayList;

public class BlackjackGameObject extends GameObject {
    private double ratio;
    private ArrayList<BlackjackPlayerObject> players;

    public BlackjackGameObject(String name, Chip defaultChips, double[] chipValues, double ratio) {
        super(name, defaultChips, chipValues);
        this.ratio = ratio;
        players = new ArrayList<BlackjackPlayerObject>();
    }

    public BlackjackGameObject(String id, ArrayList<BlackjackPlayerObject> players, String name, Chip defaultChips, double[] chipValues, double ratio) {
        super(id, name, defaultChips, chipValues);
        this.ratio = ratio;
        this.players = players;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getRatio() {
        return ratio;
    }

    public ArrayList<BlackjackPlayerObject> getPlayers() {
        return players;
    }

}
