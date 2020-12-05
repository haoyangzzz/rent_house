package com.house.rent.mapper;


import com.house.rent.model.ZsResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SamTaskMapper {
    List<ZsResponse> find();
}
