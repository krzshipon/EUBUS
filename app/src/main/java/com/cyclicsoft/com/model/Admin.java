package com.cyclicsoft.com.model;

public class Admin {
    private String aID;
    private String aName;
    private String password;
    private String aEmail;
    private String lat;
    private String lng;
    private String phone;


    public Admin() {
    }


    public Admin(String aID, String aName, String password, String aEmail, String lat, String lng, String phone) {
        this.aID = aID;
        this.aName = aName;
        this.password = password;
        this.aEmail = aEmail;
        this.lat = lat;
        this.lng = lng;
        this.phone = phone;
    }

    public String getaID() {
        return aID;
    }

    public void setaID(String aID) {
        this.aID = aID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaEmail() {
        return aEmail;
    }

    public void setaEmail(String aEmail) {
        this.aEmail = aEmail;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "aID='" + aID + '\'' +
                ", aName='" + aName + '\'' +
                ", aEmail='" + aEmail + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
