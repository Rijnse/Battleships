package controller;
import model.ProtocolMessages;
import model.game.Board;
import model.game.Field;
import model.game.HumanPlayer;
import model.networking.Client;
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
        this.lobby.updateLobbyInfo(ip, portint, onename, twoname);
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

    public void hostGame(int port, String username) {
        Server server = new Server(port);
        server.setup(port);

        Thread srv = new Thread(server);
        srv.start();

        Client client = new Client("localhost", String.valueOf(port), new HumanPlayer(username));
        this.client = client;
        Thread clientthread = new Thread(client);
        clientthread.start();

    }

    public void leaveGame() {

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

    }

    public void sendMove(String coordinates) {

    }

    public void startTurn() {
        game.startTurnTimer();
    }

    public void startGame() throws IOException {
        this.lobby.switchToGameScreen();
    }

    public void showPopUp(String title, String desc) {
        game.showPopUp(title, desc);
    }
}
