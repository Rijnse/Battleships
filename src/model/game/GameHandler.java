package model.game;

import model.ProtocolMessages;
import model.networking.ClientHandler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameHandler implements Runnable{
    public List<ClientHandler> getPlayers() {
        return players;
    }

    private List<ClientHandler> players;
    private boolean gameOver;
    private int turnCount = 0;

    public GameHandler(List<ClientHandler> players) {
        this.players = players;
    }

    @Override
    public void run() {
        Thread timer = new Thread(new GameTimer());
        timer.start();
        while (!gameOver) {
            for (ClientHandler k : players) {
                k.sendMessage(ProtocolMessages.TURN + ProtocolMessages.CS + players.get(turnCount % 2).getPlayer().getName());
            }
            synchronized (Thread.currentThread()) {
                try {
                    Thread.currentThread().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            turnCount++;
        }
    }

    public class GameTimer implements Runnable{
        @Override
        public void run() {
            Timer timer = new Timer();
            final int[] time = {ProtocolMessages.GAME_TIME};
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!(time[0] < 0)) {
                        for (ClientHandler k : players) {
                            k.sendMessage(ProtocolMessages.TIME + ProtocolMessages.CS + time[0]);
                        }
                        time[0]--;
                    }
                    else {
                        stopTurnTimer(timer);
                    }
                }
            }, 0, 1000);
        }

        public void stopTurnTimer(Timer timer) {
            timer.cancel();
        }
    }

   /* public void checkAttack() {
    if (!(player.getPlayer().getBoard().getField(Integer.parseInt(index)).isHit())) {
        player.getPlayer().getBoard().getField(Integer.parseInt(index)).setHit(true);
        if (player.getPlayer().getBoard().getField(Integer.parseInt(index)).getShip().getType() == ProtocolMessages.Ship.EMPTY) {
            for (ClientHandler k : clientlist) {
                k.sendMessage(ProtocolMessages.MISS + ProtocolMessages.CS + index + ProtocolMessages.CS + player.getPlayer().getName());
            }
        } else {
            for (ClientHandler k : clientlist) {
                k.sendMessage(ProtocolMessages.HIT + ProtocolMessages.CS + index + ProtocolMessages.CS + player.getPlayer().getName());
            }
            boolean destroyed = true;
            Ship hitShip = player.getPlayer().getBoard().getField(Integer.parseInt(index)).getShip();
            for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0] * ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
                Ship testShip = player.getPlayer().getBoard().getField(i).getShip();
                if (testShip.getIdentifier() == hitShip.getIdentifier() || testShip.getType() == hitShip.getType()) {
                    if (!player.getPlayer().getBoard().getField(i).isHit()) {
                        destroyed = false;
                    }
                }
            }
            if (destroyed) {
                int firstIndex = -1;
                for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0] * ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
                    Ship testShip = player.getPlayer().getBoard().getField(i).getShip();
                    if (testShip.getIdentifier() == hitShip.getIdentifier() || testShip.getType() == hitShip.getType()) {
                        firstIndex = i;
                        break;
                    }
                }
                if (player.getPlayer().getBoard().getField(firstIndex + 1).getShip().getType() == hitShip.getType() || player.getPlayer().getBoard().getField(firstIndex + 1).getShip().getIdentifier() == hitShip.getIdentifier()) {
                    for (ClientHandler k : clientlist) {
                        k.sendMessage(ProtocolMessages.DESTROY + ProtocolMessages.CS + hitShip.getType().toString().charAt(0) + ProtocolMessages.CS + firstIndex + ProtocolMessages.CS + "0" + ProtocolMessages.CS + player.getPlayer().getName());
                    }
                }
                else {
                    for (ClientHandler k : clientlist) {
                        k.sendMessage(ProtocolMessages.DESTROY + ProtocolMessages.CS + hitShip.getType().toString().charAt(0) + ProtocolMessages.CS + firstIndex + ProtocolMessages.CS + "1" + ProtocolMessages.CS + player.getPlayer().getName());
                    }
                }
            }
        }
    }
        else {
        player.sendMessage(ProtocolMessages.ERROR + ProtocolMessages.CS + ProtocolMessages.INVALID_INDEX);
    }
}*/
}
