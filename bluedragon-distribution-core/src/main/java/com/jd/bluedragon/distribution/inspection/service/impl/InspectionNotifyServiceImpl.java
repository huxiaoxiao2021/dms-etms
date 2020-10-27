package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by wangtingwei on 2016/2/22.
 */
@Service("inspectionNotifyService")
public class InspectionNotifyServiceImpl implements InspectionNotifyService {

    private static final Logger log = LoggerFactory.getLogger(InspectionNotifyServiceImpl.class);

    @Autowired
    @Qualifier("inspectionDataSyncMQ")
    private DefaultJMQProducer inspectionDataSyncMQ;

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
    }

    @Override
    public void sendMQFromRequest(InspectionRequest request) {
        InspectionMQBody inspectionMQBody = new InspectionMQBody();
        inspectionMQBody.setWaybillCode(getWaybillCode(request));
        inspectionMQBody.setInspectionSiteCode(request.getSiteCode());
        inspectionMQBody.setCreateUserCode(request.getUserCode());
        inspectionMQBody.setCreateUserName(request.getUserName());
        Date operateTime = StringUtils.isBlank(request.getOperateTime())? new Date() : DateHelper.getSeverTime(request.getOperateTime());
        inspectionMQBody.setOperateTime(operateTime);
        inspectionMQBody.setSource("DMS-INSPECTION");

        this.send(inspectionMQBody);
    }

    private String getWaybillCode(InspectionRequest request) {
        if (StringUtils.isNotBlank(request.getWaybillCode())) {
            return request.getWaybillCode();
        }
        else if (StringUtils.isNotBlank(request.getPackageBarcode())) {
            return WaybillUtil.getWaybillCode(request.getPackageBarcode());
        }
        else {
            return WaybillUtil.getWaybillCode(request.getPackageBarOrWaybillCode());
        }
    }
}
