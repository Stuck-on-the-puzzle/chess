package service.RequestResult;

public record CreateResult(Integer gameID, String message) {

    public CreateResult(Integer gameID) {
        this(gameID, null);
    }

    public CreateResult(String message) {
        this(null, message);
    }
}
