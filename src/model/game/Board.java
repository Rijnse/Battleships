package model.game;

import model.ProtocolMessages;

public class Board {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;
    public static final String COLUMNS = "ABCDEFGHIJKLMNO";

    private Field[] fields;

    public Board() {
        //TODO randomized board maker
    }

    //example of string 0,C1,C1,C1,C1,C1,0,P0,0 etc. Read left to right, top to bottom
    public Board(String boardarray) {
        //TODO initializing board using protocol defined string
    }

    public Board(Field[] array) {
        this.fields = array;
    }

    public String boardToString() {
        //TODO converts the board object to a protocol defined string
        return null;
    }

    public Field[] getFieldsArray() {
        return fields;
    }

    //used by ComputerPlayer for determining moves
    public Board deepCopy() {
        Field[] copy = new Field[WIDTH*HEIGHT];
        for (int i = 0; i < fields.length; i++) {
            copy[i] = new Field(fields[i].isHit(), fields[i].getShip());
        }
        return new Board(copy);
    }

    //example: A1 -> 0, B1 -> 15, A2 -> 1
    public int index(String coordinates) {
        String[] split = coordinates.split("");
        int col = COLUMNS.indexOf(split[0]);
        int row = (Integer.parseInt(split[1])) - 1;
        return (col * 15) + row;
    }

    public boolean isField(int index) {
        return 0 <= index && index < WIDTH*HEIGHT;
    }

    public boolean isField(String coordinates) {
        return 0 <= index(coordinates) && index(coordinates) < WIDTH*HEIGHT;
    }

    public Field getField(int i) {
        return this.fields[i];
    }

    public Field getField(String coordinates) {
        return this.fields[index(coordinates)];
    }

    public boolean isEmptyField(int i) {
        return this.fields[i].getShip() == null;
    }

    public boolean isEmptyField(String coordinates) {
        return this.fields[index(coordinates)].getShip() == null;
    }
}
