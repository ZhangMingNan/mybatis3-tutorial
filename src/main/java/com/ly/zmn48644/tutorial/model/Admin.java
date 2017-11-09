package com.ly.zmn48644.tutorial.model;

import java.util.Date;

public class Admin {
    private Integer userid;
    private String username;
    private String password;
    private Integer roleid;
    private String encrypt;
    private String lastloginip;
    private Date lastlogintime;
    private String email;
    private String realname;


    @Override
    public String toString() {
        return "Admin{" +
                "userid=" + userid +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roleid=" + roleid +
                ", encrypt='" + encrypt + '\'' +
                ", lastloginip='" + lastloginip + '\'' +
                ", lastlogintime=" + lastlogintime +
                ", email='" + email + '\'' +
                ", realname='" + realname + '\'' +
                '}';
    }

    public Integer getUserid() {

        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(String encrypt) {
        this.encrypt = encrypt;
    }

    public String getLastloginip() {
        return lastloginip;
    }

    public void setLastloginip(String lastloginip) {
        this.lastloginip = lastloginip;
    }

    public Date getLastlogintime() {
        return lastlogintime;
    }

    public void setLastlogintime(Date lastlogintime) {
        this.lastlogintime = lastlogintime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
}
