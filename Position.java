public class Position
{
    private int row;
    private int column;

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

}