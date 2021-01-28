package model.networking;

import model.game.Player;
import java.net.Socket;

public interface ClientInterface {
    public Player getPlayer();
    public Socket getSock();

    public void receiveMessage(String message);
    public void sendMessage(String message);
    public void exit();
}
