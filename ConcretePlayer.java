public class ConcretePlayer implements Player
{
    private final boolean isPlayerOne;
    private int wins=0;
    private int piecesRemain;


    public ConcretePlayer(boolean isPlayerOne, int picesRemain)
    {
        this.isPlayerOne = isPlayerOne;
        this.piecesRemain = picesRemain;
    }
    public int getPiecesRemain()
    {
        return piecesRemain;
    }
    public void setPicesRemain()
    {
        this.piecesRemain--;
    }
    @Override
    public boolean isPlayerOne()
    {
        return isPlayerOne;
    }

    @Override
    public int getWins()
    {
        return wins;
    }
    public void addWin()
    {
        this.wins++;
    }
}
