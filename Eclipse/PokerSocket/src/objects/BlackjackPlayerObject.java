package objects;

public class BlackjackPlayerObject extends PlayerObject {
    private Chip betChips, secondaryChips;
    private boolean isDealer;
    private int option;

    public BlackjackPlayerObject(String userID, boolean isDealer) {
        super(userID);
        this.isDealer = isDealer;
        betChips = new Chip();
        secondaryChips = new Chip();
        option = 0;
    }

    public BlackjackPlayerObject(Chip chips, Chip betChips, Chip secondaryChips, boolean isDealer, int option, String id, String userID) {
        super(chips, id, userID);
        this.betChips = betChips;
        this.secondaryChips = secondaryChips;
        this.isDealer = isDealer;
        this.option = option;
    }

    public void setIsDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }

    public void setBetChips(Chip betChips) {
        this.betChips = betChips;
    }

    public void setSecondaryChips(Chip secondaryChips) {
        this.secondaryChips = secondaryChips;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public void addBetChips(Chip c) {
        betChips.addChips(c);
    }

    public void removeBetChips(Chip c) {
        betChips.removeChips(c);
    }

    public void addSecondaryBetChips(Chip c) {
        secondaryChips.addChips(c);
    }

    public void removeSecondaryBetChips(Chip c) {
        secondaryChips.removeChips(c);
    }

    public void clearBetChips() {
        betChips = new Chip();
    }

    public void clearSecondaryBetChips() {
        secondaryChips = new Chip();
    }

    public Chip getBetChips() {
        return betChips;
    }

    public Chip getSecondaryChips() {
        return secondaryChips;
    }

    public boolean isDealer() {
        return isDealer;
    }

    public int getOption() {
        return option;
    }
}
