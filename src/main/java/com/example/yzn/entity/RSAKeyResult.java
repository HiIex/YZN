package com.example.yzn.entity;

public class RSAKeyResult {
    private String clientpk;

    public RSAKeyResult(String clientpk){
        this.clientpk=clientpk;
    }


    public String getClientpk() {
        return clientpk;
    }

    public void setClientpk(String clientpk) {
        this.clientpk = clientpk;
    }
}
