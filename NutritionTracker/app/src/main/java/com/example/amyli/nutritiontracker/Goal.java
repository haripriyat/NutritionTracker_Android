package com.example.amyli.nutritiontracker;

public class Goal {
    long id;
    String nutrientName;
    String goalAmount;

    public long getId(){return id;}
    public void setId(long p){id = p;}
    public String getName(){return nutrientName;}
    public void setName(String n){nutrientName = n;}
    public String getGoalAmount(){return goalAmount;}
    public void setGoalAmount(String d){goalAmount = d;}
}
