package com.revature.jash.datasource.documents;

public class PlayerStats {
    private int wins; // this will tally the players wins
    private int losses; // this will tally the players losses
    private int totalGamesPlayed; // this will tally the number of games this player has played
    private float winPercentage; // wins/totalGamesPlayed

    public int getWins() {
        return wins;
    }

    public void incrementWins()
    {
        incrementTGP();
        wins++;
    }

    public void incrementlosses()
    {
        incrementTGP();
        losses++;
    }
    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void incrementTGP() {
        totalGamesPlayed++;
    }

    public float getWinPercentage() {
        if(wins !=0)
        winPercentage =  wins/totalGamesPlayed;
        return winPercentage;
    }

}
