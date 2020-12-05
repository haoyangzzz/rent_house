package com.house.rent.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HouseDTO {
    private Integer hid;
    private String name;
    private List<String> city;
    private Integer money;
    private String area;
    private String xqname;
    private String housemodel;
    private String cx;
    private List<String> tag;
    private String czfs;
    private List<String> fwpt;
    private Integer dt;
    private String picture;
    private Date publishTime;
    private Integer pid;
    private Integer state;
}
