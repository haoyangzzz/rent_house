package com.house.rent.mapper;

import com.house.rent.model.House;
import com.house.rent.model.Order;
import com.house.rent.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface HouseMapper {
    Integer addHouse(House house);
    List<House> findHouse(@Param("page") Integer page, @Param("size") Integer size,
                          @Param("house") House house,@Param("moneyqj") int[] moneyqj,
                          @Param("pxList") String[] pxList);
    List<House> findHouseByIdAndState(@Param("house") House house);

    List<House> findHouseByOrder(@Param("house") House house);

    List<House> findAllHouse();

    Integer findPidByHid(@Param("hid") Integer hid);

    Long getTotal(@Param("house") House house,@Param("moneyqj") int[] moneyqj);

    Integer addorder(Order order);

    Integer updateHouse(@Param("hid") Integer hid,@Param("state") Integer state);

    Integer deleteHouseByHid(@Param("hid") Integer hid);

    Integer updateHouseInfo(@Param("house") House house);

    void deleteOrder(@Param("hid") Integer hid);

    House getHouseById(Integer empId);
}
