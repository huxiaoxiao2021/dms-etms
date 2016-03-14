package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 龙门架发货消费器
 * Created by wangtingwei on 2016/3/14.
 */
@Service("scannerFrameSendConsume")
public class ScannerFrameSendConsume implements ScannerFrameConsume {
    private static final Log logger= LogFactory.getLog(ScannerFrameSendConsume.class);

    @Resource
    private DeliveryService deliveryService;

    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {
        SendM domain = new SendM();
        domain.setReceiveSiteCode(SerialRuleUtil.getReceiveSiteCodeFromSendCode(config.getSendCode()));
        domain.setSendCode(config.getSendCode());
        domain.setCreateSiteCode(config.getSiteCode());
        domain.setBoxCode(uploadData.getBarCode());
        domain.setCreateUser(config.getOperteUserName());
        domain.setCreateUserCode(config.getOperteUserId());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + 30000));
        domain.setOperateTime(new Date(System.currentTimeMillis() + 30000));
        SendResult result= deliveryService.atuoPackageSend(domain, true);
        return result.getKey()>0;
    }
}
