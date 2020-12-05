package com.house.rent.model;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Integer id;
    private String pname;
    private String username;
    private String password;
    private Date createtime;
    private String token;
    private String userface;
    private Integer type;
}
