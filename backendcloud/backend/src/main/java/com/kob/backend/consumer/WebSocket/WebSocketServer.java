package com.kob.backend.consumer.WebSocket;

import com.alibaba.fastjson.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    //把user.id和websocketserver 对应起来
    public static final ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    private User user;

    //每个连接链接使用session来维护的
    private Session session = null;
    private Game game = null;
    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static RestTemplate restTemplate;

    @Autowired//注入user数据表（特殊一点）
    public  void setUserMapper(UserMapper userMapper){
        WebSocketServer.userMapper = userMapper;
    }
    @Autowired
    public  void setRecordMapper(RecordMapper recordMapper){
            WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public  void setRestTemplate(RestTemplate restTemplate){
        WebSocketServer.restTemplate = restTemplate;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        System.out.println("connected");
        Integer userId = JwtAuthentication.getUserId(token);//使用jwt验证登录匹配
        this.user = userMapper.selectById(userId);
        if(this.user != null){
            users.put(userId, this);
            System.out.println(user);
        }else{
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnected");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        //向matchingsystem发送 取消匹配的用户的信息
        restTemplate.postForObject("http://localhost:3001/player/remove/", data, String.class);
        if(this.user != null){
            users.remove(this.user.getId());
        }
    }

    //⬇这样外面也能调用它
    public static void startGame(Integer aId, Integer bId){
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);

        Game game = new Game(13,14,20, a.getId(), b.getId());
        game.createMap();
        if(users.get(a.getId()) != null){
            users.get(a.getId()).game = game;
        }
        if(users.get(b.getId()) != null){
            users.get(b.getId()).game = game;//把这局游戏传给a和b
        }
        game.start();//Thread类的一个API，多线程使用

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("a_sx", game.getPlayerA().getSx());
        respGame.put("a_sy", game.getPlayerA().getSy());
        respGame.put("b_id", game.getPlayerB().getId());
        respGame.put("b_sx", game.getPlayerB().getSx());
        respGame.put("b_sy", game.getPlayerB().getSy());
        respGame.put("map", game.getG());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        if(users.get(a.getId()) != null){
            users.get(a.getId()).sendMessage(respA.toJSONString());
        }

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        if(users.get(b.getId()) != null){
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }

    public void startMatching(){
        System.out.println("开始匹配" + this.user);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        //向matchingsystem发送匹配用户的相关信息
        restTemplate.postForObject("http://localhost:3001/player/add/",data,String.class);
    }

    private void stopMatching() {
        System.out.println("取消匹配");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        //向matchingsystem发送 取消匹配的用户的信息
        restTemplate.postForObject("http://localhost:3001/player/remove/", data, String.class);
    }

    public void move(int direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("receive message" + user.getUsername());
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event)){
            startMatching();
        }
        else if ("stop-matching".equals(event)) {
            stopMatching();
        }
        else if("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }



    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public  void sendMessage(String message){
        //后端向前端发送信息
        synchronized (this.session){
            try{
                this.session.getBasicRemote().sendText(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
