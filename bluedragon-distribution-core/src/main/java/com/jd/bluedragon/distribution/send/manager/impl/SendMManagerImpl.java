package com.jd.bluedragon.distribution.send.manager.impl;

import com.jd.bluedragon.distribution.box.domain.BoxStatusEnum;
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
        Integer result = sendMDao.add(namespace, sendM);
        if (result > 0) {
            //更新箱号状态缓存
            boxService.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.SENT_STATUS.getCode());
        }
        return result;
    }

    @Override
    public Integer addBatch(List<SendM> param) {
        return sendMDao.addBatch(param);
    }

    @Override
    public List<SendM> findSendMByBoxCode(SendM sendM) {
        List<SendM> list = sendMDao.findSendMByBoxCode(sendM);
        //sendm不为空，说明已发货，否则视为初始状态
        if (list != null && list.isEmpty()) {
            //更新箱号状态缓存为已发货
            boxService.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.SENT_STATUS.getCode());
        } else {
            //更新箱号状态缓存为初始状态
            boxService.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.INIT_STATUS.getCode());
        }
        return list;
    }

    @Override
    public boolean insertSendM(SendM sendM) {
        boolean result = sendMDao.insertSendM(sendM);
        if (result) {
            //更新箱号状态缓存
            boxService.updateBoxStatusRedis(sendM.getBoxCode(), sendM.getCreateSiteCode(), BoxStatusEnum.SENT_STATUS.getCode());
        }
        return result;
    }

}
