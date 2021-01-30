package model.networking;

import model.ProtocolMessages;
import static model.ProtocolMessages.*;
import model.game.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server implements Runnable {
    //Server info
    private ServerSocket ssock;
    private boolean stopped;

    //Lists containing clients, games and gamehandlers
    private List<ClientHandler> clientlist = new ArrayList<ClientHandler>();
    private List<List<ClientHandler>> gameslist = new ArrayList<List<ClientHandler>>();
    private List<GameHandler> gameHandlers = new ArrayList<GameHandler>();

    /**
     * @ensures that new Server object is created using given port and ip
     * @param ip is non-null string of IP address
     * @param port is int representing port of server
     * @throws IOException if Server can not be created on that IP and port
     */
    public Server(String ip, int port) throws IOException {
        this.ssock = null;
        while (ssock == null) {
            this.ssock = new ServerSocket(port, 0,
                    InetAddress.getByName(ip));
            System.out.println("Server started at port " + port);
        }

        stopped = false;
    }

    /**
     * @ensures that Server waits for Clients to connect and creates ClientHandler objects accordingly
     */
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

    /**
     * @ensures that HELLO message from newly connected clients is handled properly. Duplicate names are rejected from server and ERROR is sent
     * @param name is the username of the newly connected player
     * @param handler is the newly created ClientHandler object
     * @return true if successful, false if duplicate name
     */
    public boolean handleHello(String name, ClientHandler handler) {
        //Check if name already exists on server
        int count = 0;
        for (ClientHandler k : clientlist) {
            if (k.getPlayer().getName().equals(name)) {
                count++;
            }
        }

        //If count > 1 (meaning there are more players connected with the same name), error message is sent and ClientHandler is removed from server
        if (count > 1) {
            handler.sendMessage(ERROR + CS + DUPLICATE_NAME);
            handler.exit();
            clientlist.remove(handler);
            return false;
        }
        //Else, the ClientHandler is assigned a game
        else {
            //There are no games: a new game list is made and Handler is added to new game
            if (gameslist.size() == 0) {
                ArrayList<ClientHandler> game = new ArrayList<ClientHandler>();
                game.add(clientlist.get(clientlist.size() - 1));
                gameslist.add(game);
                handler.setGamePlayers(game);
            }
            else {
                //Handler is added to existing game
                if (gameslist.get(gameslist.size() - 1).size() == 1) {
                    gameslist.get(gameslist.size() - 1).add(clientlist.get(clientlist.size() - 1));
                    handler.setGamePlayers(gameslist.get(gameslist.size() - 1));
                }
                //New game is made and Handler is added
                else {
                    ArrayList<ClientHandler> game = new ArrayList<ClientHandler>();
                    game.add(clientlist.get(clientlist.size() - 1));
                    gameslist.add(game);
                    handler.setGamePlayers(game);
                }
            }

            //Finally, new HELLO message is sent to all players in game and true is returned
            sendHello(handler);
            return true;
        }
    }

    /**
     * @ensures that HELLO message is sent to all players in game of given ClientHandler
     * @param handler is the Handler whose playmates (and itself) is sent the HELLO message
     */
    public void sendHello(ClientHandler handler) {
        StringBuilder hellomsg = new StringBuilder(HELLO + CS);
        Iterator<ClientHandler> iterator = handler.getGamePlayers().iterator();
        while (iterator.hasNext()) {
            hellomsg.append(iterator.next().getPlayer().getName()).append(CS);
        }
        for (ClientHandler k : handler.getGamePlayers()) {
            k.sendMessage(hellomsg.toString());
        }
    }

    /**
     * @ensures that if a Client leaves it is handled properly by the server
     * @param handler is the ClientHandler of the Client leaving the server
     */
    public void clientLeft(ClientHandler handler) {
        //Removes client from clientlist of server
        clientlist.remove(handler);

        //Checks if client is currently in a game
        boolean inGame = false;
        for (GameHandler g : gameHandlers) {
            if (g.getPlayers().contains(handler)) {
                g.playerLeft(handler);
                inGame = true;
                break;
            }
        }

        //If the player is not in a game, the client is removed from the game's playerlist
        if (!inGame) {
            List<ClientHandler> game = null;
            for (List<ClientHandler> g : gameslist) {
                if (g.contains(handler)) {
                    game = g;
                }
            }
            game.remove(handler);
            handler.getGamePlayers().remove(handler);

            //If game is now empty, game is removed. Else, a new HELLO message without the leaving player is sent
            if (game.size() == 0) {
                gameslist.remove(game);
            }
            else {
                sendHello(handler);
            }
        }

        //If the client was the last connected person to the server, the server is also shut down
        if (clientlist.size() == 0) {
            try {
                this.closeServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @ensures that if a Client sends in a board, it is handled and assigned properly by the server
     * @requires that a valid Board object is sent in
     * @param board is the Board object to be handled by the server
     * @param handler is the ClientHandler sending in the new board
     */
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

    /**
     * @ensures that START message is broadcast to all players in same game as given ClientHandler
     * @param handler is the handler requesting the START
     */
    public void startGame(ClientHandler handler) {
        for (ClientHandler k : handler.getGamePlayers()) {
            k.sendMessage(START + CS + "0");
        }
    }

    /**
     * @ensures that ATTACK command of client is handled by the server. Turn is further handled by GameHandler object.
     * @param player is the ClientHandler of the player sending in the new attack
     * @param index is the index of the field at which the player wants to shoot
     */
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

    /**
     * @ensures that standard message is sent to Clients sending in chat messages, since server does not support chatting
     */
    public void processMessage() {
        for (ClientHandler k : clientlist) {
            k.sendMessage(MSGRECEIVED + CS + "Server" + CS + "This server does not support chatting");
        }
    }

    /**
     * @ensures that server socket is closed down
     * @throws IOException if not able to close socket
     */
    public void closeServer() throws IOException {
        ssock.close();
        this.stopped = true;
    }

    //Standard main method for starting an independent server
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
