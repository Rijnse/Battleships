package controller;
import view.View;
import view.ViewDelegate;

public class ViewController implements ViewDelegate {

    protected View view;
    public static ViewDelegate sharedInstance = new ViewController();

    private ViewController() {

    }

    @Override
    public View getView() {
        return this.view;
    }

    @Override
    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void hostGame(int port, String username) {

    }

    @Override
    public void joinGame(String ip, int port, String username) {

    }

    @Override
    public void botGame() {

    }
}
