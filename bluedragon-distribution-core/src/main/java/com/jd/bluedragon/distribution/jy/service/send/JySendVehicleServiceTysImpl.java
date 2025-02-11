package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendAbnormalPackRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.send.*;
import com.jd.bluedragon.distribution.jy.dto.unload.ExcepScanDto;
import com.jd.bluedragon.distribution.jy.enums.ExcepScanTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadBarCodeQueryEntranceEnum;
import com.jd.bluedragon.distribution.jy.manager.IJySendVehicleJsfManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jdl.basic.api.domain.vehicle.VehicleVolumeDicReq;
import com.jdl.basic.api.domain.vehicle.VehicleVolumeDicResp;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("jySendVehicleServiceTys")
public class JySendVehicleServiceTysImpl extends JySendVehicleServiceImpl implements ScanStatisticsService {
    @Autowired
    private IJySendVehicleJsfManager sendVehicleJsfManager;
    @Autowired
    JySendAggsService jySendAggsService;


    @Override
    public boolean checkBeforeFetchTask(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> result) {
        if (request.getVehicleStatus() == null) {
            result.parameterError("请选择车辆状态！");
            return false;
        }
        if (!NumberHelper.gt0(request.getPageSize()) || !NumberHelper.gt0(request.getPageNumber())) {
            result.parameterError("缺少分页参数！");
            return false;
        }
        if (request.getCurrentOperate() == null || !NumberHelper.gt0(request.getCurrentOperate().getSiteCode())) {
            result.parameterError("缺少当前场地信息！");
            return false;
        }
        return true;
    }

    @Override
    public void extracted(SendVehicleTaskResponse response, List<String> sendVehicleBizList) {
        response.setSendVehicleBizList(sendVehicleBizList);
    }

    @Override
    public ExcepWaybillDto queryExcepScanWaybill(QueryExcepWaybillDto queryExcepWaybillDto) {
        Pager<SendVehicleTaskQuery> queryPager =assembleQueryWaybillDto(queryExcepWaybillDto);
        Pager<SendWaybillDto> sendWaybillDtoPager =sendVehicleJsfManager.querySendTaskWaybill(queryPager);
        if (ObjectHelper.isNotNull(sendWaybillDtoPager)){
            ExcepWaybillDto excepWaybillDto =new ExcepWaybillDto();
            excepWaybillDto.setSendWaybillDtoList(sendWaybillDtoPager.getData());
            excepWaybillDto.setTotal(sendWaybillDtoPager.getTotal());
            return excepWaybillDto;
        }
        return null;
    }

    private Pager<SendVehicleTaskQuery> assembleQueryWaybillDto(QueryExcepWaybillDto queryExcepWaybillDto) {
        SendVehicleTaskQuery sendVehicleTaskQuery =new SendVehicleTaskQuery();
        sendVehicleTaskQuery.setSendVehicleBizId(queryExcepWaybillDto.getSendVehicleBizId());
        sendVehicleTaskQuery.setOperateSiteId(queryExcepWaybillDto.getOperateSiteId());
        sendVehicleTaskQuery.setQueryBarCodeFlag(queryExcepWaybillDto.getExcepScanTypeEnum().getCode());
        sendVehicleTaskQuery.setReceiveSiteIdList(queryExcepWaybillDto.getReceiveSiteIdList());
        Pager<SendVehicleTaskQuery> queryPager = new Pager<>();
        queryPager.setSearchVo(sendVehicleTaskQuery);
        queryPager.setPageNo(queryExcepWaybillDto.getPageNo());
        queryPager.setPageSize(queryExcepWaybillDto.getPageSize());
        return queryPager;
    }

    @Override
    public ExcepPackageDto queryExcepPackageUnderWaybill(QueryExcepPackageDto queryExcepPackageDto) {
        Pager<SendVehicleTaskQuery> queryPager = assembleQueryPackageDto(queryExcepPackageDto);
        Pager<SendPackageDto> sendPackageDtoPager = sendVehicleJsfManager.querySendPackageDetail(queryPager);
        if (ObjectHelper.isNotNull(sendPackageDtoPager)){
            ExcepPackageDto excepPackageDto = new ExcepPackageDto();
            excepPackageDto.setSendPackageDtoList(sendPackageDtoPager.getData());
            excepPackageDto.setTotal(sendPackageDtoPager.getTotal());
            return excepPackageDto;
        }
        return null;
    }

    @Override
    public List<SendGoodsCategoryDto> listGoodsCategory(SendGoodsQueryDto sendGoodsQueryDto) {
        return null;
    }

    @Override
    public List<SendExcepScanDto> listExcepScanType(ExcepScanQueryDto query) {
        JySendAggsEntity agg =jySendAggsService.getVehicleSendStatistics(query.getSendVehicleBizId());
        if (agg == null) {
            return null;
        }
        List<SendExcepScanDto> sendExcepScanDtoList =new ArrayList<>();
        SendExcepScanDto haveScan = new SendExcepScanDto();
        haveScan.setType(ExcepScanTypeEnum.HAVE_SCAN.getCode());
        haveScan.setName(ExcepScanTypeEnum.HAVE_SCAN.getName());
        haveScan.setCount(agg.getTotalScannedWaybillCount());
        sendExcepScanDtoList.add(haveScan);

        SendExcepScanDto inCompelete = new SendExcepScanDto();
        inCompelete.setType(ExcepScanTypeEnum.INCOMPELETE.getCode());
        inCompelete.setName(ExcepScanTypeEnum.INCOMPELETE.getName());
        inCompelete.setCount(agg.getTotalIncompleteWaybillCount());
        sendExcepScanDtoList.add(inCompelete);

        SendExcepScanDto haveInspectionButNotSend = new SendExcepScanDto();
        haveInspectionButNotSend.setType(ExcepScanTypeEnum.HAVE_INSPECTION_BUT_NOT_SEND.getCode());
        haveInspectionButNotSend.setName(ExcepScanTypeEnum.HAVE_INSPECTION_BUT_NOT_SEND.getName());
        haveInspectionButNotSend.setCount(agg.getTotalNotScannedWaybillCount());
        sendExcepScanDtoList.add(haveInspectionButNotSend);

        SendExcepScanDto interceptedAndForced = new SendExcepScanDto();
        interceptedAndForced.setType(ExcepScanTypeEnum.INTERCEPTED_AND_FORCE.getCode());
        interceptedAndForced.setName(ExcepScanTypeEnum.INTERCEPTED_AND_FORCE.getName());
        interceptedAndForced.setCount(agg.getTotalInterceptWaybillCount()+agg.getTotalForceWaybillCount());
        sendExcepScanDtoList.add(interceptedAndForced);

        return sendExcepScanDtoList;
    }

    private Pager<SendVehicleTaskQuery> assembleQueryPackageDto(QueryExcepPackageDto queryExcepPackageDto) {
        SendVehicleTaskQuery sendVehicleTaskQuery = new SendVehicleTaskQuery();
        sendVehicleTaskQuery.setSendVehicleBizId(queryExcepPackageDto.getSendVehicleBizId());
        sendVehicleTaskQuery.setOperateSiteId(queryExcepPackageDto.getOperateSiteId());
        sendVehicleTaskQuery.setReceiveSiteIdList(queryExcepPackageDto.getReceiveSiteIdList());
        sendVehicleTaskQuery.setWaybillCode(queryExcepPackageDto.getWaybillCode());
        sendVehicleTaskQuery.setQueryBarCodeFlag(queryExcepPackageDto.getExcepScanTypeEnum().getCode());
        Pager<SendVehicleTaskQuery> queryPager = new Pager<>();
        queryPager.setSearchVo(sendVehicleTaskQuery);
        queryPager.setPageNo(queryExcepPackageDto.getPageNo());
        queryPager.setPageSize(queryExcepPackageDto.getPageSize());
        return queryPager;
    }

    @Override
    Integer getVehicleVolume(JyBizTaskSendVehicleEntity taskSend,VehicleVolumeDicResp vehicleVolumeDicResp){
        if (JyLineTypeEnum.TRUNK_LINE.getCode().equals(taskSend.getLineType())){
            return vehicleVolumeDicResp.getVolumeTrunkB();
        }
        else {
            return vehicleVolumeDicResp.getVolumeBranchB();
        }
    }
}
