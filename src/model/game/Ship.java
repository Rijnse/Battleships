package model.game;
import model.ProtocolMessages;

public class Ship {
    private int identifier;
    private int length;
    private ProtocolMessages.Ship type;

    public Ship(int identifier, int length, ProtocolMessages.Ship type) {
        this.identifier = identifier;
        this.length = length;
        this.type = type;
    }
}
