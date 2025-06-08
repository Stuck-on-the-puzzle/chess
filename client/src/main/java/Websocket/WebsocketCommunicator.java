package Websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator {

    private Session session;
    private final ServerMessageObserver observer;

    public WebsocketCommunicator(String serverURL, ServerMessageObserver observer) throws ResponseException {
        try {
            serverURL = serverURL.replace("http", "ws");
            URI uri = new URI(serverURL + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    WebsocketCommunicator.this.session = session;

                    session.addMessageHandler(new MessageHandler.Whole<String>() {
                        @Override
                        public void onMessage(String message) {
                            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                            observer.notify(serverMessage);
                        }
                    });
                }
            }, uri);

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, "Websocket connection error");
        }
    }

    public void send(String message) {
        this.session.getAsyncRemote().sendText(message);
    }
    
    /// Notifications:
    // 1 - User connects and message displays Player's name and team color
    // 2 - User connect as observer and display's observer's name
    // 3 - User makes a move. Display player's name and move (board updates)
    // 4 - User leaves a game. Display user's name
    // 5 - User resigns. Display user's name
    // 6 - Player is in check. Display user's name
    // 7 - Player is in checkmate. Display user's name

    /// Gameplay functionality:
    // Help
    // Redraw Chess Board
    // Leave
    // Make Move
    // Resign
    // Highlight Legal Moves
}
