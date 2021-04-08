package errorhandling;

/**
 *
 * @author lam@cphbusiness.dk
 */
public class NotFound extends Exception {

    public NotFound(String message) {
        super(message);
    }

    public NotFound() {
        super("Requested item could not be found");
    }
}
