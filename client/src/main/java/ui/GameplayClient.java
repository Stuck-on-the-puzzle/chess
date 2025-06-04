//package ui;
//
//import exception.ResponseException;
//
//import java.util.Arrays;
//
//public class GameplayClient {
//    private final ServerFacade server;
//    private final String serverUrl;
//    private String state = "Logged Out";
//
//    public GameplayClient(String serverUrl) {
//        server = new ServerFacade(serverUrl);
//        this.serverUrl = serverUrl;
//    }
//
//    public String eval(String input) {
//        try {
//            var tokens = input.toLowerCase().split(" ");
//            var cmd = (tokens.length > 0) ? tokens[0] : "help";
//            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
//            return switch (cmd) {
//                case "login" -> login(params);
//                case "register" -> register(params);
//                case "quit" -> "quit";
//                default -> help();
//            };
//        } catch (ResponseException ex) {
//            return ex.getMessage();
//        }
//    }
//
////    public String login(String... params) throws ResponseException {
////        if (params.length >= 1) {
////            state = State.SIGNEDIN;
////            visitorName = String.join("-", params);
////            ws = new WebSocketFacade(serverUrl, notificationHandler);
////            ws.enterPetShop(visitorName);
////            return String.format("You signed in as %s.", visitorName);
////        }
////        throw new ResponseException(400, "Expected: <yourname>");
////    }
////
////    public String register(String... params) throws ResponseException {
////        assertSignedIn();
////        if (params.length >= 2) {
////            var name = params[0];
////            var type = PetType.valueOf(params[1].toUpperCase());
////            var pet = new Pet(0, name, type);
////            pet = server.addPet(pet);
////            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
////        }
////        throw new ResponseException(400, "Expected: <name> <CAT|DOG|FROG>");
////    }
//
//    public String help() {
//        return """
//               register <USERNAME> <PASSWORD> <EMAIL> - to create an account
//               login <USERNAME> <PASSWORD> - to play chess
//               quit - playing chess
//               help - with possible commands
//               """;
//    }
//}
