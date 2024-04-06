package com.kob.backend.service.user.account;

public interface UserLikeService {
        String Count_key(Integer userId);
        String alikeb(Integer givelikeid, Integer getlikeid);
        Integer getlikescount(Integer userId);
        Boolean isliked(Integer givelikeid, Integer getlikeid);
        Boolean changeliked(Integer givelikeid, Integer getlikeid);
}
