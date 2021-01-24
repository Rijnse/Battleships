package model.networking;

import controller.ViewController;
import model.exceptions.ExitProgram;
import model.game.Board;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private ServerSocket ssock;
    private int port;
    private InetAddress ip;

    private ClientHandler playerOne;
    private ClientHandler playerTwo;

    private final ViewController controller = ViewController.getInstance();

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting for client to connect!");
            Socket sock = ssock.accept();
            System.out.println("client connected on port" + sock.getLocalPort());


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void setup(int port) {
        this.ssock = null;
        while (ssock == null) {
            try {
                System.out.println("Attempting to open a socket at 127.0.0.1 "
                        + "on port " + port + "...");
                this.ssock = new ServerSocket(port, 0,
                        InetAddress.getByName("localhost"));
                System.out.println("Server started at port " + port);
            } catch (IOException e) {
                System.out.println("Something went wrong!");
            }
        }
    }

    public int getPort() {
      return this.port;
    }

    public InetAddress getIp() {
        return this.ip;
    }

    public ClientHandler getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(ClientHandler playerOne) {
        this.playerOne = playerOne;
    }

    public ClientHandler getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(ClientHandler playerTwo) {
        this.playerTwo = playerTwo;
    }

    public void startGame() {

    }

    public void attackCommand(String name) {

    }

    public void processMessage(String name, String message) {

    }

    public void processBoardInput(String name, Board board) {

    }
}
