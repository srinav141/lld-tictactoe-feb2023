package com.scaler.tictactoe.models;

import com.scaler.tictactoe.exceptions.InvalidGameConstructionParametersException;
import com.scaler.tictactoe.strategies.gamewinningstrategy.GameWinningStrategy;
import com.scaler.tictactoe.strategies.gamewinningstrategy.OrderOneGameWinningStrategy;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Board board;
    private List<Player> players;
    private List<Move> moves;
    private GameStatus gameStatus;
    private int nextPlayerIndex;
    private GameWinningStrategy gameWinningStrategy;
    private Player winner;

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public GameWinningStrategy getGameWinningStrategy() {
        return gameWinningStrategy;
    }

    public void setGameWinningStrategy(GameWinningStrategy gameWinningStrategy) {
        this.gameWinningStrategy = gameWinningStrategy;
    }

    private Game() {
    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public void undo() {

        int currSize = this.moves.size();

        if (currSize == 0) {
            System.out.println("no more undo can be performed");
        }else{
            Move undoMove = this.moves.get(currSize-1);
            int row = undoMove.getCell().getRow();
            int col = undoMove.getCell().getCol();

            System.out.println("Undo happened at: " + row + "," + col + ".");

            // update board
            board.getBoard().get(row).get(col).setCellState(CellState.EMPTY);
            //board.getBoard().get(row).get(col).setPlayer(toMovePlayer);

            this.moves.remove(currSize-1);
            System.out.println("nextPlayerIndex before "+this.nextPlayerIndex);
            if (this.nextPlayerIndex == 0){
                this.nextPlayerIndex += (players.size()-1);
            }else{
                this.nextPlayerIndex -= 1;
            }

            System.out.println("nextPlayerIndex "+this.nextPlayerIndex);

        }



    }

    public void makeNextMove() {
        Player toMovePlayer = players.get(this.nextPlayerIndex);

        System.out.println("It is " + toMovePlayer.getName() + "'s turn.");

        Move move = toMovePlayer.decideMove(this.board);

        // Validate the move


        int row = move.getCell().getRow();
        int col = move.getCell().getCol();
        if (board.getBoard().get(row).get(col).getCellState().equals(CellState.FILLED)){
            System.out.println("Cell at: " + row + "," + col + "is already filled");
        }else{
            System.out.println("Move happened at: " + row + "," + col + ".");

            // update board
            board.getBoard().get(row).get(col).setCellState(CellState.FILLED);
            board.getBoard().get(row).get(col).setPlayer(toMovePlayer);

            Move finalMove = new Move(
                    toMovePlayer,
                    board.getBoard().get(row).get(col)
            );

            this.moves.add(finalMove);

            // Check the winner
            if(gameWinningStrategy.checkWinner(
                    board, toMovePlayer, finalMove.getCell()
            )){
                gameStatus = GameStatus.ENDED;
                winner = toMovePlayer;
            }
            // Check the game status
            int totalPossibleMoves = board.getBoard().size()*board.getBoard().size();
            if (this.moves.size() == totalPossibleMoves){
                gameStatus =GameStatus.DRAW;
            }

            this.nextPlayerIndex += 1;
            this.nextPlayerIndex %= players.size();
        }



    }

    public void displayBoard() {
        this.board.display();
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getNextPlayerIndex() {
        return nextPlayerIndex;
    }

    public void setNextPlayerIndex(int nextPlayerIndex) {
        this.nextPlayerIndex = nextPlayerIndex;
    }

    public static class Builder {
        private int dimension;
        private List<Player> players;

        public Builder setDimension(int dimension) {
            this.dimension = dimension;
            return this;
        }

        public Builder setPlayers(List<Player> players) {
            this.players = players;
            return this;
        }

        private boolean valid() throws InvalidGameConstructionParametersException{
            if(this.dimension < 3){
                throw new InvalidGameConstructionParametersException("Dimension of game can't be < 3");
            }
            if(this.players.size() != this.dimension - 1){
                throw new InvalidGameConstructionParametersException("Number of players must be dimension - 1");
            }

            // Validate no 2 people with same symbol

            // Validate that there should be only 1 bot

            // TODO:: Add more validations

            return true;
        }

        public Game build() throws InvalidGameConstructionParametersException {
            try {
                valid();
            }
            catch (Exception e){
                throw new InvalidGameConstructionParametersException(e.getMessage());
            }

            Game game = new Game();
            game.setGameStatus(GameStatus.IN_PROGRESS);
            game.setPlayers(players);
            game.setMoves(new ArrayList<>());
            game.setBoard(new Board(dimension));
            game.setNextPlayerIndex(0);
            game.setGameWinningStrategy(new OrderOneGameWinningStrategy(dimension));

            return game;
        }
    }
}
