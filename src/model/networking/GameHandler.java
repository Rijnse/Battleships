package model.networking;

import model.ProtocolMessages;
import model.game.Board;
import model.game.Field;
import model.game.Ship;
import java.util.List;

import static model.ProtocolMessages.*;

public class GameHandler implements Runnable{

    //List of players in this game
    private List<ClientHandler> players;

    //tieScores (only useful when there is a tie in normal score, see game rules)
    private int tieScorePlayerOne;
    private int tieScorePlayerTwo;

    private int turnCount = 0;
    private boolean playerLeft;
    public boolean turnFound;
    private boolean hitAgain;
    public int turnIndex;

    /**
     * @ensures that new GameHandler object is created with the players list provided
     * @requires ClientHandlers in players list to be fully functional
     * @param players is the list of players in the game
     */
    public GameHandler(List<ClientHandler> players) {
        this.players = players;
        hitAgain = true;
        turnFound = false;
        playerLeft = false;
        tieScorePlayerOne = 0;
        tieScorePlayerTwo = 0;
    }

    public List<ClientHandler> getPlayers() {
        return players;
    }

    /**
     * @ensures that game is started by handing turn to first player and then looping till winner is found or clock runs out
     */
    @Override
    public void run() {
        ClientHandler name;

        //Game timer is started on different thread
        Thread timer = new Thread(new GameTimer());
        timer.start();

        //As long as there is no winner, time hasn't run out or all players are still in the game, the while loop continues
        while (gameOver(timer) == null && !playerLeft) {
            //ClientHandler whose turn it is is selected
            name = players.get(turnCount % 2);

            //if the player HITs, he is allowed another turn. That is what this while loop is for
            while (hitAgain && timer.isAlive()) {
                //Send TURN message to all players
                for (ClientHandler k : players) {
                    k.sendMessage(ProtocolMessages.TURN + CS + name.getPlayer().getName());
                }
                hitAgain = false;

                //Starts turn timer of 30 seconds, ends after 30 seconds or when ATTACK is sent in
                int i = 30;
                while (i > 0 && !turnFound) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i--;
                }

                //If no turn is sent in, ERROR TimeOver is sent to player. Else, attackHandler method is called
                if (turnFound && turnIndex != -1) {
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

            //Turn count is increased so next player may make a move
            turnCount++;
        }

        //After game ends (while loop ends) the winner is calculated and message is sent to players
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

    //new class for GameTimer, making it possible to run it on a separate thread
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

    /**
     * @ensures that ATTACK command is handled by the GameHandler
     * @param handler is the player sending in the attack
     * @param index is the index at which the player is shooting
     */
   public void attackHandler(ClientHandler handler, int index) {
        Board opponent;
        ClientHandler enemy;

        //Finds opponent of player (so the person's board who is shot at)
        if (handler.getPlayer().getName().equals(players.get(0).getPlayer().getName())) {
            opponent = players.get(1).getPlayer().getBoard();
            enemy = players.get(1);
        }
        else {
            opponent = players.get(0).getPlayer().getBoard();
            enemy = players.get(0);
        }

        //If the Field is empty and has not been shot at, a MISS message is sent to all players
        if (opponent.getField(index).getShip().getType() == ProtocolMessages.Ship.EMPTY || opponent.getField(index).isHit()) {
            for (ClientHandler k : players) {
                k.sendMessage(ProtocolMessages.MISS + CS + index + CS + enemy.getPlayer().getName());
            }
            opponent.getField(index).setHit(true);
        }
        else {
            //Else, a HIT message is sent and score of player is incremented
            for (ClientHandler k : players) {
                k.sendMessage(ProtocolMessages.HIT + CS + index + CS + enemy.getPlayer().getName());
            }
            opponent.getField(index).setHit(true);
            handler.getPlayer().incrementScore(1);
            this.hitAgain = true;

            //Wait for two seconds to check if ship hit is also destroyed
            int z = 2;
            while (z > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                z--;
            }

            //Calls for destroyHandler and changes tieScore accordingly. The smaller the ship, the more tieScore is added, as is specified in the game rules.
            if (destroyHandler(index, opponent, enemy)) {
                handler.getPlayer().incrementScore(1);
                if (handler.getPlayer().getName().equals(getPlayers().get(0).getPlayer().getName())) {
                    tieScorePlayerOne = tieScorePlayerOne + (int) (1 / (double) (opponent.getField(index).getShip().getLength() / 5));
                }
                else {
                    tieScorePlayerTwo = tieScorePlayerTwo + (int) (1 / (double) (opponent.getField(index).getShip().getLength() / 5));
                }
            }
        }
   }

    /**
     * @ensures that a check is made if the ship HIT is also DESTROYED
     * @param index is the index of the field shot at
     * @param opponent is the board of the opponent
     * @param enemy is the ClientHandler object of the opponent
     * @return true if destroyed, false if no ship is destroyed
     */
   public boolean destroyHandler(int index, Board opponent, ClientHandler enemy) {
       //Check if ship is sunk by counting squares hit and comparing them to ship length
       Ship ship = opponent.getField(index).getShip();
       int count = 0;
       for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0]*ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
           if (opponent.getField(i).getShip().toString().equals(ship.toString()) && opponent.getField(i).isHit()) {
               count++;
           }
       }

       if (count == ship.getLength()) {
           int firstSquare = -1;

           //Checks for first occurrence of ship on board
           for (int i = 0; i < ProtocolMessages.BOARD_DIMENSIONS[0]*ProtocolMessages.BOARD_DIMENSIONS[1]; i++) {
               if (opponent.getField(i).getShip().toString().equals(ship.toString())) {
                   firstSquare = i;
                   break;
               }
           }

           //Checks the orientation of the ship on the board
           int orientation;
           if (opponent.getField(firstSquare + 1).getShip().toString().equals(ship.toString())) {
               orientation = 0;
           }
           else {
               orientation = 1;
           }

           //Sends DESTROY message to all players in game according to protocol
           for (ClientHandler k : players) {
               k.sendMessage(ProtocolMessages.DESTROY + CS + ship.getType().toString().charAt(0) + CS + firstSquare + CS + orientation + CS + enemy.getPlayer().getName());
           }
           return true;
       }
       else {
          return false;
       }
   }

    /**
     * @ensures that player leaving is handled. Player left in game is announced the winner
     * @param handler is the ClientHandler object of the leaving player
     */
    public void playerLeft(ClientHandler handler) {
        playerLeft = true;
        getPlayers().remove(handler);
        getPlayers().get(0).sendMessage(WON + CS + getPlayers().get(0).getPlayer().getName());
    }

    /**
     * @ensures checks if all ships of a player in the game are sunk
     * @return ClientHandler object of player whose ships have been sunk, null if no players have lost all of their ships
     */
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

    /**
     * @ensures that a check is made whether or not the game is over
     * @param timer is the Thread on which the game timer takes place
     * @return array of ClientHandler(s) who has won, null if game is not yet over
     */
   private ClientHandler[] gameOver(Thread timer) {
       //If all ships have been sunk, other player is announced winner
        if (checkShipsSunk() != null) {
            ClientHandler losingPlayer = checkShipsSunk();
            ClientHandler win;
            if (losingPlayer.getPlayer().getName().equals(players.get(0).getPlayer().getName())) {
                win = players.get(1);
            }
            else {
                win = players.get(0);
            }
            return new ClientHandler[] {win};
        }
        else if (!timer.isAlive()) {
            ClientHandler winner = null;
            //Check if score of players differs and returns winner
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
            //If scores are the same, a check is made between the tieScores. If even the tieScores are the same, a tie is announced according to protocol.
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
        //No winner is announced yet
        else {
            return null;
        }
   }
}
