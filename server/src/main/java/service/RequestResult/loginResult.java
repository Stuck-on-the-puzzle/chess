package service.RequestResult;

public record loginResult(String username, String authToken, String message) {
    // Success constructor
    public loginResult(String username, String authToken) {
        this(username, authToken, null);
    }

    // Error constructor
    public loginResult(String message) {
        this(null, null, message);
    }
}
