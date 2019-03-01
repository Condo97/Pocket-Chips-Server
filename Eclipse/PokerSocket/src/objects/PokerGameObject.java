package objects;

import java.util.ArrayList;

public class PokerGameObject extends GameObject {
    private ArrayList<PokerPlayerObject> players;
    private PotObject pot;

    public PokerGameObject(String name, Chip defaultChips, double[] chipValues) {
        super(name, defaultChips, chipValues);
        players = new ArrayList<PokerPlayerObject>();
    }

    public PokerGameObject(String id, ArrayList<PokerPlayerObject> players, PotObject pot, String name, Chip defaultChips, double[] chipValues) {
        super(id, name, defaultChips, chipValues);

        this.players = players;
        this.pot = pot;
    }

    public void addPlayer(PokerPlayerObject p) {
        players.add(p);
    }

    public void removePlayer(PokerPlayerObject p) {
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

    public ArrayList<PokerPlayerObject> getPlayers() {
        return players;
    }

    public void setPot(PotObject pot) {
        this.pot = pot;
    }

    public PotObject getPotObject() {
        return pot;
    }
}
