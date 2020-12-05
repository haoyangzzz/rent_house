package com.house.rent.mapper;

import com.house.rent.model.House;
import com.house.rent.model.Message;
import com.house.rent.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserMapper {
    int addUser(User user);

    int findByPname(String pname);

    User findByUsername(String username);

    User findByToken(String token);

    List<User> findAllUser();

    void updateUser(String username,String token);

    User findById(Integer pid);

    void insertMessage(Message message);

    List<Message> findMessage(@Param("rid") Integer rid);

    List<Integer> findNotReadMessageIds(@Param("rid") Integer rid);

    Integer notReadMessageCount(@Param("rid") Integer rid);

    void updateMessageState(@Param("ids") List<Integer> ids);
//    Integer findMessageCount(@Param("rid") Integer rid,@Param("state") Integer state);

    Integer findRidByHid(@Param("hid") Integer hid);

}
