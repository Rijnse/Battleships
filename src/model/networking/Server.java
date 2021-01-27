package model.networking;

import controller.ViewController;
import model.ProtocolMessages;
import model.exceptions.ExitProgram;
import model.game.Board;
import model.game.Game;
import model.game.HumanPlayer;
import model.game.Ship;

import java.io.*;
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
    private List<List<ClientHandler>> gameslist = new ArrayList<List<ClientHandler>>();
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

                ClientHandler newPlayer = new ClientHandler(sock, this);
                clientlist.add(newPlayer);
                new Thread(newPlayer).start();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void setup(int port) {
        this.ssock = null;
        while (ssock == null) {
            try {
                this.ssock = new ServerSocket(port, 0,
                        InetAddress.getByName("localhost"));
                System.out.println("Server started at port " + port);
            } catch (IOException e) {
                System.out.println(".");
            }
        }
    }

    public void startGame(ClientHandler handler) {
        for (ClientHandler k : handler.getGamePlayers()) {
            k.sendMessage(ProtocolMessages.START + ProtocolMessages.CS + "0");
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
        int count = 0;
        for (ClientHandler k : clientlist) {
            if (k.getPlayer().getName().equals(name)) {
                count++;
            }
        }
        if (count > 1) {
            handler.sendMessage(ProtocolMessages.ERROR + ProtocolMessages.CS + ProtocolMessages.DUPLICATE_NAME);
            handler.exit();
            clientlist.remove(handler);
            return false;
        }
        else {
            if (gameslist.size() == 0) {
                ArrayList<ClientHandler> game = new ArrayList<ClientHandler>();
                game.add(clientlist.get(clientlist.size() - 1));
                gameslist.add(game);
                handler.setGamePlayers(game);
            }
            else {
                if (gameslist.get(gameslist.size() - 1).size() == 1) {
                    gameslist.get(gameslist.size() - 1).add(clientlist.get(clientlist.size() - 1));
                    handler.setGamePlayers(gameslist.get(gameslist.size() - 1));
                }
                else {
                    ArrayList<ClientHandler> game = new ArrayList<ClientHandler>();
                    game.add(clientlist.get(clientlist.size() - 1));
                    gameslist.add(game);
                    handler.setGamePlayers(game);
                }
            }

            sendHello(handler);
            return true;
        }
    }

    public void sendHello(ClientHandler handler) {
        String hellomsg = ProtocolMessages.HELLO + ProtocolMessages.CS;

        for (ClientHandler k : handler.getGamePlayers()) {
            hellomsg = hellomsg + k.getPlayer().getName() + ProtocolMessages.CS;
        }
        for (ClientHandler k : handler.getGamePlayers()) {
            k.sendMessage(hellomsg);
        }
    }

    public void receiveBoards(Board board, ClientHandler handler) {
        handler.getPlayer().setBoard(board);
        handler.getPlayer().newBoardSet = true;

        boolean allBoardSentByPlayers = true;
        for (ClientHandler k: handler.getGamePlayers()) {
            if (!k.getPlayer().newBoardSet) {
                allBoardSentByPlayers = false;
            }
        }
        if (allBoardSentByPlayers) {
            System.out.println("hutssss");
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

    public void closeServer() throws IOException {
        for (ClientHandler k : clientlist) {
            k.exit();
        }
        ssock.close();
    }
}
