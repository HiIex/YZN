package com.example.yzn;

public class RSAKey {
    private String id;
    private String clientpk;
    private String clientsk;

    public RSAKey(String id,String clientpk,String clientsk){
        this.id=id;
        this.clientpk=clientpk;
        this.clientsk=clientsk;
    }

    public String getClientpk() {
        return clientpk;
    }

    public void setClientpk(String clientpk) {
        this.clientpk = clientpk;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientsk() {
        return clientsk;
    }

    public void setClientsk(String clientsk) {
        this.clientsk = clientsk;
    }
}
