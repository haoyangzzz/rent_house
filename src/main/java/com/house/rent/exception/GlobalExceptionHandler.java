package com.house.rent.exception;

import com.house.rent.DTO.Result;
import com.house.rent.controller.BaseApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private BaseApiController baseApiController;
    @ExceptionHandler(Exception.class)
    public Result Exception(Exception e){
        if (e instanceof RuntimeException){
            return baseApiController.error("服务器炸了！!请联系管理员");
        }
        return baseApiController.error("操作失败！请联系管理员");
    }
}
