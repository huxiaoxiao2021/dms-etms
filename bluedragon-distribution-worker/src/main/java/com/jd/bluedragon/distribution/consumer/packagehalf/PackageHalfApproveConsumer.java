package com.jd.bluedragon.distribution.consumer.packagehalf;


import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.half.domain.*;
import com.jd.bluedragon.distribution.half.service.PackageHalfApproveService;
import com.jd.bluedragon.utils.JsonHelper;
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

@Service("packageHalfApproveConsumer")
public class PackageHalfApproveConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private PackageHalfApproveService packageHalfApproveService;

    @JProfiler(jKey = "DMSCORE.PackageHalfRedeliveryConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("[B网半收]消费协商再投MQ-消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        log.info("[B网半收]消费终端提交的协商再投MQ-消息体：{}" , message.getText());
        PackageHalfApproveDto dto = JSON.parseObject(message.getText(), PackageHalfApproveDto.class);
        List<PackageHalfApproveDetailDto> packageList = dto.getPackagePartMsgDTOList();
        if(packageList == null || packageList.isEmpty()){
            log.warn("[B网半收]消费终端提交的协商再投MQ-包裹明细为空：{}" , message.getText());
            return;
        }
        log.warn("[B网半收]消费终端提交的协商再投MQ-包裹数量：{}；运单号：{}" ,packageList.size(), dto.getWaybillCode());
        //ModelType为空的数据也保留，兼容老数据
        if(dto.getModelType() != null && !Constants.PACKAGE_APPROVE_TYPE.equals(dto.getModelType())){
            log.warn("[B网半收]消费终端提交的协商再投MQ-非按包裹审核类型，执行丢弃：{}" , message.getText());
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
        List<PackageHalfApprove> packageHalfRedeliveryList = getPackageHalfRedeliveryList(dto, erp);
        if(packageHalfRedeliveryList != null && !packageHalfRedeliveryList.isEmpty()){
            packageHalfApproveService.batchAdd(packageHalfRedeliveryList);
        }
    }

    /**
     * 根据协商消息体获取新增协商再投数据列表
     * @param dto
     * @param userErp
     * @return
     */
    private List<PackageHalfApprove> getPackageHalfRedeliveryList(PackageHalfApproveDto dto, String userErp){
        List<PackageHalfApproveDetailDto> packageList = dto.getPackagePartMsgDTOList();
        List<PackageHalfApprove> packageHalfRedeliveryList = new ArrayList<PackageHalfApprove>(packageList.size());
        for (PackageHalfApproveDetailDto packageDto : packageList){
            //终端提交的MQ包裹状态是“再投待审核”时才落库
            if(Constants.PACKAGE_REDELIVERY_CODE.equals(packageDto.getPackageState())){
                PackageHalfApprove approve = new PackageHalfApprove();
                approve.setWaybillCode(dto.getWaybillCode());
                approve.setOrderId(dto.getOrderId());
                approve.setWaybillSign(dto.getWaybillSign());
                approve.setOperatetime(dto.getOperateTime());
                approve.setDmsSiteCode(dto.getOperateSiteId());
                approve.setDmsSiteName(dto.getOperateSiteName());
                approve.setCreateUserCode(dto.getOperatorId());
                approve.setCreateUserName(dto.getOperatorName());
                approve.setModelType(dto.getModelType());
                approve.setCreateUser(userErp);
                approve.setPackageCode(packageDto.getPackageCode());
                approve.setPackageState(packageDto.getPackageState());
                approve.setPackageRemark(packageDto.getRemark());
                packageHalfRedeliveryList.add(approve);
            }
        }
        return packageHalfRedeliveryList;
    }
}
