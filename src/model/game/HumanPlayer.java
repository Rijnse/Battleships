package model.game;

public class HumanPlayer extends Player{
    private String name;

    public HumanPlayer(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int determineMove(Board board) {
        String command = "";
        return board.index(command);
    }
}
