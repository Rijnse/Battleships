package model.networking;

import model.ProtocolMessages;
import model.game.*;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server implements Runnable {
    private ServerSocket ssock;
    private int port;
    private InetAddress ip;

    private List<ClientHandler> clientlist = new ArrayList<ClientHandler>();
    private List<List<ClientHandler>> gameslist = new ArrayList<List<ClientHandler>>();
    private List<GameHandler> gameHandlers = new ArrayList<GameHandler>();

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
        StringBuilder hellomsg = new StringBuilder(ProtocolMessages.HELLO + ProtocolMessages.CS);
        Iterator<ClientHandler> iterator = handler.getGamePlayers().iterator();

        while (iterator.hasNext()) {
            hellomsg.append(iterator.next().getPlayer().getName()).append(ProtocolMessages.CS);
        }

        for (ClientHandler k : handler.getGamePlayers()) {
            k.sendMessage(hellomsg.toString());
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
            GameHandler newGame = new GameHandler(handler.getGamePlayers());
            Thread gamehandler = new Thread(newGame);
            this.gameHandlers.add(newGame);
            gamehandler.start();
        }
    }

    public void attackCommand(ClientHandler player, String index) {
        GameHandler result = null;
        for (GameHandler k : gameHandlers) {
            List<ClientHandler> i = k.getPlayers();
            for (ClientHandler o : i) {
                if (o.getPlayer().getName().equals(player.getPlayer().getName())) {
                    result = k;
                }
            }
        }
        System.out.println("Move reached server");
        result.turnFound = true;
        result.turnIndex = Integer.parseInt(index);
    }

    public void processMessage(String name, String message) {

    }

    public void closeServer() throws IOException {
        for (ClientHandler k : clientlist) {
            k.exit();
        }
        ssock.close();
    }

    public static void main(String[] args) {
        int standardPort = 1337;
        Server server = new Server(standardPort);

        Thread srv = new Thread(server);
        srv.start();
    }
}
