import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
this class should be an abstract class, and the concrete pieces should extend it
*/

public abstract class ConcretePiece implements Piece
{
    final protected Player owner;
    protected String type;
    private String ID;
    protected int StepCounter;
    protected ArrayList<Position> MovesHistory;


    public ConcretePiece(Player owner,String ID)
    {
        this.owner = owner;
        this.StepCounter = 0;
        this.ID = ID;
        this.MovesHistory= new ArrayList<Position>();
    }
    public String getID() {return this.ID;}
    @Override
    public Player getOwner()
    {
        return this.owner;
    }

    @Override
    public String getType()
    {
        return this.type;
    }
    
    public int getStepCounter()
    {
        return this.StepCounter;
    }
    public void setStepCounter(int steps)
    {
        this.StepCounter += steps;
    }

}




//Comparators
class movesComparator implements Comparator<ConcretePiece>
{
    public int compare(ConcretePiece a, ConcretePiece b)
    {
        if(a.MovesHistory.size()!=b.MovesHistory.size())
            return a.MovesHistory.size() - b.MovesHistory.size();
        int aa = Integer.parseInt(a.getID().substring(1));
        int bb = Integer.parseInt(b.getID().substring(1));
        return aa - bb;
    }
}
class disComparator implements Comparator<ConcretePiece>
{
    public int compare(ConcretePiece a, ConcretePiece b)
    {
        return -1;
    }
}