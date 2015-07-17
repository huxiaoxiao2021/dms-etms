package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.send.domain.SendQuery;

import java.util.List;

/**
 * Created by wangtingwei on 2014/12/5.
 */
public interface SendQueryService {

    public boolean insert(SendQuery domain);

    public List<SendQuery> queryBySendCode(String sendCode);
}
