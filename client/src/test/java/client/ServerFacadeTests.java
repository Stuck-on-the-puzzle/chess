package client;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import requestresult.*;
import server.Server;
import ui.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade facade;
    static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    void setup() {
        facade = new ServerFacade("http://localhost:8080");
    }

    @AfterEach
    void after() throws ResponseException {
        facade.clear();
    }
    @Test
    public void registerPos() throws ResponseException {
        UserData user = new UserData("name", "password", "email");
        Assertions.assertEquals("name", facade.register(user).username());
    }

    @Test
    public void registerNeg() throws ResponseException{
        UserData user = new UserData("name", "password", "email");
        UserData user2 = new UserData("name", "password", "email");
        facade.register(user);
        Assertions.assertThrows(ResponseException.class, () -> facade.register(user2));
    }

    @Test
    public void loginPos() throws ResponseException{
        UserData user = new UserData("name", "password", "email");
        facade.register(user);
        LoginRequest loginRequest = new LoginRequest("name", "password");
        assertDoesNotThrow(() -> facade.login(loginRequest));
    }

    @Test
    public void loginNeg() {
        LoginRequest loginRequest = new LoginRequest("name", "password");
        Assertions.assertThrows(ResponseException.class, () -> facade.login(loginRequest));
    }

    @Test
    public void logoutPos() throws ResponseException {
        UserData user = new UserData("name", "password", "email");
        facade.register(user);
        LoginRequest loginRequest = new LoginRequest("name", "password");
        LoginResult loginResult = facade.login(loginRequest);
        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        assertDoesNotThrow(() -> facade.logout(logoutRequest));
    }

    @Test
    public void logoutNeg() {
        LogoutRequest logoutRequest = new LogoutRequest("badAuth");
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(logoutRequest));
    }

    @Test
    public void listPos() throws ResponseException {
        UserData user = new UserData("name", "password", "email");
        facade.register(user);
        LoginRequest loginRequest = new LoginRequest("name", "password");
        LoginResult loginResult = facade.login(loginRequest);
        assertDoesNotThrow(() -> facade.listGames(loginResult.authToken()));
    }

    @Test
    public void listNeg(){
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("badAuth"));
    }

    @Test
    public void joinPos() throws ResponseException {
        UserData user = new UserData("name", "password", "email");
        facade.register(user);
        LoginRequest loginRequest = new LoginRequest("name", "password");
        LoginResult loginResult = facade.login(loginRequest);
        CreateRequest createRequest = new CreateRequest("gameName");
        CreateResult createResult = facade.createGame(createRequest, loginResult.authToken());
        JoinRequest joinRequest = new JoinRequest("WHITE", createResult.gameID());
        assertDoesNotThrow(() -> facade.joinGame(joinRequest, loginResult.authToken()));
    }

    @Test
    public void joinNeg(){
        JoinRequest joinRequest = new JoinRequest("WHITE", 1234);
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame(joinRequest, "badAuth"));
    }

    @Test
    public void createPos() throws ResponseException {
        UserData user = new UserData("name", "password", "email");
        facade.register(user);
        LoginRequest loginRequest = new LoginRequest("name", "password");
        LoginResult loginResult = facade.login(loginRequest);
        CreateRequest createRequest = new CreateRequest("gameName");
        assertDoesNotThrow(() -> facade.createGame(createRequest, loginResult.authToken()));
    }

    @Test
    public void createNeg() {
        CreateRequest createRequest = new CreateRequest("newGame");
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame(createRequest, "badAuth"));
    }

    @Test
    public void clear() {
        assertDoesNotThrow(() -> facade.clear());
    }


}
