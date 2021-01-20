package model.networking;

import model.ProtocolMessages;
import model.game.HumanPlayer;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private BufferedReader in;
    private BufferedWriter out;
    private Socket sock;

    private Server server;
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
        String[] array = message.split(" ");
        if (array.length > 3 || array.length <= 0) {
            sendMessage(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER + "IllegalCommand" + ProtocolMessages.DELIMITER + ((HumanPlayer) client.getPlayer()).getName());
        }
        else {
            switch (array[0]) {
                case ProtocolMessages.HELLO:

                    break;
                case ProtocolMessages.START:

                    break;
                case ProtocolMessages.BOARD:

                    break;
                case ProtocolMessages.ATTACK:

                    break;
                case ProtocolMessages.MSGSEND:

                    break;
                default:
                    sendMessage(ProtocolMessages.ERROR + ProtocolMessages.DELIMITER + "IllegalCommand" + ProtocolMessages.DELIMITER + ((HumanPlayer) client.getPlayer()).getName());
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
