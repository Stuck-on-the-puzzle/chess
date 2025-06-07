package Websocket;

import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator {

    private Session session;
    private final ServerMessageObserver observer;

    public WebsocketCommunicator(String serverURL, ServerMessageObserver observer) throws Exception {
        try {
            serverURL = serverURL.replace("http", "ws");
            URI uri = new URI(serverURL + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = (Session) container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception();
        }
    }
}
