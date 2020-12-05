package com.house.rent.interceptor;

import com.house.rent.mapper.UserMapper;
import com.house.rent.model.User;
import com.house.rent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class SessionInterceptor implements HandlerInterceptor {
    @Autowired
    private UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null&&cookies.length !=0){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);//token就像服务器发给有信息的用户的一张通行证
                    if(user != null){
                        request.getSession().setAttribute("user", user);
//                        Authentication authentication =
//                                new UsernamePasswordAuthenticationToken(user,user.getPassword(), new UserService().getAuthorities(user.getId()));
//                        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
