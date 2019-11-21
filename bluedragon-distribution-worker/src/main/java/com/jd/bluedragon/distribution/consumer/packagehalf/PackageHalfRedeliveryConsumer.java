package com.jd.bluedragon.distribution.consumer.packagehalf;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.half.domain.PackageHalfApprove;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedelivery;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedeliveryDetailDto;
import com.jd.bluedragon.distribution.half.domain.PackageHalfRedeliveryDto;
import com.jd.bluedragon.distribution.half.service.PackageHalfApproveService;
import com.jd.bluedragon.distribution.half.service.PackageHalfRedeliveryService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("packageHalfRedeliveryConsumer")
public class PackageHalfRedeliveryConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private PackageHalfRedeliveryService packageHalfRedeliveryService;

    @Autowired
    private PackageHalfApproveService packageHalfApproveService;

    @JProfiler(jKey = "DMSCORE.PackageHalfRedeliveryConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("[B网半收]消费协商再投MQ-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        log.info("[B网半收]消费协商再投MQ-消息体：{}" , message.getText());
        PackageHalfRedeliveryDto dto = JSON.parseObject(message.getText(), PackageHalfRedeliveryDto.class);
        List<PackageHalfRedeliveryDetailDto> packageList = dto.getPackagePartMsgDTOList();
        if(packageList == null || packageList.isEmpty()){
            log.warn("[B网半收]消费协商再投MQ-包裹明细为空：{}" , message.getText());
            return;
        }
        log.warn("[B网半收]消费协商再投MQ-包裹数量：{}；运单号：{}" ,packageList.size(), dto.getWaybillCode());
        //ModelType为空的数据也保留，兼容老数据
        if(dto.getModelType() != null && !Constants.PACKAGE_APPROVE_TYPE.equals(dto.getModelType())){
            log.warn("[B网半收]消费协商再投MQ-非按包裹审核类型，执行丢弃：{}" , message.getText());
            return;
        }
        String wayBillCode = packageHalfRedeliveryService.queryExistsByWaybillCodeAndSiteCode(dto.getWaybillCode(), dto.getOperateSiteId());
        if(StringUtils.isNotBlank(wayBillCode)){//已消费过改运单的MQ，不再重复消费，打印日志直接返回
            log.warn("协商再投运单已消费，不再重复落库：{}" , message.getText());
            return;
        }
        repairInfo(dto);
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
        List<PackageHalfRedeliveryDetailDto> packageList = dto.getPackagePartMsgDTOList();
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
            redelivery.setRedeliverTime(dto.getRedeliverTime());
            redelivery.setModelType(dto.getModelType());
            redelivery.setRemark(dto.getRemark());
            redelivery.setPackageCode(packageDto.getPackageCode());
            redelivery.setPackageState(packageDto.getPackageState());
            redelivery.setPackageRemark(packageDto.getRemark());
            packageHalfRedeliveryList.add(redelivery);
        }
        return packageHalfRedeliveryList;
    }

    /**
     * 补齐相关终端信息
     * @param dto
     */
    private void repairInfo(PackageHalfRedeliveryDto dto){
        PackageHalfApprove packageHalfApprove = packageHalfApproveService.queryOneByWaybillCode(dto.getWaybillCode(), dto.getOperateSiteId());
        if(packageHalfApprove != null){
            if(NumberHelper.isPositiveNumber(packageHalfApprove.getDmsSiteCode())){
                dto.setOperateSiteId(packageHalfApprove.getDmsSiteCode());
            }
            if(NumberHelper.isPositiveNumber(packageHalfApprove.getCreateUserCode())){
                dto.setOperatorId(packageHalfApprove.getCreateUserCode());
            }
            if(StringUtils.isNotBlank(packageHalfApprove.getDmsSiteName())){
                dto.setOperateSiteName(packageHalfApprove.getDmsSiteName());
            }
            if(StringUtils.isNotBlank(packageHalfApprove.getCreateUserName())){
                dto.setOperatorName(packageHalfApprove.getCreateUserName());
            }
        }
    }
}
