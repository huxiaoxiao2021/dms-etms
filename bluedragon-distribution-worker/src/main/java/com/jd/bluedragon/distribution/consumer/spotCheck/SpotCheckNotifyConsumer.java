package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.base.SpotCheckServiceProxy;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckNotifyMQ;
import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckStatusEnum;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 抽检回传消息消费
 *
 * @author hujiping
 * @date 2021/12/14 9:36 下午
 */
@Service("spotCheckNotifyConsumer")
public class SpotCheckNotifyConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(SpotCheckNotifyConsumer.class);

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    private DMSWeightVolumeService dmsWeightVolumeService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private SpotCheckServiceProxy spotCheckServiceProxy;

    @Override
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("抽检回传消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        SpotCheckNotifyMQ spotCheckNotifyMQ = JsonHelper.fromJsonUseGson(message.getText(), SpotCheckNotifyMQ.class);
        if(spotCheckNotifyMQ == null) {
            logger.warn("抽检回传消息体转换失败，内容为【{}】", message.getText());
            return;
        }
        // 校验
        String waybillCode = spotCheckNotifyMQ.getWaybillCode();
        String initiationLink = spotCheckNotifyMQ.getInitiationLink();
        Integer exceedType = spotCheckNotifyMQ.getExceedType();
        if(!Objects.equals(String.valueOf(SpotCheckConstants.DMS_SPOT_CHECK_ISSUE), initiationLink)
                || (!Objects.equals(exceedType, SpotCheckConstants.EXCESS_TYPE_WEIGHT) && !Objects.equals(exceedType, SpotCheckConstants.EXCESS_TYPE_VOLUME))
                || !WaybillUtil.isWaybillCode(waybillCode)){
            logger.warn("抽检回传消息体不符合校验条件，内容为【{}】", message.getText());
            return;
        }
        if(logger.isInfoEnabled()){
            logger.info("运单号:{}的抽检回传消息:{}", waybillCode, message.getText());
        }
        SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
        condition.setWaybillCode(waybillCode);
        condition.setReviewSiteCode(Integer.valueOf(spotCheckNotifyMQ.getOrgCode()));
        condition.setIsExcess(ExcessStatusEnum.EXCESS_ENUM_YES.getCode());
        condition.setRecordType(SpotCheckRecordTypeEnum.SUMMARY_RECORD.getCode());
        List<WeightVolumeSpotCheckDto> existSpotCheckList = spotCheckQueryManager.querySpotCheckByCondition(condition);
        if(CollectionUtils.isEmpty(existSpotCheckList)){
            return;
        }
        Integer status = spotCheckNotifyMQ.getStatus();
        WeightVolumeSpotCheckDto updateDto = existSpotCheckList.get(0);
        // 上传称重流水
        if((Objects.equals(SpotCheckStatusEnum.SPOT_CHECK_STATUS_RZ.getCode(), status)
                || Objects.equals(SpotCheckStatusEnum.SPOT_CHECK_STATUS_RZ_OVERTIME.getCode(), status)
                || Objects.equals(SpotCheckStatusEnum.SPOT_CHECK_STATUS_RZ_SYSTEM_ERP_Y.getCode(), status)
                || Objects.equals(SpotCheckStatusEnum.SPOT_CHECK_STATUS_RZ_SYSTEM_ERP_N.getCode(), status)
                || Objects.equals(SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_EFFECT.getCode(), status))
                && SpotCheckSourceFromEnum.ARTIFICIAL_SOURCE_NUM.contains(updateDto.getReviewSource())
                && !Objects.equals(updateDto.getManualUploadWeight(), Constants.CONSTANT_NUMBER_ONE)){
            updateDto.setManualUploadWeight(Constants.CONSTANT_NUMBER_ONE);
            uploadWeightVolume(spotCheckNotifyMQ, updateDto.getReviewDate());
        }
        // 更新抽检状态
        updateDto.setSpotCheckStatus(status);
        if(Objects.equals(SpotCheckStatusEnum.SPOT_CHECK_STATUS_RZ_SYSTEM_ERP_Y.getCode(), status)){
            updateDto.setContrastStaffAccount(spotCheckNotifyMQ.getDutyStaffAccount());
            updateDto.setContrastStaffName(spotCheckNotifyMQ.getDutyStaffName());
            updateDto.setContrastStaffType(spotCheckNotifyMQ.getDutyStaffType());
        }
        spotCheckServiceProxy.insertOrUpdateProxyReform(updateDto);
    }

    private void uploadWeightVolume(SpotCheckNotifyMQ spotCheckNotifyMQ, Long operateTime) {
        WeightVolumeEntity weightVolumeEntity = new WeightVolumeEntity();
        weightVolumeEntity.setBarCode(spotCheckNotifyMQ.getWaybillCode());
        weightVolumeEntity.setWaybillCode(spotCheckNotifyMQ.getWaybillCode());
        weightVolumeEntity.setWeight(spotCheckNotifyMQ.getReConfirmWeight() == null ? Constants.DOUBLE_ZERO : Double.parseDouble(spotCheckNotifyMQ.getReConfirmWeight()));
        weightVolumeEntity.setVolume(spotCheckNotifyMQ.getReConfirmVolume() == null ? Constants.DOUBLE_ZERO : Double.parseDouble(spotCheckNotifyMQ.getReConfirmVolume()));
        weightVolumeEntity.setBusinessType(WeightVolumeBusinessTypeEnum.BY_WAYBILL);
        weightVolumeEntity.setSourceCode(FromSourceEnum.SPOT_CHECK);
        weightVolumeEntity.setOperateSiteCode(Integer.valueOf(spotCheckNotifyMQ.getOrgCode()));
        weightVolumeEntity.setOperateSiteName(spotCheckNotifyMQ.getOrgName());
        String reviewUserErp = spotCheckNotifyMQ.getStartStaffAccount();
        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(reviewUserErp);
        if(baseStaff == null){
            logger.warn("根据erp:{}查询基础资料为空!", reviewUserErp);
            return;
        }
        weightVolumeEntity.setOperatorCode(reviewUserErp);
        weightVolumeEntity.setOperatorId(baseStaff.getStaffNo());
        weightVolumeEntity.setOperatorName(baseStaff.getStaffName());
        weightVolumeEntity.setOperateTime(new Date(operateTime));
        // 异步上传称重流水
        dmsWeightVolumeService.dealWeightAndVolume(weightVolumeEntity, false);
    }
}
