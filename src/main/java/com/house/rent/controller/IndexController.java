package com.house.rent.controller;

import com.house.rent.DTO.RespPageBean;
import com.house.rent.DTO.Result;
import com.house.rent.mapper.HouseMapper;
import com.house.rent.model.House;
import com.house.rent.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/index/find")
public class IndexController {
    @Autowired
    private BaseApiController baseApiController;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private HouseService houseService;
    @GetMapping("/")
    public RespPageBean index(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer size,
                              House house,int[] moneyqj,
                              @RequestParam(required = false) String[] pxList){
        return houseService.getHouse(page,size,house,moneyqj,pxList);
    }
}
