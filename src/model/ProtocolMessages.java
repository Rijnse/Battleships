package model;

public class ProtocolMessages {

	//--------------General Implementation--------------//
	/** Dimensions of a board supported by this protocol. */
	public static final int[] BOARD_DIMENSIONS = {10, 15};
	
	/** Command Separator: Used to separate values of a command sent to the server. */
	public static final String CS = " ";

	/** Array Separator: Used to separate values of a Board in a String. */
	public static final String AS = ",";

	/** The time allowed per player turn in seconds. */
	public static final int TURN_TIME = 30;
	
	/** The time per game in seconds. */
	public static final int GAME_TIME = 300;

	/** The interval in seconds for the server to send the TIME command. */
	public static final int END_TIME_UPDATE = 1;

	/** The maximum length of a message sent to the general chat. */
	public static final int MAX_MESSAGE_LENGTH = 50;

	/** The String representation of a Carrier type of Boat */
	public static final String CARRIER = "C";

	/** The String representation of a Battleship type of Boat */
	public static final String BATTLESHIP = "B";

	/** The String representation of a Destroyer type of Boat */
	public static final String DESTROYER = "D";

	/** The String representation of a Super Patrol type of Boat */
	public static final String SUPERPATROL = "S";

	/** The String representation of a Patrol type of Boat */
	public static final String PATROL = "P";
	
	/** Array of valid column names. */
	public static final char[] COLUMNS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O'};
	
	/** Array of valid row numbers. */
	public static final int[] ROWS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

	/** Array of valid error names. */
	public static final String[] ERRORNAMES = {"ActionNotPermitted", "DuplicateName", "GameOver", "IllegalCommand", 
	"IllegalShipPlacement", "InvalidIndex", "OutOfTurn", "TimeOver"};
	//--------------------------------------------------//

	
	//-----------------Joining A Server-----------------//
	/** The first message sent by the client to the server to initiate the connection. */
	public static final String HELLO = "HI";
	//--------------------------------------------------//

	
	//-----------------Starting A Game------------------//
	/** The message sent by the lobby leader to the server to start the game (leader corresponds to Client1). */
	public static final String START = "S";

	/** Message sent by the client to submit a board object to the server. */
	public static final String BOARD = "B";
	//--------------------------------------------------//


	//-----------------Playing The Game-----------------//
	/** The server sends this message every {@link END_TIME_UPDATE} seconds to all connected clients. */
	public static final String TIME = "TIME";

	/** The server sends this message to all connected players, indicating whose turn it is. */
	public static final String TURN = "T";

	/** The client sends this message to launch a missle at a given index. */
	public static final String ATTACK = "A";

	/** The server response to an attack made by the client to an empty field on the board of the opponent. */
	public static final String MISS = "M";

	/** The server response to an attack made by the client to a field containing a ship on the board of the opponent. */
	public static final String HIT = "H";
	
	/** The server response to an attack made by the client to a field containing the last piece of a ship on the board of the opponent. */
	public static final String DESTROY = "D";
	
	/** The server sends this message to all players after a winner is identified, or after the game time is over. */
	public static final String WON = "W";
	//--------------------------------------------------//


	//----------------Chat Functionality----------------//
	/** A message is sent by a client to the server using this command. */
	public static final String MSGSEND = "MSGOUT";

	/** A message is broadcasted by the server to all clients using this command. */
	public static final String MSGRECEIVED = "MSGIN";
	//--------------------------------------------------//


	//----------------------Errors----------------------//
	/** When a player causes an error to occur in the server, this command is used. */
	public static final String ERROR = "E";
	
	//Please check the protocol descriptor for specifications on every error, and when it should occur
	public static final String ACTION_NOT_PERMITTED = "ActionNotPermitted";
	public static final String DUPLICATE_NAME = "DuplicateName";
	public static final String GAME_OVER = "GameOver";
	public static final String ILLEGAL_COMMAND = "IllegalCommand";
	public static final String ILLEGAL_SHIP_PLACEMENT = "IllegalShipPlacement";
	public static final String INVALID_INDEX = "InvalidIndex";
	public static final String OUT_OF_TURN = "OutOfTurn";
	public static final String TIME_OVER = "TimeOver";
	//--------------------------------------------------//

	public enum Ship {
		EMPTY, UNKNOWN, CARRIER, BATTLESHIP, DESTROYER, SUPERPATROL, PATROLBOAT
	}
}
