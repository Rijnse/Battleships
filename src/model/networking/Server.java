package model.networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private int port;
    private InetAddress ip;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(this.port);
            System.out.println("Waiting for client to connect!");
            Socket sock = server.accept();
            System.out.println("client connected on port" + sock.getLocalPort());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public int getPort() {
      return this.port;
    }

    public InetAddress getIp() {
        return this.ip;
    }

    public static void main(String[] args) {
        Thread one = new Thread(new Server(69));
        one.start();
    }
}
