package com.firatnet.businessapp.entities;

public class Register {

    String id;
    String name;
    String email;
    String created_at;
    String updated_at;

    String phone;
    String country;

    String generated_id;
    String status;
    String photo_url;

    String ip;
    String default_file;

    String password;

    public Register(String name, String email, String phone, String country, String ip, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.country = country;
        this.ip = ip;
        this.password = password;
    }

    public Register(String id, String name, String email, String created_at, String updated_at, String phone, String country, String generated_id, String status, String photo_url, String ip, String default_file) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.phone = phone;
        this.country = country;
        this.generated_id = generated_id;
        this.status = status;
        this.photo_url = photo_url;
        this.ip = ip;
        this.default_file = default_file;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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

    public String getGenerated_id() {
        return generated_id;
    }

    public void setGenerated_id(String generated_id) {
        this.generated_id = generated_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDefault_file() {
        return default_file;
    }

    public void setDefault_file(String default_file) {
        this.default_file = default_file;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
