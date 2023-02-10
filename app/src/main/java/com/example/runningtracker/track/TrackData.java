package com.example.runningtracker.track;

public class TrackData {

    private String date;
    private String distance;
    private String time;
    private String avgSpeed;
    private String maxSpeed;
    private String comment;
    private String username;
    private String height;
    private String weight;

    public TrackData(String date, String username, String height, String weight, String distance, String time, String avgSpeed, String maxSpeed, String comment){
        this.date = date;
        this.username = username;
        this.height = height;
        this.weight = weight;
        this.distance = distance;
        this.time = time;
        this.avgSpeed = avgSpeed;
        this.maxSpeed = maxSpeed;
        this.comment = comment;
    }

    //Getter
    public String getDate(){
        return date;
    }
    public String getDistance(){
        return distance;
    }
    public String getTime(){
        return time;
    }
    public String getAvgSpeed(){
        return avgSpeed;
    }
    public String getMaxSpeed(){
        return maxSpeed;
    }
    public String getComment() {
        return comment;
    }
    public String getUsername() {
        return username;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }
}
