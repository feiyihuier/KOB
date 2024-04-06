package com.kob.backend.controller.user.account;

import com.kob.backend.service.user.account.UserLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/like")
public class UserLikeController {
    @Autowired
    private UserLikeService userLikeService;
    @PostMapping("/count/")
    public Integer likeCount(@RequestParam Map<String, String> data){
        Integer userId = Integer.parseInt(data.get("id"));
        System.out.println("这里是like链接" + userId);
        return userLikeService.getlikescount(userId);
    }

    @PostMapping("/ask/")
    public Boolean likeZan(@RequestParam Map<String, String> data){
        Integer givelikeid = Integer.parseInt(data.get("givelikeid"));
        Integer getlikeid = Integer.parseInt(data.get("getlikeid"));
        System.out.println("这里是两人点赞的controller函数");
        return userLikeService.isliked(givelikeid, getlikeid);
    }

    @PostMapping("/change/")
    public Boolean changelike(@RequestParam Map<String, String> data){
        Integer givelikeid = Integer.parseInt(data.get("givelikeid"));
        Integer getlikeid = Integer.parseInt(data.get("getlikeid"));
        System.out.println("改变点赞状态的controller函数");
        return userLikeService.changeliked(givelikeid, getlikeid);
    }

}
