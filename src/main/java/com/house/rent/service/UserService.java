package com.house.rent.service;

import com.house.rent.mapper.UserMapper;
import com.house.rent.model.User;
import com.house.rent.utils.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public int addUser(User user){
        return userMapper.addUser(user);
    }
    public int checkPname(String pname){
        return userMapper.findByPname(pname);
    }

    public User checkUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public Integer checkUserAndPwd(User user) {
        User byUsername = userMapper.findByUsername(user.getUsername());
        if (byUsername != null){
            boolean matches = new BCryptPasswordEncoder().
                    matches(user.getPassword(), byUsername.getPassword());
            if (matches){
                return 1;
            }else {
                return 0;
            }
        }else {
            return 0;
        }
    }
    public Object findUser2House;
//    public Collection<? extends GrantedAuthority> getAuthorities(int userId){
//        User user = userMapper.findById(userId);
//        List<GrantedAuthority> list = new ArrayList<>();
//        list.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                switch (user.getType()){
//                    case 1:
//                        return "user";
//                    default:
//                        return "admin";
//                }
//            }
//        });
//        return list;
//    }
}
