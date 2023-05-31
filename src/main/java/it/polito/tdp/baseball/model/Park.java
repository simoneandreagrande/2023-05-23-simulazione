package it.polito.tdp.baseball.model;

public class Park {

    private Integer iD;
    private String parkCode;
    private String parkName;
    private String city;
    private String state;


    public Integer getID(){
        return iD;
    }

    public void setID(Integer iD){
        this.iD=iD;
    }

    public String getParkCode(){
        return parkCode;
    }

    public void setParkCode(String parkCode){
        this.parkCode=parkCode;
    }

    public String getParkName(){
        return parkName;
    }

    public void setParkName(String parkName){
        this.parkName=parkName;
    }

    public String getCity(){
        return city;
    }

    public void setCity(String city){
        this.city=city;
    }

    public String getState(){
        return state;
    }

    public void setState(String state){
        this.state=state;
    }


}


