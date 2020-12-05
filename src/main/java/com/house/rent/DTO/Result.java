package com.house.rent.DTO;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String description;
    private Object detail;
}
