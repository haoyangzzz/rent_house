package com.house.rent.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private Integer id;
    private Integer pid;
    private String messagebody;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date ptime;
    private Integer rid;
    private Integer state;
}
