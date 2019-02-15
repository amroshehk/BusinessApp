package com.firatnet.businessapp.entities;

public class Register {

    String name;
    String email;
    String phone;
    String country;
    String password;
    String ipaddres;

    public Register(String name, String email, String phone, String country, String password, String ipaddres) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.password = password;
        this.ipaddres = ipaddres;
    }
    public Register() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPassword() {
        return password;
    }



    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpaddres() {
        return ipaddres;
    }

    public void setIpaddres(String ipaddres) {
        this.ipaddres = ipaddres;
    }
}
