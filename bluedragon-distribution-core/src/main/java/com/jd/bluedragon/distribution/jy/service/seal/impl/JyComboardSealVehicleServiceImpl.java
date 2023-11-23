package com.jd.bluedragon.distribution.jy.service.seal.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleInfoReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.response.JyAppDataSealVo;
import com.jd.bluedragon.common.dto.seal.response.SealVehicleInfoResp;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.jsf.work.WorkGridFlowDirectionManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.group.JyGroupService;
import com.jd.bluedragon.distribution.jy.service.seal.JyAppDataSealService;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleServiceImpl;
import com.jd.bluedragon.distribution.jy.service.send.IJySendAttachmentService;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.workStation.WorkGridFlowDirection;
import com.jdl.basic.api.domain.workStation.WorkGridFlowDirectionQuery;
import com.jdl.basic.api.enums.FlowDirectionTypeEnum;
import com.jdl.basic.api.enums.GridFlowLineTypeEnum;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;

/**
 * @author liwenji
 * @description 组板封车
 * @date 2023-10-13 16:00
 */
@Service("JyComboardSealVehicleService")
@Slf4j
public class JyComboardSealVehicleServiceImpl extends JySealVehicleServiceImpl {

    @Autowired
    private JyAppDataSealService jyAppDataSealService;

    @Autowired
    DmsConfigManager dmsConfigManager;

    @Autowired
    private JyBizTaskComboardService jyBizTaskComboardService;

    @Autowired
    private JyComboardAggsService jyComboardAggsService;

    @Autowired
    private IJySendAttachmentService jySendAttachmentService;

    @Autowired
    private WorkGridFlowDirectionManager workGridFlowDirectionManager;

    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComboardSealVehicleServiceImpl.setSavedPageData", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void setSavedPageData(SealVehicleInfoReq sealVehicleInfoReq, SealVehicleInfoResp sealVehicleInfoResp) {
        JyAppDataSealVo jyAppDataSealVo = jyAppDataSealService.loadSavedPageData(sealVehicleInfoReq.getSendVehicleDetailBizId());
        sealVehicleInfoResp.setSavedPageData(selectBoardByTmsAndInitWeightVolume(sealVehicleInfoReq, jyAppDataSealVo));
        sealVehicleInfoResp.setBoardLimit(dmsConfigManager.getPropertyConfig().getJyComboardSealBoardListLimit());
    }

    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComboardSealVehicleServiceImpl.sealVehicle", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Void> czSealVehicle(SealVehicleReq sealVehicleReq) {
        InvokeResult<Void> result = new InvokeResult<>();
        // 组板岗封车前置校验
        if (!comBoardSealCheckHandler(sealVehicleReq, result)) {
            return result;
        }
        return super.czSealVehicle(sealVehicleReq);
    }

    private boolean comBoardSealCheckHandler(SealVehicleReq sealVehicleReq, InvokeResult<Void> result) {
        Integer boardMinLimit = dmsConfigManager.getPropertyConfig().getCzSealCarBoardCountMinLimit();
        Integer packageMinLimit = dmsConfigManager.getPropertyConfig().getCzSealCarPackageCountMinLimit();

        // 获取封车网格流向校验
        if (sealVehicleReq.getCurrentOperate() != null
                && sealVehicleReq.getCurrentOperate().getOperatorData() != null
                && !StringUtils.isEmpty(sealVehicleReq.getCurrentOperate().getOperatorData().getWorkStationGridKey())) {
            List<Integer> flowList = getFlowByCondition(sealVehicleReq);
            JyBizTaskSendVehicleDetailEntity taskDetail = jyBizTaskSendVehicleDetailService.findByBizId(sealVehicleReq.getSendVehicleDetailBizId());
            if (CollectionUtils.isEmpty(flowList) || !flowList.contains(taskDetail.getEndSiteId().intValue())) {
                result.setCode(CZ_SEAL_CAR_GRID_NOT_HAVA_FLOW_CODE);
                result.setMessage(String.format(CZ_SEAL_CAR_GRID_NOT_HAVA_FLOW_MESSAGE, taskDetail.getEndSiteName()));
                return false;
            }
        }

        // 根据任务ID查询封车前拍照数据
        JySendAttachmentEntity query = new JySendAttachmentEntity();
        query.setSendVehicleBizId(sealVehicleReq.getSendVehicleBizId());
        JySendAttachmentEntity attachmentEntity = jySendAttachmentService.selectBySendVehicleBizId(query);
        if (attachmentEntity == null || StringUtils.isEmpty(attachmentEntity.getSealImgUrl())) {

            // 根据批次查询板信息
            JyBizTaskComboardEntity querySendCode = new JyBizTaskComboardEntity();
            querySendCode.setSendCodeList(new ArrayList<>(sealVehicleReq.getBatchCodes()));
            List<JyBizTaskComboardEntity> taskList = jyBizTaskComboardService.listBoardTaskBySendCode(querySendCode);
            List<String> boardList = taskList.stream().map(JyBizTaskComboardEntity::getBoardCode).collect(Collectors.toList());

            // 查询板的统计数据
            List<JyComboardAggsEntity> aggsEntities;
            try {
                aggsEntities = jyComboardAggsService.queryComboardAggs(boardList);
            } catch (Exception e) {
                log.info("组版封车查询板统计数据异常：{}", JsonHelper.toJson(boardList), e);
                return true;
            }

            // 封车包裹件数校验
            int packageCount = 0;
            if (!CollectionUtils.isEmpty(aggsEntities)) {
                for (JyComboardAggsEntity aggsEntity : aggsEntities) {
                    packageCount += aggsEntity.getPackageScannedCount() == null ? 0 : aggsEntity.getPackageScannedCount();
                    packageCount += aggsEntity.getBoxScannedCount() == null ? 0 : aggsEntity.getBoxScannedCount();
                }
            }
            // 封车板数量校验
            if (packageCount < packageMinLimit && sealVehicleReq.getBatchCodes().size() < boardMinLimit) {
                log.info("车辆封车的板数量不能小于{}", boardMinLimit);
                result.setCode(CZ_SEAL_CAR_BOARD_COUNT_MIN_LIMIT_CODE);
                result.setMessage(String.format(CZ_SEAL_CAR_BOARD_COUNT_MIN_LIMIT_MESSAGE, boardMinLimit, packageMinLimit));
                return false;
            }
        }

        return true;
    }

    private List<Integer> getFlowByCondition(SealVehicleReq sealVehicleReq) {
        WorkGridFlowDirectionQuery queryFlow = new WorkGridFlowDirectionQuery();
        queryFlow.setWorkStationGridKey(sealVehicleReq.getCurrentOperate().getOperatorData().getWorkStationGridKey());
        queryFlow.setLineType(GridFlowLineTypeEnum.TRANSFER.getCode());
        queryFlow.setFlowDirectionType(FlowDirectionTypeEnum.OUT_SITE.getCode());
        // 获取流向信息
        Result<List<WorkGridFlowDirection>> flowRes = workGridFlowDirectionManager.queryFlowByCondition(queryFlow);
        if (flowRes == null || !flowRes.isSuccess() || CollectionUtils.isEmpty(flowRes.getData())) {
            return null;
        }
        return flowRes.getData().stream().map(WorkGridFlowDirection::getFlowSiteCode).collect(Collectors.toList());
    }
}
