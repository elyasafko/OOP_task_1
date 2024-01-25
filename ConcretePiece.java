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


    public ConcretePiece(Player owner)
    {
        this.owner = owner;
        this.StepCounter = 0;
        this.MovesHistory= new ArrayList<Position>();
    }

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