package service.RequestResult;

public record createResult(Integer gameID, String message) {

    public createResult(Integer gameID) {
        this(gameID, null);
    }

    public createResult(String message) {
        this(null, message);
    }
}
