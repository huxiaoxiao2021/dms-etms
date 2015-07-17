package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.distribution.send.dao.SendQueryDao;
import com.jd.bluedragon.distribution.send.domain.SendQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangtingwei on 2014/12/5.
 */
@Service("SendQueryService")
public class SendQueryServiceImpl implements SendQueryService {

    @Autowired
    private SendQueryDao sendQueryDao;

    @Override
    public boolean insert(SendQuery domain) {
        return sendQueryDao.add(domain);
    }

    @Override
    public List<SendQuery> queryBySendCode(String sendCode) {
        return sendQueryDao.queryBySendCode(sendCode);
    }
}
