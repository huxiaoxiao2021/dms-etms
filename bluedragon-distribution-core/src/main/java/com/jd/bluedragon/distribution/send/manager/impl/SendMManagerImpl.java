package com.jd.bluedragon.distribution.send.manager.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.box.domain.BoxStatusEnum;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @JProfiler(jKey = "DMSWEB.SendMManagerImpl.add", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Integer add(String namespace, SendM sendM) {
        Integer result = sendMDao.add(namespace, sendM);
        if (result > 0) {
            //更新箱号状态缓存
            boxService.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.SENT_STATUS.getCode(), sendM.getCreateUser());
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendMManagerImpl.insertSendM", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public boolean insertSendM(SendM sendM) {
        boolean result = sendMDao.insertSendM(sendM);
        if (result) {
            //更新箱号状态缓存
            boxService.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.SENT_STATUS.getCode(), sendM.getCreateUser());
        }

        return result;
    }

    /**
     * 通过箱号和始发地获取发货数据
     * @param sendM
     * @return
     */
    @Override
    public List<SendM> findSendMByBoxCode(SendM sendM){
        return sendMDao.findSendMByBoxCode(sendM);
    }


}
