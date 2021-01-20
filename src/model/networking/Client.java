package model.networking;

import model.game.HumanPlayer;
import model.game.Player;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private Player player;
    private Socket sock;

    public Client(String name, Socket sock) {
        this.player = new HumanPlayer(name);
        this.sock = sock;
    }
    public Client(String name, String IP, String port) {
        this.player = new HumanPlayer(name);
        try {
            this.sock = new Socket(InetAddress.getByName(IP), Integer.parseInt(port));
        } catch (UnknownHostException e) {
            System.out.println("Host could not be found!");
        } catch (IOException e) {
            System.out.println("Some I/O error has occurred. Try again!");
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public Socket getSock() {
        return this.sock;
    }

}
