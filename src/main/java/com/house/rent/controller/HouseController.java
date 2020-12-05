package com.house.rent.controller;

import com.house.rent.DTO.Result;
import com.house.rent.DTO.HouseDTO;
import com.house.rent.constants.MailConstants;
import com.house.rent.mapper.HouseMapper;
import com.house.rent.model.House;
import com.house.rent.model.Message;
import com.house.rent.model.Order;
import com.house.rent.service.HouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/house")
public class HouseController {
    @Autowired
    private HouseService houseService;
    @Autowired
    private BaseApiController baseApiController;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/search/{keywords}/{pageNo}/{pageSize}")
    public Result search(@PathVariable("keywords") String keywords,
                         @PathVariable("pageNo")@RequestParam(defaultValue = "0") int pageNo,
                         @PathVariable("pageSize") int pageSize) throws IOException {
//        System.out.println("++++++++++++++++++++++++++++++++++++");
        return baseApiController.success(houseService.getHouseByEs(keywords,pageNo,pageSize));
    }
    @PostMapping("/find")
    public Result findHouseById(@RequestBody House house){
        List<House> houseByIdAndState = houseMapper.findHouseByIdAndState(house);
        return baseApiController.success(houseByIdAndState);
    }
    @PostMapping("/find1")
    public Result findHouseByOrder(@RequestBody House house){
        List<House> houses = houseMapper.findHouseByOrder(house);
        return baseApiController.success(houses);
    }
    @PostMapping("findAllHouses")
    public Result findAllHouses(){
        List<House> allHouse = houseMapper.findAllHouse();
        return baseApiController.success(allHouse);
    }
    @PostMapping("/add")
    public Result addHouse(@RequestBody HouseDTO houseDTO) throws Exception {
//        System.out.println(houseDTO);
        Integer flag = houseService.addHouse(houseDTO);
        if (flag==1) {
            return baseApiController.success();
        }else {return baseApiController.error("插入失败！");}
    }
    @PutMapping("/update")
    public Result updateHouse(@RequestBody HouseDTO houseDTO){
        Integer flag = 1;
                houseService.updateHouse(houseDTO);
        if (flag==1){
            return baseApiController.success();
        }
        return baseApiController.error("更新失败!");
    }
    @PutMapping("/updateState")
    public Result updateHouseState(@RequestBody House house){
        if (house.getState() == 0) {
            houseMapper.deleteOrder(house.getHid());
        }
        houseService.insertMessageForAng(house);
        Integer flag = houseService.updateHouseState(house.getHid(),house.getState());
        if (flag==1){
            return baseApiController.success();
        }
        return baseApiController.error("更新失败!");
    }
    @PostMapping("/order")
    @Transactional(rollbackFor = Exception.class)
    public Result orderHouse(@RequestBody Order order){
//        Message message = new Message();
        houseService.insertMessage(order);
        String msgId = UUID.randomUUID().toString();
        House house = new House();
        house.setName("233333");
        Integer flagofadd = houseService.orderHouse(order);
        Integer flagofupdate = houseService.updateHouseState(order.getHid(), 1);
        if (flagofadd == 1 && flagofupdate == 1) {
            try {
                rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, house, new CorrelationData(msgId));
            }catch (Exception e){
                log.error(e.getMessage());
            }
            return baseApiController.success();
        }else {
            return baseApiController.error("预定失败");
        }
    }
    @DeleteMapping("/{hid}")
    public Result deleteHouse(@PathVariable Integer hid){
        if (houseMapper.deleteHouseByHid(hid) == 1){
            return baseApiController.success();
        }
        return baseApiController.error("删除失败");
    }
}
