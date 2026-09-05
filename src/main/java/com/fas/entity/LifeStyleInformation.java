package com.fas.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;


@Entity
@Table(name = "Lifestyle_Information")
public class LifeStyleInformation {

    @Id
    @Column(name = "Client_ID")
    private Long clientId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "Client_ID")
    @JsonIgnore
    private Client client;

    @Column(name = "Meals_per_Day", precision = 2)
    @Min(1)
    private Integer mealsPerDay;

    @Column(name = "Budget", precision = 10, scale = 2)
    private BigDecimal budget;

    @Column(name = "Breakfast", length = 100)
    private String breakfast;

    @Column(name = "Lunch", length = 100)
    private String lunch;

    @Column(name = "Dinner", length = 100)
    private String dinner;

    @Column(name = "Snacks", length = 100)
    private String snacks;

    @Column(name = "Drinks", length = 100)
    private String drinks;

    @Column(name = "Bad_Habits", length = 200)
    private String badHabits;

    @Column(name = "Sleep_Hours", length = 20)
    private String sleepHours;

    @Column(name = "Food_Dislike", length = 250)
    private String foodDislike;


    public LifeStyleInformation(){}


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Client getClient(){
        return client;
    }


    public void setClient(Client client){
        this.client = client;
    }


    public Integer getMealsPerDay(){
        return mealsPerDay;
    }


    public void setMealsPerDay(Integer mealsPerDay){
        this.mealsPerDay = mealsPerDay;
    }


    public BigDecimal getBudget(){
        return budget;
    }


    public void setBudget(BigDecimal budget){
        this.budget = budget;
    }


    public String getBreakfast(){
        return breakfast;
    }


    public void setBreakfast(String breakfast){
        this.breakfast = breakfast;
    }


    public String getLunch(){
        return lunch;
    }


    public void setLunch(String lunch){
        this.lunch = lunch;
    }


    public String getDinner(){
        return dinner;
    }


    public void setDinner(String dinner){
        this.dinner = dinner;
    }


    public String getSnacks(){
        return snacks;
    }


    public void setSnacks(String snacks){
        this.snacks = snacks;
    }


    public String getDrinks(){
        return drinks;
    }


    public void setDrinks(String drinks){
        this.drinks = drinks;
    }


    public String getBadHabits(){
        return badHabits;
    }


    public void setBadHabits(String badHabits){
        this.badHabits = badHabits;
    }


    public String getSleepHours(){
        return sleepHours;
    }


    public void setSleepHours(String sleepHours){
        this.sleepHours = sleepHours;
    }


    public String getFoodDislike(){
        return foodDislike;
    }


    public void setFoodDislike(String foodDislike){
        this.foodDislike = foodDislike;
    }
}