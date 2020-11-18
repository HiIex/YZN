package com.example.yzn.entity;

public class IDRequest {
    private String phone;

    public IDRequest(){}

    public IDRequest(String phone){
        this.phone=phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
