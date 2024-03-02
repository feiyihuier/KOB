package com.kob.backend.service.impl.user.account;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_SRC_OUTPeer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Literal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {

        Map <String, String> map = new HashMap<>();
        //长度为0的字符串代表一个空字符串，即没有字符的字符串。它是一个有效的字符串对象，只是它的长度为0。您可以调用字符串方法和操作符对其进行操作，例如获取其长度、拼接其他字符串等。
        //而null表示一个空引用，即没有引用任何对象。它表示变量或引用没有指向任何有效的对象。对于null值，您不能调用任何对象方法，因为该引用不指向任何实际的对象。
        if(username == null){//这个是判断有没有参数的
            map.put("error_message", "用户名不能为空");
            return map;
        }
        if(password == null || confirmedPassword == null){
            map.put("error_message", "密码不能为空");
            return map;
        }

        username = username.trim();//去除字符串 username 前后的空白字符（例如空格、制表符、换行符等）。
        if(username.isEmpty()){//这个判断长度是不是为0的
            map.put("error_message", "用户名不能为空");
            return map;
        }
        if(password.isEmpty() || confirmedPassword.isEmpty()){
            map.put("error_message", "密码不能为空");
            return map;
        }

        if(username.length() >100){
            map.put("error_message", "用户名长度不能大于100");
        }

        if(password.length() >100 ||confirmedPassword.length() >100){
            map.put("error_message", "密码长度不能大于100");
            return map;
        }
        if(!password.equals(confirmedPassword)){
            map.put("error_message", "两次密码不一致");
            return map;
        }
        //对用户名进行查重
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if(!users.isEmpty()){
            map.put("error_message", "用户名已存在");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);//对密码进行加密
        //暂时用这个统一的头像
        String photo = "https://cdn.acwing.com/media/user/profile/photo/1_lg_18c163c611.jpg";
        User user = new User(null,  1500, username, encodedPassword, photo);//id是自增的，不需要传入
        userMapper.insert(user);//插入数据库中
        map.put("error_message", "success");
        return map;
    }
}
