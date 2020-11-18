package com.example.yzn.entity;

public class Message {
    private String fromid;
    private String toid;
    private String content;
    private String time;

    public Message(String fromid, String toid, String content, String time) {
        this.fromid = fromid;
        this.toid = toid;
        this.content = content;
        this.time = time;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
