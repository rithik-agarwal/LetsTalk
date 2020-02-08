package com.example.letstalk;

import java.net.URL;

public class User {
    private String id;
    private String username;
    private String dpurl;
    private String status;
    private String state;
    private String incognito;
    public User(String id,String username,String dpurl,String status,String state)
    {
        this.dpurl=dpurl;
        this.id=id;
        this.username=username;
        this.status=status;
        this.state=state;

    }
    public User()
    {

    }

    public String getId() {
        return id;
    }

    public String getDpurl() {
        return dpurl;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus(){return status;}



    public void setDpurl(String dpurl) {
        this.dpurl = dpurl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
