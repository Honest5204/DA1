package com.example.musicapplication.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Usre {

    private int id;
    private String dateofbirth;

    private String email;
    private String gender;
    private String name;
    private String profileimgae;

    private String usertype;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileimgae() {
        return profileimgae;
    }

    public void setProfileimgae(String profileimgae) {
        this.profileimgae = profileimgae;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public Usre(int id, String dateofbirth, String email, String gender, String name, String profileimgae, String usertype) {
        this.id = id;
        this.dateofbirth = dateofbirth;
        this.email = email;
        this.gender = gender;
        this.name = name;
        this.profileimgae = profileimgae;
        this.usertype = usertype;
    }


    public Usre() {
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("email", email);
        result.put("gender", gender);
        result.put("dateofbirth", dateofbirth);
        result.put("profileimgae", profileimgae);
        return result;
    }
}
