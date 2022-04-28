package com.example.playpal;




import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class PlayActivity implements Serializable {
    public String sport;
    public String location;
    public String date;
    public String time;
    public List<User> users;
    public String uID;
    private User owner;

    public PlayActivity(){}

    public PlayActivity(List<User> users, String sport, String location, String date, String time,User owner) {
        System.out.println("Constr"+users.get(0).getuID()+"coovv"+users.get(0).getFullName());
        this.sport = sport;
        this.location = location;
        this.date = date;
        this.time = time;
        this.users = new LinkedList<>();
        this.users.addAll(users);
        this.owner = owner;
    }


    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }



    public void addUser(User user){
        users.add(user);
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
