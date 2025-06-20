package ui;

import com.google.gson.Gson;
import exception.ResponseException;

import model.UserData;
import requestresult.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;

    }

    public ClearResult clear() throws ResponseException {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, null);
    }

    public RegisterResult register(UserData registerRequest) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, registerRequest, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, loginRequest, LoginResult.class);
    }

    public void logout(LogoutRequest logoutRequest) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, logoutRequest.authToken());
    }

    public ListResult listGames(String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListResult.class, authToken);
    }

    public CreateResult createGame(CreateRequest createRequest, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, createRequest, CreateResult.class, authToken);
    }

    public JoinResult joinGame(JoinRequest joinRequest, String authToken) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, joinRequest, JoinResult.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            if (authToken != null && !authToken.isBlank()) {
                http.addRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T>  responseClass) throws ResponseException {
        return makeRequest(method, path, request, responseClass, null);
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            switch (status) {
                case 400 -> throw new ResponseException(status, "Bad Request");
                case 401 -> throw new ResponseException(status, "Unauthorized");
                case 403 -> throw new ResponseException(status, "Already taken");
                case 404 -> throw new ResponseException(status, "Not Found");
                default -> throw new ResponseException(status, "Server Error");
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
