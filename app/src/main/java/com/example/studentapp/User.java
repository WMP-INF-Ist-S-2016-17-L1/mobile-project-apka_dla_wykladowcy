package com.example.studentapp;

public class User {
    public String name, email;
    public boolean lecturer;

    public User(){

    }

    public User(String name, String email, boolean lecturer){
        this.name = name;
        this.email = email;
        this.lecturer = lecturer;
    }

    public boolean getLecturer(){
        return this.lecturer;
    }
}
