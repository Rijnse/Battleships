package model.networking;

import controller.ViewController;
import model.ProtocolMessages;
import model.exceptions.ExitProgram;
import model.game.Board;
import model.game.Game;
import model.game.HumanPlayer;
import model.game.Player;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable{
    private Player player;
    private Socket sock;
    private BufferedReader in;
    private BufferedWriter out;

    public Client(String IP, String port, Player player) {
        this.player = player;
        try {
            this.sock = new Socket(InetAddress.getByName(IP), Integer.parseInt(port));
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            sendMessage(ProtocolMessages.HELLO + ProtocolMessages.CS + player.getName());
        } catch (UnknownHostException e) {
            System.out.println("Host could not be found!");
            exit();
        } catch (IOException e) {
            System.out.println("Some I/O error has occurred. Try again!");
            exit();
        }
    }

    public Player getPlayer() {
        return this.player;
    }

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
                    ViewController.getInstance().showPopUp("NEW TURN!", array[1] + " may now take a shot");
                    if (array[1].equals(player.getName())) {
                        ViewController.getInstance().startTurn();
                    }
                    break;
                case ProtocolMessages.HIT:
                    ViewController.getInstance().showPopUp("HIT!", array[2] + " has been hit on " + Board.indexToCoordinates(Integer.parseInt(array[1])));
                    if (array[2].equals(player.getCurrentGame().getPlayerOne().getName())) {
                        player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateOwnField(player.getBoard());
                        player.getCurrentGame().getPlayerTwo().incrementScore(1);
                        ViewController.getInstance().updateScores(player.getScore(), player.getCurrentGame().getPlayerTwo().getScore());
                    }
                    else {
                        player.getCurrentGame().getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        player.getCurrentGame().getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).getShip().setType(ProtocolMessages.Ship.UNKNOWN);
                        ViewController.getInstance().updateEnemyField(player.getCurrentGame().getPlayerTwo().getBoard());
                        player.getCurrentGame().getPlayerOne().incrementScore(1);
                        ViewController.getInstance().updateScores(player.getScore(), player.getCurrentGame().getPlayerTwo().getScore());
                    }
                    break;
                case ProtocolMessages.MISS:
                    ViewController.getInstance().showPopUp("MISS!", array[2] + " does not have a ship at " + Board.indexToCoordinates(Integer.parseInt(array[1])));
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
                    ProtocolMessages.Ship ship = ProtocolMessages.Ship.EMPTY;
                    int length = 0;
                    switch (array[1]) {
                        case "C":
                            ship = ProtocolMessages.Ship.CARRIER;
                            length = 5;
                            break;
                        case "B":
                            ship = ProtocolMessages.Ship.BATTLESHIP;
                            length = 4;
                            break;
                        case "D":
                            ship = ProtocolMessages.Ship.DESTROYER;
                            length = 3;
                            break;
                        case "S":
                            ship = ProtocolMessages.Ship.SUPERPATROL;
                            length = 2;
                            break;
                        case "P":
                            ship = ProtocolMessages.Ship.PATROLBOAT;
                            length = 1;
                            break;
                    }
                    ViewController.getInstance().showPopUp("DESTROYED!", array[4] + " has lost a " + ship.toString());
                    if (array[4].equals(player.getCurrentGame().getPlayerOne().getName())) {
                        player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateOwnField(player.getBoard());
                        player.getCurrentGame().getPlayerTwo().incrementScore(1);
                        ViewController.getInstance().updateScores(player.getScore(), player.getCurrentGame().getPlayerTwo().getScore());
                    }
                    else {
                        if (array[3].equals("0")) {
                            for (int i = Integer.parseInt(array[2]); i < length + Integer.parseInt(array[2]); i++) {
                                player.getCurrentGame().getPlayerTwo().getBoard().getField(i).getShip().setType(ship);
                            }
                        }
                        else {
                            for (int i = Integer.parseInt(array[2]); i < (length*ProtocolMessages.BOARD_DIMENSIONS[15]) + Integer.parseInt(array[2]); i = i + ProtocolMessages.BOARD_DIMENSIONS[1]) {
                                player.getCurrentGame().getPlayerTwo().getBoard().getField(i).getShip().setType(ship);
                            }
                        }
                        ViewController.getInstance().updateEnemyField(player.getCurrentGame().getPlayerTwo().getBoard());
                        player.getCurrentGame().getPlayerOne().incrementScore(1);
                        ViewController.getInstance().updateScores(player.getScore(), player.getCurrentGame().getPlayerTwo().getScore());
                    }
                    break;
                case ProtocolMessages.WON:
                    if (array.length == 2) {
                        ViewController.getInstance().showPopUp("WON!", array[1] + " has won the game!");
                    }
                    else {
                        ViewController.getInstance().showPopUp("WON!", "The game was a tie!");
                    }
                    break;
                case ProtocolMessages.MSGRECEIVED:

                    break;
            }
    }

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

    public void exit() {
        try {
            in.close();
            out.close();
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
