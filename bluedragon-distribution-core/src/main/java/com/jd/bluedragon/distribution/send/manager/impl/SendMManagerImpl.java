package com.jd.bluedragon.distribution.send.manager.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.box.domain.BoxStatusEnum;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


/**
 * Created by hanjiaxing1 on 2018/10/16.
 */
@Component
public class SendMManagerImpl implements SendMManager {

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private BoxService boxService;

    @Autowired
    @Qualifier("cycleMaterialHandleSendMQ")
    private DefaultJMQProducer cycleMaterialHandleSendMQ;

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
        if (StringUtils.isNotBlank(sendM.getBoxCode()) && BusinessUtil.isBoxcode(sendM.getBoxCode())){
            String businessId=sendM.getBoxCode();
            try {
                BoxMaterialRelationMQ mq=new BoxMaterialRelationMQ();
                mq.setBoxCode(businessId);
                mq.setBusinessType(1);
                mq.setOperatorName(sendM.getCreateUser());
                mq.setOperatorCode(sendM.getCreateUserCode());
                mq.setSiteCode(sendM.getCreateSiteCode().toString());

                cycleMaterialHandleSendMQ.send(businessId, JsonHelper.toJson(mq));
            }catch (JMQException e) {//todo 加日志。封装一个方法
            }
        }

        return result;
    }

}
