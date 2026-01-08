package com.stayhealth.model;

public class UserProfile {
    public String name;
    public String email;
    public Integer age;
    public Integer weight;
    public Integer height;
    public String chronic;
    public Integer kcal;

    public UserProfile() {}

    public UserProfile(String name, String email, Integer age, Integer weight, Integer height, String chronic, Integer kcal) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.chronic = chronic;
        this.kcal = kcal;
    }
}
