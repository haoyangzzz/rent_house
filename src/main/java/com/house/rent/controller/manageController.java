package com.house.rent.controller;

import com.house.rent.DTO.Result;
import com.house.rent.model.User;
import com.house.rent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/manage")
public class manageController {
    @Autowired
    private UserService userService;
    @Autowired
    private BaseApiController baseApiController;

    @PostMapping("/findUser")
    public Result test(){
        return baseApiController.success("fail");
    }
}
