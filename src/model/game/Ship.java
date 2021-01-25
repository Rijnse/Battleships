package model.game;
import model.ProtocolMessages;

public class Ship {
    private int identifier; // between 0 and 9, depends on ship type, -1 if empty
    private ProtocolMessages.Ship type;
    private boolean sunk;
    private int length;

    public Ship(int identifier, ProtocolMessages.Ship type) {
        this.sunk = false;
        this.identifier = identifier;
        this.type = type;
        switch (type) {
            case CARRIER:
                this.length = 5;
                break;
            case BATTLESHIP:
                this.length = 4;
                break;
            case DESTROYER:
                this.length = 3;
                break;
            case SUPERPATROL:
                this.length = 2;
                break;
            case PATROLBOAT:
                this.length = 1;
                break;
            default:
                this.length = 0;
                break;
        }
    }

    public Ship(int identifier, ProtocolMessages.Ship type, boolean sunk) {
        this.sunk = sunk;
        this.identifier = identifier;
        this.type = type;
        switch (type) {
            case CARRIER:
                this.length = 5;
                break;
            case BATTLESHIP:
                this.length = 4;
                break;
            case DESTROYER:
                this.length = 3;
                break;
            case SUPERPATROL:
                this.length = 2;
                break;
            case PATROLBOAT:
                this.length = 1;
                break;
            default:
                this.length = 0;
                break;
        }
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

    public void setType(ProtocolMessages.Ship ship) {
        this.type = ship;
    }

    @Override
    public String toString() {
        return this.type.toString() + this.identifier;
    }
}
