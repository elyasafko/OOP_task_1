import java.util.ArrayList;
public class Position
{
    private int row;
    private int column;

    private ArrayList<ConcretePiece> pieceWasHere = new ArrayList<>();

    public Position(int column, int row)
    {
        this.row = row;
        this.column = column;
    }

    public int getRow()
    {
        return row;
    }
    public int getColumn()
    {
        return column;
    }
    public void setColumn(int column)
    {
        this.column = column;
    }
    public void setRow(int row)
    {
        this.row = row;
    }
    public boolean equals(Position other)
    {
        return this.row == other.row && this.column == other.column;
    }
    public String toString()
    {
        return "(" + row + ", " + column + ")";
    }

    public int lengthPieceWasHere()
    {
        return pieceWasHere.size();
    }
    public Boolean containsPieceWasHere(ConcretePiece piece)
    {
        return pieceWasHere.contains(piece);
    }
    public void addPieceWasHere(ConcretePiece piece)
    {
        pieceWasHere.add(piece);
    }
    public void removeLastPieceWasHere()
    {
        pieceWasHere.removeLast();
    }
}