public class Pawn extends ConcretePiece
{
    public int KillCount;
    public Pawn(Player owner)
    {
        super(owner);
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
}