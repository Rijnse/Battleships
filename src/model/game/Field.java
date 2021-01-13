package model.game;

import model.ProtocolMessages;

public class Field {
    private boolean hit;
    private Ship ship;

    public Field() {
        this.hit = false;
        this.ship = new Ship(-1, ProtocolMessages.Ship.EMPTY);
    }

    public Field(Ship ship) {
        this.hit = false;
        this.ship = ship;
    }

    public Field(boolean hit, Ship ship) {
        this.hit = hit;
        this.ship = ship;
    }

    public Field copyField(boolean hit, Ship ship) {
        return new Field(hit, ship);
    }

    public boolean isHit() {
        return this.hit;
    }

    public Ship getShip() {
        return this.ship;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
