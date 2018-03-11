package com.leagueofshadows.abhyas;



class Student {
    private String id;
    private String name;
    private String fathername;
    private String standard;
    private String roll;
    private String dob;
    Student(String id,String name,String fathername,String standard,String roll,String dob)
    {
        this.id=id;
        this.name=name;
        this.fathername = fathername;
        this.standard=standard;
        this.roll= roll;
        this.dob=dob;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    String getFathername() {
        return fathername;
    }

    String getRoll() {
        return roll;
    }

    String getDob() {
        return dob;
    }

    public String getStandard() {
        return standard;
    }
}
