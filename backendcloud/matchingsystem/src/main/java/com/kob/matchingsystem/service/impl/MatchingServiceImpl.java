package com.kob.matchingsystem.service.impl;

import com.kob.matchingsystem.service.MatchingService;
import com.kob.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

@Service
public class MatchingServiceImpl implements MatchingService {
    public final static MatchingPool matchingPool = new MatchingPool();//类->实例

    @Override
    public String addPlayer(Integer userId, Integer rating,Integer botId) {
        System.out.println("matching_system add player" + userId);
        matchingPool.addPlayer(userId,rating,botId);
        return "add player success";//这个字符串返回给Controller层的MatchingController.java
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("matching_system remove player");
        matchingPool.removePlayer(userId);
        return "remove player success";
    }
}
