package com.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private  static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();//用于线程加锁
    private static RestTemplate restTemplate;//注解
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }


    public void addPlayer(Integer userId, Integer rating){
        lock.lock();
        try{
            players.add(new Player(userId, rating, 0));
        }finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId){
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>();
            for(Player player:players){//狸猫换太子手法
                if(!player.getUserId().equals(userId)){
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        }finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime(){//将当前所有玩家的等待时间+1
        for(Player player:players){
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    private boolean checkMatched(Player a, Player b){//判断两名玩家是否匹配
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime*10;
    }

    private void sendResult(Player a, Player b){//返回匹配结果
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("b_id", b.getUserId().toString());
        System.out.println("已经进行到发送result的部分啦");
        //向backend发送匹配上的两名玩家的信息
        restTemplate.postForObject("http://localhost:3000/pk/start/game/", data, String.class);
    }

    private void matchingPlayers(){//匹配函数，尝试匹配
        System.out.println("match players:" + players);
        boolean[] used = new boolean[players.size()];//表示当前那些玩家已经被匹配了
        for(int i = 0;i < players.size();i++){
            if(used[i])continue;
            for(int j = i+1;j<players.size();j++){
                if(used[j])continue;
                Player a = players.get(i),b = players.get(j);
                if(checkMatched(a, b)){
                    used[i] = used[j] = true;
                    sendResult(a, b);
                    break;
                }
            }
        }

        List<Player> newPlayers = new ArrayList<>();
        for(int i=0;i<players.size();i++){
            if(!used[i]){
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
                lock.lock();
                try{//涉及读写冲突就要加锁保护
                    increaseWaitingTime();
                    matchingPlayers();
                }finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
