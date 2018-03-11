package com.leagueofshadows.abhyas;

interface Communicator  {
    void modifyTeacher(String id,String name,String standard,int pos);
    void delete(Teacher teacher,int pos);
}
