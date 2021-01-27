package model.game;
import model.ProtocolMessages;

public class Board {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 10;
    public static final String COLUMNS = "ABCDEFGHIJKLMNO";

    private Field[] fields;

    //All of the ships to be placed on the board are predefined. This way, we don't have to make 'duplicate' Ships with the same ID and Type
    private final Ship[] SHIPS = {
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

    /**
     * @ensures the creation of a board object with randomly placed ships, both vertical and horizontal.
     * @requires the placeShip() method to be functional according to documentation for working correctly
     */
    public Board() {
        Field[] array = new Field[HEIGHT*WIDTH];
        // fill the board with empty fields
        for (int i = 0; i < HEIGHT*WIDTH; i ++) {
            array[i] = new Field();
        }
        // fill the board with ships
        for (Ship s : SHIPS) {
            array = placeShip(array, s);
        }
        this.fields = array;
    }

    /**
     * @ensures that the ship included in the parameters is placed on the array in a proper way according to game regulations
     * @requires a proper field array (Meaning size should be WIDTH*HEIGHT) and a ship object as mentioned in the SHIPS array defined in the Board class
     * @param array is the fields array of the board created using the method (see constructor above)
     * @param ship is the ship object which needs to be placed in the array
     * @return the array in the parameter including the newly placed ship.
     */
    public Field[] placeShip(Field[] array, Ship ship) {
        //Initial & placeholder values
        int length = ship.getLength();
        boolean run = true;
        int index = -1;
        char orientation = 'N';


        while (run) {
            index = randomIndexOnFreeField(array);
            orientation = randomOrientation();
            if (orientation == 'E') {
                if ((index % WIDTH) + (length - 1) < WIDTH) {
                    int i = index;
                    // look if there are enough empty places to the right of the index.
                    while (array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY && i < (index + (length - 1))) {
                        i++;
                    }
                    // if there are enough empty places, place a ship beginning at index i and continuing to the right.
                    if (i == (index + (length - 1)) && array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
                        for (int z = index; z < (index + length); z++) {
                            array[z] = new Field(ship);
                        }
                        run = false;
                    }
                }
                else if ((index % WIDTH) - (length - 1) >= 0) {
                    int i = index;
                    // look if there are enough empty places to the left of the index
                    while (array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY && i > (index - (length - 1))) {
                        i--;
                    }
                    // if there are enough empty places, place a ship beginning at index i and continuing to the left.
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
                    // look if there are enough empty places on the bottom of the index.
                    while (array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY && i < (index + ((length - 1) * WIDTH))) {
                        i = i + WIDTH;
                    }
                    // if there are enough empty places, place a ship beginning at index i and continuing to the bottom.
                    if (i == (index + ((length - 1) * WIDTH)) && array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
                        for (int z = index; z < (index + (length * WIDTH)); z = z + WIDTH) {
                            array[z] = new Field(ship);
                        }
                        run = false;
                    }
                }
                else if (((index / WIDTH)) - (length - 1) >= 0) {
                    int i = index;
                    // look if there are enough empty places on the top of the index
                    while (array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY && i > (index - ((length - 1) * WIDTH))) {
                        i = i - WIDTH;
                    }
                    // if there are enough empty places, place a ship beginning at index i and continuing to the top.
                    if (i == (index - ((length - 1) * WIDTH)) && array[i].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
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

    /**
     * @ensures the returning of an index which is still free on the specified array (meaning that the type of the field should be ProtocolMessages.EMPTY)
     * @requires a proper field array (Meaning size should be WIDTH*HEIGHT)
     * @param array to be taken into account when looking for a free field
     * @return int value between 0 and 149
     */
    public static int randomIndexOnFreeField(Field[] array) {
        int random = (int) (Math.random() * (WIDTH*HEIGHT));
        if (array[random].getShip().getType() == ProtocolMessages.Ship.EMPTY) {
            return random;
        }
        else {
            return randomIndexOnFreeField(array);
        }
    }

    /**
     * @ensures that an orientation is returned
     * @return character 'S' or 'E' (50/50 chance)
     */
    public static char randomOrientation() {
        int random = (int) (Math.random() * 2);
        if (random == 0) {
            return 'S';
        }
        else {
            return 'E';
        }
    }

    /**
     * @ensures that a board object is created with a filled fields array
     * @requires a proper string with fields as specified in the protocol
     * @param boardarray is a string containing a full board array, as specified in the protocol (example of string 0,C1,C1,C1,C1,C1,0,P0,0 etc. Read left to right, top to bottom)
     */
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

    /**
     * @ensures that a board object is created with given fields array
     * @requires a valid field array as specified under @param
     * @param array is a Field[] array with size WIDTH*HEIGHT
     */
    public Board(Field[] array) {
        this.fields = array;
    }

    /**
     * @ensures (see @return)
     * @requires that the board object has a valid fields array of size WIDTH*HEIGHT
     * @return a string representing the board object fields array as specified in the protocol
     */
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

    /**
     * @return the fields array of the board object
     */
    public Field[] getFieldsArray() {
        return fields;
    }

    /**
     * @ensures that field is returned on given index on board
     * @param i is the index of requested field
     * @return Field object on index
     */
    public Field getField(int i) {
        return this.fields[i];
    }

    /**
     * @requires valid input String object ("A6" for example is valid, "R16" is not)
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

    /**
     * @ensures that valid coordinates are returned according to specification
     * @requires a valid int
     * @param index int between 0 and 149
     * @return a coordinate string according to game regulations: (A-O)+(1-10)
     */
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

    /**
     * @ensures that the board object it's fields array is validated to the specification of the game rules
     * @requires a valid fields array (size WIDTH*HEIGHT)
     * @return a boolean (true/false) regarding the validity of the board
     */
    public boolean checkValidBoard () {
        int correctShipCount = 0;
        for (Ship s : SHIPS) {
            for (int i = 0; i < (WIDTH * HEIGHT) -1; i ++){
                // When the ship is find in the board.
                if (getField(i).getShip().equals(s)) {
                    int k = i;
                    int p = i;
                    // look if the ship is placed in horizontal position.
                    while (getField(k).getShip().equals(s) && k < (WIDTH * HEIGHT) - 1) {
                        k ++;
                    }
                    // look if the ship is placed in vertical position.
                    while (getField(p).getShip().equals(s) && p < (WIDTH * (HEIGHT - 1))) {
                        p = p + WIDTH;
                    }
                    System.out.println("I " + i);
                    System.out.println("K " + k);
                    System.out.println("P " + p);
                    System.out.println("Length " + s.getLength());
                    // if the ship is placed in horizontal or vertical position, add one to correctShipCount and break.
                    if ((k - i) == s.getLength() || ((p - i) / WIDTH) == s.getLength()) {
                        correctShipCount ++;
                        break;
                    }
                    // if the ship is at index 149 add one to correctShipCount and break.
                    else if (k == (WIDTH * HEIGHT) - 1) {
                        if (getField(k).getShip().equals(s)) {
                            correctShipCount ++;
                            break;
                        }
                    }
                    // if the the ship is at the bottom row, add one to correctShipCount and break.
                    else if (p >= (WIDTH * (HEIGHT - 1))) {
                        if (getField(p).getShip().equals(s)){
                          correctShipCount ++;
                          break;
                        }
                    }
                    else {
                        return false;
                    }
                }
            }
        }
        // if the correctShipCount is equal to the number of ships, every ship is correctly placed so the board is valid.
        if (correctShipCount == SHIPS.length) {
            return true;
        }
        else {
            return false;
        }
    }
}