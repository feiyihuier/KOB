package com.kob.botrunningsystem.service.impl.utils;
//Bot代码最终执行的地方
import com.kob.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot){
        this.bot = bot;
        this.start();//线程启动

        try {
            this.join(timeout);//最多等待timeout秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.interrupt();//中断当前线程
        }
    }

    private String addUid(String code, String uid){//在code中的Bot类名后添加uid
        //在每一个代码里加一个随机的uid，可以把代码看成一个新的代码，每次都编译执行一次
        int k = code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterface");
        return code.substring(0,k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();//返回一个随机的ID
        String uid = uuid.toString().substring(0,8);
        System.out.println("这里是consumer 的 run 函数");
        System.out.println(addUid(bot.getBotCode(), uid));
        BotInterface botInterface = Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        Integer direction = botInterface.nextMove(bot.getInput());
        System.out.println("move direction" + bot.getUserId() + " " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        restTemplate.postForObject("http://localhost:3000/pk/receive/bot/move/", data, String.class);

    }
}
