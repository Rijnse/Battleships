package model.networking;

import static model.ProtocolMessages.*;

import model.ProtocolMessages;
import model.game.Board;
import model.game.HumanPlayer;
import model.game.Player;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable{
    private BufferedReader in;
    private BufferedWriter out;

    public Socket getSock() {
        return sock;
    }

    public void setSock(Socket sock) {
        this.sock = sock;
    }

    private Socket sock;
    private Server server;

    public List<ClientHandler> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(List<ClientHandler> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    private List<ClientHandler> gamePlayers;
    private Player player;

    public ClientHandler(Socket sock, Server server) {
        try {
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            this.sock = sock;
            this.server = server;
        } catch (IOException e) {
            exit();
        }
    }

    @Override
    public void run() {
        String message;
        try {
            message = in.readLine();
            while (message != null) {
                receiveMessage(message);
                message = in.readLine();
            }
            exit();
        } catch (IOException e) {
            exit();
        }
    }

    public void receiveMessage(String message) {
        System.out.println("Clienthandler receives: " + message);
        String[] array = message.split(CS);
        if (array.length > 3 || array.length <= 0) {
            illegalCommandSender();
        }
        else {
            switch (array[0]) {
                case HELLO:
                    if (array[1] != null) {
                        this.player = new HumanPlayer(array[1]);
                        this.server.handleHello(array[1], this);
                    }
                    else {
                        illegalCommandSender();
                    }
                    break;
                case START:
                    int index = this.gamePlayers.indexOf(this);
                    if (index == 0) {
                        if (this.gamePlayers.size() == 2) {
                            server.startGame(this);
                        }
                    }
                    else {
                        sendMessage(ERROR + CS + ACTION_NOT_PERMITTED);
                    }
                    break;
                case BOARD:
                    if (array[1] != null && array[2] != null && this.player.getName().equals(array[1])) {
                       Board board = new Board(array[2]);
                       if (board.checkValidBoard()) {
                            this.server.receiveBoards(board, this);
                       }
                       else {
                           sendMessage(ERROR + CS + ILLEGAL_SHIP_PLACEMENT);
                       }
                    }
                    else {
                        illegalCommandSender();
                    }
                    break;
                case ATTACK:
                    this.server.attackCommand(this, array[1]);
                    break;
                case MSGSEND:
                    if (array[1] != null && array[2] != null) {
                        this.server.processMessage();
                    }
                    else {
                        illegalCommandSender();
                    }
                    break;
                case EXIT:
                    this.server.clientLeft(this);
                    this.exit();
                    break;
                default:
                    illegalCommandSender();
                    break;
            }
        }


    }

    public void sendMessage(String message) {
        try {
            System.out.println("Clienthandler sends: " + message);
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.out.println("Something went wrong!");
        }
    }

    public void illegalCommandSender() {
        sendMessage(ERROR + CS + ILLEGAL_COMMAND);
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

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
