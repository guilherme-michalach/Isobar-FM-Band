package fm.isobar.band.exception;

public class BandNotFoundException extends RuntimeException {
    public BandNotFoundException(String id) {
        super("Band not found with id: " + id);
    }
}
