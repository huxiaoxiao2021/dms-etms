package com.jd.bluedragon.distribution.consumer.packagehalf;


import com.alibaba.fastjson.JSON;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service("packageHalfApproveConsumer")
public class PackageHalfApproveConsumer extends MessageBaseConsumer {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private PackageHalfApproveService packageHalfApproveService;

    @JProfiler(jKey = "DMSCORE.PackageHalfRedeliveryConsumer.consume", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn(MessageFormat.format("[B网半收]消费协商再投MQ-消息体非JSON格式，内容为【{0}】", message.getText()));
            return;
        }
        logger.info("[B网半收]消费终端提交的协商再投MQ-消息体：" + message.getText());
        PackageHalfApproveDto dto = JSON.parseObject(message.getText(), PackageHalfApproveDto.class);
        List<PackageHalfApproveDetailDto> packageList = dto.getPackagePartMsgDTOList();
        if(packageList == null || packageList.isEmpty()){
            logger.warn("[B网半收]消费终端提交的协商再投MQ-包裹明细为空：" + message.getText());
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
        packageHalfApproveService.batchAdd(getPackageHalfRedeliveryList(dto, erp));
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
            PackageHalfApprove approve = new PackageHalfApprove();
            approve.setWaybillCode(dto.getWaybillCode());
            approve.setOrderId(dto.getOrderId());
            approve.setWaybillSign(dto.getWaybillSign());
            approve.setOperatetime(dto.getOperateTime());
            approve.setDmsSiteCode(dto.getOperateSiteId());
            approve.setDmsSiteName(dto.getOperateSiteName());
            approve.setCreateUserCode(dto.getOperatorId());
            approve.setCreateUserName(dto.getOperatorName());
            approve.setCreateUser(userErp);
            approve.setPackageCode(packageDto.getPackageCode());
            approve.setPackageState(packageDto.getPackageState());
            approve.setPackageRemark(packageDto.getRemark());
            packageHalfRedeliveryList.add(approve);
        }
        return packageHalfRedeliveryList;
    }
}
