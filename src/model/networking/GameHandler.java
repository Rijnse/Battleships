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
    private int turnCount = 0;

    private static final int[] scoreSheet = {2,4,6,8,10};

    private int tieScorePlayerOne;
    private int tieScorePlayerTwo;


    public boolean turnFound;
    private boolean hitAgain;
    public int turnIndex;

    public GameHandler(List<ClientHandler> players) {
        this.players = players;
        hitAgain = true;
        turnFound = false;
        tieScorePlayerOne = 0;
        tieScorePlayerTwo = 0;
    }

    public List<ClientHandler> getPlayers() {
        return players;
    }

    @Override
    public void run() {
        ClientHandler name;
        Thread timer = new Thread(new GameTimer());
        timer.start();
        while (gameOver(timer) == null) {
            name = players.get(turnCount % 2);
            while (hitAgain && timer.isAlive()) {
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
            hitAgain = true;
            turnCount++;
        }

        ClientHandler[] array = gameOver(timer);
        for (ClientHandler k : players) {
            if (array.length > 1) {
                k.sendMessage(WON + CS + array[0].getPlayer().getName()+ CS + array[1].getPlayer().getName());
            }
            else {
                k.sendMessage(WON + CS + array[0].getPlayer().getName());
            }
        }
    }

    public class GameTimer implements Runnable{
        private boolean stopClock = false;
        private boolean isActive = true;

        public boolean isActive() {
            return isActive;
        }

        @Override
        public void run() {
            int time = GAME_TIME;
            while (time > 0 && !stopClock) {
                try {
                    for (ClientHandler k : getPlayers()) {
                        k.sendMessage(ProtocolMessages.TIME + CS + time);
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time--;
            }
            isActive = false;
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

            int z = 2;
            while (z > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                z--;
            }

            if (destroyHandler(index, opponent, enemy)) {
                handler.getPlayer().incrementScore(1);
               /* if (handler.getPlayer().getName().equals(getPlayers().get(0).getPlayer().getName())) {
                    tieScorePlayerOne = tieScorePlayerOne + (1 / (opponent.getField(index).getShip().getLength() / 5));
                }
                else {
                    tieScorePlayerTwo = tieScorePlayerTwo + (1 / (opponent.getField(index).getShip().getLength() / 5));
                }*/
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

   private ClientHandler checkShipsSunk() {
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

   private ClientHandler[] gameOver(Thread timer) {
        if (checkShipsSunk() != null) {
            return new ClientHandler[] {checkShipsSunk()};
        }
        else if (!timer.isAlive()) {
            ClientHandler winner = null;
            if (getPlayers().get(0).getPlayer().getScore() != getPlayers().get(1).getPlayer().getScore()) {
                for (ClientHandler k : getPlayers()) {
                    if (winner != null) {
                        if (k.getPlayer().getScore() > winner.getPlayer().getScore()) {
                            winner = k;
                        }
                    }
                    else {
                        winner = k;
                    }
                }
                return new ClientHandler[]{winner};
            }
            else {
                if (tieScorePlayerOne != tieScorePlayerTwo) {
                    if (tieScorePlayerOne > tieScorePlayerTwo) {
                        return new ClientHandler[]{getPlayers().get(0)};
                    }
                    else {
                        return new ClientHandler[]{getPlayers().get(1)};
                    }
                }
                else {
                    return new ClientHandler[]{getPlayers().get(0), getPlayers().get(1)};
                }
            }
        }
        else {
            return null;
        }
   }
}
