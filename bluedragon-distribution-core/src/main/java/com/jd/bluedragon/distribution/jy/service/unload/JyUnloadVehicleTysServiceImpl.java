package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.utils.JsonHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@Slf4j
public class JyUnloadVehicleTysServiceImpl implements JyUnloadVehicleTysService{
    @Override
    public InvokeResult<UnloadVehicleTaskRespDto> listUnloadVehicleTask(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        return null;
    }

    @Override
    public InvokeResult<List<UnloadVehicleTaskDto>> queryUnloadVehicleTaskByVehicleNumOrPackage(QueryUnloadTaskDto queryUnloadTaskDto) {
        return null;
    }

    @Override
    public InvokeResult updateUnloadVehicleTaskProper(UnloadVehicleTaskDto unloadVehicleTask) {
        return null;
    }

    @Override
    public InvokeResult<UnloadVehicleTaskDto> queryStatisticsByDiffDimension(DimensionQueryDto dto) {
        return null;
    }

    @Override
    public InvokeResult<UnloadVehicleTaskDto> queryStatisticsDetailByDiffDimension(String bizId) {
        return null;
    }

    @Override
    public InvokeResult<ScanPackageRespDto> scanAndComBoard(ScanPackageDto scanPackageDto) {
        log.info("invoking jy scanAndComBoard,params: {}", JsonHelper.toJson(scanPackageDto));
        //校验一下当前有没有进行中的板，有

        return null;
    }

    @Override
    public InvokeResult<ScanPackageRespDto> queryComBoardDataByBoardCode(String boardCode) {
        return null;
    }

    @Override
    public InvokeResult<List<GoodsCategoryDto>> queryGoodsCategoryByDiffDimension(QueryGoodsCategory queryGoodsCategory) {
        return null;
    }

    @Override
    public InvokeResult<List<UnloadWaybillDto>> queryUnloadDetailByDiffDimension(QueryUnloadDetailDto queryUnloadDetailDto) {
        return null;
    }

    @Override
    public InvokeResult<List<UnloadTaskFlowDto>> queryUnloadTaskFlow(String bizId) {
        return null;
    }

    @Override
    public InvokeResult<List<ComBoardDto>> queryComBoarUnderTaskFlow(TaskFlowDto taskFlowDto) {
        return null;
    }

    @Override
    public InvokeResult<List<UnloadWaybillDto>> queryWaybillUnderBoard(QueryBoardDto queryBoardDto) {
        return null;
    }

    @Override
    public InvokeResult<List<UnloadPackageDto>> queryPackageUnderWaybill(QueryWaybillDto queryWaybillDto) {
        return null;
    }

    @Override
    public InvokeResult cancelComBoard(CancelComBoardDto cancelComBoardDto) {
        return null;
    }
}
