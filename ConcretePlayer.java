public class ConcretePlayer implements Player
{
    private final boolean isPlayerOne;
    private int wins=0;
    private int piecesRemain;



    public ConcretePlayer(boolean isPlayerOne, int piecesRemain)
    {
        this.isPlayerOne = isPlayerOne;
        this.piecesRemain = piecesRemain;
    }
    public int getPiecesRemain()
    {
        return piecesRemain;
    }
    public void minusPiecesRemain() {this.piecesRemain--;}
    @Override
    public boolean isPlayerOne()
    {
        return isPlayerOne;
    }

    @Override
    public int getWins()
    {
        return this.wins;
    }
    public void addWin()
    {
        this.wins++;
    }
}
