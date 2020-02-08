package com.example.letstalk;

import com.google.firebase.database.Exclude;

public class Chat {
    public String receiver;
    public String sender;
    public String message;
    public boolean isseen;
    public String key;
    public String incognito;

    public Chat(String r,String s,String m)
    {
        receiver=r;
        sender=s;
        message=m;
    }
    public Chat()
    {

    }
    public String getReceiver()
    {
        return receiver;
    }
    public String getSender()
    {
        return sender;
    }
    public String getMessage()
    {
        return message;
    }

    @Exclude
    public String getKey(){return key;}

    public Boolean getIsseen() {
        return isseen;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    @Exclude
    public void setKey(String key){this.key=key;}



    public void setIsseen(Boolean isseen) {
        this.isseen = isseen;
    }
}
