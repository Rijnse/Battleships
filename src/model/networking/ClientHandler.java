package model.networking;

import model.ProtocolMessages;
import model.game.Board;
import model.game.HumanPlayer;
import model.game.Player;

import java.io.*;
import java.net.Socket;

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
    private Player player;

    public ClientHandler(Socket sock, Server server, Player player) {
        try {
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            this.sock = sock;
            this.server = server;
            this.player = player;
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
                out.newLine();
                out.flush();
                message = in.readLine();
            }
            exit();
        } catch (IOException e) {
            exit();
        }
    }

    public void receiveMessage(String message) {
        String[] array = message.split(ProtocolMessages.CS);
        if (array.length > 3 || array.length <= 0) {
            illegalCommandSender();
        }
        else {
            switch (array[0]) {
                case ProtocolMessages.HELLO:
                    if (array[1] != null) {
                        if (!(this.server.handleHello(array[1], this))) {
                            sendMessage(ProtocolMessages.ERROR + ProtocolMessages.CS + ProtocolMessages.DUPLICATE_NAME);
                        }
                        else {
                            String hellomsg = ProtocolMessages.HELLO + ProtocolMessages.CS;
                            for (ClientHandler k : this.server.getClientList()) {
                                hellomsg = hellomsg + k.getPlayer().getName() + ProtocolMessages.CS;
                            }
                            sendMessage(hellomsg);
                        }
                    }
                    else {
                        illegalCommandSender();
                    }
                    break;
                case ProtocolMessages.START:
                    if (this.player.getName().equals(this.server.getPlayer(0).getPlayer().getName())) {
                       this.server.startGame();
                    }
                    else {
                        sendMessage(ProtocolMessages.ERROR + ProtocolMessages.CS + ProtocolMessages.ACTION_NOT_PERMITTED);
                    }
                    break;
                case ProtocolMessages.BOARD:
                    if (array[1] != null && array[2] != null) {
                        if (this.player.getName().equals(array[1])) {
                            this.server.processBoardInput(this.player.getName(), new Board(array[2]));
                        }
                    }
                    else {
                        illegalCommandSender();
                    }
                    break;
                case ProtocolMessages.ATTACK:
                    this.server.attackCommand(this.player.getName(), array[1]);
                    break;
                case ProtocolMessages.MSGSEND:
                    if (array[1] != null && array[2] != null) {
                        if (this.player.getName().equals(array[1]) && !(array[2].length() > 50)) {
                            this.server.processMessage(array[1], array[2]);
                        }
                    }
                    else {
                        illegalCommandSender();
                    }
                    break;
                default:
                    illegalCommandSender();
                    break;
            }
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

    public void illegalCommandSender() {
        sendMessage(ProtocolMessages.ERROR + ProtocolMessages.CS + ProtocolMessages.ILLEGAL_COMMAND);
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
