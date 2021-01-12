package model.game;

import model.ProtocolMessages;

public class Board {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;

    private ProtocolMessages.Ship[] fields;

    public Board() {
        //TODO randomized board maker
    }

    public Board(String boardarray) {
        //TODO initializing board using protocol defined string
    }

    public Board(ProtocolMessages.Ship[] array) {
        this.fields = array;
    }

    public ProtocolMessages.Ship[] getFieldsArray() {
        return fields;
    }

    //used by ComputerPlayer for determining moves
    public Board deepCopy() {
        ProtocolMessages.Ship[] copy = new ProtocolMessages.Ship[WIDTH*HEIGHT];
        for (int i = 0; i < fields.length; i++) {
            switch (fields[i]) {
                case EMPTY:
                    copy[i] = ProtocolMessages.Ship.EMPTY;
                    break;
                case CARRIER:
                    copy[i] = ProtocolMessages.Ship.CARRIER;
                    break;
                case BATTLESHIP:
                    copy[i] = ProtocolMessages.Ship.BATTLESHIP;
                    break;
                case DESTROYER:
                    copy[i] = ProtocolMessages.Ship.DESTROYER;
                    break;
                case SUPERPATROL:
                    copy[i] = ProtocolMessages.Ship.SUPERPATROL;
                    break;
                case PATROLBOAT:
                    copy[i] = ProtocolMessages.Ship.PATROLBOAT;
                    break;
            }
        }
        return new Board(copy);
    }

    //example: A1 -> 0, B1 -> 15, A2 -> 1
    public int index(String coordinates) {
        String columns = "ABCDEFGHIJKLMNO";
        String[] split = coordinates.split("");
        int col = columns.indexOf(split[0]);
        int row = (Integer.parseInt(split[1])) - 1;
        return (col * 15) + row;
    }

    public boolean isField(int index) {
        return 0 <= index && index < WIDTH*HEIGHT;
    }

    public boolean isField(String coordinates) {
        return 0 <= index(coordinates) && index(coordinates) < WIDTH*HEIGHT;
    }

    public ProtocolMessages.Ship getField(int i) {
        return this.fields[i];
    }

    public ProtocolMessages.Ship getField(String coordinates) {
        return this.fields[index(coordinates)];
    }

    public boolean isEmptyField(int i) {
        return this.fields[i] == ProtocolMessages.Ship.EMPTY;
    }

    public boolean isEmptyField(String coordinates) {
        return this.fields[index(coordinates)] == ProtocolMessages.Ship.EMPTY;
    }



    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board.index("B1"));
    }
}
