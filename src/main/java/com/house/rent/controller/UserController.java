package com.house.rent.controller;

import com.house.rent.DTO.Result;
import com.house.rent.mapper.UserMapper;
import com.house.rent.model.Message;
import com.house.rent.model.User;
import com.house.rent.service.UserService;
import com.house.rent.utils.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BaseApiController baseApiController;
    @PostMapping("/login")
    public Result test(@RequestBody User user, HttpServletResponse response){
        Integer flag = userService.checkUserAndPwd(user);
        if(flag == 1){
            String token = UUID.randomUUID().toString();
            userMapper.updateUser(user.getUsername(),token);
            User dbUser = userService.checkUsername(user.getUsername());
            response.addCookie(new Cookie("token", dbUser.getToken()));
            return baseApiController.success(dbUser.getToken());
        }
        return baseApiController.error("fail");
    }
    @PostMapping("/loginForAdmin")
    public Result loginForAdmin(@RequestBody User user){
        if ("admin".equals(user.getUsername())&&"123".equals(user.getPassword())) {
            return baseApiController.success();
        }
        return baseApiController.error("fail");
    }
    @PostMapping("/register")
    public String test1(@RequestBody User user){
        User dbuser = new User();
        dbuser.setPname(user.getPname());
        dbuser.setUsername(user.getUsername());
        dbuser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
//        dbuser.setToken(token);
        dbuser.setUserface("/upload/20200601204348youke.jpg");
        dbuser.setCreatetime(new Date());
        int i = userService.addUser(dbuser);
        if (i == 1){
            return "success";
        }else {
            return "fail";
        }
    }
    @PostMapping("/checkpname")
    public Integer CheckPname(@RequestBody User user){
        User checkUser = new User();
        checkUser.setPname(user.getPname());
        return userService.checkPname(checkUser.getPname());
    }
    @PostMapping("/checkusername")
    public Integer CheckUsername(@RequestBody User user){
        User checkUser = new User();
        checkUser.setUsername(user.getUsername());
        User dbUser = userService.checkUsername(checkUser.getUsername());
        if (dbUser!=null){
            return 1;
        }else {
            return 0;
        }
    }
    @PostMapping("/dologin")
    public User dologin(@RequestBody String token){
        String dbToken = token.substring(0, token.length()-1);
        User byToken = userMapper.findByToken(dbToken);
        return byToken;
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
//        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
    @GetMapping("/findById")
    public User findById(Integer pid){
        User byId = userMapper.findById(pid);
        return byId;
    }
    @PostMapping("/findAllUsers")
    public Result findAllUsers(){
        List<User> allUser = userMapper.findAllUser();
        return baseApiController.success(allUser);
    }

    @GetMapping("/message/{id}")
    public Result findMessage(@PathVariable Integer id){
        List<Message> message = userMapper.findMessage(id);
        return baseApiController.success(message);
    }
    @PostMapping("/updatemessage")
    public Result updateMessageState(HttpServletRequest request){
//        userMapper.updateMessageState();
        User user = (User) request.getSession().getAttribute("user");
        List<Integer> ids = userMapper.findNotReadMessageIds(user.getId());
        userMapper.updateMessageState(ids);
        return baseApiController.success();
    }
    @GetMapping("/messcount")
    public Result findMessageCount(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        Integer mescount = userMapper.notReadMessageCount(user.getId());
        return baseApiController.success(mescount);
    }
}
