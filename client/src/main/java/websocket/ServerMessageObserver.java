package websocket;

import exception.ResponseException;
import websocket.messages.ServerMessage;

public interface ServerMessageObserver {
    void notify(ServerMessage message) throws ResponseException;
}
