package requestresult;

public record LoginResult(String username, String authToken, String message) {
    // Success constructor
    public LoginResult(String username, String authToken) {
        this(username, authToken, null);
    }

    // Error constructor
    public LoginResult(String message) {
        this(null, null, message);
    }
}
