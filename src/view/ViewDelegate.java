package view;

import controller.ViewController;

public interface ViewDelegate {
    public static ViewDelegate sharedInstance = null;

    public void setLobby(Lobby lobby);

    public void hostGame(int port, String username);
    public void joinGame(String ip, int port, String username);
    public void botGame(String username);

    public void sendMove(String coordinates);

    public void testMethod();
}
