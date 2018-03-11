package com.leagueofshadows.abhyas;


 class Teacher {
    private String name;
    private String id;
    private String standard;
    Teacher(String id,String name,String standard)
    {
        this.name=name;
        this.id=id;
        this.standard=standard;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getStandard() {
        return standard;
    }
}
