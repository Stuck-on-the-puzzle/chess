package client.websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final MessageHandler messageHandler;

    public interface MessageHandler {
        void handle(ServerMessage message);
    }

    public WebSocketFacade(String url, MessageHandler handler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageHandler = handler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new javax.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    messageHandler.handle(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // Can be used to log or set initial state
    }

    public void sendCommand(UserGameCommand command) throws ResponseException {
        try {
            String json = new Gson().toJson(command);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void close() throws ResponseException {
        try {
            session.close();
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
