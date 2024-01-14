public class ConcretePlayer implements Player
{
    private boolean isPlayerOne=true;
    private int wins=0;


    public ConcretePlayer(boolean isPlayerOne)
    {
        this.isPlayerOne = isPlayerOne;
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
