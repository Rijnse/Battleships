package model.game;

import model.ProtocolMessages;

public class Board {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;
    public static final String COLUMNS = "ABCDEFGHIJKLMNO";

    private static final Ship[] SHIPS = {
            new Ship(0, ProtocolMessages.Ship.CARRIER),
            new Ship(1, ProtocolMessages.Ship.CARRIER),

            new Ship(0, ProtocolMessages.Ship.BATTLESHIP),
            new Ship(1, ProtocolMessages.Ship.BATTLESHIP),
            new Ship(2, ProtocolMessages.Ship.BATTLESHIP),

            new Ship(0, ProtocolMessages.Ship.DESTROYER),
            new Ship(1, ProtocolMessages.Ship.DESTROYER),
            new Ship(2, ProtocolMessages.Ship.DESTROYER),
            new Ship(3, ProtocolMessages.Ship.DESTROYER),
            new Ship(4, ProtocolMessages.Ship.DESTROYER),

            new Ship(0, ProtocolMessages.Ship.SUPERPATROL),
            new Ship(1, ProtocolMessages.Ship.SUPERPATROL),
            new Ship(2, ProtocolMessages.Ship.SUPERPATROL),
            new Ship(3, ProtocolMessages.Ship.SUPERPATROL),
            new Ship(4, ProtocolMessages.Ship.SUPERPATROL),
            new Ship(5, ProtocolMessages.Ship.SUPERPATROL),
            new Ship(6, ProtocolMessages.Ship.SUPERPATROL),
            new Ship(7, ProtocolMessages.Ship.SUPERPATROL),

            new Ship(0, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(1, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(2, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(3, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(4, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(5, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(6, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(7, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(8, ProtocolMessages.Ship.PATROLBOAT),
            new Ship(9, ProtocolMessages.Ship.PATROLBOAT),
    };
    private Field[] fields;

    public Board() {
        Field[] array = new Field[WIDTH * HEIGHT];
        for (int i = 0; i < WIDTH * HEIGHT; i ++) {
            array[i] = new Field();
        }

        for (Ship s : SHIPS) {
            switch (s.getType()) {
                case CARRIER:
                    checkPlacement(array, 5, s);
                    break;
                case BATTLESHIP:
                    checkPlacement(array, 4, s);
                    break;
                case DESTROYER:
                    checkPlacement(array, 3, s);
                    break;
                case SUPERPATROL:
                    checkPlacement(array, 2, s);
                    break;
                case PATROLBOAT:
                    checkPlacement(array, 1, s);
                    break;
            }
            this.fields=array;
            }
        }

    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board.boardToString());
    }

    public boolean checkIfNotFull (Field[] array, int i) {
        return array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY;
    }

    public boolean checkRight (Field[] array, int index, int length) {
        int k = index;
        while (checkIfNotFull(array, k) && (k - index) < length && k < (WIDTH * HEIGHT)) {
            k++;
        }
        if ((k - index) < length) {
            return false;
        }
        else if ((k % WIDTH) > (length - 2)){
            return true;
        }
        else {
        return false;
        }
    }

    public boolean checkLeft (Field[] array, int index, int length) {
        int k = index;
        while (checkIfNotFull(array, k) && (index - k) < length && k > 0) {
            k--;
        }
        if ((index - k) < length) {
            return false;
        }
        else if ((k % WIDTH) < WIDTH - length + 1) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkTop (Field[] array, int index, int length) {
        int k = index;
        while (checkIfNotFull(array, k) && k > (index - (WIDTH * (length - 1))) && k >= WIDTH) {
            k = k - WIDTH;
        }
        if (k <= (index - (WIDTH * (length -1)))){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkBottom (Field[] array, int index, int length) {
        int k = index;
        while (checkIfNotFull(array, k) && (k) < (index + (WIDTH * (length - 1))) && k < (WIDTH * (HEIGHT - 1))) {
            k = k + WIDTH;
        }
        if (k >= (index + WIDTH * (length - 1))) {
            return true;
        }
        else {
            return false;
        }
    }

    public void checkPlacement (Field[] array, int length, Ship s) {
        int randomOrientation = (int) (Math.random() * 2);
        int randomIndex = (int) (Math.random() * (WIDTH * HEIGHT));
        while (true) {
            if (checkIfNotFull(array, randomIndex)) {
                if (randomOrientation == 0) {
                    if (checkRight(array, randomIndex, length)) {
                        for (int i = randomIndex; i < (length + randomIndex); i++) {
                            array[i] = new Field(s);
                        }
                        break;
                    } else if (checkLeft(array, randomIndex, length)) {
                        for (int i = randomIndex; i > (randomIndex - length); i--) {
                            array[i] = new Field(s);
                        }
                        break;
                    }
                }
                else if (randomOrientation == 1) {
                        if (checkTop(array, randomIndex, length)) {
                            for (int i = randomIndex; i > (randomIndex - (WIDTH * length)); i = i - WIDTH) {
                                array[i] = new Field(s);
                            }
                            break;
                        }
                        if (checkBottom(array, randomIndex, length)) {
                            for (int i = randomIndex; i < (randomIndex + (length * WIDTH)); i = i + WIDTH) {
                                array[i] = new Field(s);
                            }
                            break;
                        }
                    }
                }
            randomIndex = (int) (Math.random() * (WIDTH * HEIGHT));
            }
        }

    //example of string 0,C1,C1,C1,C1,C1,0,P0,0 etc. Read left to right, top to bottom
    public Board(String boardarray) {
        Ship placeholder = null;
        Field[] array = new Field[WIDTH*HEIGHT];
        String[] strarray = boardarray.split(",");
        for (int i = 0; i < strarray.length; i++) {
            if (strarray[i].equals("0")) {
                array[i] = new Field();
            }
            else {
                switch (strarray[i].charAt(0)) {
                    case 'C':
                        for (int k = 0; k < SHIPS.length; k++) {
                            if (SHIPS[k].getIdentifier() == Integer.parseInt(String.valueOf(strarray[i].charAt(1))) && SHIPS[k].getType() == ProtocolMessages.Ship.CARRIER) {
                                placeholder = SHIPS[k];
                                break;
                            }
                        }
                        array[i] = new Field(placeholder);
                        break;
                    case 'B':
                        for (int k = 0; k < SHIPS.length; k++) {
                            if (SHIPS[k].getIdentifier() == Integer.parseInt(String.valueOf(strarray[i].charAt(1))) && SHIPS[k].getType() == ProtocolMessages.Ship.BATTLESHIP) {
                                placeholder = SHIPS[k];
                                break;
                            }
                        }
                        array[i] = new Field(placeholder);
                        break;
                    case 'D':
                        for (int k = 0; k < SHIPS.length; k++) {
                            if (SHIPS[k].getIdentifier() == Integer.parseInt(String.valueOf(strarray[i].charAt(1))) && SHIPS[k].getType() == ProtocolMessages.Ship.DESTROYER) {
                                placeholder = SHIPS[k];
                                break;
                            }
                        }
                        array[i] = new Field(placeholder);
                        break;
                    case 'S':
                        for (int k = 0; k < SHIPS.length; k++) {
                            if (SHIPS[k].getIdentifier() == Integer.parseInt(String.valueOf(strarray[i].charAt(1))) && SHIPS[k].getType() == ProtocolMessages.Ship.SUPERPATROL) {
                                placeholder = SHIPS[k];
                                break;
                            }
                        }
                        array[i] = new Field(placeholder);
                        break;
                    case 'P':
                        for (int k = 0; k < SHIPS.length; k++) {
                            if (SHIPS[k].getIdentifier() == Integer.parseInt(String.valueOf(strarray[i].charAt(1))) && SHIPS[k].getType() == ProtocolMessages.Ship.PATROLBOAT) {
                                placeholder = SHIPS[k];
                                break;
                            }
                        }
                        array[i] = new Field(placeholder);
                        break;
                }
            }
        }
        this.fields = array;
    }

    public Board(Field[] array) {
        this.fields = array;
    }

    public String boardToString() {
        String result = "";
        for (int i = 0; i < fields.length; i++) {
            switch (fields[i].getShip().getType()) {
                case CARRIER:
                    result = result + ("C" + fields[i].getShip().getIdentifier()) + ",";
                    break;
                case BATTLESHIP:
                    result = result + ("B" + fields[i].getShip().getIdentifier()) + ",";
                    break;
                case DESTROYER:
                    result = result + ("D" + fields[i].getShip().getIdentifier()) + ",";
                    break;
                case SUPERPATROL:
                    result = result + ("S" + fields[i].getShip().getIdentifier()) + ",";
                    break;
                case PATROLBOAT:
                    result = result + ("P" + fields[i].getShip().getIdentifier()) + ",";
                    break;
                default:
                    result = result + 0 + ",";
                    break;
            }
        }
        return result;
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

    /**
     * @requires valid input String object
     * @ensures returning of index
     * @param coordinates in form "letter (between A and O) + number (between 1 and 10) e.g. "A1", "G6" or "O10"
     * @return a number between 0 and 149, which resembles the index of the field in the board fields array
     */
    public static int index(String coordinates) {
        String[] split = coordinates.split("");
        int col = COLUMNS.indexOf(split[0]);
        int row;
        if (!COLUMNS.contains(split[0])) {
            return -1;
        }
        if (split[1].equals("0")) {
            return -1;
        }
        if (split.length == 2) {
            row = (Integer.parseInt(split[1])) - 1;
        }
        else {
            if (split[1].equals("1") && split[2].equals("0")) {
                row = 9;
            }
            else {
                return -1;
            }
        }
        return (row * WIDTH) + col;
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
        return this.fields[i].getShip().getType() == ProtocolMessages.Ship.EMPTY;
    }

    public boolean isEmptyField(String coordinates) {
        return this.fields[index(coordinates)].getShip().getType() == ProtocolMessages.Ship.EMPTY;
    }
}
