package view;

public interface ViewDelegate {
    public View getView();
    public void setView(View view);
    public static ViewDelegate sharedInstance = null;

    public void hostGame(int port, String username);
    public void joinGame(String ip, int port, String username);
    public void botGame();
}
