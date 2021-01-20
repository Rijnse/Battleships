package controller;
import model.game.HumanPlayer;
import model.networking.Server;
import view.Lobby;
import view.ViewDelegate;

public class ViewController implements ViewDelegate {
    private static ViewController sharedInstance;
    private Lobby lobby;

    private ViewController() {

    }

    public synchronized static ViewController getInstance() {
        if(sharedInstance==null) {
            sharedInstance = new ViewController();
            return sharedInstance;
        }
        return sharedInstance;
    }

    @Override
    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public void hostGame(int port, String username) {

        Server server = new Server(port);
        server.setup(port);

        Thread srv = new Thread(server);
        HumanPlayer playerOne = new HumanPlayer(username);

    }

    @Override
    public void joinGame(String ip, int port, String username) {

    }

    @Override
    public void botGame(String username) {

    }

    @Override
    public void sendMove(String coordinates) {

    }

    @Override
    public void testMethod() {
        lobby.updateLobbyInfo("Test", 123, "test", "whatever");
    }
}
