import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Position
{
    private int row;
    private int column;

    //private ArrayList<ConcretePiece> pieceWasHere = new ArrayList<>();
    private Set<ConcretePiece> pieceWasHere = new HashSet<>();
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
    public String toString()
    {
        return "(" + column + ", " + row + ")";
    }

    public int lengthPieceWasHere()
    {
        return pieceWasHere.size();
    }
    public void addPieceWasHere(ConcretePiece piece)
    {
        pieceWasHere.add(piece);
    }
}

class tailsComparator implements Comparator<Position>
{
    public int compare(Position a, Position b)
    {
        if(a.lengthPieceWasHere() != b.lengthPieceWasHere())
            return b.lengthPieceWasHere() - a.lengthPieceWasHere();
        if (a.getColumn() != b.getColumn())
            return a.getColumn() - b.getColumn();
        if (a.getRow() != b.getRow())
            return a.getRow() - b.getRow();
        return 0;
    }
}