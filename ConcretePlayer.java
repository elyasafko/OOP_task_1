public class ConcretePlayer implements Player
{
    private boolean isPlayerOne=true;
    private int wins=0;
    private int picesRemain;


    public ConcretePlayer(boolean isPlayerOne, int picesRemain)
    {
        this.isPlayerOne = isPlayerOne;
        this.picesRemain = picesRemain;
    }
    public int getPicesRemain()
    {
        return picesRemain;
    }
    public void setPicesRemain()
    {
        this.picesRemain --;
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
