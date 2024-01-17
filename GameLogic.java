public class GameLogic implements PlayableLogic
{
    int boardSize = 11;
    boolean isSecondPlayerTurn = true;
    ConcretePiece[][] board = new ConcretePiece[boardSize][boardSize];
    ConcretePlayer playerOne = new ConcretePlayer(true,13);
    ConcretePlayer playerTwo = new ConcretePlayer(false,24);

    public GameLogic()
    {
        reset();
    }
    @Override
    public boolean move(Position a, Position b)
    {
        if(board[a.getColumn()][a.getRow()] == null)
            return false;
        if(board[b.getColumn()][b.getRow()] != null)
            return false;
        if (board[a.getColumn()][a.getRow()].getOwner().isPlayerOne() == isSecondPlayerTurn)
            return false;
        if (board[a.getColumn()][a.getRow()] instanceof Pawn)
        {
            //corner check
            if (b.getRow()==0 && b.getColumn()==0)
                return false;
            if (b.getRow()==0 && b.getColumn()==10)
                return false;
            if (b.getRow()==10 && b.getColumn()==0)
                return false;
            if (b.getRow()==10 && b.getColumn()==10)
                return false;
        }
        //check if point 'a' is equal point 'b'
        if (a.getRow()==b.getRow() && a.getColumn()==b.getColumn())
            return false;
        // check if not moving in a straight line
        if (a.getRow()!=b.getRow() && a.getColumn()!=b.getColumn())
            return false;
        // check if the road is available
        if (a.getRow()==b.getRow())
        {
            if (a.getColumn()<b.getColumn())
            {
                for (int i = a.getColumn()+1; i < b.getColumn(); i++)
                {
                    if (board[i][a.getRow()] != null)
                        return false;
                }
            }
            else
            {
                for (int i = a.getColumn()-1; i > b.getColumn(); i--)
                {
                    if (board[i][a.getRow()] != null)
                        return false;
                }
            }
        }
        else
        {
            if (a.getRow()<b.getRow())
            {
                for (int i = a.getRow()+1; i < b.getRow(); i++)
                {
                    if (board[a.getColumn()][i] != null)
                        return false;
                }
            }
            else
            {
                for (int i = a.getRow()-1; i > b.getRow(); i--)
                {
                    if (board[a.getColumn()][i] != null)
                        return false;
                }
            }
        }
        //swap turns
        isSecondPlayerTurn = !isSecondPlayerTurn;
        // update the step counter
        board[a.getColumn()][a.getRow()].setStepCounter(calculateSteps(a,b));
        // move the piece
        board[b.getColumn()][b.getRow()] = board[a.getColumn()][a.getRow()];
        board[a.getColumn()][a.getRow()] = null;
        // kill check
        if (board[b.getColumn()][b.getRow()] instanceof Pawn)
            kill(b);
        // return true if the move was successful
        return true;
    }

    public void kill(Position b)
    {
        int X = b.getColumn();
        int Y = b.getRow();
        //check left
        if ((X != 0) && (board[X - 1][Y]!= null) &&(board[X - 1][Y].getOwner() != board[X][Y].getOwner()))
        {
            if (board[X - 1][Y] instanceof Pawn)
            {
                // check if we close to a wall
                if (X == 1)
                {
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if (X == 2 && (Y == 0) || (X == 10))
                {
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                else if (((board[X - 2][Y]!= null)) && (board[X - 2][Y].getOwner() == board[X][Y].getOwner()) && (board[X - 2][Y] instanceof Pawn))
                {
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                    ((Pawn) board[X-2][Y]).AddKillCount();
                }
            }
            else if (board[X - 1][Y] instanceof King)
            {
                Position c = new Position(X - 1, Y);
               if (kingCheckSurrounding(c))
               {
                   playerTwo.addWin();
                   reset();
               }
            }
        }
    }

    private boolean kingCheckSurrounding(Position c)
    {

        /*
        need to fix the out of range problem
         */
        int X = c.getColumn();
        int Y = c.getRow();
        // check if the king is surrounded by attackers and a wall
        if (board[X][Y+1] != null && (board[X][Y+1].getOwner() == playerTwo || X==10))
            if (Y==10 || board[X-1][Y].getOwner() == playerTwo)
                if (X==0 || board[X][Y-1].getOwner() == playerTwo )
                    return true;
        return false;
    }
    private int calculateSteps(Position a, Position b)
    {
        if (a.getRow()==b.getRow())
            return Math.abs(a.getColumn()-b.getColumn());
        else
            return Math.abs(a.getRow()-b.getRow());
    }

    @Override
    public Piece getPieceAtPosition(Position position)
    {
        return board[position.getColumn()][position.getRow()];
    }

    @Override
    public Player getFirstPlayer()
    {
        return playerOne;
    }

    @Override
    public Player getSecondPlayer()
    {
        return playerTwo;
    }

    @Override
    public boolean isGameFinished()
    {
        //check if one of the players has no pieces left.
        if (playerOne.getPiecesRemain() == 0)
        {
            playerTwo.addWin();
            return true;
        }
        if (playerTwo.getPiecesRemain() == 0)
        {
            playerOne.addWin();
            return true;
        }
        //check if the king reach the corner.
        else if (board[0][0] instanceof King || board[0][10] instanceof King || board[10][0] instanceof King || board[10][10] instanceof King)
        {
            playerOne.addWin();
            return true;
        }
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
        board = new ConcretePiece[boardSize][boardSize];
        //Pawns
        for(int i = 3; i <= 7; i++)
        {
            //Attackers
            board[0][i] = new Pawn(playerTwo);
            board[10][i] = new Pawn(playerTwo);
            board[i][0] = new Pawn(playerTwo);
            board[i][10] = new Pawn(playerTwo);
            //Defenders
            board[5][i] = new Pawn(playerOne);
            board[i][5] = new Pawn(playerOne);
        }
        //King
        board[5][5] = new King(playerOne);
        //Defenders
        board[4][6] = new Pawn(playerOne);
        board[6][4] = new Pawn(playerOne);
        board[4][4] = new Pawn(playerOne);
        board[6][6] = new Pawn(playerOne);
        //Attackers
        board[1][5] = new Pawn(playerTwo);
        board[5][1] = new Pawn(playerTwo);
        board[5][9] = new Pawn(playerTwo);
        board[9][5] = new Pawn(playerTwo);
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