package model.networking;

import model.ProtocolMessages;
import model.game.Board;
import model.game.HumanPlayer;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;

    private Server server;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    private Client client;

    public ClientHandler(Socket sock, Server server, Client client) {
        try {
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            this.sock = sock;
            this.server = server;
            this.client = client;
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

                    break;
                case ProtocolMessages.START:
                    if (this.client.getPlayer().getName().equals(this.server.getPlayerOne().getClient().getPlayer().getName())) {
                       this.server.startGame();
                    }
                    else {
                        sendMessage(ProtocolMessages.ERROR + ProtocolMessages.CS + ProtocolMessages.ACTION_NOT_PERMITTED + ProtocolMessages.CS + client.getPlayer().getName());
                    }
                    break;
                case ProtocolMessages.BOARD:
                    if (array[1] != null && array[2] != null) {
                        if (this.client.getPlayer().getName().equals(array[1])) {
                            this.server.processBoardInput(client.getPlayer().getName(), new Board(array[2]));
                        }
                    }
                    else {
                        illegalCommandSender();
                    }
                    break;
                case ProtocolMessages.ATTACK:
                    this.server.attackCommand(this.client.getPlayer().getName());
                    break;
                case ProtocolMessages.MSGSEND:
                    if (array[1] != null && array[2] != null) {
                        if (this.client.getPlayer().getName().equals(array[1]) && !(array[2].length() > 50)) {
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
        sendMessage(ProtocolMessages.ERROR + ProtocolMessages.CS + ProtocolMessages.ILLEGAL_COMMAND + ProtocolMessages.CS + client.getPlayer().getName());
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
