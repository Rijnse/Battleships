package model.game;
import model.ProtocolMessages;

public class Ship {
    private int identifier; // between 0 and 9, depends on ship type, -1 if empty
    private ProtocolMessages.Ship type;
    private int length;

    /**
     * @ensures that a valid Ship object is constructed using the given parameters
     * @requires a valid int as identifier (differs per ship type, see game rules) and a proper type as described in the protocol
     * @param identifier is the identifier used to tell multiple ships of the same type apart (e.g. two ships have type CARRIER, one with id 0 and one with id 1)
     * @param type is the type of the Ship object as described in the protocol. UNKNOWN is used when the type of the ship is yet to be determined by the player shooting at it...
     */
    public Ship(int identifier, ProtocolMessages.Ship type) {
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

    /**
     * @ensures that the length property is returned of the Ship object
     * @return an integer -1 or between 1 and 5
     */
    public int getLength() {
        return length;
    }

    /**
     * @ensures that the identifier property is returned of the Ship object
     * @return an integer between 0 and 9 (depends on ship type, see protocol)
     */
    public int getIdentifier() {
        return this.identifier;
    }

    /**
     * @ensures that the type of the Ship object is returned
     * @return a custom enum Ship, as described in ProtocolMessages and the protocol
     */
    public ProtocolMessages.Ship getType() {
        return this.type;
    }

    /**
     * @ensures that the type of the Ship object is set to the parameter-given type
     * @param ship a custom enum Ship, as described in ProtocolMessages and the protocol
     */
    public void setType(ProtocolMessages.Ship ship) {
        this.type = ship;
    }

    /**
     * @ensures that a String representation of the Ship object is created using it's type and identifier
     * @return a non-null String
     */
    @Override
    public String toString() {
        return this.type.toString() + this.identifier;
    }
}
