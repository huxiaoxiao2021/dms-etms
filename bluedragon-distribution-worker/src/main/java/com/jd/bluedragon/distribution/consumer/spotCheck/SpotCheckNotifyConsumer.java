package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.base.SpotCheckServiceProxy;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.service.attachment.JyAttachmentDetailService;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppealEntity;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckAppendixDto;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckNotifyMQ;
import com.jd.bluedragon.distribution.spotcheck.enums.*;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckAppealService;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.distribution.weightvolume.FromSourceEnum;
import com.jd.bluedragon.distribution.weightvolume.WeightVolumeBusinessTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
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

import java.util.ArrayList;
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

    @Autowired
    private SpotCheckAppealService spotCheckAppealService;

    @Autowired
    private JyAttachmentDetailService jyAttachmentDetailService;

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
        String flowSystem = spotCheckNotifyMQ.getFlowSystem();
        Integer exceedType = spotCheckNotifyMQ.getExceedType();
        if(!Objects.equals(String.valueOf(SpotCheckConstants.DMS_SPOT_CHECK_ISSUE), initiationLink)
                || (!Objects.equals(flowSystem, SpotCheckConstants.EQUIPMENT_SPOT_CHECK) && !Objects.equals(flowSystem, SpotCheckConstants.ARTIFICIAL_SPOT_CHECK))
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

        // 如果是申诉状态，走另一个流程
        if (SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_UPGRADE.getCode().equals(status)) {
            // 组装申诉记录数据
            SpotCheckAppealEntity spotCheckAppealEntity = transformData(spotCheckNotifyMQ, updateDto);
            // 插入数据库
            spotCheckAppealService.insertRecord(spotCheckAppealEntity);
            // 组装附件数据
            List<JyAttachmentDetailEntity> jyAttachmentDetailEntityList = createAttachmentList(spotCheckNotifyMQ);
            // 插入附件表
            jyAttachmentDetailService.batchInsert(jyAttachmentDetailEntityList);
            return;
        }
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

    private List<JyAttachmentDetailEntity> createAttachmentList(SpotCheckNotifyMQ spotCheckNotifyMQ) {
        List<JyAttachmentDetailEntity> jyAttachmentDetailEntityList = new ArrayList<>();
        List<SpotCheckAppendixDto> spotCheckAppendixDtoList = spotCheckNotifyMQ.getAppendixList();
        if (CollectionUtils.isEmpty(spotCheckAppendixDtoList)) {
            logger.warn("设备抽检申诉附件列表为空:waybillCode={}", spotCheckNotifyMQ.getWaybillCode());
            return jyAttachmentDetailEntityList;
        }
        for (SpotCheckAppendixDto appendixDto : spotCheckAppendixDtoList) {
            Integer appendixType = appendixDto.getAppendixType();
            String appendixUrl = appendixDto.getAppendixUrl();
            JyAttachmentDetailEntity attachmentDetailEntity = new JyAttachmentDetailEntity();
            attachmentDetailEntity.setBizId(spotCheckNotifyMQ.getFlowId());
            attachmentDetailEntity.setSiteCode(Integer.valueOf(spotCheckNotifyMQ.getOrgCode()));
            attachmentDetailEntity.setBizType(JyAttachmentBizTypeEnum.DEVICE_SPOT_APPEAL.getCode());
            attachmentDetailEntity.setCreateUserErp(Constants.SYS_NAME);
            attachmentDetailEntity.setUpdateUserErp(Constants.SYS_NAME);
            // 如果是图片
            if (SpotCheckAppendixTypeEnum.ESCALATION_PICTURE.getCode().equals(appendixType)) {
                attachmentDetailEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
                // 如果是视频
            } else if (SpotCheckAppendixTypeEnum.ESCALATION_VIDEO.getCode().equals(appendixType)) {
                attachmentDetailEntity.setAttachmentType(JyAttachmentTypeEnum.VIDEO.getCode());
            }
            attachmentDetailEntity.setAttachmentUrl(appendixUrl);
            jyAttachmentDetailEntityList.add(attachmentDetailEntity);
        }
        return jyAttachmentDetailEntityList;
    }

    private SpotCheckAppealEntity transformData(SpotCheckNotifyMQ spotCheckNotifyMQ, WeightVolumeSpotCheckDto updateDto) {
        SpotCheckAppealEntity spotCheckAppealEntity = new SpotCheckAppealEntity();
        spotCheckAppealEntity.setBizId(spotCheckNotifyMQ.getFlowId());
        spotCheckAppealEntity.setWaybillCode(spotCheckNotifyMQ.getWaybillCode());
        spotCheckAppealEntity.setDeviceCode(updateDto.getMachineCode());
        spotCheckAppealEntity.setStartTime(DateHelper.parseDateTime(spotCheckNotifyMQ.getStartTime()));
        spotCheckAppealEntity.setStartProvinceCode(spotCheckNotifyMQ.getStartProvinceAgencyCode());
        spotCheckAppealEntity.setStartProvinceName(spotCheckNotifyMQ.getStartProvinceAgencyName());
        spotCheckAppealEntity.setStartHubCode(updateDto.getReviewAreaHubCode());
        spotCheckAppealEntity.setStartHubName(updateDto.getReviewAreaHubName());
        spotCheckAppealEntity.setStartSiteCode(spotCheckNotifyMQ.getOrgCode());
        spotCheckAppealEntity.setStartSiteName(spotCheckNotifyMQ.getOrgName());
        spotCheckAppealEntity.setStartErp(spotCheckNotifyMQ.getStartStaffAccount());
        spotCheckAppealEntity.setDutyProvinceCode(spotCheckNotifyMQ.getDutyProvinceAgencyCode());
        spotCheckAppealEntity.setDutyProvinceName(spotCheckNotifyMQ.getDutyProvinceAgencyCode());
        spotCheckAppealEntity.setDutyWarCode(spotCheckNotifyMQ.getDutyProvinceCompanyCode());
        spotCheckAppealEntity.setDutyWarName(spotCheckNotifyMQ.getDutyProvinceCompanyName());
        spotCheckAppealEntity.setDutyAreaCode(spotCheckNotifyMQ.getDutyAreaCode());
        spotCheckAppealEntity.setDutyAreaName(spotCheckNotifyMQ.getDutyAreaName());
        spotCheckAppealEntity.setDutySiteCode(spotCheckNotifyMQ.getDutyOrgCode());
        spotCheckAppealEntity.setDutySiteName(spotCheckNotifyMQ.getDutyOrgName());
        spotCheckAppealEntity.setDutyErp(spotCheckNotifyMQ.getDutyStaffAccount());
        spotCheckAppealEntity.setConfirmWeight(spotCheckNotifyMQ.getConfirmWeight());
        spotCheckAppealEntity.setConfirmVolume(spotCheckNotifyMQ.getConfirmVolume());
        spotCheckAppealEntity.setReConfirmWeight(spotCheckNotifyMQ.getReConfirmWeight());
        spotCheckAppealEntity.setReConfirmVolume(spotCheckNotifyMQ.getReConfirmVolume());
        spotCheckAppealEntity.setDiffWeight(spotCheckNotifyMQ.getDiffWeight());
        spotCheckAppealEntity.setStanderDiff(spotCheckNotifyMQ.getStanderDiff());
        // 调用自动化接口获取设备校准 todo
        spotCheckAppealEntity.setBeforeWeightStatus(0);
        spotCheckAppealEntity.setBeforeVolumeStatus(0);
        spotCheckAppealEntity.setAfterWeightStatus(0);
        spotCheckAppealEntity.setAfterVolumeStatus(0);
        spotCheckAppealEntity.setAppealWeightStatus(0);
        spotCheckAppealEntity.setAppealVolumeStatus(0);
        spotCheckAppealEntity.setConfirmStatus(Constants.NUMBER_ZERO);
        spotCheckAppealEntity.setAutoStatus(Constants.NUMBER_ZERO);
        spotCheckAppealEntity.setYn(Constants.YN_YES);
        return spotCheckAppealEntity;
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
