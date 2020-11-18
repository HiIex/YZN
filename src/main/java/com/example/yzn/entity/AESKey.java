package com.example.yzn.entity;

public class AESKey {
    private String date;
    private String aeskey;

    public AESKey(String date,String aeskey){
        this.aeskey=aeskey;
        this.date=date;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAeskey() {
        return aeskey;
    }

    public void setAeskey(String aeskey) {
        this.aeskey = aeskey;
    }
}
