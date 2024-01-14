public class GameLogic implements PlayableLogic
{
    int boardSize = 11;
    boolean isSecondPlayerTurn = true;
    Piece[][] board = new Piece[boardSize][boardSize];
    Player playerone = new ConcretePlayer(true);
    Player playertwo = new ConcretePlayer(false);

    public GameLogic()
    {
        reset();
    }
    @Override
    public boolean move(Position a, Position b)
    {
        /**
         * need to check if the move is valid
         * if it is, then move the piece,
         * if not, then return false.
         * 1. check if there is no one in the way or in the destination
         * 2. check if the destination is in the same x or y
         * 3. check if it's a pawn that the destination is not a corner
         *
         */
        if(board[a.getColumn()][a.getRow()] == null)
            return false;
        if(board[b.getColumn()][b.getRow()] != null)
            return false;
        if (board[a.getColumn()][a.getRow()].getOwner().isPlayerOne() != isSecondPlayerTurn)
            return false;
        if (board[a.getColumn()][a.getRow()] instanceof Pawn)
        {
            if (a.getColumn() == 0 || a.getColumn() == 10 || a.getRow() == 0 || a.getRow() == 10)
                return false;
            if (b.getColumn() == 0 || b.getColumn() == 10 || b.getRow() == 0 || b.getRow() == 10)
                return false;
        }
        return true;
    }

    @Override
    public Piece getPieceAtPosition(Position position)
    {
        return board[position.getColumn()][position.getRow()];
    }

    @Override
    public Player getFirstPlayer()
    {
        return playerone;
    }

    @Override
    public Player getSecondPlayer()
    {
        return playertwo;
    }

    @Override
    public boolean isGameFinished()
    {
        return false;
    }

    @Override
    public boolean isSecondPlayerTurn()
    {
        return isSecondPlayerTurn;
    }

    @Override
    public void reset()
    {
        board = new Piece[boardSize][boardSize];
        //Pawns
        for(int i = 3; i <= 7; i++)
        {
            //Attackers
            board[0][i] = new Pawn(playertwo);
            board[10][i] = new Pawn(playertwo);
            board[i][0] = new Pawn(playertwo);
            board[i][10] = new Pawn(playertwo);
            //Defenders
            board[5][i] = new Pawn(playerone);
            board[i][5] = new Pawn(playerone);
        }
        //King
        board[5][5] = new King(playerone);
        //Defenders
        board[4][6] = new Pawn(playerone);
        board[6][4] = new Pawn(playerone);
        board[4][4] = new Pawn(playerone);
        board[6][6] = new Pawn(playerone);
        //Attackers
        board[1][5] = new Pawn(playertwo);
        board[5][1] = new Pawn(playertwo);
        board[5][9] = new Pawn(playertwo);
        board[9][5] = new Pawn(playertwo);
    }

    @Override
    public void undoLastMove()
    {

    }

    @Override
    public int getBoardSize()
    {
        return boardSize;
    }
}