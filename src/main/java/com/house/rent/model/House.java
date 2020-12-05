package com.house.rent.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class House implements Serializable {
    private Integer hid;
    private String name;
    private String addressCity;
    private String addressQx;
    private String addressJd;
    private Integer money;
    private String area;
    private String xqname;
    private String housemodel;
    private String czfs;
    private String cx;
    private String tag;
    private Integer ptChuang;
    private Integer ptXyj;
    private Integer ptKt;
    private Integer ptBx;
    private Integer ptWsj;
    private Integer ptKzf;
    private Integer dt;
    private String picture;
    private Date publishTime;
    private Integer pid;
    private Integer state;
}
