package Websocket;

import Websocket.ServerMessageObserver;
import Websocket.WebsocketCommunicator;
import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private final WebsocketCommunicator communicator;

    WebSocketFacade(String serverURL, ServerMessageObserver observer) throws ResponseException {
        this.communicator = new WebsocketCommunicator(serverURL, observer);
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // Can be used to log or set initial state
    }

    // game options go here.
}
