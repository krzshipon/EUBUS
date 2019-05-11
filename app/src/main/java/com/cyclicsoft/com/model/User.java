package com.cyclicsoft.com.model;

public class User {
    private String name,phone,password,email;



    public User(){

    }
    public User(String name, String email, String phone, String password){
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }


    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }


    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }



    public String getPhone(String phone){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }



    public String getPassword(String password){
        return password;
    }
    public void setPassword(String phone){
        this.password = password;
    }
}
