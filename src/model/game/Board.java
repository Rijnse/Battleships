package model.game;

import controller.ViewController;
import model.ProtocolMessages;
import view.ViewDelegate;

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
        Field[] array = new Field[HEIGHT*WIDTH];
        for (int i = 0; i < HEIGHT*WIDTH; i ++) {
            array[i] = new Field();
        }

        for (Ship s : SHIPS) {
            switch (s.getType()) {
                case CARRIER:
                    array = placeShip(array, 5, s);
                    break;
                case BATTLESHIP:
                    array = placeShip(array, 4, s);
                    break;
                case DESTROYER:
                    array = placeShip(array, 3, s);
                    break;
                case SUPERPATROL:
                    array = placeShip(array, 2, s);
                    break;
                case PATROLBOAT:
                    array = placeShip(array, 1, s);
                    break;
            }
        }
        this.fields = array;
    }

    public Field[] placeShip(Field[] array, int length, Ship ship) {
        boolean run = true;
        int index = -1;
        char orientation = 'N';
        while (run) {
            index = randomIndexOnFreeField(array);
            orientation = randomOrientation();
            if (orientation == 'E') {
                if ((index % WIDTH) + (length - 1) < WIDTH) {
                    int i = index;
                    while (array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY && i < (index + (length - 1))) {
                        i++;
                    }
                    if (i == (index + (length - 1)) && array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
                        for (int z = index; z < (index + length); z++) {
                            array[z] = new Field(ship);
                        }
                        run = false;
                    }
                }
                else if ((index % WIDTH) - (length - 1) >= 0) {
                    int i = index;
                    while (array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY && i > (index - (length - 1))) {
                        i--;
                    }
                    if (i == (index - (length - 1)) && array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
                        for (int z = index; z > (index - length); z--) {
                            array[z] = new Field(ship);
                        }
                        run = false;
                    }
                }
            }
            else if (orientation == 'S') {
                if (((index / WIDTH)) + length < (HEIGHT + 1)) {
                    int i = index;
                    while (array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY && i < (index + ((length - 1) * WIDTH))) {
                        i = i + WIDTH;
                    }
                    if (i == (index + ((length - 1) * 15)) && array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
                        for (int z = index; z < (index + (length * WIDTH)); z = z + WIDTH) {
                            array[z] = new Field(ship);
                        }
                        run = false;
                    }
                }
                else if (((index / WIDTH)) - (length - 1) >= 0) {
                    int i = index;
                    while (array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY && i > (index - ((length - 1) * WIDTH))) {
                        i = i - WIDTH;
                    }
                    if (i == (index - ((length - 1) * 15)) && array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
                        for (int z = index; z > (index - (length*WIDTH)); z = z - WIDTH) {
                            array[z] = new Field(ship);
                        }
                        run = false;
                    }
                }
            }
        }
        return array;
    }

    public static int randomIndexOnFreeField(Field[] array) {
        int random = (int) (Math.random() * 150);
        if (array[random].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
            return random;
        }
        else {
            return randomIndexOnFreeField(array);
        }
    }

    public static char randomOrientation() {
        int random = (int) (Math.random() * 2);
        if (random == 0) {
            return 'S';
        }
        else {
            return 'E';
        }
    }

    //example of string 0,C1,C1,C1,C1,C1,0,P0,0 etc. Read left to right, top to bottom
    public Board(String boardarray) {
        Ship placeholder = null;
        Field[] array = new Field[WIDTH*HEIGHT];
        String[] strarray = boardarray.split(ProtocolMessages.AS);
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
                    result = result + ("C" + fields[i].getShip().getIdentifier()) + ProtocolMessages.AS;
                    break;
                case BATTLESHIP:
                    result = result + ("B" + fields[i].getShip().getIdentifier()) + ProtocolMessages.AS;
                    break;
                case DESTROYER:
                    result = result + ("D" + fields[i].getShip().getIdentifier()) + ProtocolMessages.AS;
                    break;
                case SUPERPATROL:
                    result = result + ("S" + fields[i].getShip().getIdentifier()) + ProtocolMessages.AS;
                    break;
                case PATROLBOAT:
                    result = result + ("P" + fields[i].getShip().getIdentifier()) + ProtocolMessages.AS;
                    break;
                default:
                    result = result + 0 + ProtocolMessages.AS;
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

    public static String indexToCoordinates(int index) {
        if (index < 0 || index > 149) {
            return "NO VALID INDEX";
        }
        else {
            String first = String.valueOf(ProtocolMessages.COLUMNS[index % ProtocolMessages.BOARD_DIMENSIONS[1]]);
            String second = String.valueOf((index / ProtocolMessages.BOARD_DIMENSIONS[1]) + 1);
            return first + second;
        }
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
