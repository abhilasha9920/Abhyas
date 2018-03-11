package com.leagueofshadows.abhyas;



class Question  {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String id;
    private int ans;
    Question(String id,String question,String option1,String option2,String option3,String option4,int ans)
    {
        this.id=id;
        this.question=question;
        this.option1=option1;
        this.option2=option2;
        this.option3=option3;
        this.option4=option4;
        this.ans=ans;
    }

    String getQuestion() {
        return question;
    }

    String getOption1() {
        return option1;
    }

    String getOption2() {
        return option2;
    }

    String getOption3() {
        return option3;
    }

    String getOption4() {
        return option4;
    }

    int getAns() {
        return ans;
    }

    public String getId() {
        return id;
    }
}
