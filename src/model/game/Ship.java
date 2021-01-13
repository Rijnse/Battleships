package model.game;
import model.ProtocolMessages;

public class Ship {
    private int identifier; // between 0 and 9, depends on ship type, -1 if empty
    private ProtocolMessages.Ship type;
    private boolean sunk;

    public Ship(int identifier, ProtocolMessages.Ship type) {
        this.sunk = false;
        this.identifier = identifier;
        this.type = type;
    }

    public Ship(int identifier, ProtocolMessages.Ship type, boolean sunk) {
        this.sunk = sunk;
        this.identifier = identifier;
        this.type = type;
    }

    public void setSunk(boolean sunk) {
        this.sunk = sunk;
    }

    public boolean isSunk() {
        return this.sunk;
    }

    public int getIdentifier() {
        return this.identifier;
    }

    public ProtocolMessages.Ship getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type.toString() + this.identifier;
    }
}
