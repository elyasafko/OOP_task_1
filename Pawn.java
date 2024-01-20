public class Pawn extends ConcretePiece
{
    public int KillCount;
    public boolean IsALive;

    public Pawn(Player owner)
    {
        super(owner);
        this.IsALive = true;
        if(owner.isPlayerOne())
            this.type = "♙";
        else
            this.type = "♟";
        this.KillCount = 0;
    }
    public void AddKillCount()
    {
        KillCount++;
    }

    public void setALive(boolean ALive) {
        IsALive = ALive;
    }

    public boolean isALive() {
        return IsALive;
    }






}