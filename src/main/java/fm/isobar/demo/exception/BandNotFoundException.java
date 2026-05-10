package fm.isobar.demo.exception;

public class BandNotFoundException extends RuntimeException {
    public BandNotFoundException(String id) {
        super("Band not found with id: " + id);
    }
}
