package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserLikeMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.UserLike;
import com.kob.backend.service.user.account.UserLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserlikeServiceImpl implements UserLikeService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserLikeMapper userLikeMapper;
    @Override
    public String Count_key(Integer userId) {
        return "userId:" + userId;
    }

    @Override
    public String alikeb(Integer givelikeid, Integer getlikeid) {
        return givelikeid + ":" + getlikeid;
    }

    @Override
    public Integer getlikescount(Integer userId) {
        Integer count;
        if(redisTemplate.opsForValue().get(Count_key(userId)) == null){
            System.out.println("这里是查询redis的代码，redis里没有，查询数据库喽");
            Integer rediscount = userMapper.selectById(userId).getLikes();
            redisTemplate.opsForValue().set(Count_key(userId), rediscount);
            return rediscount;
        }else{
            System.out.println("redis里面有，不用查数据库");
            count = (Integer) redisTemplate.opsForValue().get(Count_key(userId));
        }
        return count;
    }

    @Override
    public Boolean isliked(Integer givelikeid, Integer getlikeid) {
        if(redisTemplate.opsForValue().get(alikeb(givelikeid,getlikeid)) == null){
            QueryWrapper<UserLike> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("givelikeid", givelikeid);
            List<UserLike> userLikeList = userLikeMapper.selectList(queryWrapper);
            UserLike selectUser = null;
            for(UserLike userLike:userLikeList){
                if(userLike.getGetlikeid() == getlikeid)selectUser = userLike;
            }
            if(selectUser == null) {//说明是一直没有点赞过,需要插入数据库
                System.out.println("redis和数据库里都没有这一对关系，需新增");
                UserLike insertuserlike = new UserLike(null, givelikeid, getlikeid, false, new Date(), new Date());
                userLikeMapper.insert(insertuserlike);
                selectUser = insertuserlike;
            }
            redisTemplate.opsForValue().set(alikeb(givelikeid, getlikeid), selectUser.getStatus());
            return selectUser.getStatus();
        }
        System.out.println("redis里面有这一对关系");
        //redis里面有这一对关系
        return (Boolean) redisTemplate.opsForValue().get(alikeb(givelikeid, getlikeid));

    }

    @Override
    public Boolean changeliked(Integer givelikeid, Integer getlikeid) {
        Boolean flag = isliked(givelikeid, getlikeid);
        Boolean newflag = !flag;
        if(newflag){
            Integer newcount = getlikescount(getlikeid);
            newcount +=1;
            redisTemplate.opsForValue().set(Count_key(getlikeid), newcount);
        }else{
            Integer newcount = getlikescount(getlikeid);
            newcount -=1;
            redisTemplate.opsForValue().set(Count_key(getlikeid), newcount);
        }
        redisTemplate.opsForValue().set(alikeb(givelikeid, getlikeid), newflag);
        return newflag;
    }
}
