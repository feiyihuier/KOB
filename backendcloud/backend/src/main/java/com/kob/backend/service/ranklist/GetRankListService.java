package com.kob.backend.service.ranklist;

import com.alibaba.fastjson.JSONObject;
public interface GetRankListService {
    JSONObject getlist(Integer page);
}
