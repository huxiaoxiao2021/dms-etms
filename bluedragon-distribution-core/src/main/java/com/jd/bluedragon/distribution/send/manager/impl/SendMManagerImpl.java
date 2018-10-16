package com.jd.bluedragon.distribution.send.manager.impl;

import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hanjiaxing1 on 2018/10/16.
 */
@Component
public class SendMManagerImpl implements SendMManager {

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private BoxService boxService;

    @Override
    public Integer add(String namespace, SendM sendM) {
        //更新箱号状态缓存
        boxService.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), 5);
        return sendMDao.add(namespace, sendM);
    }

    @Override
    public Integer addBatch(List<SendM> param) {
        return sendMDao.addBatch(param);
    }

    @Override
    public List<SendM> findSendMByBoxCode2(SendM sendM) {
        return sendMDao.findSendMByBoxCode2(sendM);
    }

    @Override
    public List<SendM> findSendMByBoxCode(SendM sendM) {
        return sendMDao.findSendMByBoxCode(sendM);
    }

    @Override
    public boolean insertSendM(SendM sendM) {
        //更新箱号状态缓存
        boxService.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), 5);
        return sendMDao.insertSendM(sendM);
    }

}
