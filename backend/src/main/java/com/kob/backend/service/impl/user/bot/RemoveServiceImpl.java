package com.kob.backend.service.impl.user.bot;


import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    private BotMapper botMapper;//使用数据库表就把它引入进来
    @Override
    public Map<String, String> remove(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int bot_id = Integer.parseInt(data.get("bot_id"));//取出要删除的bot的id
        Bot bot = botMapper.selectById(bot_id);          //使用mapper层查询bot

        Map<String, String> map =new HashMap<>();
        if(bot == null){
            map.put("error_message", "bot不存在或者已经被删除");
            return map;
        }

        if(!bot.getUserId().equals(user.getId())){
            map.put("errormessage", "没有权限删除此Bot");
            return map;
        }
        
        botMapper.deleteById(bot_id);
        map.put("error_message", "success");
        return map;
    }
}
