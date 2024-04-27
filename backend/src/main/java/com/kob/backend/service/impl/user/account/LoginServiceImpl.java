package com.kob.backend.service.impl.user.account;


import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.account.LoginService;
import com.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Override
    public Map<String, String> getToken(String username, String password) {
        // 创建一个包含提供的用户名和密码的UsernamePasswordAuthenticationToken对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username,password);

        // 使用AuthenticationManager对用户进行认证，传入认证令牌authenticationToken
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        // 从认证结果中获取通过认证的用户的UserDetailsImpl对象
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();

        // 从UserDetailsImpl对象中获取User对象，确保使用POJO中的user字段
        User user = loginUser.getUser();//这里的user一定用pojo里的user

        // 使用用户的ID创建JWT令牌
        String jwt = JwtUtil.createJWT(user.getId().toString());

        Map<String, String> map = new HashMap<>();
        map.put("error_message", "success");
        map.put("token", jwt);
        return map;
    }
}
