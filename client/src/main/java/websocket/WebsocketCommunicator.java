package websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageDeserializer;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator {

    private Session session;
    private final ServerMessageObserver observer;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(
            ServerMessage.class, new ServerMessageDeserializer()).create();

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
                            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                            try {
                                observer.notify(serverMessage);
                            } catch (ResponseException e) {
                                throw new RuntimeException(e);
                            }
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
}
