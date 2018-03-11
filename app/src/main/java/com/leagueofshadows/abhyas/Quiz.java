package com.leagueofshadows.abhyas;



public class Quiz {
    private String name;
    private int marks;
    private int total;
    Quiz(String name,int marks,int total)
    {
        this.name=name;
        this.marks=marks;
        this.total=total;
    }

    public int getMarks() {
        return marks;
    }

    public int getTotal() {
        return total;
    }

    public String getName() {
        return name;
    }
}
