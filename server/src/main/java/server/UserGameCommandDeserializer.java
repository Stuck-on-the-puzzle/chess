package server;

import chess.ChessMove;
import websocket.commands.*;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import java.lang.reflect.Type;

public class UserGameCommandDeserializer implements JsonDeserializer<UserGameCommand> {

    @Override
    public UserGameCommand deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String commandTypeString = jsonObject.get("commandType").getAsString();
        UserGameCommand.CommandType commandType = UserGameCommand.CommandType.valueOf(commandTypeString);

        String authToken = jsonObject.get("authToken").getAsString();
        int gameID = jsonObject.get("gameID").getAsInt();

        switch (commandType) {
            case MAKE_MOVE:
                ChessMove move = context.deserialize(jsonObject.get("move"), ChessMove.class);
                return new MakeMove(authToken, gameID, move);

            case CONNECT:
                return new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);

            case LEAVE:
                return new Leave(authToken, gameID);

            case RESIGN:
                return new Resign(authToken, gameID);

            default:
                throw new JsonParseException("Unknown command type: " + commandType);
        }
    }
}




