package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 龙门架发货消费器
 * Created by wangtingwei on 2016/3/14.
 */
@Service("scannerFrameSendConsume")
public class ScannerFrameSendConsume implements ScannerFrameConsume {
    private static final Log logger = LogFactory.getLog(ScannerFrameSendConsume.class);

    @Resource
    private DeliveryService deliveryService;


    @Autowired
    private GantryDeviceConfigService gantryDeviceConfigService;

    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {

        SendM domain = new SendM();
        if (StringHelper.isEmpty(config.getSendCode())) {
            logger.error(MessageFormat.format("龙门架发货批次号为空机器号：{0},发货站点：{1},操作号：{2}", config.getMachineId(), config.getCreateSiteName(), config.getId()));
            return false;
        }

        domain.setReceiveSiteCode(SerialRuleUtil.getReceiveSiteCodeFromSendCode(config.getSendCode()));
        if (null == domain.getReceiveSiteCode()) {
            logger.error(MessageFormat.format("从批次号{0}获取目的站点为空", config.getSendCode()));
            return false;
        }
        domain.setSendCode(config.getSendCode());
        // 龙门架老版本超过24小时换批次逻辑
        if (config.getVersion() == null || config.getVersion().intValue() == 0) {
            Calendar now = Calendar.getInstance();
            now.setTime(config.getStartTime());
            now.add(Calendar.HOUR, 25);
            Date endTime = now.getTime();
            if (endTime.before(uploadData.getScannerTime())) {/*如果一天后，则进行自动切换批次号*/
                String sendCode = new StringBuilder()
                        .append(SerialRuleUtil.getCreateSiteCodeFromSendCode(config.getSendCode()))
                        .append("-")
                        .append(SerialRuleUtil.getReceiveSiteCodeFromSendCode(config.getSendCode()))
                        .append("-")
                        .append(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS)).toString();
                GantryDeviceConfig model = gantryDeviceConfigService.findMaxStartTimeGantryDeviceConfigByMachineId(Integer.valueOf(config.getMachineId()));
                model.setSendCode(sendCode);
                model.setStartTime(new Date(uploadData.getScannerTime().getTime() - 1000));
                model.setEndTime(new Date(model.getStartTime().getTime() + 1000 * 60 * 60 * 24));
                gantryDeviceConfigService.addUseJavaTime(model);
                domain.setSendCode(sendCode);
            }
        }
        domain.setCreateSiteCode(config.getCreateSiteCode());
//        domain.setBoxCode(uploadData.getBarCode());
        //区分自动分拣机还是龙门架自动发货，若有箱号存箱号按箱号自动发货   若无箱号则存包裹号按原包发货     add by lhc 2017.11.27
        boolean isSortingSend = false;
        if (uploadData.getSource() != null && uploadData.getSource().intValue() == 2){
        	isSortingSend = true;
        	String boxCode = uploadData.getBoxCode();
            if(boxCode != null && !"".equals(boxCode)){
            	domain.setBoxCode(boxCode);
            }else{
            	domain.setBoxCode(uploadData.getBarCode());
            }
        }else{
        	domain.setBoxCode(uploadData.getBarCode());
        }
        	
        
        domain.setCreateUser(config.getOperateUserName());
        domain.setCreateUserCode(config.getOperateUserId());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + 30000));
        domain.setOperateTime(new Date(uploadData.getScannerTime().getTime() + 30000));
        SendResult result = deliveryService.atuoPackageSend(domain, isSortingSend);
        return result.getKey().equals(SendResult.CODE_OK);
    }
}
