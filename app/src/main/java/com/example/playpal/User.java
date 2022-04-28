package com.example.playpal;

import java.io.Serializable;

public class User implements Serializable {

    public String uID,fullName,email;
    String count;

    public User(){

    }

    public User(String fullName,String email,String count){
        this.fullName = fullName;
        this.email = email;
        this.count = count;
    }
    public User(String uId,String fullName,String email,String count){
        this.uID = uId;
        this.fullName = fullName;
        this.email = email;
        this.count = count;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
