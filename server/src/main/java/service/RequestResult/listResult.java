package service.RequestResult;

import model.GameData;

import java.util.HashSet;

public record listResult(HashSet<GameData> games, String message ) {

    public listResult(HashSet<GameData> games) {
        this(games, null);
    }

    public listResult(String message) {
        this(null, message);
    }
}
