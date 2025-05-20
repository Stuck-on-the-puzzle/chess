package service.RequestResult;

import model.GameData;

import java.util.HashSet;

public record ListResult(HashSet<GameData> games, String message ) {
}
