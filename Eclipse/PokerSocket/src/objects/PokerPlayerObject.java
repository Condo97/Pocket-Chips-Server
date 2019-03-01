package objects;

public class PokerPlayerObject extends PlayerObject {

    public PokerPlayerObject(String userID) {
        super(userID);
    }

    public PokerPlayerObject(Chip chips, String id, String userID) {
        super(chips, id, userID);
    }

}
