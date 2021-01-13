package model.game;

public class ComputerPlayer extends Player {
    private String name;

    public ComputerPlayer() {
        super();
        this.name = "AI player";
    }

    public String getName() {
        return this.name;
    }
}
