package com.house.rent.controller;

import com.house.rent.DTO.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BaseApiController {

    public static Result success() {
        return success(null);
    }

    public static Result success(Object detail) {
        Result result = new Result();
        result.setCode(200);
        result.setDescription("SUCCESS");
        result.setDetail(detail);
        return result;
    }

    public static Result error(String description) {
        Result result = new Result();
        result.setCode(201);
        result.setDescription(description);
        return result;
    }

}
