package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DWSCheckAppealDto;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DWSCheckAppealRequest;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentBizTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateStatusEnum;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.DWSCheckManager;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.base.SpotCheckServiceProxy;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
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
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 抽检回传消息消费
 *
 * @author hujiping
 * @date 2021/12/14 9:36 下午
 */
@Service("spotCheckNotifyConsumer")
public class SpotCheckNotifyConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(SpotCheckNotifyConsumer.class);

    private static final String SPOT_CHECK_APPEAL_PREFIX = "spot:check:appeal:";

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

    @Autowired
    private DWSCheckManager dwsCheckManager;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

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

        // 更新抽检状态
        updateDto.setSpotCheckStatus(status);
        // 二级超标类型
        updateDto.setExcessSubType(spotCheckNotifyMQ.getExceedSubType());

        // 是否是分拣-设备抽检
        boolean isDwsSpotCheck = SpotCheckConstants.EQUIPMENT_SPOT_CHECK.equals(flowSystem);
        // 是否是申诉状态
        boolean isAppealStatus = SpotCheckStatusEnum.SPOT_CHECK_STATUS_PZ_UPGRADE.getCode().equals(status);
        // 针对设备抽检且是申诉状态的单据及申诉附件需要走新业务流程
        if (isDwsSpotCheck && isAppealStatus) {
            // 运单加锁防止并发问题
            String mutexKey = SPOT_CHECK_APPEAL_PREFIX + waybillCode;
            try {
                if (!redisClientOfJy.set(mutexKey, Constants.EMPTY_FILL, Constants.CONSTANT_NUMBER_ONE, TimeUnit.MINUTES, false)) {
                    String warnMsg = String.format("运单号:%s-设备抽检申诉核对记录保存正在处理中!", waybillCode);
                    logger.warn(warnMsg);
                    throw new JyBizException(warnMsg);
                }
                // 组装申诉记录数据
                SpotCheckAppealEntity spotCheckAppealEntity = transformData(spotCheckNotifyMQ, updateDto);
                // 查询是否已经保存过
                if (spotCheckAppealService.findByBizId(spotCheckAppealEntity) == null) {
                    // 插入数据库
                    spotCheckAppealService.insertRecord(spotCheckAppealEntity);
                    // 组装附件数据
                    List<JyAttachmentDetailEntity> jyAttachmentDetailEntityList = createAttachmentList(spotCheckNotifyMQ);
                    // 插入附件表
                    jyAttachmentDetailService.batchInsert(jyAttachmentDetailEntityList);
                    spotCheckServiceProxy.insertOrUpdateProxyReform(updateDto);
                }
            } catch (Exception e) {
                logger.error("设备抽检申诉核对记录保存出现异常:waybillCode={},e=", waybillCode, e);
            } finally {
                redisClientOfJy.del(mutexKey);
            }
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
            attachmentDetailEntity.setBizSubType(String.valueOf(appendixType));
            attachmentDetailEntity.setCreateUserErp(Constants.SYS_NAME);
            attachmentDetailEntity.setUpdateUserErp(Constants.SYS_NAME);
            // 如果是图片
            if (SpotCheckAppendixTypeEnum.ESCALATION_PICTURE.getCode().equals(appendixType) || SpotCheckAppendixTypeEnum.REPORT_PICTURE.getCode().equals(appendixType)) {
                attachmentDetailEntity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
                // 如果是视频
            } else if (SpotCheckAppendixTypeEnum.ESCALATION_VIDEO.getCode().equals(appendixType) || SpotCheckAppendixTypeEnum.REPORT_VIDEO.getCode().equals(appendixType)) {
                attachmentDetailEntity.setAttachmentType(JyAttachmentTypeEnum.VIDEO.getCode());
            } else {
                attachmentDetailEntity.setAttachmentType(Constants.NUMBER_ZERO);
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
        spotCheckAppealEntity.setStartTime(new Date(updateDto.getReviewDate()));
        spotCheckAppealEntity.setStartProvinceCode(spotCheckNotifyMQ.getStartProvinceAgencyCode());
        spotCheckAppealEntity.setStartProvinceName(spotCheckNotifyMQ.getStartProvinceAgencyName());
        // 查询青龙基础资料补全抽检人枢纽
        if (StringUtils.isNotBlank(spotCheckNotifyMQ.getOrgCode())) {
            BaseStaffSiteOrgDto startBaseSite = baseMajorManager.getBaseSiteBySiteId(Integer.valueOf(spotCheckNotifyMQ.getOrgCode()));
            if (startBaseSite != null) {
                spotCheckAppealEntity.setStartHubCode(startBaseSite.getAreaCode());
                spotCheckAppealEntity.setStartHubName(startBaseSite.getAreaName());
            }
        }
        spotCheckAppealEntity.setStartSiteCode(spotCheckNotifyMQ.getOrgCode());
        spotCheckAppealEntity.setStartSiteName(spotCheckNotifyMQ.getOrgName());
        spotCheckAppealEntity.setStartErp(spotCheckNotifyMQ.getStartStaffAccount());
        spotCheckAppealEntity.setDutyType(spotCheckNotifyMQ.getDutyType());
        spotCheckAppealEntity.setDutyProvinceCode(spotCheckNotifyMQ.getDutyProvinceAgencyCode());
        spotCheckAppealEntity.setDutyProvinceName(spotCheckNotifyMQ.getDutyProvinceAgencyCode());
        // 查询青龙基础资料补全申诉人枢纽
        if (StringUtils.isNotBlank(spotCheckNotifyMQ.getDutyOrgCode())) {
            BaseStaffSiteOrgDto dutyBaseSite = baseMajorManager.getBaseSiteBySiteId(Integer.valueOf(spotCheckNotifyMQ.getDutyOrgCode()));
            if (dutyBaseSite != null) {
                spotCheckAppealEntity.setDutyHubCode(dutyBaseSite.getAreaCode());
                spotCheckAppealEntity.setDutyHubName(dutyBaseSite.getAreaName());
            }
        }
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
        spotCheckAppealEntity.setConfirmStatus(Constants.NUMBER_ZERO);
        spotCheckAppealEntity.setAutoStatus(Constants.NUMBER_ZERO);
        // 创建时间和更新时间初始化为申诉时间
        spotCheckAppealEntity.setCreateTime(DateHelper.parseDateTime(spotCheckNotifyMQ.getStatusUpdateTime()));
        spotCheckAppealEntity.setUpdateTime(DateHelper.parseDateTime(spotCheckNotifyMQ.getStatusUpdateTime()));
        spotCheckAppealEntity.setYn(Constants.YN_YES);
        // 调用自动化接口获取设备校准
        setDwsCheckStatus(spotCheckAppealEntity);
        return spotCheckAppealEntity;
    }

    private void setDwsCheckStatus(SpotCheckAppealEntity spotCheckAppealEntity) {
        DWSCheckAppealRequest dwsCheckAppealRequest = new DWSCheckAppealRequest();
        // 设备编码
        dwsCheckAppealRequest.setMachineCode(spotCheckAppealEntity.getDeviceCode());
        // 抽检时间
        dwsCheckAppealRequest.setStartTime(spotCheckAppealEntity.getStartTime());
        // 申诉时间
        dwsCheckAppealRequest.setAppealTime(spotCheckAppealEntity.getCreateTime());
        DWSCheckAppealDto dwsCheckResult = dwsCheckManager.getDwsCheckStatusAppeal(dwsCheckAppealRequest);
        if (dwsCheckResult != null) {
            spotCheckAppealEntity.setBeforeWeightStatus(getRealStatus(dwsCheckResult.getBeforeWeightStatus()));
            spotCheckAppealEntity.setBeforeVolumeStatus(getRealStatus(dwsCheckResult.getBeforeVolumeStatus()));
            spotCheckAppealEntity.setAfterWeightStatus(getRealStatus(dwsCheckResult.getAfterWeightStatus()));
            spotCheckAppealEntity.setAfterVolumeStatus(getRealStatus(dwsCheckResult.getAfterVolumeStatus()));
            spotCheckAppealEntity.setAppealWeightStatus(getRealStatus(dwsCheckResult.getAppealWeightStatus()));
            spotCheckAppealEntity.setAppealVolumeStatus(getRealStatus(dwsCheckResult.getAppealVolumeStatus()));
        } else {
            spotCheckAppealEntity.setBeforeWeightStatus(Constants.NUMBER_ONE);
            spotCheckAppealEntity.setBeforeVolumeStatus(Constants.NUMBER_ONE);
            spotCheckAppealEntity.setAfterWeightStatus(Constants.NUMBER_ONE);
            spotCheckAppealEntity.setAfterVolumeStatus(Constants.NUMBER_ONE);
            spotCheckAppealEntity.setAppealWeightStatus(Constants.NUMBER_ZERO);
            spotCheckAppealEntity.setAppealVolumeStatus(Constants.NUMBER_ZERO);
        }
    }

    private Integer getRealStatus(Integer dwsCheckStatus) {
        if (Constants.NUMBER_ZERO.equals(dwsCheckStatus)) {
            return JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode();
        } else if (Constants.NUMBER_ONE.equals(dwsCheckStatus)) {
            return JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode();
        }
        return JyBizTaskMachineCalibrateStatusEnum.NO_CALIBRATE.getCode();
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
