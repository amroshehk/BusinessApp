package com.digits.business.entities;

import java.io.Serializable;

public class Business implements Serializable {

    private String businessName;
    private String businessType;
    private String partnershipType;
    private String yearEstablished;
    private String employeesNumber;
    private String turnover;
    private String address;
    private String country;
    private String city;
    private String pinCode;
    private String products;
    private String keywords;

    public Business() {
    }

    public Business(String businessName, String businessType, String partnershipType,
                    String yearEstablished, String employeesNumber, String turnover,
                    String address, String country, String city, String pinCode,
                    String products, String keywords) {

        this.businessName = businessName;
        this.businessType = businessType;
        this.partnershipType = partnershipType;
        this.yearEstablished = yearEstablished;
        this.employeesNumber = employeesNumber;
        this.turnover = turnover;
        this.address = address;
        this.country = country;
        this.city = city;
        this.pinCode = pinCode;
        this.products = products;
        this.keywords = keywords;

    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getPartnershipType() {
        return partnershipType;
    }

    public void setPartnershipType(String partnershipType) {
        this.partnershipType = partnershipType;
    }

    public String getYearEstablished() {
        return yearEstablished;
    }

    public void setYearEstablished(String yearEstablished) {
        this.yearEstablished = yearEstablished;
    }

    public String getEmployeesNumber() {
        return employeesNumber;
    }

    public void setEmployeesNumber(String employeesNumber) {
        this.employeesNumber = employeesNumber;
    }

    public String getTurnover() {
        return turnover;
    }

    public void setTurnover(String turnover) {
        this.turnover = turnover;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }


}
