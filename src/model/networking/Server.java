package model.networking;

import controller.ViewController;
import model.ProtocolMessages;
import model.exceptions.ExitProgram;
import model.game.Board;
import model.game.Game;
import model.game.HumanPlayer;
import model.game.Ship;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    private ServerSocket ssock;
    private int port;
    private InetAddress ip;

    private List<ClientHandler> clientlist = new ArrayList<ClientHandler>();
    private List<Game> gameslist = new ArrayList<Game>();
    private ClientHandler playerOne;
    private ClientHandler playerTwo;

    private final ViewController controller = ViewController.getInstance();

    public Server(int port) {
            setup(port);
            this.port = port;
            this.ip = ssock.getInetAddress();
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Waiting for client to connect!");
                Socket sock = ssock.accept();
                System.out.println("client connected on port" + sock.getLocalPort());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
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

    public ClientHandler getPlayer(int index) {
        return this.clientlist.get(index);
    }

    public List<ClientHandler> getClientList() {
        return this.clientlist;
    }

    public void setPlayer(ClientHandler player, int index) {
        this.clientlist.set(index, player);
    }


    public boolean handleHello(String name, ClientHandler handler) {
        boolean test = false;
        for (ClientHandler k : clientlist) {
            if (k.getPlayer().getName().equals(name)) {
                test = true;
            }
        }
        if (test) {
            handler.exit();
            return false;
        }
        else {
            handler.setPlayer(new HumanPlayer(name));
            clientlist.add(handler);
            return true;
        }
    }

    public void startGame() {
        for (ClientHandler k : clientlist) {
            k.sendMessage(ProtocolMessages.START + ProtocolMessages.CS + 0);
        }
    }

    public void attackCommand(String name, String index) {
        ClientHandler player = null;
        for (ClientHandler k : clientlist) {
            if (k.getPlayer().getName().equals(name)) {
                player = k;
            }
        }

        if (!(player.getPlayer().getBoard().getField(Integer.parseInt(index)).isHit())) {
            player.getPlayer().getBoard().getField(Integer.parseInt(index)).setHit(true);
            if (player.getPlayer().getBoard().getField(Integer.parseInt(index)).getShip().getType() == ProtocolMessages.Ship.EMPTY) {
                for (ClientHandler k : clientlist) {
                    k.sendMessage(ProtocolMessages.MISS + ProtocolMessages.CS + index + ProtocolMessages.CS + player.getPlayer().getName());
                }
            } else {
                for (ClientHandler k : clientlist) {
                    k.sendMessage(ProtocolMessages.HIT + ProtocolMessages.CS + index + ProtocolMessages.CS + player.getPlayer().getName());
                }
                boolean destroyed = true;
                Ship hitShip = player.getPlayer().getBoard().getField(Integer.parseInt(index)).getShip();
                for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0] * ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
                    Ship testShip = player.getPlayer().getBoard().getField(i).getShip();
                    if (testShip.getIdentifier() == hitShip.getIdentifier() || testShip.getType() == hitShip.getType()) {
                        if (!player.getPlayer().getBoard().getField(i).isHit()) {
                            destroyed = false;
                        }
                    }
                }
                if (destroyed) {
                    int firstIndex = -1;
                    for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0] * ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
                        Ship testShip = player.getPlayer().getBoard().getField(i).getShip();
                        if (testShip.getIdentifier() == hitShip.getIdentifier() || testShip.getType() == hitShip.getType()) {
                            firstIndex = i;
                            break;
                        }
                    }
                    if (player.getPlayer().getBoard().getField(firstIndex + 1).getShip().getType() == hitShip.getType() || player.getPlayer().getBoard().getField(firstIndex + 1).getShip().getIdentifier() == hitShip.getIdentifier()) {
                        for (ClientHandler k : clientlist) {
                            k.sendMessage(ProtocolMessages.DESTROY + ProtocolMessages.CS + hitShip.getType().toString().charAt(0) + ProtocolMessages.CS + firstIndex + ProtocolMessages.CS + "0" + ProtocolMessages.CS + player.getPlayer().getName());
                        }
                    }
                    else {
                        for (ClientHandler k : clientlist) {
                            k.sendMessage(ProtocolMessages.DESTROY + ProtocolMessages.CS + hitShip.getType().toString().charAt(0) + ProtocolMessages.CS + firstIndex + ProtocolMessages.CS + "1" + ProtocolMessages.CS + player.getPlayer().getName());
                        }
                    }
                }
            }
        }
        else {
            player.sendMessage(ProtocolMessages.ERROR + ProtocolMessages.CS + ProtocolMessages.INVALID_INDEX);
        }
    }

    public void processMessage(String name, String message) {

    }

    public void processBoardInput(String name, Board board) {

    }

    public static void main(String[] args) {
        Thread a = new Thread(new Server(69));
        a.start();
    }
}
