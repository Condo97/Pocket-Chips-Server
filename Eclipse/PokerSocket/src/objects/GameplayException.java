package objects;

public class GameplayException extends Exception {
    private String description;
    private int errorNumber;

    public GameplayException(String description, int errorNumber) {
        this.description = description;
        this.errorNumber = errorNumber;
    }

    public String getDescription() {
        return description;
    }

    public int getErrorNumber() {
        return errorNumber;
    }
}
