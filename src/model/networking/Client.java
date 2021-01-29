package model.networking;

import controller.ViewController;
import javafx.application.Platform;
import model.ProtocolMessages;
import model.exceptions.ExitProgram;
import model.game.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable, ClientInterface{
    private Player player;
    private Socket sock;
    private BufferedReader in;
    private BufferedWriter out;

    public Client(String IP, String port, Player player) throws IOException {
        this.player = player;
            this.sock = new Socket(InetAddress.getByName(IP), Integer.parseInt(port));
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            sendMessage(ProtocolMessages.HELLO + ProtocolMessages.CS + player.getName());
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Socket getSock() {
        return this.sock;
    }

    @Override
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null) {
                receiveMessage(msg);
                msg = in.readLine();
            }
            exit();
        } catch (IOException e) {
            exit();
        }
    }

    @Override
    public void receiveMessage(String message) {
        System.out.println("Client receives: " + message);
        String[] array = message.split(ProtocolMessages.CS);
            switch (array[0]) {
                case ProtocolMessages.HELLO:
                    if (array.length > 2) {
                        if (array[2] != null) {
                            if (array[1].equals(getPlayer().getName())) {
                                this.player.setCurrentGame(new Game(this.player, new HumanPlayer(array[2], true)));
                                ViewController.getInstance().updateLobbyInfo(sock.getInetAddress().toString(), sock.getPort(), this.player.getName(), this.player.getCurrentGame().getPlayerTwo().getName());
                            }
                            else {
                                this.player.setCurrentGame(new Game(this.player, new HumanPlayer(array[1], true)));
                                ViewController.getInstance().updateLobbyInfo(sock.getInetAddress().toString(), sock.getPort(), this.player.getCurrentGame().getPlayerTwo().getName(), this.player.getName());
                            }
                        }
                    }
                    else {
                        this.player.setCurrentGame(new Game(this.player));
                        ViewController.getInstance().updateLobbyInfo(sock.getInetAddress().toString(), sock.getPort(), this.player.getName(), this.player.getCurrentGame().getPlayerTwo().getName());
                    }
                    break;
                case ProtocolMessages.START:
                    sendMessage(ProtocolMessages.BOARD + ProtocolMessages.CS + player.getName() + ProtocolMessages.CS + player.getBoard().boardToString());
                    try {
                        ViewController.getInstance().startGame();
                        while (true) {
                            if (ViewController.getInstance().gameIni) {
                                ViewController.getInstance().updateOwnField(player.getBoard());
                                ViewController.getInstance().updateNames(player.getName(), player.getCurrentGame().getPlayerTwo().getName());
                                ViewController.getInstance().gameIni = false;
                                break;
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Something went wrong!");
                    }
                    break;
                case ProtocolMessages.ERROR:
                    if (array[1] != null) {
                        ViewController.getInstance().errorHandling(array[1]);
                    }
                    break;
                case ProtocolMessages.TIME:
                    ViewController.getInstance().updateGameTime(Integer.parseInt(array[1]));
                    break;
                case ProtocolMessages.TURN:
                    if (array[1].equals(player.getName())) {
                        ViewController.getInstance().startTurn();
                    }
                    break;
                case ProtocolMessages.HIT:
                    ViewController.getInstance().showPopUp("HIT!", array[2] + " has been hit on " + Board.indexToCoordinates(Integer.parseInt(array[1])), 3);
                    if (array[2].equals(player.getCurrentGame().getPlayerOne().getName())) {
                        player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateOwnField(player.getBoard());
                        player.getCurrentGame().getPlayerTwo().incrementScore(1);
                    }
                    else {
                        player.getCurrentGame().getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        player.getCurrentGame().getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).getShip().setType(ProtocolMessages.Ship.UNKNOWN);
                        ViewController.getInstance().updateEnemyField(player.getCurrentGame().getPlayerTwo().getBoard());
                        player.getCurrentGame().getPlayerOne().incrementScore(1);
                    }
                    ViewController.getInstance().updateScores(player.getScore(), player.getCurrentGame().getPlayerTwo().getScore());
                    break;
                case ProtocolMessages.MISS:
                    ViewController.getInstance().showPopUp("MISS!", array[2] + " does not have a ship at " + Board.indexToCoordinates(Integer.parseInt(array[1])), 3);
                    if (array[2].equals(player.getCurrentGame().getPlayerOne().getName())) {
                        player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateOwnField(player.getBoard());
                    }
                    else {
                        player.getCurrentGame().getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateEnemyField(player.getCurrentGame().getPlayerTwo().getBoard());
                    }
                    break;
                case ProtocolMessages.DESTROY:
                    Player playerDestroyed = null;
                    int updateBoard = 0;
                    if (array[4].equals(player.getCurrentGame().getPlayerOne().getName())) {
                        playerDestroyed = player.getCurrentGame().getPlayerOne();
                        player.getCurrentGame().getPlayerTwo().incrementScore(1);
                    }
                    else {
                        playerDestroyed = player.getCurrentGame().getPlayerTwo();
                        player.getCurrentGame().getPlayerOne().incrementScore(1);
                        updateBoard = 1;
                    }

                    if (!player.getName().equals(playerDestroyed.getName())) {
                        Ship ship = null;
                        switch (array[1]) {
                            case "C":
                                ship = new Ship(-1, ProtocolMessages.Ship.CARRIER);
                                break;
                            case "B":
                                ship = new Ship(-1, ProtocolMessages.Ship.BATTLESHIP);
                                break;
                            case "D":
                                ship = new Ship(-1, ProtocolMessages.Ship.DESTROYER);
                                break;
                            case "S":
                                ship = new Ship(-1, ProtocolMessages.Ship.SUPERPATROL);
                                break;
                            case "P":
                                ship = new Ship(-1, ProtocolMessages.Ship.PATROLBOAT);
                                break;
                        }
                        ship.setSunk(true);
                        if (array[3].contains("0")) {
                            for (int i = 0; i < ship.getLength(); i++) {
                                playerDestroyed.getBoard().getFieldsArray()[Integer.parseInt(array[2]) + i] = new Field(ship);
                                playerDestroyed.getBoard().getFieldsArray()[Integer.parseInt(array[2]) + i].setHit(true);
                            }
                        } else {
                            for (int i = 0; i < (ship.getLength()*15); i = i + 15) {
                                playerDestroyed.getBoard().getFieldsArray()[Integer.parseInt(array[2]) + i] = new Field(ship);
                                playerDestroyed.getBoard().getFieldsArray()[Integer.parseInt(array[2]) + i].setHit(true);
                            }
                        }
                    }
                    ViewController.getInstance().showPopUp("DESTROYED!", array[4] + " had a ship taken down!", 3);
                    ViewController.getInstance().updateScores(player.getScore(), player.getCurrentGame().getPlayerTwo().getScore());
                    if (updateBoard == 1) {
                        ViewController.getInstance().updateEnemyField(player.getCurrentGame().getPlayerTwo().getBoard());
                    }
                    break;
                case ProtocolMessages.WON:
                    if (array.length == 2) {
                        ViewController.getInstance().showPopUp("WON!", array[1] + " has won the game!", 15);
                    }
                    else {
                        ViewController.getInstance().showPopUp("WON!", "The game was a tie!", 15);
                    }

                    int z = 15;
                    while (z > 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        z--;
                    }

                    System.out.println("Closing program...");
                    Platform.exit();
                    System.exit(0);
                    break;
            }
    }

    @Override
    public void sendMessage(String message) {
        try {
            System.out.println("Client sends: " + message);
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.out.println("Something went wrong!");
        }
    }

    @Override
    public void exit() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
