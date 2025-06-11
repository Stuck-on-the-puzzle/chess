package server;

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
        String commandType = jsonObject.get("commandType").getAsString();

        return switch (commandType) {
            case "MAKE_MOVE" -> context.deserialize(jsonObject, MakeMove.class);
            case "LEAVE" -> context.deserialize(jsonObject, Leave.class);
            case "RESIGN" -> context.deserialize(jsonObject, Resign.class);
            case "CONNECT" -> context.deserialize(jsonObject, Connect.class);
            default -> throw new JsonParseException("Unknown commandType: " + commandType);
        };
    }
}
