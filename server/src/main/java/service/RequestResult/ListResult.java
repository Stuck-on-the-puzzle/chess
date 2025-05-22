package service.RequestResult;

import model.GameData;

import java.util.HashSet;

public record ListResult(HashSet<GameData> games, String message ) {

    public ListResult(HashSet<GameData> games) {
        this(games, null);
    }

    public ListResult(String message) {
        this(null, message);
    }
}
