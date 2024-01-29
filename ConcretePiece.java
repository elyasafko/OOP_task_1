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
    private final String ID;
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
    public void addStepCounter(int steps)
    {
        this.StepCounter += steps;
    }

    public void subStepCounter(int steps)
    {
        this.StepCounter -= steps;
    }
    public int getKillCount() {return 0;}
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
class killComparator implements Comparator<ConcretePiece>
{
    public int compare(ConcretePiece a, ConcretePiece b)
    {
        int aa = Integer.parseInt(a.getID().substring(1));
        int bb = Integer.parseInt(b.getID().substring(1));

        if(a.getKillCount()!=b.getKillCount())
            return b.getKillCount() - a.getKillCount();
        if (aa != bb)
            return aa - bb;
        return 0;
    }
}
class disComparator implements Comparator<ConcretePiece>
{
    public int compare(ConcretePiece a, ConcretePiece b)
    {
        return -1;
    }
}