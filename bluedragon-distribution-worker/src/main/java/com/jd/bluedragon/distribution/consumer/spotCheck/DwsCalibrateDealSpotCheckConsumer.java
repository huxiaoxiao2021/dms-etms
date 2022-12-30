package com.jd.bluedragon.distribution.consumer.spotCheck;

import com.google.common.collect.Lists;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckAroundRecord;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckPackageRequest;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateStatusEnum;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.DWSCheckManager;
import com.jd.bluedragon.core.base.SpotCheckQueryManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckRecordTypeEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.service.SpotCheckDealService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.report.domain.spotcheck.SpotCheckQueryCondition;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * dws设备校准抽检处理消费
 *
 * @author hujiping
 * @date 2022/12/22 2:34 PM
 */
@Service("dwsCalibrateDealSpotCheckConsumer")
public class DwsCalibrateDealSpotCheckConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(DwsCalibrateDealSpotCheckConsumer.class);

    @Autowired
    private SpotCheckDealService spotCheckDealService;

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    @Qualifier("dwsCheckManager")
    private DWSCheckManager dwsCheckManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Override
    public void consume(Message message) throws Exception {
        CallerInfo info = Profiler.registerInfo("DwsCalibrateDealSpotCheck.consume", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        try {
            if (!JsonHelper.isJsonString(message.getText())) {
                logger.warn("dws设备校准抽检处理消息体非JSON格式，内容为【{}】", message.getText());
                return;
            }
            if (logger.isInfoEnabled()){
                logger.info("dws设备校准抽检处理开始...{}", message.getText());
            }
            WeightVolumeSpotCheckDto weightVolumeSpotCheckDto = JsonHelper.fromJsonUseGson(message.getText(), WeightVolumeSpotCheckDto.class);
            if(weightVolumeSpotCheckDto == null) {
                logger.warn("dws设备校准抽检处理消息体转换失败，内容为【{}】", message.getText());
                return;
            }
            if(!Objects.equals(weightVolumeSpotCheckDto.getReviewSource(), SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getCode())){
                logger.warn("非自动化设备抽检不处理!");
                return;
            }

            Integer waybillMachineStatus = null; // 运单维度记录设备状态

            if(Objects.equals(weightVolumeSpotCheckDto.getIsMultiPack(), Constants.CONSTANT_NUMBER_ONE)){
                // 一单多件处理
                if(Objects.equals(weightVolumeSpotCheckDto.getMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())) {
                    SpotCheckQueryCondition condition = new SpotCheckQueryCondition();
                    condition.setWaybillCode(weightVolumeSpotCheckDto.getWaybillCode());
                    condition.setReviewSiteCode(weightVolumeSpotCheckDto.getReviewSiteCode());
                    condition.setRecordType(SpotCheckRecordTypeEnum.DETAIL_RECORD.getCode());
                    List<WeightVolumeSpotCheckDto> packDetailList = spotCheckQueryManager.querySpotCheckByCondition(condition);
                    if(CollectionUtils.isEmpty(packDetailList)){
                        logger.warn("根据运单号:{}未查询到明细数据!", weightVolumeSpotCheckDto.getWaybillCode());
                        return;
                    }
                    // 批量校验包裹的设备状态
                    waybillMachineStatus = batchCheckWaybillMachineStatus(packDetailList);
                }else {
                    waybillMachineStatus = JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode();
                }
            }else {
                // 一单一件
                waybillMachineStatus = weightVolumeSpotCheckDto.getMachineStatus();
            }
            if(waybillMachineStatus != null){
                // 更新运单维度抽检记录的设备状态
                WeightVolumeSpotCheckDto updateSpotDto = new WeightVolumeSpotCheckDto();
                updateSpotDto.setPackageCode(weightVolumeSpotCheckDto.getPackageCode());
                updateSpotDto.setReviewSiteCode(weightVolumeSpotCheckDto.getReviewSiteCode());
                updateSpotDto.setMachineStatus(waybillMachineStatus);
                spotCheckQueryManager.insertOrUpdateSpotCheck(updateSpotDto);
            }

            // 下发抽检数据
            if(spotCheckDealService.spotCheckIssueIsRelyOnMachineStatus(weightVolumeSpotCheckDto.getReviewSiteCode())
                    && Objects.equals(waybillMachineStatus, JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())){
                spotCheckDealService.executeIssue(weightVolumeSpotCheckDto);
            }

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("dws设备校准抽检处理处理异常, 消息体:{}", message.getText(), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private Integer batchCheckWaybillMachineStatus(List<WeightVolumeSpotCheckDto> packDetailList) {
        for (List<WeightVolumeSpotCheckDto> singleList : Lists.partition(packDetailList, 100)) {
            List<DwsCheckPackageRequest> queryList = Lists.newArrayList();
            for (WeightVolumeSpotCheckDto packDto : singleList) {
                DwsCheckPackageRequest dwsCheckPackageRequest = new DwsCheckPackageRequest();
                dwsCheckPackageRequest.setPackageCode(packDto.getPackageCode());
                dwsCheckPackageRequest.setMachineCode(packDto.getMachineCode());
                dwsCheckPackageRequest.setOperateTime(packDto.getReviewDate());
                queryList.add(dwsCheckPackageRequest);
            }
            List<DwsCheckAroundRecord> dwsCheckAroundRecords = dwsCheckManager.batchSelectMachineStatus(queryList);
            if(CollectionUtils.isEmpty(dwsCheckAroundRecords)){
                logger.warn("批量查询设备状态为空!{}", JsonHelper.toJson(queryList));
                return null;
            }
            for (DwsCheckAroundRecord dwsCheckAroundRecord : dwsCheckAroundRecords) {
                // 只要出现不合格，运单维度就是不合格
                if(Objects.equals(dwsCheckAroundRecord.getPreviousMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode())
                        || Objects.equals(dwsCheckAroundRecord.getNextMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode())
                        // 没有上次设备状态也是不合格（设备刚投入使用）
                        || dwsCheckAroundRecord.getPreviousMachineStatus() == null){
                    return JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode();
                }
                // 上次下次均合格，但是时间间隔超过3h，则此包裹维度设备状态不合格，那么运单维度也是不合格
                if(Objects.equals(dwsCheckAroundRecord.getPreviousMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())
                        && Objects.equals(dwsCheckAroundRecord.getNextMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())
                        && dwsCheckAroundRecord.getPreviousCalibrateTime() != null
                        && dwsCheckAroundRecord.getNextCalibrateTime() != null
                        && (dwsCheckAroundRecord.getNextCalibrateTime() - dwsCheckAroundRecord.getPreviousCalibrateTime() > uccPropertyConfiguration.getMachineCalibrateIntervalTimeOfSpotCheck())){
                    return JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode();
                }
                if(dwsCheckAroundRecord.getNextMachineStatus() == null){
                    logger.info("包裹号:{}的抽检记录的设备编码:{}的下次设备状态未知!", dwsCheckAroundRecord.getPackageCode(), dwsCheckAroundRecord.getMachineCode());
                    return null;
                }
            }
        }
        return JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode();
    }

}
