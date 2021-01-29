package model.networking;

import model.ProtocolMessages;
import model.game.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server implements Runnable {
    private ServerSocket ssock;
    private int port;
    private InetAddress ip;

    private boolean stopped;

    private List<ClientHandler> clientlist = new ArrayList<ClientHandler>();
    private List<List<ClientHandler>> gameslist = new ArrayList<List<ClientHandler>>();
    private List<GameHandler> gameHandlers = new ArrayList<GameHandler>();

    public Server(String ip, int port) throws IOException {
            setup(ip, port);
            this.port = port;
            this.ip = ssock.getInetAddress();
            stopped = false;
    }

    @Override
    public void run() {
        while (!stopped) {
            try {
                System.out.println("Waiting for client to connect!");
                Socket sock = ssock.accept();
                System.out.println("client connected on port" + sock.getLocalPort());

                ClientHandler newPlayer = new ClientHandler(sock, this);
                clientlist.add(newPlayer);
                new Thread(newPlayer).start();

            } catch (IOException ioException) {
                System.out.println("Socket was closed!");
            }
        }
        System.out.println("Goodbye!");
    }

    public void setup(String ip, int port) throws IOException {
        this.ssock = null;
        while (ssock == null) {
                this.ssock = new ServerSocket(port, 0,
                        InetAddress.getByName(ip));
                System.out.println("Server started at port " + port);
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

    public void clientLeft(ClientHandler handler) {
        clientlist.remove(handler);
        boolean inGame = false;

        for (GameHandler g : gameHandlers) {
            if (g.getPlayers().contains(handler)) {
                g.playerLeft(handler);
                inGame = true;
                break;
            }
        }

        if (!inGame) {
            List<ClientHandler> game = null;
            for (List<ClientHandler> g : gameslist) {
                if (g.contains(handler)) {
                    game = g;
                }
            }

            game.remove(handler);
            handler.getGamePlayers().remove(handler);
            if (game.size() == 0) {
                gameslist.remove(game);
            }
            else {
                sendHello(handler);
            }
        }

        if (clientlist.size() == 0) {
            try {
                this.closeServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        result.turnFound = true;
        result.turnIndex = Integer.parseInt(index);
    }

    public void processMessage() {
        for (ClientHandler k : clientlist) {
            k.sendMessage(ProtocolMessages.MSGRECEIVED + ProtocolMessages.CS + "Server" + ProtocolMessages.CS + "This server does not support chatting");
        }
    }

    public void closeServer() throws IOException {
        ssock.close();
        this.stopped = true;
    }

    public static void main(String[] args) {
        int standardPort = 1337;
        Server server = null;
        try {
            server = new Server("localhost",standardPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread srv = new Thread(server);
        srv.start();
    }
}
