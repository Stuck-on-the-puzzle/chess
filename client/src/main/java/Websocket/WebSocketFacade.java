package Websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;

public class WebSocketFacade extends Endpoint {

    private final WebsocketCommunicator communicator;

    public WebSocketFacade(String serverURL, ServerMessageObserver observer) throws ResponseException {
        this.communicator = new WebsocketCommunicator(serverURL, observer);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // helper function to send commands through websocket
    private void sendCommand(UserGameCommand command) throws IOException {
        String json = new Gson().toJson(command);
        communicator.send(json);
    }

    public void connect(String authToken, int gameID) throws IOException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        sendCommand(new MakeMove(authToken, gameID, move));
    }

    public void leave(String authToken, int gameID) throws IOException {
        sendCommand(new Leave(authToken, gameID));
    }

    public void resign(String authToken, int gameID) throws IOException {
        sendCommand(new Resign(authToken, gameID));
    }

}
