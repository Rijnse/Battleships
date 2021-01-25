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
    private Game game;
    private Socket sock;
    private BufferedReader in;
    private BufferedWriter out;



    public Client(Player player, Socket sock) throws ExitProgram {
        try {
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            this.sock = sock;
            this.player = player;
            }
        catch (IOException e) {
            exit();
            throw new ExitProgram();
        }
    }

    public Client(String IP, String port, Player player) {
        this.player = player;
        try {
            this.sock = new Socket(InetAddress.getByName(IP), Integer.parseInt(port));
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
        } catch (UnknownHostException e) {
            System.out.println("Host could not be found!");
        } catch (IOException e) {
            System.out.println("Some I/O error has occurred. Try again!");
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
                out.newLine();
                out.flush();
                msg = in.readLine();
            }
            exit();
        } catch (IOException e) {
            exit();
        }
    }

    public void receiveMessage(String message) {
        String[] array = message.split(ProtocolMessages.CS);
            switch (array[0]) {
                case ProtocolMessages.HELLO:
                    if (array[2] != null) {
                        this.game = new Game(this.player, new HumanPlayer(array[2], true));
                    }
                    else {
                        this.game = new Game(this.player);
                    }
                    ViewController.getInstance().updateLobbyInfo(sock.getInetAddress().toString(), sock.getPort(), array[1], array[2]);
                    break;
                case ProtocolMessages.START:
                    sendMessage(ProtocolMessages.BOARD + ProtocolMessages.CS + player.getName() + ProtocolMessages.CS + player.getBoard().boardToString());
                    try {
                        ViewController.getInstance().startGame();
                        ViewController.getInstance().updateOwnField(player.getBoard());
                        ViewController.getInstance().updateNames(player.getName(), game.getPlayerTwo().getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case ProtocolMessages.ERROR:
                    switch (array[1]) {

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
                    if (array[2].equals(game.getPlayerOne().getName())) {
                        player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateOwnField(player.getBoard());
                        game.getPlayerTwo().incrementScore(1);
                        ViewController.getInstance().updateScores(player.getScore(), game.getPlayerTwo().getScore());
                    }
                    else {
                        game.getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        game.getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).getShip().setType(ProtocolMessages.Ship.UNKNOWN);
                        ViewController.getInstance().updateEnemyField(game.getPlayerTwo().getBoard());
                        game.getPlayerOne().incrementScore(1);
                        ViewController.getInstance().updateScores(player.getScore(), game.getPlayerTwo().getScore());
                    }
                    break;
                case ProtocolMessages.MISS:
                    ViewController.getInstance().showPopUp("MISS!", array[2] + " does not have a ship at " + Board.indexToCoordinates(Integer.parseInt(array[1])));
                    if (array[2].equals(game.getPlayerOne().getName())) {
                        player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateOwnField(player.getBoard());
                    }
                    else {
                        game.getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateEnemyField(game.getPlayerTwo().getBoard());
                    }
                    break;
                case ProtocolMessages.DESTROY:
                    String ship = "";
                    int length = 0;
                    switch (array[1]) {
                        case "C":
                            ship = "CARRIER";
                            length = 5;
                            break;
                        case "B":
                            ship = "BATTLESHIP";
                            length = 4;
                            break;
                        case "D":
                            ship = "DESTROYER";
                            length = 3;
                            break;
                        case "S":
                            ship = "SUPER PATROL";
                            length = 2;
                            break;
                        case "P":
                            ship = "PATROLBOAT";
                            length = 1;
                            break;
                    }
                    ViewController.getInstance().showPopUp("DESTROYED!", array[4] + " has lost a " + ship);
                    if (array[4].equals(game.getPlayerOne().getName())) {
                        player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                        ViewController.getInstance().updateOwnField(player.getBoard());
                        game.getPlayerTwo().incrementScore(1);
                        ViewController.getInstance().updateScores(player.getScore(), game.getPlayerTwo().getScore());
                    }
                    else {
                        if (array[3].equals("0")) {
                            for (int i = Integer.parseInt(array[2]); i < length; i++) {
                                game.getPlayerTwo().getBoard().getField(i).getShip().setType(ProtocolMessages.Ship.UNKNOWN);
                            }
                        }

                        ViewController.getInstance().updateEnemyField(game.getPlayerTwo().getBoard());
                        game.getPlayerOne().incrementScore(1);
                        ViewController.getInstance().updateScores(player.getScore(), game.getPlayerTwo().getScore());
                    }
                    break;
                case ProtocolMessages.WON:

                    break;
                case ProtocolMessages.MSGRECEIVED:

                    break;
                default:

                    break;
            }
    }

    public void sendMessage(String message) {
        try {
            out.write(message);
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

    public static void main(String[] args) {
        Thread a = new Thread(new Client("localhost", "69", new HumanPlayer("Rinse")));
        a.start();
    }
}
