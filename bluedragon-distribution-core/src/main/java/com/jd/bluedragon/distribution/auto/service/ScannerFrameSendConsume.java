package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 龙门架发货消费器
 * Created by wangtingwei on 2016/3/14.
 */
@Service("scannerFrameSendConsume")
public class ScannerFrameSendConsume implements ScannerFrameConsume {

    private static final Logger log = LoggerFactory.getLogger(ScannerFrameSendConsume.class);

    @Resource
    private DeliveryService deliveryService;

    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {

        SendM domain = new SendM();
        if (StringHelper.isEmpty(config.getSendCode())) {
            log.warn("龙门架发货批次号为空机器号：{},发货站点：{},操作号：{}", config.getMachineId(), config.getCreateSiteName(), config.getId());
            return false;
        }

        domain.setReceiveSiteCode(SerialRuleUtil.getReceiveSiteCodeFromSendCode(config.getSendCode()));
        if (null == domain.getReceiveSiteCode()) {
            log.warn("从批次号{}获取目的站点为空", config.getSendCode());
            return false;
        }
        domain.setSendCode(config.getSendCode());

        domain.setCreateSiteCode(config.getCreateSiteCode());
        domain.setBoxCode(uploadData.getBarCode());

        domain.setCreateUser(config.getOperateUserName());
        domain.setCreateUserCode(config.getOperateUserId());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperateTime(new Date(uploadData.getScannerTime().getTime() + Constants.DELIVERY_DELAY_TIME));
        SendResult result = deliveryService.autoPackageSend(domain, uploadData);
        return result.getKey().equals(SendResult.CODE_OK) || result.getKey().equals(SendResult.CODE_SENDED);
    }
}
