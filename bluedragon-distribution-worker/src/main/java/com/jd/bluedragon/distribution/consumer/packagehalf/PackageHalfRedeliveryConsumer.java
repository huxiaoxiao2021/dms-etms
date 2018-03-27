package com.jd.bluedragon.distribution.consumer.packagehalf;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedelivery;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedeliveryDetailDto;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedeliveryDto;
import com.jd.bluedragon.distribution.half.service.PackageHalfRedeliveryService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


@Service("packageHalfRedeliveryConsumer")
public class PackageHalfRedeliveryConsumer extends MessageBaseConsumer {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private PackageHalfRedeliveryService packageHalfRedeliveryService;

    @JProfiler(jKey = "DMSCORE.PackageHalfRedeliveryConsumer.consume", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("[B网半收]消费协商再投MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        logger.info("[B网半收]消费协商再投MQ-消息体：" + message.getText());
        PackageHalfRedeliveryDto dto = JsonHelper.fromJsonUseGson(message.getText(), PackageHalfRedeliveryDto.class);
        List<PackageHalfRedeliveryDetailDto> packageList = dto.getPackageSyncPartParameterList();
        if(packageList == null || packageList.isEmpty()){
            logger.warn("[B网半收]消费协商再投MQ-包裹明细为空：" + message.getText());
            return;
        }
        BaseStaffSiteOrgDto user = baseMajorManager.getBaseStaffByStaffId(dto.getOperatorId());
        String erp = "";
        if(user != null){
            erp = StringUtils.isNotBlank(user.getErp()) ? user.getErp() : user.getAccountNumber();
        }
        if(StringUtils.isBlank(erp)){
            erp = dto.getOperatorName();  //查不到时取名字字段，保证页面显示的有信息
        }
        packageHalfRedeliveryService.batchAdd(getPackageHalfRedeliveryList(dto, erp));
    }

    /**
     * 根据协商消息体获取新增协商再投数据列表
     * @param dto
     * @param userErp
     * @return
     */
    private List<PackageHalfRedelivery> getPackageHalfRedeliveryList(PackageHalfRedeliveryDto dto, String userErp){
        List<PackageHalfRedeliveryDetailDto> packageList = dto.getPackageSyncPartParameterList();
        List<PackageHalfRedelivery> packageHalfRedeliveryList = new ArrayList<PackageHalfRedelivery>(packageList.size());
        for (PackageHalfRedeliveryDetailDto packageDto : packageList){
            PackageHalfRedelivery redelivery = new PackageHalfRedelivery();
            redelivery.setWaybillCode(dto.getWaybillCode());
            redelivery.setEclpDealTime(dto.getOperateTime());
            redelivery.setDmsSiteCode(dto.getOperateSiteId());
            redelivery.setDmsSiteName(dto.getOperateSiteName());
            redelivery.setCreateUserCode(dto.getOperatorId());
            redelivery.setCreateUserName(dto.getOperatorName());
            redelivery.setCreateUser(userErp);
            redelivery.setWaybillState(dto.getWaybillState());
            redelivery.setPackageCode(packageDto.getPackageCode());
            redelivery.setPackageState(packageDto.getPackageState());
            redelivery.setPackageRemark(packageDto.getRemark());
            packageHalfRedeliveryList.add(redelivery);
        }
        return packageHalfRedeliveryList;
    }
}
