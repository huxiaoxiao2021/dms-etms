package com.jd.bluedragon.distribution.jy.service.unload;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum.*;

@Service
@Slf4j
public class JyUnloadVehicleTysServiceImpl implements JyUnloadVehicleTysService {
    @Autowired
    JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Override
    public InvokeResult<UnloadVehicleTaskRespDto> listUnloadVehicleTask(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        //查询统计数据
        JyBizTaskUnloadStatusEnum[] statusEnums = {WAIT_UN_LOAD, UN_LOADING, UN_LOAD_DONE};
        List<JyBizTaskUnloadCountDto> unloadCountDtos = jyBizTaskUnloadVehicleService.findStatusCountByCondition4Status(null, null, statusEnums);
        if (!ObjectHelper.isNotNull(unloadCountDtos)){
            return new InvokeResult(TASK_NO_FOUND_BY_STATUS_CODE,TASK_NO_FOUND_BY_STATUS_MESSAGE);
        }
        UnloadVehicleTaskRespDto respDto = new UnloadVehicleTaskRespDto();
        initCountToResp(respDto,unloadCountDtos);

        PageHelper.startPage(unloadVehicleTaskReqDto.getPageNo(), unloadVehicleTaskReqDto.getPageSize());
        JyBizTaskUnloadVehicleEntity entity=new JyBizTaskUnloadVehicleEntity();
        entity.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        entity.setEndSiteId(Long.valueOf(unloadVehicleTaskReqDto.getCurrentOperate().getSiteCode()));
        List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = jyBizTaskUnloadVehicleService.listUnloadVehicleTask(entity);
        respDto.setUnloadVehicleTaskDtoList(unloadVehicleTaskDtoList);
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,respDto);
    }

    private void initCountToResp(UnloadVehicleTaskRespDto unloadVehicleTaskRespDto, List<JyBizTaskUnloadCountDto> unloadCountDtos) {
        for (JyBizTaskUnloadCountDto unloadCountDto : unloadCountDtos) {
            switch (unloadCountDto.getVehicleStatus()) {
                case 3:
                    unloadVehicleTaskRespDto.setToUnloadCount(unloadCountDto.getSum());
                    break;
                case 4:
                    unloadVehicleTaskRespDto.setUnloadingCount(unloadCountDto.getSum());
                    break;
                case 5:
                    unloadVehicleTaskRespDto.setUnloadedCount(unloadCountDto.getSum());
                    break;
                default:
                    log.info("");
            }
        }
    }

    @Override
    public InvokeResult<List<UnloadVehicleTaskDto>> queryUnloadVehicleTaskByVehicleNumOrPackage(QueryUnloadTaskDto queryUnloadTaskDto) {
        if (ObjectHelper.isNotNull(queryUnloadTaskDto.getVehicleNumber())){
            JyBizTaskUnloadVehicleEntity entity =new JyBizTaskUnloadVehicleEntity();
            entity.setFuzzyVehicleNumber(queryUnloadTaskDto.getVehicleNumber());
            entity.setEndSiteId(Long.valueOf(queryUnloadTaskDto.getCurrentOperate().getSiteCode()));
            jyBizTaskUnloadVehicleService.listUnloadVehicleTask(entity);

        }
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
