package controller;
import javafx.application.Platform;
import static model.ProtocolMessages.*;
import model.game.Board;
import model.game.Field;
import model.game.HumanPlayer;
import model.networking.Client;
import model.networking.ComputerClient;
import model.networking.Server;
import view.Game;
import view.Lobby;
import view.Start;
import java.io.IOException;

//ViewController class is responsible for handling communication between the Model (game and networking classes) and the View (JavaFX files and classes)
public class ViewController {

    //JavaFX controller attributes. These are set when the screen is opened in the view. E.g. when the lobby screen is started, the lobby attribute here is set to the Lobby object used.
    private Start startscreen;
    private Lobby lobby;
    private Game game;

    //Used to check if Game screen in view is initialized. Prevents NullPointerExceptions when loading in game screen and updating information on the screen.
    public boolean gameIni = false;

    //Used to check whether it is necessary to send EXIT message to server (e.g. if on "start", there is no connection and thus it is not necessary)
    public String currentScreen;

    private Client client;

    /**
     * @ensures that a ViewController object is created. Private because of the singleton pattern
     */
    private ViewController() {
        currentScreen = "start";
    }

    //Single instance of the ViewController
    private static ViewController sharedInstance;

    /**
     * @ensures that single ViewController object is returned. If it does not exist yet, it will be created.
     * @requires that the constructor of ViewController is private to prevent other classes from creating multiple ViewControllers (singleton pattern)
     * @return a ViewController object
     */
    public synchronized static ViewController getInstance() {
        if(sharedInstance==null) {
            sharedInstance = new ViewController();
            return sharedInstance;
        }
        return sharedInstance;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    public void setGameView(Game game) {
        this.game = game;
    }

    public void setStartScreen(Start start) {
        this.startscreen = start;
    }

    public Client getClient() {
        return client;
    }

    /**
     * @ensures that server and player information is updated on Lobby screen in the view
     * @requires valid non-null Strings and int
     * @param ip is the ip of the server
     * @param portint is the port of the serversocket
     * @param onename is the name of the first player in the game
     * @param twoname is the name of the second player in the game
     */
    public void updateLobbyInfo(String ip, int portint, String onename, String twoname) {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                lobby.updateLobbyInfo(ip, portint, onename, twoname);
            }
        });
    }

    /**
     * @ensures that player field in Game View is updated with provided Board object
     * @requires valid Board object
     * @param board is the Board to use
     */
    public void updateOwnField(Board board) {
        Field[] array = board.getFieldsArray();
        for (int i = 0; i < BOARD_DIMENSIONS[0] * BOARD_DIMENSIONS[1]; i++) {
            game.updateOwnField(array[i], i);
        }
    }

    /**
     * @ensures that enemy field in Game View is updated with provided Board object
     * @requires valid Board object
     * @param board is the Board to use
     */
    public void updateEnemyField(Board board) {
        Field[] array = board.getFieldsArray();
        for (int i = 0; i < BOARD_DIMENSIONS[0] * BOARD_DIMENSIONS[1]; i++) {
            game.updateEnemyField(array[i], i);
        }
    }

    /**
     * @ensures that names of players are updated in Game View
     * @param one != null
     * @param two != null
     */
    public void updateNames(String one, String two) {
        this.game.updatePlayerNames(one, two);
    }

    /**
     * @ensures that scores of players are updated in Game View
     * @param one != null
     * @param two != null
     */
    public void updateScores(int one, int two) {
        this.game.updatePlayerScores(one, two);
    }

    /**
     * @ensures that remaining game time is updated in Game View
     * @requires valid int
     * @param seconds is the amount of seconds remaining for the game to end
     */
    public void updateGameTime(int seconds) {
        this.game.updateGeneralTime(seconds);
    }

    /**
     * @ensures that game is hosted (meaning Server and Client thread are created and linked up)
     * @param ip is the ip address to host the server on
     * @param port is the port to host the server on
     * @param username is the name which the host wants to use in-game
     * @throws IOException is thrown if the method does not succeed at creating the Server/Client thread, probably meaning that an invalid ip/port was provided
     */
    public void hostGame(String ip, int port, String username) throws IOException {
            Server server = new Server(ip, port);
            Thread srv = new Thread(server);
            srv.start();

            startscreen.switchToLobby();

            Client client = new Client(ip, String.valueOf(port), new HumanPlayer(username));
            this.client = client;
            Thread clientthread = new Thread(client);
            clientthread.start();
    }

    /**
     * @ensures that game is joined by creating a Client object with provided arguments
     * @param ip is the ip address of the server the client wishes to join
     * @param port is the port of the server the client wishes to join
     * @param username is the name which the user wants to use in-game
     * @throws IOException if the method can't properly connect to the server, meaning that provided arguments are probably invalid
     */
    public void joinGame(String ip, String port, String username) throws IOException {
        Client client = new Client(ip, port, new HumanPlayer(username));
        this.client = client;

        startscreen.switchToLobby();

        Thread clientthread = new Thread(this.client);
        clientthread.start();
    }

    /**
     * @ensures that singleplayer game against AI is hosted. Server and Client info is predefined since no other players need to join...
     * @param username is the name of the player
     * @throws IOException if the method is not able to create Server or Clients
     */
    public void botGame(String username) throws IOException {
        Server server = new Server("localhost", 1337);
        Thread srv = new Thread(server);
        srv.start();

        startscreen.switchToLobby();

        Client client = new Client("localhost", String.valueOf(1337), new HumanPlayer(username));
        this.client = client;
        Thread clientthread = new Thread(client);
        clientthread.start();

        ComputerClient bot = new ComputerClient("localhost", String.valueOf(1337));
        Thread botthread = new Thread(bot);
        botthread.start();
    }

    /**
     * @ensures that EXIT message is sent to server and Client object is ordered to close down. Used when clicking 'Leave game' in lobby.
     */
    public void leaveGame() {
        client.sendMessage(EXIT + CS + client.getPlayer().getName());
        client.exit();
    }

    /**
     * @ensures that START command is sent to server
     */
    public void pressStartButton() {
        client.sendMessage(START);
    }


    /**
     * @ensures that ERRORS received by server are properly handled and displayed for the user. Most of these errors are unable to occur in our implementation of the clientside.
     * @requires the provided String to be a proper ERROR
     * @param error is the String version of the ERROR as defined in the protocol
     */
    public void errorHandling(String error) {
        switch (error) {
            case ACTION_NOT_PERMITTED:
                lobby.showPopUp("ERROR!", "Only first player (host) may start game!");
                break;
            case DUPLICATE_NAME:
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            try {
                                lobby.switchToStart();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            client.exit();
                            startscreen.showPopUp("ERROR!", "This server already has a player with that name! Try again please...");
                        }
                    });
                break;
            case GAME_OVER:
                game.showPopUp("ERROR!", "The game has ended, so you can't do that right now!", 3);
                break;
            case ILLEGAL_COMMAND:
                game.showPopUp("ERROR!", "That is an illegal command! Try again...", 3);
                break;
            case ILLEGAL_SHIP_PLACEMENT:
                game.showPopUp("ERROR!", "You can't place your ships like that!", 3);
                break;
            case INVALID_INDEX:
                game.showPopUp("ERROR!", "That is not a valid move (either because it's already been hit or is not on the board)", 3);
                break;
            case OUT_OF_TURN:
                game.showPopUp("ERROR!", "It is not your turn!", 3);
                break;
            case TIME_OVER:
                game.showPopUp("ERROR!", "You ran out of time!", 3);
                break;
        }
    }

    /**
     * @ensures that View is switched from start screen to lobby screen. This is a separate method since name first needs to be verified by server before allowing Client in lobby.
     * @throws IOException if the method is unable to switch to Lobby screen, which is a critical error regarding the FXML files. Should not occur in practice.
     */
    public void startGame() throws IOException {
        Platform.runLater(new Runnable() {
            @Override public void run() {
                try {
                    lobby.switchToGameScreen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @ensures that the turn of the player is started (player clock is started and player may enter move in textfield)
     */
    public void startTurn() {
        game.startTurnTimer();
    }

    /**
     * @ensures that move of player is sent to Client and forwarded to server
     * @param coordinates are the coordinates of the Field the Player wishes to shoot at
     */
    public void sendMove(String coordinates) {
        this.client.sendMessage(ATTACK + CS + Board.index(coordinates));
    }

    /**
     * @ensures that popup is shown on Game View with provided arguments. Separate method for ease of use in this class.
     * @param title is the title of the popup
     * @param desc is the description of the popup
     * @param popuptime is the amount of time for which the popup should remain on the screen of the player
     */
    public void showPopUp(String title, String desc, int popuptime) {
        game.showPopUp(title, desc, popuptime);
    }
}
