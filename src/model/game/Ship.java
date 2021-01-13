package model.game;
import model.ProtocolMessages;

public class Ship {
    private int identifier; // between 0 and 9, depends on ship type
    private ProtocolMessages.Ship type;

    public Ship(int identifier, ProtocolMessages.Ship type) {
        this.identifier = identifier;
        this.type = type;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public ProtocolMessages.Ship getShip() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type.toString() + this.identifier;
    }
}
