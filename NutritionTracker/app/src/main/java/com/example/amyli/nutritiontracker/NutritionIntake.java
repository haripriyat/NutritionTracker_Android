package com.example.amyli.nutritiontracker;

public class NutritionIntake {
    long id;
    String nutrientName;
    String dateTime; //datetime of intake
    String consumedAmount;

    public long getId(){return id;}
    public void setId(long p){id = p;}
    public String getName(){return nutrientName;}
    public void setName(String n){nutrientName = n;}
    public String getDateTime(){return dateTime;}
    public void setDateTime(String d){dateTime = d;}
    public String getConsumedAmount(){return consumedAmount;}
    public void setConsumedAmount(String l){consumedAmount = l;}
}
