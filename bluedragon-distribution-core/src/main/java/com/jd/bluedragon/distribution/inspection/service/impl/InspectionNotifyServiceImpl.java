package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationEnum;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationMQ;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Created by wangtingwei on 2016/2/22.
 */
@Service("inspectionNotifyService")
public class InspectionNotifyServiceImpl implements InspectionNotifyService {

    private static final Logger log = LoggerFactory.getLogger(InspectionNotifyServiceImpl.class);

    @Autowired
    @Qualifier("inspectionDataSyncMQ")
    private DefaultJMQProducer inspectionDataSyncMQ;

    @Autowired
    @Qualifier("cycleMaterialSendMQ")
    private DefaultJMQProducer cycleMaterialSendMQ;

    @Override
    public void send(InspectionMQBody body){
        //判断过滤条件的修改 by wzx 2017年12月14日10:24:07  原来调用的方法是isMatchReceiveWaybillNo 判断是否是外单单号，现在改为自营外单都通过
        if(!WaybillUtil.isWaybillCode(body.getWaybillCode())){
            if(log.isInfoEnabled()){
                log.info("推送验货MQ至本地分拣机，运单{}被过滤",body.getWaybillCode());
            }
            return;
        }
        inspectionDataSyncMQ.sendOnFailPersistent(body.getWaybillCode(), JsonHelper.toJson(body));

        //循环集包袋在验货环节发送解绑MQ
        BoxMaterialRelationMQ loopPackageMq = new BoxMaterialRelationMQ();
        loopPackageMq.setBusinessType(BoxMaterialRelationEnum.INSPECTION.getType());
        loopPackageMq.setOperatorCode(body.getCreateUserCode()==null?0:body.getCreateUserCode());
        loopPackageMq.setOperatorName(body.getCreateUserName());
        loopPackageMq.setSiteCode(String.valueOf(body.getInspectionSiteCode()));
        loopPackageMq.setWaybillCode(Collections.singletonList(body.getWaybillCode()));
        loopPackageMq.setOperatorTime(body.getOperateTime());
        cycleMaterialSendMQ.sendOnFailPersistent(body.getWaybillCode(), JsonHelper.toJson(loopPackageMq));
    }
}
