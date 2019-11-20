package com.synram.morningbucket.Modal;

public class HorizontalCalanderModal {
    int year;
    int monty;
    int day;
    String dayofmonth;


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonty() {
        return monty;
    }

    public void setMonty(int monty) {
        this.monty = monty;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDayofmonth() {
        return dayofmonth;
    }

    public void setDayofmonth(String dayofmonth) {
        this.dayofmonth = dayofmonth;
    }


    public HorizontalCalanderModal(int year, int monty, int day, String dayofmonth) {
        this.year = year;
        this.monty = monty;
        this.day = day;
        this.dayofmonth = dayofmonth;
    }
}
