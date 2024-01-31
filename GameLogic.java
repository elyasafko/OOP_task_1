import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    int boardSize = 11;
    boolean isSecondPlayerTurn = true;
    ConcretePiece[][] board = new ConcretePiece[boardSize][boardSize];
    ConcretePlayer playerOne = new ConcretePlayer(true, 13);
    ConcretePlayer playerTwo = new ConcretePlayer(false, 24);
    //lifo stack for the undo contains concrete pieces that represent the move order if a piece is being killed it will be added to the end of the queue before the killer
    Stack<ConcretePiece> MoveOrder = new Stack<ConcretePiece>();
    ArrayList<ConcretePiece> piecesList = new ArrayList<ConcretePiece>();
    ArrayList<Position> positionsUsedArr = new ArrayList<>();
    Set<Position> positionsUsed = new HashSet<>();

    public GameLogic() {
        reset();
    }

    @Override
    public boolean move(Position a, Position b)
    {
        //check if the move is valid
        if (board[a.getColumn()][a.getRow()] == null)
            return false;
        if (board[b.getColumn()][b.getRow()] != null)
            return false;
        if (board[a.getColumn()][a.getRow()].getOwner().isPlayerOne() == isSecondPlayerTurn)
            return false;
        if (board[a.getColumn()][a.getRow()] instanceof Pawn) {
            //corner check
            if (b.getRow() == 0 && b.getColumn() == 0)
                return false;
            if (b.getRow() == 0 && b.getColumn() == 10)
                return false;
            if (b.getRow() == 10 && b.getColumn() == 0)
                return false;
            if (b.getRow() == 10 && b.getColumn() == 10)
                return false;
        }
        //check if point 'a' is equal point 'b'
        if (a.getRow() == b.getRow() && a.getColumn() == b.getColumn())
            return false;
        // check if not moving in a straight line
        if (a.getRow() != b.getRow() && a.getColumn() != b.getColumn())
            return false;
        // check if the road is available
        if (a.getRow() == b.getRow()) {
            if (a.getColumn() < b.getColumn()) {
                for (int i = a.getColumn() + 1; i < b.getColumn(); i++) {
                    if (board[i][a.getRow()] != null)
                        return false;
                }
            } else {
                for (int i = a.getColumn() - 1; i > b.getColumn(); i--) {
                    if (board[i][a.getRow()] != null)
                        return false;
                }
            }
        } else {
            if (a.getRow() < b.getRow()) {
                for (int i = a.getRow() + 1; i < b.getRow(); i++) {
                    if (board[a.getColumn()][i] != null)
                        return false;
                }
            } else {
                for (int i = a.getRow() - 1; i > b.getRow(); i--) {
                    if (board[a.getColumn()][i] != null)
                        return false;
                }
            }
        }
        //swap turns
        isSecondPlayerTurn = !isSecondPlayerTurn;
        // update the step counter
        board[a.getColumn()][a.getRow()].addStepCounter(calculateSteps(a, b));
        // move the piece
        board[b.getColumn()][b.getRow()] = board[a.getColumn()][a.getRow()];
        board[a.getColumn()][a.getRow()] = null;
        // kill check
        if (board[b.getColumn()][b.getRow()] instanceof Pawn)
        {
            if (kill(b))
            {
                board[b.getColumn()][b.getRow()].MovesHistory.add(b);
                printFinish(playerTwo);
                playerTwo.addWin();
                reset();
                return true;
            }
        }

        // add the piece to the move order
        MoveOrder.push(board[b.getColumn()][b.getRow()]);
        // add the move to the piece history
        board[b.getColumn()][b.getRow()].MovesHistory.add(b);

        // return true if the move was successful
        return true;
    }

    //make the kill operation if someone has killed and return true if the king is killed
    public boolean kill(Position b) {
        int X = b.getColumn();
        int Y = b.getRow();
        //check left
        if ((X != 0) && (board[X - 1][Y] != null) && (board[X - 1][Y].getOwner() != board[X][Y].getOwner())) {
            //check if the piece is a pawn
            if (board[X - 1][Y] instanceof Pawn) {
                // check if we close to a wall
                if (X == 1) {
                    MoveOrder.push(board[X - 1][Y]);
                    ((Pawn) board[X - 1][Y]).setALive(false);
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if ((X == 2 && (Y == 0 || Y == 10))) {
                    MoveOrder.push(board[X - 1][Y]);
                    ((Pawn) board[X - 1][Y]).setALive(false);
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                //add kill counts and remove the killed piece
                else if (((board[X - 2][Y] != null)) && (board[X - 2][Y].getOwner() == board[X][Y].getOwner()) && (board[X - 2][Y] instanceof Pawn)) {
                    MoveOrder.push(board[X - 1][Y]);
                    ((Pawn) board[X - 1][Y]).setALive(false);
                    board[X - 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
            }
            //if the piece is a king
            else if (board[X - 1][Y] instanceof King) {
                Position c = new Position(X - 1, Y);
                //check if the king is surrounded
                if (kingCheckSurrounding(c)) {
                    return true;
                }
            }
        }
        //check right
        if ((X != 10) && (board[X + 1][Y] != null) && (board[X + 1][Y].getOwner() != board[X][Y].getOwner())) {
            //check if the piece is a pawn
            if (board[X + 1][Y] instanceof Pawn) {
                // check if we close to a wall
                if (X == 9) {
                    MoveOrder.push(board[X + 1][Y]);
                    ((Pawn) board[X + 1][Y]).setALive(false);
                    board[X + 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if (X == 8 && ((Y == 0) || (Y == 10))) {
                    MoveOrder.push(board[X + 1][Y]);
                    ((Pawn) board[X + 1][Y]).setALive(false);
                    board[X + 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                //add kill counts and remove the killed piece
                else if (((board[X + 2][Y] != null)) && (board[X + 2][Y].getOwner() == board[X][Y].getOwner()) && (board[X + 2][Y] instanceof Pawn)) {
                    MoveOrder.push(board[X + 1][Y]);
                    ((Pawn) board[X + 1][Y]).setALive(false);
                    board[X + 1][Y] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
            }
            //if the piece is a king
            else if (board[X + 1][Y] instanceof King) {
                Position c = new Position(X + 1, Y);
                //check if the king is surrounded
                if (kingCheckSurrounding(c)) {
                    return true;
                }
            }
        }
        //check up
        if ((Y != 0) && (board[X][Y - 1] != null) && (board[X][Y - 1].getOwner() != board[X][Y].getOwner())) {
            //check if the piece is a pawn
            if (board[X][Y - 1] instanceof Pawn) {
                // check if we close to a wall
                if (Y == 1) {
                    MoveOrder.push(board[X][Y - 1]);
                    ((Pawn) board[X][Y - 1]).setALive(false);
                    board[X][Y - 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if (Y == 2 && ((X == 0) || (X == 10))) {
                    MoveOrder.push(board[X][Y - 1]);
                    ((Pawn) board[X][Y - 1]).setALive(false);
                    board[X][Y - 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                //add kill counts and remove the killed piece
                else if (((board[X][Y - 2] != null)) && (board[X][Y - 2].getOwner() == board[X][Y].getOwner()) && (board[X][Y - 2] instanceof Pawn)) {
                    MoveOrder.push(board[X][Y - 1]);
                    ((Pawn) board[X][Y - 1]).setALive(false);
                    board[X][Y - 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
            }
            //if the piece is a king
            else if (board[X][Y - 1] instanceof King) {
                Position c = new Position(X, Y - 1);
                //check if the king is surrounded
                if (kingCheckSurrounding(c)) {
                    return true;
                }
            }
        }
        //check down
        if ((Y != 10) && (board[X][Y + 1] != null) && (board[X][Y + 1].getOwner() != board[X][Y].getOwner())) {
            //check if the piece is a pawn
            if (board[X][Y + 1] instanceof Pawn) {
                // check if we close to a wall
                if (Y == 9) {
                    MoveOrder.push(board[X][Y + 1]);
                    ((Pawn) board[X][Y + 1]).setALive(false);
                    board[X][Y + 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                // check if we close to a corner
                else if (Y == 8 && ((X == 0) || (X == 10))) {
                    MoveOrder.push(board[X][Y + 1]);
                    ((Pawn) board[X][Y + 1]).setALive(false);
                    board[X][Y + 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
                //add kill counts and remove the killed piece
                else if (((board[X][Y + 2] != null)) && (board[X][Y + 2].getOwner() == board[X][Y].getOwner()) && (board[X][Y + 2] instanceof Pawn)) {
                    MoveOrder.push(board[X][Y + 1]);
                    ((Pawn) board[X][Y + 1]).setALive(false);
                    board[X][Y + 1] = null;
                    ((Pawn) board[X][Y]).AddKillCount();
                }
            }
            //if the piece is a king
            else if (board[X][Y + 1] instanceof King) {
                Position c = new Position(X, Y + 1);
                //check if the king is surrounded
                if (kingCheckSurrounding(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean kingCheckSurrounding(Position c) {
        //make instance for every side position
        Position up = new Position(c.getColumn(), c.getRow() + 1);
        Position down = new Position(c.getColumn(), c.getRow() - 1);
        Position left = new Position(c.getColumn() - 1, c.getRow());
        Position right = new Position(c.getColumn() + 1, c.getRow());

        // check if all sides are attackers walls or a corner
        if (check_side_for_king(up) && check_side_for_king(down) && check_side_for_king(left) && check_side_for_king(right))
            return true;
        return false;
    }

    private boolean check_side_for_king(Position d) {
        //check if outside the board
        if (d.getColumn() < 0 || d.getColumn() > 10 || d.getRow() < 0 || d.getRow() > 10) {
            return true;
        }
        //if corner return true
        else if (d.getColumn() == 0 && d.getRow() == 0 || d.getColumn() == 10 && d.getRow() == 0 || d.getColumn() == 10 && d.getRow() == 10 || d.getColumn() == 0 && d.getRow() == 10) {
            return true;
        }
        //check if there is a piece
        else if (board[d.getColumn()][d.getRow()] == null) {
            return false;
        }
        //if attacker return true
        else if (board[d.getColumn()][d.getRow()].getOwner() == playerTwo) {
            return true;
        }
        return false;

    }

    private int calculateSteps(Position a, Position b)
    {
        if (a.getRow() == b.getRow())
            return Math.abs(a.getColumn() - b.getColumn());
        else
            return Math.abs(a.getRow() - b.getRow());
    }

    @Override
    public Piece getPieceAtPosition(Position position) {
        return board[position.getColumn()][position.getRow()];
    }

    @Override
    public Player getFirstPlayer() {
        return playerOne;
    }

    @Override
    public Player getSecondPlayer() {
        return playerTwo;
    }

    @Override
    public boolean isGameFinished() {
        //check if one of the players has no pieces left.
        if (playerOne.getPiecesRemain() == 0) {
            playerTwo.addWin();
            printFinish(playerTwo);
            return true;
        }
        if (playerTwo.getPiecesRemain() == 0) {
            playerOne.addWin();
            printFinish(playerOne);
            return true;
        }
        //check if the king reach the corner.
        else if (board[0][0] instanceof King || board[0][10] instanceof King || board[10][0] instanceof King || board[10][10] instanceof King) {
            playerOne.addWin();
            printFinish(playerOne);
            return true;
        }
        return false;
    }

    @Override
    public boolean isSecondPlayerTurn() {
        return isSecondPlayerTurn;
    }

    @Override
    public void reset() {
        board = new ConcretePiece[boardSize][boardSize];
        isSecondPlayerTurn = true;
        MoveOrder.clear();

        //King

        board[5][5] = new King(playerOne, "K7");
        board[5][5].MovesHistory.add(new Position(5, 5));
        piecesList.add(board[5][5]);
        //Defenders
        board[5][3] = new Pawn(playerOne, "D1");
        board[5][3].MovesHistory.add(new Position(5, 3));
        piecesList.add(board[5][3]);
        board[4][4] = new Pawn(playerOne, "D2");
        board[4][4].MovesHistory.add(new Position(4, 4));
        piecesList.add(board[4][4]);
        board[5][4] = new Pawn(playerOne, "D3");
        board[5][4].MovesHistory.add(new Position(5, 4));
        piecesList.add(board[5][4]);
        board[6][4] = new Pawn(playerOne, "D4");
        board[6][4].MovesHistory.add(new Position(6, 4));
        piecesList.add(board[6][4]);
        board[3][5] = new Pawn(playerOne, "D5");
        board[3][5].MovesHistory.add(new Position(3, 5));
        piecesList.add(board[3][5]);
        board[4][5] = new Pawn(playerOne, "D6");
        board[4][5].MovesHistory.add(new Position(4, 5));
        piecesList.add(board[4][5]);
        board[6][5] = new Pawn(playerOne, "D8");
        board[6][5].MovesHistory.add(new Position(6, 5));
        piecesList.add(board[6][5]);
        board[7][5] = new Pawn(playerOne, "D9");
        board[7][5].MovesHistory.add(new Position(7, 5));
        piecesList.add(board[7][5]);
        board[4][6] = new Pawn(playerOne, "D10");
        board[4][6].MovesHistory.add(new Position(4, 6));
        piecesList.add(board[4][6]);
        board[5][6] = new Pawn(playerOne, "D11");
        board[5][6].MovesHistory.add(new Position(5, 6));
        piecesList.add(board[5][6]);
        board[6][6] = new Pawn(playerOne, "D12");
        board[6][6].MovesHistory.add(new Position(6, 6));
        piecesList.add(board[6][6]);
        board[5][7] = new Pawn(playerOne, "D13");
        board[5][7].MovesHistory.add(new Position(5, 7));
        piecesList.add(board[5][7]);
        //Attackers
        board[3][0] = new Pawn(playerTwo, "A1");
        board[3][0].MovesHistory.add(new Position(3, 0));
        piecesList.add(board[3][0]);
        board[4][0] = new Pawn(playerTwo, "A2");
        board[4][0].MovesHistory.add(new Position(4, 0));
        piecesList.add(board[4][0]);
        board[5][0] = new Pawn(playerTwo, "A3");
        board[5][0].MovesHistory.add(new Position(5, 0));
        piecesList.add(board[5][0]);
        board[6][0] = new Pawn(playerTwo, "A4");
        board[6][0].MovesHistory.add(new Position(6, 0));
        piecesList.add(board[6][0]);
        board[7][0] = new Pawn(playerTwo, "A5");
        board[7][0].MovesHistory.add(new Position(7, 0));
        piecesList.add(board[7][0]);
        board[5][1] = new Pawn(playerTwo, "A6");
        board[5][1].MovesHistory.add(new Position(5, 1));
        piecesList.add(board[5][1]);
        board[0][3] = new Pawn(playerTwo, "A7");
        board[0][3].MovesHistory.add(new Position(0, 3));
        piecesList.add(board[0][3]);
        board[10][3] = new Pawn(playerTwo, "A8");
        board[10][3].MovesHistory.add(new Position(10, 3));
        piecesList.add(board[10][3]);
        board[0][4] = new Pawn(playerTwo, "A9");
        board[0][4].MovesHistory.add(new Position(0, 4));
        piecesList.add(board[0][4]);
        board[10][4] = new Pawn(playerTwo, "A10");
        board[10][4].MovesHistory.add(new Position(10, 4));
        piecesList.add(board[10][4]);
        board[0][5] = new Pawn(playerTwo, "A11");
        board[0][5].MovesHistory.add(new Position(0, 5));
        piecesList.add(board[0][5]);
        board[1][5] = new Pawn(playerTwo, "A12");
        board[1][5].MovesHistory.add(new Position(1, 5));
        piecesList.add(board[1][5]);
        board[9][5] = new Pawn(playerTwo, "A13");
        board[9][5].MovesHistory.add(new Position(9, 5));
        piecesList.add(board[9][5]);
        board[10][5] = new Pawn(playerTwo, "A14");
        board[10][5].MovesHistory.add(new Position(10, 5));
        piecesList.add(board[10][5]);
        board[0][6] = new Pawn(playerTwo, "A15");
        board[0][6].MovesHistory.add(new Position(0, 6));
        piecesList.add(board[0][6]);
        board[10][6] = new Pawn(playerTwo, "A16");
        board[10][6].MovesHistory.add(new Position(10, 6));
        piecesList.add(board[10][6]);
        board[0][7] = new Pawn(playerTwo, "A17");
        board[0][7].MovesHistory.add(new Position(0, 7));
        piecesList.add(board[0][7]);
        board[10][7] = new Pawn(playerTwo, "A18");
        board[10][7].MovesHistory.add(new Position(10, 7));
        piecesList.add(board[10][7]);
        board[5][9] = new Pawn(playerTwo, "A19");
        board[5][9].MovesHistory.add(new Position(5, 9));
        piecesList.add(board[5][9]);
        board[3][10] = new Pawn(playerTwo, "A20");
        board[3][10].MovesHistory.add(new Position(3, 10));
        piecesList.add(board[3][10]);
        board[4][10] = new Pawn(playerTwo, "A21");
        board[4][10].MovesHistory.add(new Position(4, 10));
        piecesList.add(board[4][10]);
        board[5][10] = new Pawn(playerTwo, "A22");
        board[5][10].MovesHistory.add(new Position(5, 10));
        piecesList.add(board[5][10]);
        board[6][10] = new Pawn(playerTwo, "A23");
        board[6][10].MovesHistory.add(new Position(6, 10));
        piecesList.add(board[6][10]);
        board[7][10] = new Pawn(playerTwo, "A24");
        board[7][10].MovesHistory.add(new Position(7, 10));
        piecesList.add(board[7][10]);
    }

    @Override
    public void undoLastMove()
    {
        //check if the move order is empty
        if (MoveOrder.isEmpty())
            return;
        //get the last mover position and the position before it
        ConcretePiece pMovedLast = MoveOrder.pop();
        Position thisPosition = pMovedLast.MovesHistory.getLast();
        Position lastPosition = pMovedLast.MovesHistory.get(pMovedLast.MovesHistory.size() - 2);

        //check if the mover before got killed if so keep checking until you find a piece that is alive
        //no need to check if it's a king ot if no other moved before
        if (pMovedLast instanceof Pawn && !MoveOrder.isEmpty()) {
            //cant cat king to a pawn so no need to check if it's a king then check if the piece is alive
            while (MoveOrder.peek() instanceof Pawn && !((Pawn) MoveOrder.peek()).isALive()) {
                ConcretePiece killedLast = MoveOrder.pop();
                //add the killed piece to the board
                board[killedLast.MovesHistory.getLast().getColumn()][killedLast.MovesHistory.getLast().getRow()] = killedLast;
                //toggle the isAlive boolean
                ((Pawn) killedLast).setALive(true);
                //reduceKillCount the kill count of the killer piece
                ((Pawn) pMovedLast).reduceKillCount();

            }
        }

        //remove the piece from the current position and add it to the last position
        board[thisPosition.getColumn()][thisPosition.getRow()] = null;
        board[lastPosition.getColumn()][lastPosition.getRow()] = pMovedLast;
        //decrees step counter
        pMovedLast.subStepCounter(calculateSteps(thisPosition, lastPosition));



        //delete the last move from the history
        pMovedLast.MovesHistory.removeLast();

        //toggle the turn
        isSecondPlayerTurn = !isSecondPlayerTurn;
    }


//    //this function will rewind the kill count of the last piece that moved and the piece that assisted it
//    public void rewindKillCount(ConcretePiece killedPiece, ConcretePiece killerPiece) {
//        // the coordinates of the killed piece
//        int Xd = killedPiece.MovesHistory.getLast().getColumn();
//        int Yd = killedPiece.MovesHistory.getLast().getRow();
//        // the coordinates of the killer piece
//        int Xr = killerPiece.MovesHistory.getLast().getColumn();
//        int Yr = killerPiece.MovesHistory.getLast().getRow();
//
//        //rewind the kill count of the killer piece
//        ((Pawn) killerPiece).reduceKillCount();
//
//        // find if there is a piece that assisted the killer piece or if it has killed by a wall or a corner
//        //check if they are in the same column
//        if (Xd == Xr) {
//            //if the killer is above the killed piece
//            if (Yd > Yr) {
//                //check if is piece is a wall or a corner
//                if (Yd != 10 && board[Xd][Yd + 1] != null && board[Xd][Yd + 1] instanceof Pawn) {
//                    //rewind the kill count of the assisted piece
//                    ((Pawn) board[Xd][Yd + 1]).reduceKillCount();
//                }
//            }
//            //if the killer is below the killed piece
//            else if (Yd < Yr) {
//                //check if is piece is a wall or a corner
//                if (Yd != 0 && board[Xd][Yd - 1] != null && board[Xd][Yd - 1] instanceof Pawn) {
//                    //rewind the kill count of the assisted piece
//                    ((Pawn) board[Xd][Yd - 1]).reduceKillCount();
//                }
//            }
//        }
//        //killed piece and killer piece are in the same row
//        else {
//            //if the killer is to the right of the killed piece
//            if (Xd > Xr) {
//                //check if is piece is a wall or a corner
//                if (Xd != 10 && board[Xd + 1][Yd] != null && board[Xd + 1][Yd] instanceof Pawn) {
//                    //rewind the kill count of the assisted piece
//                    ((Pawn) board[Xd + 1][Yd]).reduceKillCount();
//                }
//            }
//            //if the killer is to the left of the killed piece
//            else {
//                //check if is piece is a wall or a corner
//                if (Xd != 0 && board[Xd - 1][Yd] != null && board[Xd - 1][Yd] instanceof Pawn) {
//                    //rewind the kill count of the assisted piece
//                    ((Pawn) board[Xd - 1][Yd]).reduceKillCount();
//                }
//            }
//        }
//    }


    @Override
    public int getBoardSize() {
        return boardSize;
    }

    public void printFinish(ConcretePlayer winner)
    {
        //sort and print by moves
        piecesList.sort(new movesComparator());
        //need to add win and lose prints
        for (ConcretePiece piece : piecesList)
        {
            if ((piece.MovesHistory.size() > 1) && (piece.getOwner() == winner))
                System.out.println(piece.getID() + ": " + piece.MovesHistory.toString());
        }
        for (ConcretePiece piece : piecesList)
        {
            if ((piece.MovesHistory.size() > 1) && (piece.getOwner() != winner))
                System.out.println(piece.getID() + ": " + piece.MovesHistory.toString());
        }
        System.out.println("***************************************************************************");
        //sort and print by kill count
        piecesList.sort(new killComparator());
        sortSameKillsAndNumber(piecesList, winner);
        for (ConcretePiece piece : piecesList)
        {
            if (piece.getKillCount() > 0)
                System.out.println(piece.getID() + ": " + piece.getKillCount() + " kills");
        }
        System.out.println("***************************************************************************");
        //sort and print by distance
        piecesList.sort(new stepComparator());
        sortSameStepsAndNumber(piecesList, winner);
        for (ConcretePiece piece : piecesList)
        {
            if (piece.getStepCounter() > 0)
                System.out.println(piece.getID() + ": " + piece.getStepCounter() + " squares");
        }
        System.out.println("***************************************************************************");
        //sort and print tails count
        checkMoveHistory();
        positionsUsedArr.sort(new tailsComparator());
        for(int i = positionsUsedArr.size() -1 ; i >0 ; i--)
        {
            if (positionsUsedArr.get(i).getColumn() == positionsUsedArr.get(i-1).getColumn() &&
                    positionsUsedArr.get(i).getRow() == positionsUsedArr.get(i-1).getRow())
                positionsUsedArr.remove(i-1);
        }
        for (Position position : positionsUsedArr)
        {
            if (position.lengthPieceWasHere() > 1)
                System.out.println(position.toString() + position.lengthPieceWasHere() + " pieces");
        }
        System.out.println("***************************************************************************");
    }

    public void sortSameKillsAndNumber(ArrayList<ConcretePiece> piecesList, ConcretePlayer winner)
    {
        //sort by winner
        for (int i = 0; i < piecesList.size() - 1; i++)
        {
            if (piecesList.get(i).getKillCount() != piecesList.get(i + 1).getKillCount())
                continue;
            if ((piecesList.get(i).getID().substring(1).equals(piecesList.get(i + 1).getID().substring(1))) && (piecesList.get(i).getOwner() != winner))
            {
                //System.out.println("the string is:" + piecesList.get(i).getID() + "   the substring is:" + piecesList.get(i).getID().substring(1));
                ConcretePiece temp = piecesList.get(i);
                piecesList.set(i, piecesList.get(i + 1));
                piecesList.set(i + 1, temp);
            }
        }
    }
    public void sortSameStepsAndNumber(ArrayList<ConcretePiece> piecesList, ConcretePlayer winner)
    {
        //sort by winner
        for (int i = 0; i < piecesList.size() - 1; i++)
        {
            if (piecesList.get(i).getStepCounter() != piecesList.get(i + 1).getStepCounter())
                continue;
            if ((piecesList.get(i).getID().substring(1).equals(piecesList.get(i + 1).getID().substring(1))) && (piecesList.get(i).getOwner() != winner))
            {
                //System.out.println("the string is:" + piecesList.get(i).getID() + "   the substring is:" + piecesList.get(i).getID().substring(1));
                ConcretePiece temp = piecesList.get(i);
                piecesList.set(i, piecesList.get(i + 1));
                piecesList.set(i + 1, temp);
            }
        }
    }
    public void checkMoveHistory()
    {
        // add to positionsUsed all the moves history
        for(ConcretePiece piece:piecesList)
            positionsUsed.addAll(piece.MovesHistory);

        positionsUsedArr.addAll(positionsUsed);
        // add to positionsUsed all the pieces that were in the same position
        for (Position position : positionsUsedArr)
            for (ConcretePiece piece : piecesList)
                for(Position position1 : piece.MovesHistory)
                    if (position.getRow() == position1.getRow() && position.getColumn() == position1.getColumn())
                        position.addPieceWasHere(piece);
    }
}



//todo: check invalid inputs to the methods
//todo: check better if the eat is perfect
//todo: add is game finished to the correct place and remove 3 print lines from him and put outside is a single if
//TODO: fix the kill count that it will only only count kills for the pice that moved
//todo:
