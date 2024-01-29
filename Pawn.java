import java.util.Comparator;

public class Pawn extends ConcretePiece
{
    private int KillCount;
    public boolean IsALive;


    public Pawn(Player owner,String ID)
    {
        super(owner,ID);
        this.IsALive = true;
        if(owner.isPlayerOne())
            this.type = "♙";
        else
            this.type = "♟";
        this.KillCount = 0;
    }

    public void setALive(boolean ALive) {IsALive = ALive;}

    public boolean isALive() {return IsALive;}
    @Override
    public int getKillCount() {return KillCount;}
    public void reduceKillCount() {KillCount--;}
    public void AddKillCount() {KillCount++;}

}
