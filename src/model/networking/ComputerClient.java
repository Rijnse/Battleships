package model.networking;

import model.ProtocolMessages;
import model.game.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

//This class is basically a stripped down version of the Client interface/class
public class ComputerClient implements Runnable, ClientInterface{
    private Player player;
    private Socket sock;
    private BufferedReader in;
    private BufferedWriter out;

    /**
     * @ensures that Client object is created and that client tries to connect to server with given parameters
     * @requires valid Strings (meaning non-null)
     * @param IP is the ip address of the desired server
     * @param port is the port of the desired server
     * @throws IOException if unable to connect to server for whatever reason. The controller catches this exception to make sure the user is notified of this error.
     */
    public ComputerClient(String IP, String port) throws IOException {
        this.player = new ComputerPlayer();
            this.sock = new Socket(InetAddress.getByName(IP), Integer.parseInt(port));
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            out = new BufferedWriter(
                    new OutputStreamWriter(sock.getOutputStream()));
            sendMessage(ProtocolMessages.HELLO + ProtocolMessages.CS + player.getName());
    }

    /**
     * @ensures that ComputerClient continuously awaits a message from the socket. This is happening on a new Thread.
     * Calls for receiveMessage() method when it receives a new command from server.
     * @requires a proper connection with a server using the this.socket.
     */
    @Override
    public void run() {
        String msg;
        try {
            msg = in.readLine();
            while (msg != null) {
                receiveMessage(msg);
                msg = in.readLine();
            }
            exit();
        } catch (IOException e) {
            exit();
        }
    }

    /**
     * @ensures that message from server is broken down and properly handled on clientside. If command is unknown, client will not respond.
     * @requires a valid String as a command
     * @param message != null
     */
    @Override
    public void receiveMessage(String message) {
        System.out.println("ComputerClient receives: " + message);
        String[] array = message.split(ProtocolMessages.CS);
        switch (array[0]) {
            case ProtocolMessages.HELLO:
                if (array.length > 2) {
                    if (array[2] != null) {
                        if (array[1].equals(getPlayer().getName())) {
                            this.player.setCurrentGame(new Game(this.player, new HumanPlayer(array[2], true)));
                        }
                        else {
                            this.player.setCurrentGame(new Game(this.player, new HumanPlayer(array[1], true)));
                        }
                    }
                }
                else {
                    sendMessage(ProtocolMessages.EXIT + ProtocolMessages.CS + player.getName());
                }
                break;
            case ProtocolMessages.START:
                sendMessage(ProtocolMessages.BOARD + ProtocolMessages.CS + player.getName() + ProtocolMessages.CS + player.getBoard().boardToString());
                break;
            case ProtocolMessages.ERROR:
            case ProtocolMessages.TIME:

                break;
            case ProtocolMessages.TURN:
                if (array[1].equals(player.getName())) {
                    sendMessage(ProtocolMessages.ATTACK + ProtocolMessages.CS + this.player.determineMove());
                }
                break;
            case ProtocolMessages.HIT:
                if (array[2].equals(player.getCurrentGame().getPlayerOne().getName())) {
                    player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                    player.getCurrentGame().getPlayerTwo().incrementScore(1);
                }
                else {
                    player.getCurrentGame().getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                    player.getCurrentGame().getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).getShip().setType(ProtocolMessages.Ship.UNKNOWN);
                    player.getCurrentGame().getPlayerOne().incrementScore(1);
                }
                break;
            case ProtocolMessages.MISS:
                if (array[2].equals(player.getCurrentGame().getPlayerOne().getName())) {
                    player.getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                }
                else {
                    player.getCurrentGame().getPlayerTwo().getBoard().getField(Integer.parseInt(array[1])).setHit(true);
                }
                break;
            case ProtocolMessages.DESTROY:
                Player playerDestroyed = null;
                if (array[4].equals(player.getCurrentGame().getPlayerOne().getName())) {
                    playerDestroyed = player.getCurrentGame().getPlayerOne();
                    player.getCurrentGame().getPlayerTwo().incrementScore(1);
                }
                else {
                    playerDestroyed = player.getCurrentGame().getPlayerTwo();
                    player.getCurrentGame().getPlayerOne().incrementScore(1);
                }

                Ship ship = null;
                switch (array[1]) {
                    case "C":
                        ship = new Ship(-1, ProtocolMessages.Ship.CARRIER);
                        break;
                    case "B":
                        ship = new Ship(-1, ProtocolMessages.Ship.BATTLESHIP);
                        break;
                    case "D":
                        ship = new Ship(-1, ProtocolMessages.Ship.DESTROYER);
                        break;
                    case "S":
                        ship = new Ship(-1, ProtocolMessages.Ship.SUPERPATROL);
                        break;
                    case "P":
                        ship = new Ship(-1, ProtocolMessages.Ship.PATROLBOAT);
                        break;
                }
                ship.setSunk(true);
                if (array[3].contains("0")) {
                    for (int i = Integer.parseInt(array[2]); i < ship.getLength(); i++) {
                        playerDestroyed.getBoard().getFieldsArray()[i] = new Field(ship);
                    }
                }
                else {
                    for (int i = Integer.parseInt(array[2]); i < ship.getLength(); i = i + 15) {
                        playerDestroyed.getBoard().getFieldsArray()[i] = new Field(ship);
                    }
                }
                break;
            case ProtocolMessages.WON:
                exit();
                break;
        }
    }

    @Override
    public Player getPlayer() {
        return this.player;
    }

    @Override
    public Socket getSock() {
        return this.sock;
    }

    /**
     * @ensures that given message is sent to server
     * @param message is the message that the client wishes to send (!= null)
     */
    @Override
    public void sendMessage(String message) {
        try {
            System.out.println("Computerclient sends: " + message);
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            System.out.println("Something went wrong!");
        }
    }

    /**
     * @ensures that socket streams are closed down
     */
    @Override
    public void exit() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
