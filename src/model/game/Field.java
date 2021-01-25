package model.game;

import model.ProtocolMessages;

public class Field {
    private boolean hit;
    private Ship ship;

    /**
     * @ensures that a Field object is created with a new ship object with identifier -1 and type EMPTY.
     * This is used when generating empty fields, because they're type is EMPTY. Thus, when searching for empty fields, the type is used in the comparison.
     * @requires a valid Ship object constructor with identifier and type
     */
    public Field() {
        this.hit = false;
        this.ship = new Ship(-1, ProtocolMessages.Ship.EMPTY);
    }

    /**
     * @ensures that a field is created with the ship given in the parameter
     * @requires a valid Ship object
     * @param ship is the Ship object which the field contains
     */
    public Field(Ship ship) {
        this.hit = false;
        this.ship = ship;
    }

    /**
     * @ensures that the state of the field (either hit or not hit, default is not hit since none of the fields have been fired at at the beginning) is returned as a boolean
     * @return boolean (true/false)
     */
    public boolean isHit() {
        return this.hit;
    }

    /**
     * @ensures that the ship object within the field is returned
     * @return a Ship object
     */
    public Ship getShip() {
        return this.ship;
    }

    /**
     * @ensures that the property 'hit' of the Field object is set to parameter-given boolean
     * @requires a valid boolean true or false
     * @param hit is true or false (false is rarely used since all fields are false when constructed and hit during the game...)
     */
    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
