package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Created by wangtingwei on 2016/2/22.
 */
@Service("inspectionNotifyService")
public class InspectionNotifyServiceImpl implements InspectionNotifyService {

    private static final Log logger= LogFactory.getLog(InspectionNotifyServiceImpl.class);

    @Autowired
    @Qualifier("inspectionDataSyncMQ")
    private DefaultJMQProducer inspectionDataSyncMQ;

    @Override
    public void send(InspectionMQBody body){
        //判断过滤条件的修改 by wzx 2017年12月14日10:24:07  原来调用的方法是isMatchReceiveWaybillNo 判断是否是外单单号，现在改为自营外单都通过
        if(!SerialRuleUtil.isMatchAllWaybillCode(body.getWaybillCode())){
            if(logger.isInfoEnabled()){
                logger.info(MessageFormat.format("推送验货MQ至本地分拣机，运单{0}被过滤",body.getWaybillCode()));
            }
            return;
        }
        inspectionDataSyncMQ.sendOnFailPersistent(body.getWaybillCode(), JsonHelper.toJson(body));
    }
}
