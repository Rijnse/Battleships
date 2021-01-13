package model;

public class ProtocolMessages {
    public static final String TURN = "T";
    public static final String TIME = "TIME";
    public static final String ERROR = "E";
    public static final String MSGRECEIVED = "MSGIN";
    public static final String MISS = "M";
    public static final String HIT = "H";
    public static final String DESTROY = "D"; //follows after hit
    public static final String START = "S";
    public static final String WON = "W";

    public static final String HELLO = "HI";
    public static final String BOARD = "B";
    public static final String ATTACK = "A";
    public static final String MSGSEND = "MSGOUT";

    public static final char[] COLUMNS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'};
    public static final int[] ROWS = {0,1,2,3,4,5,6,7,8,9};

    public enum Ship {
        EMPTY, UNKNOWN, CARRIER, BATTLESHIP, DESTROYER, SUPERPATROL, PATROLBOAT
    }
}
