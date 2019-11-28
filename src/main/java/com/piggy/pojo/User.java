package com.piggy.pojo;

import java.io.Serializable;

public class User implements Serializable{
    private Integer d_id;
    private String d_name;
    private Integer d_age;
    private String d_sex;
    private String d_password;

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "d_id=" + d_id +
                ", d_name='" + d_name + '\'' +
                ", d_age=" + d_age +
                ", d_sex='" + d_sex + '\'' +
                ", d_password='" + d_password + '\'' +
                '}';
    }

    public User(Integer d_id, String d_name, Integer d_age, String d_sex, String d_password) {
        this.d_id = d_id;
        this.d_name = d_name;
        this.d_age = d_age;
        this.d_sex = d_sex;
        this.d_password = d_password;
    }

    public Integer getD_id() {
        return d_id;
    }

    public void setD_id(Integer d_id) {
        this.d_id = d_id;
    }

    public String getD_name() {
        return d_name;
    }

    public void setD_name(String d_name) {
        this.d_name = d_name;
    }

    public Integer getD_age() {
        return d_age;
    }

    public void setD_age(Integer d_age) {
        this.d_age = d_age;
    }

    public String getD_sex() {
        return d_sex;
    }

    public void setD_sex(String d_sex) {
        this.d_sex = d_sex;
    }

    public String getD_password() {
        return d_password;
    }

    public void setD_password(String d_password) {
        this.d_password = d_password;
    }
}
