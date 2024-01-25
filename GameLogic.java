import java.util.ArrayList;
import java.util.Stack;

public class GameLogic implements PlayableLogic
{
    int boardSize = 11;
    boolean isSecondPlayerTurn = true;
    ConcretePiece[][] board = new ConcretePiece[boardSize][boardSize];
    ConcretePlayer playerOne = new ConcretePlayer(true,13);
    ConcretePlayer playerTwo = new ConcretePlayer(false,24);
    //lifo stack for the undo contains concrete pieces that represent the move order if a piece is being killed it will be added to the end of the queue before the killer
    Stack<ConcretePiece> MoveOrder = new Stack<ConcretePiece>();

    public GameLogic() {reset();}
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

        // add the piece to the move order
        MoveOrder.push(board[b.getColumn()][b.getRow()]);
        System.out.println(MoveOrder.peek());
        // add the move to the piece history
        board[b.getColumn()][b.getRow()].MovesHistory.add(b);

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
            //check if the piece is a pawn
            if (board[X - 1][Y] instanceof Pawn)
            {
                // check if we close to a wall
                if (X == 1)
                {
                    MoveOrder.push(board[X - 1][Y]);
                    ((Pawn)board[X - 1][Y]).setALive(false);
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if (X == 2 && (Y == 0) || (X == 10))
                {
                    MoveOrder.push(board[X - 1][Y]);
                    ((Pawn)board[X - 1][Y]).setALive(false);
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                //add kill counts and remove the killed piece
                else if (((board[X - 2][Y]!= null)) && (board[X - 2][Y].getOwner() == board[X][Y].getOwner()) && (board[X - 2][Y] instanceof Pawn))
                {
                    MoveOrder.push(board[X - 1][Y]);
                    ((Pawn)board[X - 1][Y]).setALive(false);
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                    ((Pawn) board[X-2][Y]).AddKillCount();
                }
            }
            //if the piece is a king
            else if (board[X - 1][Y] instanceof King)
            {
                Position c = new Position(X - 1, Y);
                //check if the king is surrounded
               if (kingCheckSurrounding(c))
               {
                   //add win reset the game and change the turn
                   playerTwo.addWin();
                   reset();
                   isSecondPlayerTurn = !isSecondPlayerTurn;/////////////////////////////////////////////////////////////////////
               }
            }
        }
        //check right
        if ((X != 10) && (board[X + 1][Y]!= null) &&(board[X + 1][Y].getOwner() != board[X][Y].getOwner()))
        {
            //check if the piece is a pawn
            if (board[X + 1][Y] instanceof Pawn)
            {
                // check if we close to a wall
                if (X == 9)
                {
                    MoveOrder.push(board[X + 1][Y]);
                    ((Pawn)board[X + 1][Y]).setALive(false);
                    board[X + 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if (X == 8 && (Y == 0) || (X == 10))
                {
                    MoveOrder.push(board[X + 1][Y]);
                    ((Pawn)board[X + 1][Y]).setALive(false);
                    board[X + 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                //add kill counts and remove the killed piece
                else if (((board[X + 2][Y]!= null)) && (board[X + 2][Y].getOwner() == board[X][Y].getOwner()) && (board[X + 2][Y] instanceof Pawn))
                {
                    MoveOrder.push(board[X + 1][Y]);
                    ((Pawn)board[X + 1][Y]).setALive(false);
                    board[X + 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                    ((Pawn) board[X+2][Y]).AddKillCount();
                }
            }
            //if the piece is a king
            else if (board[X + 1][Y] instanceof King)
            {
                Position c = new Position(X + 1, Y);
                //check if the king is surrounded
                if (kingCheckSurrounding(c))
                {
                    //add win reset the game and change the turn
                    playerTwo.addWin();
                    reset();
                    isSecondPlayerTurn = !isSecondPlayerTurn;////////////////////////////////////////////////////////////////////////////////
                }
            }
        }
        //check up
        if ((Y != 10) && (board[X][Y + 1]!= null) &&(board[X][Y + 1].getOwner() != board[X][Y].getOwner()))
        {
            //check if the piece is a pawn
            if (board[X][Y + 1] instanceof Pawn)
            {
                // check if we close to a wall
                if (Y == 9)
                {
                    MoveOrder.push(board[X][Y + 1]);
                    ((Pawn)board[X][Y + 1]).setALive(false);
                    board[X][Y + 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if (Y == 8 && (X == 0) || (Y == 10))
                {
                    MoveOrder.push(board[X][Y + 1]);
                    ((Pawn)board[X][Y + 1]).setALive(false);
                    board[X][Y + 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                //add kill counts and remove the killed piece
                else if (((board[X][Y + 2]!= null)) && (board[X][Y + 2].getOwner() == board[X][Y].getOwner()) && (board[X][Y + 2] instanceof Pawn))
                {
                    MoveOrder.push(board[X][Y + 1]);
                    ((Pawn)board[X][Y +1 ]).setALive(false);
                    board[X][Y + 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                    ((Pawn) board[X][Y+2]).AddKillCount();
                }
            }
            //if the piece is a king
            else if (board[X][Y + 1] instanceof King)
            {
                Position c = new Position(X, Y + 1);
                //check if the king is surrounded
                if (kingCheckSurrounding(c)) ///////////////////////////////////////////////////////////////////////////////
                {
                    //add win reset the game and change the turn
                    playerTwo.addWin();
                    reset();
                    isSecondPlayerTurn = !isSecondPlayerTurn;
                }
            }
        }
        //check down
        if ((Y != 0) && (board[X][Y - 1]!= null) &&(board[X][Y - 1].getOwner() != board[X][Y].getOwner()))
        {
            //check if the piece is a pawn
            if (board[X][Y - 1] instanceof Pawn)
            {
                // check if we close to a wall
                if (Y == 1)
                {
                    MoveOrder.push(board[X][Y - 1]);
                    ((Pawn)board[X][Y - 1]).setALive(false);
                    board[X][Y - 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if (Y == 2 && (X == 0) || (Y == 10))
                {
                    MoveOrder.push(board[X][Y - 1]);
                    ((Pawn)board[X][Y - 1]).setALive(false);
                    board[X][Y - 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                //add kill counts and remove the killed piece
                else if (((board[X][Y - 2]!= null)) && (board[X][Y - 2].getOwner() == board[X][Y].getOwner()) && (board[X][Y - 2] instanceof Pawn))
                {
                    MoveOrder.push(board[X][Y - 1]);
                    ((Pawn)board[X][Y - 1]).setALive(false);
                    board[X][Y - 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                    ((Pawn) board[X][Y-2]).AddKillCount();
                }
            }
            //if the piece is a king
            else if (board[X][Y - 1] instanceof King)
            {
                Position c = new Position(X, Y - 1);
                //check if the king is surrounded
                if (kingCheckSurrounding(c))
                {
                    //add win reset the game and change the turn
                    playerTwo.addWin();
                    reset();
                    isSecondPlayerTurn = !isSecondPlayerTurn;
                }
            }
        }
    }

    private boolean kingCheckSurrounding(Position c)
    {
        //make instance for every side position
        Position up = new Position(c.getColumn(),c.getRow()+1);
        Position down = new Position(c.getColumn(),c.getRow()-1);
        Position left = new Position(c.getColumn()-1,c.getRow());
        Position right = new Position(c.getColumn()+1,c.getRow());

        // check if all sides are attackers walls or a corner
        if (check_side_for_king(up) && check_side_for_king(down) && check_side_for_king(left) && check_side_for_king(right))
            return true;
        return false;
    }
    private boolean check_side_for_king(Position d)
    {
        //check if outside the board
        if (d.getColumn() < 0 || d.getColumn() > 10 || d.getRow() < 0 || d.getRow() > 10)
            return true;
        //if corner return true
        else if (d.getColumn() == 0 && d.getRow() == 0 || d.getColumn() == 10 && d.getRow() == 0 || d.getColumn() == 10 && d.getRow() == 10 || d.getColumn() == 0 && d.getRow() == 10)
            return true;
        //check if there is a piece
        else if (board[d.getColumn()][d.getRow()] == null)
            return false;
        //if attacker return true
        else if (board[d.getColumn()][d.getRow()].getOwner() == playerTwo)
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
    public Piece getPieceAtPosition(Position position) {return board[position.getColumn()][position.getRow()];}
    @Override
    public Player getFirstPlayer() {return playerOne;}
    @Override
    public Player getSecondPlayer() {return playerTwo;}
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
            board[0][i].MovesHistory.add(new Position(0,i));
            board[10][i] = new Pawn(playerTwo);
            board[10][i].MovesHistory.add(new Position(10,i));
            board[i][0] = new Pawn(playerTwo);
            board[i][0].MovesHistory.add(new Position(i,0));
            board[i][10] = new Pawn(playerTwo);
            board[i][10].MovesHistory.add(new Position(i,10));
            //Defenders
            board[5][i] = new Pawn(playerOne);
            board[5][i].MovesHistory.add(new Position(5,i));
            board[i][5] = new Pawn(playerOne);
            board[i][5].MovesHistory.add(new Position(i,5));
        }
        //King
        board[5][5] = new King(playerOne);
        board[5][5].MovesHistory.add(new Position(5,5));
        //Defenders
        board[4][6] = new Pawn(playerOne);
        board[4][6].MovesHistory.add(new Position(4,6));
        board[6][4] = new Pawn(playerOne);
        board[6][4].MovesHistory.add(new Position(6,4));
        board[4][4] = new Pawn(playerOne);
        board[4][4].MovesHistory.add(new Position(4,4));
        board[6][6] = new Pawn(playerOne);
        board[6][6].MovesHistory.add(new Position(6,6));
        //Attackers
        board[1][5] = new Pawn(playerTwo);
        board[1][5].MovesHistory.add(new Position(1,5));
        board[5][1] = new Pawn(playerTwo);
        board[5][1].MovesHistory.add(new Position(5,1));
        board[5][9] = new Pawn(playerTwo);
        board[5][9].MovesHistory.add(new Position(5,9));
        board[9][5] = new Pawn(playerTwo);
        board[9][5].MovesHistory.add(new Position(9,5));
    }

    @Override
    public void undoLastMove()
    {
        //check if the move order is empty
        if (MoveOrder.isEmpty())
            return;
        //get the last mover position and the position before it
        ConcretePiece lastMove = MoveOrder.pop();
        Position thisPosition = lastMove.MovesHistory.getLast();
        Position lastPosition = lastMove.MovesHistory.get(lastMove.MovesHistory.size()-2);

        isSecondPlayerTurn = !isSecondPlayerTurn;

        //check if the last move was a kill if so keep checking until you find a piece that is alive
        if (lastMove instanceof Pawn && !MoveOrder.isEmpty()){
            while (!((Pawn) MoveOrder.peek()).isALive()){
                ConcretePiece killedLast = MoveOrder.pop();
                //add the killed piece to the board
                board[killedLast.MovesHistory.getLast().getColumn()][killedLast.MovesHistory.getLast().getRow()] = killedLast;
                //rewind the kill count piece assisted it if there is one
                rewindKillCount(killedLast,lastMove);

            }
        }
        //remove the piece from the current position and add it to the last position
        board[thisPosition.getColumn()][thisPosition.getRow()] = null;
        board[lastPosition.getColumn()][lastPosition.getRow()] = lastMove;

        // bring them back from the dead + toggle the isAlive boolean

        // swap turns

        // remove the last move from the history

        // decrees kill count for the last piece that moved and the piece that assisted it

        // decrees step count

        //


    }

    //this function will rewind the kill count of the last piece that moved and the piece that assisted it
    public void rewindKillCount(ConcretePiece killedPiece, ConcretePiece killerPiece){
     // the cordinats of the killed piece
        int Xd = killedPiece.MovesHistory.getLast().getColumn();
        int Yd = killedPiece.MovesHistory.getLast().getRow();
        // the cordinats of the killer piece
        int Xr = killerPiece.MovesHistory.getLast().getColumn();
        int Yr = killerPiece.MovesHistory.getLast().getRow();

        //rewind the kill count of the killer piece
        System.out.println(((Pawn) killerPiece).KillCount + "kill count of the killer piece");
        ((Pawn) killerPiece).KillCount--;
        System.out.println(((Pawn) killerPiece).KillCount + "kill count of the killer piece");
        // find if there is a piece that assisted the killer piece or it has killed by a wall or a corner
        if (Xd == Xr){
        //if the killer is above the killed piece
           if (Yd > Yr) {
               //check if is piece is a wall or a corner
               if (Yd != 10 && board[Xd][Yd + 1] != null && board[Xd][Yd + 1] instanceof Pawn) {
                   //rewind the kill count of the assisted piece
                   ((Pawn) board[Xd][Yd + 1]).KillCount--;
                   System.out.println(((Pawn) board[Xd][Yd + 1]).KillCount + "kill count of the assisted piece");
               }
           }
        //if the killer is below the killed piece
            else if (Yd < Yr) {
               //check if is piece is a wall or a corner
               if (Yd != 0 && board[Xd][Yd - 1] != null && board[Xd][Yd - 1] instanceof Pawn) {
                   //rewind the kill count of the assisted piece
                   ((Pawn) board[Xd][Yd - 1]).KillCount--;
               }
           }
        }
        else {
            //if the killer is to the right of the killed piece
            if (Xd > Xr) {
                //check if is piece is a wall or a corner
                if (Xd != 10 && board[Xd + 1][Yd] != null && board[Xd + 1][Yd] instanceof Pawn) {
                    //rewind the kill count of the assisted piece
                    ((Pawn) board[Xd + 1][Yd]).KillCount--;
                }
            }
            //if the killer is to the left of the killed piece
            else {
                //check if is piece is a wall or a corner
                if (Xd != 0 && board[Xd - 1][Yd] != null && board[Xd - 1][Yd] instanceof Pawn) {
                    //rewind the kill count of the assisted piece
                    ((Pawn) board[Xd - 1][Yd]).KillCount--;
                }
            }
        }
                //check if is piece is a wall or a corner
                if (Xd != 0 && board[Xd - 1][Yd] != null && board[Xd - 1][Yd] instanceof Pawn) {
                    //rewind the kill count of the assisted piece
                    ((Pawn) board[Xd - 1][Yd]).KillCount--;
                }
            }


   // this one does not cover the option of invalid input: diagonal pieces and same location!!!!!!!!!!


    @Override
    public int getBoardSize()
    {
        return boardSize;
    }
}


//todo: invalid input to the undo function - handel it
//todo: check better if the eat is perfect
//TODO: fix the change of the turn in the undo function
//TODO: fix the change turn if win