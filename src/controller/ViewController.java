package controller;
import javafx.application.Platform;
import model.ProtocolMessages;
import model.exceptions.OutOfTurn;
import model.game.Board;
import model.game.Field;
import model.game.HumanPlayer;
import model.networking.Client;
import model.networking.ComputerClient;
import model.networking.Server;
import view.Game;
import view.Lobby;
import view.Start;
import view.ViewDelegate;

import java.io.IOException;

public class ViewController {
    private static ViewController sharedInstance;

    private Start startscreen;
    private Lobby lobby;

    private Game game;
    public boolean gameIni = false;

    public Client getClient() {
        return client;
    }

    private Client client;

    private ViewController() {

    }

    public synchronized static ViewController getInstance() {
        if(sharedInstance==null) {
            sharedInstance = new ViewController();
            return sharedInstance;
        }
        return sharedInstance;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setGameView(Game game) {
        this.game = game;
    }

    public void setStartScreen(Start start) {
        this.startscreen = start;
    }

    public void updateLobbyInfo(String ip, int portint, String onename, String twoname) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                lobby.updateLobbyInfo(ip, portint, onename, twoname);
            }
        });
    }

    public void updateOwnField(Board board) {
        Field[] array = board.getFieldsArray();
        for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0] * ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
            game.updateOwnField(array[i], i);
        }
    }

    public void updateEnemyField(Board board) {
        Field[] array = board.getFieldsArray();
        for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0] * ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
            game.updateEnemyField(array[i], i);
        }
    }

    public void updateNames(String one, String two) {
        this.game.updatePlayerNames(one, two);
    }

    public void updateScores(int one, int two) {
        this.game.updatePlayerScores(one, two);
    }

    public void hostGame(String ip, int port, String username) {
        Server server = new Server(ip, port);
        Thread srv = new Thread(server);
        srv.start();

        Client client = new Client(ip, String.valueOf(port), new HumanPlayer(username));
        this.client = client;
        Thread clientthread = new Thread(client);
        clientthread.start();
    }

    public void leaveGame() {
        client.exit();
    }

    public void pressStartButton() {
        client.sendMessage(ProtocolMessages.START);
    }

    public void joinGame(String ip, String port, String username) {
        Client client = new Client(ip, port, new HumanPlayer(username));
        this.client = client;
        Thread clientthread = new Thread(this.client);
        clientthread.start();
    }

    public void updateGameTime(int seconds) {
        this.game.updateGeneralTime(seconds);
    }

    public void botGame(String username) {
        Server server = new Server("localhost", 1337);
        Thread srv = new Thread(server);
        srv.start();

        Client client = new Client("localhost", String.valueOf(1337), new HumanPlayer(username));
        this.client = client;
        Thread clientthread = new Thread(client);
        clientthread.start();

        ComputerClient bot = new ComputerClient("localhost", String.valueOf(1337));
        Thread botthread = new Thread(bot);
        botthread.start();
    }

    public void sendMove(String coordinates) {
       this.client.sendMessage(ProtocolMessages.ATTACK + ProtocolMessages.CS + Board.index(coordinates));
    }

    public void errorHandling(String error) {
        switch (error) {
            case ProtocolMessages.ACTION_NOT_PERMITTED:
                lobby.showPopUp("ERROR!", "Only first player (host) may start game!");
                break;
            case ProtocolMessages.DUPLICATE_NAME:
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            try {
                                lobby.switchToStart();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            client.exit();
                            startscreen.showPopUp("ERROR!", "This server already has a player with that name! Try again please...");
                        }
                    });
                break;
            case ProtocolMessages.GAME_OVER:
                game.showPopUp("ERROR!", "The game has ended, so you can't do that right now!", 3);
                break;
            case ProtocolMessages.ILLEGAL_COMMAND:
                game.showPopUp("ERROR!", "That is an illegal command! Try again...", 3);
                break;
            case ProtocolMessages.ILLEGAL_SHIP_PLACEMENT:
                game.showPopUp("ERROR!", "You can't place your ships like that!", 3);
                break;
            case ProtocolMessages.INVALID_INDEX:
                game.showPopUp("ERROR!", "That is not a valid move (either because it's already been hit or is not on the board)", 3);
                break;
            case ProtocolMessages.OUT_OF_TURN:
                game.showPopUp("ERROR!", "It is not your turn!", 3);
                break;
            case ProtocolMessages.TIME_OVER:

                break;
        }
    }

    public void startTurn() {
        game.startTurnTimer();
    }

    public void startGame() throws IOException {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                try {
                    lobby.switchToGameScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void showPopUp(String title, String desc, int popuptime) {
        game.showPopUp(title, desc, popuptime);
    }
}
