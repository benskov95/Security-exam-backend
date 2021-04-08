package errorhandling;

public class AlreadyExists extends Exception{

    public AlreadyExists(String message) {
        super(message);
    }
}
