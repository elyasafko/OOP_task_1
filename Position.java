public class Position
{
    private int row;
    private int column;
    private boolean[] BeenThereDoneThat;

    public Position(int column, int row)
    {
        this.row = row;
        this.column = column;
        this.BeenThereDoneThat = new boolean[36];
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