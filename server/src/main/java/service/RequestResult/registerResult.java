package service.RequestResult;

public record registerResult(String username, String authToken, String message) {

    public registerResult(String username, String authToken) {
        this(username, authToken, null);
    }

    public registerResult(String message) {
        this(null, null, message);
    }
}
