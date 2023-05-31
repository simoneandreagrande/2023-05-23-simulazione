package it.polito.tdp.baseball.model;

public class Manager {

    private Integer iD;
    private String playerID;
    private Integer year;
    private String teamCode;
    private Integer teamID;
    private Integer managerOrder;
    private Integer games;
    private Integer wins;
    private Integer losses;
    private Integer teamRank;


    public Integer getID(){
        return iD;
    }

    public void setID(Integer iD){
        this.iD=iD;
    }

    public String getPlayerID(){
        return playerID;
    }

    public void setPlayerID(String playerID){
        this.playerID=playerID;
    }

    public Integer getYear(){
        return year;
    }

    public void setYear(Integer year){
        this.year=year;
    }

    public String getTeamCode(){
        return teamCode;
    }

    public void setTeamCode(String teamCode){
        this.teamCode=teamCode;
    }

    public Integer getTeamID(){
        return teamID;
    }

    public void setTeamID(Integer teamID){
        this.teamID=teamID;
    }

    public Integer getManagerOrder(){
        return managerOrder;
    }

    public void setManagerOrder(Integer managerOrder){
        this.managerOrder=managerOrder;
    }

    public Integer getGames(){
        return games;
    }

    public void setGames(Integer games){
        this.games=games;
    }

    public Integer getWins(){
        return wins;
    }

    public void setWins(Integer wins){
        this.wins=wins;
    }

    public Integer getLosses(){
        return losses;
    }

    public void setLosses(Integer losses){
        this.losses=losses;
    }

    public Integer getTeamRank(){
        return teamRank;
    }

    public void setTeamRank(Integer teamRank){
        this.teamRank=teamRank;
    }
}


