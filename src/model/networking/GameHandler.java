package model.networking;

import model.ProtocolMessages;
import model.game.Board;
import model.game.Field;
import model.game.Ship;
import model.networking.ClientHandler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static model.ProtocolMessages.*;

public class GameHandler implements Runnable{

    private List<ClientHandler> players;
    private boolean gameOver;
    private int turnCount = 0;

    public boolean turnFound;
    private boolean hitAgain;
    public int turnIndex;

    public GameHandler(List<ClientHandler> players) {
        this.players = players;
        hitAgain = true;
        turnFound = false;
    }

    public List<ClientHandler> getPlayers() {
        return players;
    }

    @Override
    public void run() {
        ClientHandler name;
        Thread timer = new Thread(new GameTimer());
        timer.start();
        while (!gameOver) {
            name = players.get(turnCount % 2);
            while (hitAgain) {
                for (ClientHandler k : players) {
                    k.sendMessage(ProtocolMessages.TURN + CS + name.getPlayer().getName());
                }
                hitAgain = false;
                int i = 30;
                while (i > 0 && !turnFound) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }

                if (turnFound) {
                    if (turnIndex < 0 || turnIndex > 149) {
                        name.sendMessage(ERROR + CS + INVALID_INDEX);
                    }
                    else {
                        attackHandler(name, turnIndex);
                    }
                } else {
                    name.sendMessage(ProtocolMessages.ERROR + CS + ProtocolMessages.TIME_OVER);
                }

                turnFound = false;
                turnIndex = -1;
            }

            //check if game over
            hitAgain = true;
            turnCount++;
        }
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
                            k.sendMessage(ProtocolMessages.TIME + CS + time[0]);
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
                k.sendMessage(ProtocolMessages.MISS + CS + index + CS + enemy.getPlayer().getName());
            }
            opponent.getField(index).setHit(true);
        }
        else {
            for (ClientHandler k : players) {
                k.sendMessage(ProtocolMessages.HIT + CS + index + CS + enemy.getPlayer().getName());
            }
            opponent.getField(index).setHit(true);
            handler.getPlayer().incrementScore(1);
            this.hitAgain = true;
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

           int orientation;
           if (opponent.getField(firstSquare + 1).getShip().toString().equals(ship.toString())) {
               orientation = 0;
           }
           else {
               orientation = 1;
           }

           for (ClientHandler k : players) {
               k.sendMessage(ProtocolMessages.DESTROY + CS + ship.getType().toString().charAt(0) + CS + firstSquare + CS + orientation + CS + enemy.getPlayer().getName());
           }
           return true;
       }
       else {
          return false;
       }
   }

   public ClientHandler checkShipsSunk() {
        for (ClientHandler k : players) {
            int count = 0;
            Board playerBoard = k.getPlayer().getBoard();
            for (Field f : playerBoard.getFieldsArray()) {
                if (f.getShip().getType() != ProtocolMessages.Ship.EMPTY && !f.isHit()) {
                    count++;
                }
            }
            if (count == 0) {
                return k;
            }
        }
        return null;
   }

}
