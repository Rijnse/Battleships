package model.networking;

import model.ProtocolMessages;
import model.game.Board;
import model.game.Ship;
import model.networking.ClientHandler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameHandler implements Runnable{

    private List<ClientHandler> players;
    private boolean gameOver;
    private int turnCount = 0;

    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();


    public GameHandler(List<ClientHandler> players) {
        this.players = players;
    }

    public List<ClientHandler> getPlayers() {
        return players;
    }


    @Override
    public void run() {
        ClientHandler name;
        Thread timer = new Thread(new GameTimer());
        timer.start();
        for (ClientHandler k : players) {
            k.sendMessage(ProtocolMessages.DESTROY + ProtocolMessages.CS + "C" + ProtocolMessages.CS + "5" + ProtocolMessages.CS + "0" + ProtocolMessages.CS + "Rinse");
        }
       /* while (!gameOver) {
            name = players.get(turnCount % 2);
            for (ClientHandler k : players) {
                k.sendMessage(ProtocolMessages.TURN + ProtocolMessages.CS + name.getPlayer().getName());
            }

            //wait for turn input of player

            //attackHandler(name, );



            turnCount++;
        }*/
    }

    public class GameTimer implements Runnable{
        private Timer timer = new Timer();
        @Override
        public void run() {
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

   public void attackHandler(ClientHandler handler, int index) {
        Board opponent;
        ClientHandler enemy;
        if (handler.getPlayer().getName().equals(players.get(0).getPlayer().getName())) {
            opponent = players.get(1).getPlayer().getBoard();
            enemy = players.get(1);
        }
        else {
            opponent = players.get(0).getPlayer().getBoard();
            enemy = players.get(0);
        }

        if (opponent.getField(index).getShip().getType() == ProtocolMessages.Ship.EMPTY || opponent.getField(index).isHit()) {
            for (ClientHandler k : players) {
                k.sendMessage(ProtocolMessages.MISS + ProtocolMessages.CS + index + ProtocolMessages.CS + enemy.getPlayer().getName());
            }
        }
        else {
            for (ClientHandler k : players) {
                k.sendMessage(ProtocolMessages.HIT + ProtocolMessages.CS + index + ProtocolMessages.CS + enemy.getPlayer().getName());
            }
            handler.getPlayer().incrementScore(1);
            if (destroyHandler(index, opponent, enemy)) {
                handler.getPlayer().incrementScore(1);
            }
        }
   }

   public boolean destroyHandler(int index, Board opponent, ClientHandler enemy) {
       Ship ship = opponent.getField(index).getShip();
       int count = 0;
       for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0]*ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
           if (opponent.getField(i).getShip().toString().equals(ship.toString()) && opponent.getField(i).isHit()) {
               count++;
           }
       }

       if (count == ship.getLength()) {
           ship.setSunk(true);

           int firstSquare = -1;
           for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0]*ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
               if (opponent.getField(i).getShip().toString().equals(ship.toString())) {
                   firstSquare = i;
                   break;
               }
           }

           String orientation = "";
           if (opponent.getField(firstSquare + 1).getShip().toString().equals(ship.toString())) {
               orientation = "0";
           }
           else {
               orientation = "1";
           }

           for (ClientHandler k : players) {
               k.sendMessage(ProtocolMessages.DESTROY + ProtocolMessages.CS + ship.getType().toString().charAt(0) + ProtocolMessages.CS + firstSquare + ProtocolMessages.CS + orientation + ProtocolMessages.CS + enemy.getPlayer().getName());
           }
           return true;
       }
       else {
          return false;
       }
   }
}
