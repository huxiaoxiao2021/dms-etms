package com.jd.bluedragon.distribution.jy.service.unload;

import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.base.BoardCommonManagerImpl;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.UnloadStatisticsQueryTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BizSourceEnum;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum.*;

@Service
@Slf4j
public class JyUnloadVehicleTysServiceImpl implements JyUnloadVehicleTysService {
    @Autowired
    JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;
    @Autowired
    IJyUnloadVehicleManager iJyUnloadVehicleManager;
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    BoardCommonManager boardCommonManager;
    @Autowired
    @Qualifier("jyUnloadScanProducer")
    private DefaultJMQProducer unloadScanProducer;
    @Override
    public InvokeResult<UnloadVehicleTaskRespDto> listUnloadVehicleTask(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        if (ObjectHelper.isNotNull(unloadVehicleTaskReqDto.getPackageCode())) {
            return queryUnloadVehicleTaskByVehicleNumOrPackage(unloadVehicleTaskReqDto);
        }
        //查询统计数据
        JyBizTaskUnloadStatusEnum[] statusEnums = {WAIT_UN_LOAD, UN_LOADING, UN_LOAD_DONE};
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setLineType(unloadVehicleTaskReqDto.getLineType());
        condition.setEndSiteId(Long.valueOf(unloadVehicleTaskReqDto.getCurrentOperate().getSiteCode()));
        condition.setFuzzyVehicleNumber(unloadVehicleTaskReqDto.getVehicleNumber());
        List<JyBizTaskUnloadCountDto> unloadCountDtos = jyBizTaskUnloadVehicleService.findStatusCountByCondition4Status(condition, null, statusEnums);
        if (!ObjectHelper.isNotNull(unloadCountDtos)) {
            return new InvokeResult(TASK_NO_FOUND_BY_STATUS_CODE, TASK_NO_FOUND_BY_STATUS_MESSAGE);
        }
        UnloadVehicleTaskRespDto respDto = new UnloadVehicleTaskRespDto();
        initCountToResp(respDto, unloadCountDtos);

        PageHelper.startPage(unloadVehicleTaskReqDto.getPageNo(), unloadVehicleTaskReqDto.getPageSize());
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        entity.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        entity.setEndSiteId(Long.valueOf(unloadVehicleTaskReqDto.getCurrentOperate().getSiteCode()));
        entity.setLineType(unloadVehicleTaskReqDto.getLineType());
        entity.setFuzzyVehicleNumber(unloadVehicleTaskReqDto.getVehicleNumber());
        List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = jyBizTaskUnloadVehicleService.listUnloadVehicleTask(entity);
        respDto.setUnloadVehicleTaskDtoList(unloadVehicleTaskDtoList);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, respDto);
    }

    private void initCountToResp(UnloadVehicleTaskRespDto unloadVehicleTaskRespDto, List<JyBizTaskUnloadCountDto> unloadCountDtos) {
        for (JyBizTaskUnloadCountDto unloadCountDto : unloadCountDtos) {
            switch (unloadCountDto.getVehicleStatus()) {
                case 3:
                    unloadVehicleTaskRespDto.setWaitUnloadCount(unloadCountDto.getSum());
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
    public InvokeResult<UnloadVehicleTaskRespDto> queryUnloadVehicleTaskByVehicleNumOrPackage(UnloadVehicleTaskReqDto queryUnloadTaskDto) {
        UnloadVehicleTaskRespDto unloadVehicleTaskRespDto = new UnloadVehicleTaskRespDto();
        if (ObjectHelper.isNotNull(queryUnloadTaskDto.getPackageCode()) && WaybillUtil.isPackageCode(queryUnloadTaskDto.getPackageCode())) {
            JyVehicleTaskUnloadDetail detail = new JyVehicleTaskUnloadDetail();
            detail.setPackageCode(queryUnloadTaskDto.getPackageCode());
            List<JyVehicleTaskUnloadDetail> unloadDetailList = iJyUnloadVehicleManager.findUnloadDetail(detail);
            if (ObjectHelper.isNotNull(unloadDetailList)) {
                List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = convertUnloadVehicleTaskDto(unloadDetailList);
                calculationCount(unloadVehicleTaskRespDto, unloadVehicleTaskDtoList);
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadVehicleTaskRespDto);
            }
        } else if (ObjectHelper.isNotNull(queryUnloadTaskDto.getVehicleNumber())) {
            JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
            entity.setFuzzyVehicleNumber(queryUnloadTaskDto.getVehicleNumber());
            entity.setEndSiteId(Long.valueOf(queryUnloadTaskDto.getCurrentOperate().getSiteCode()));
            List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = jyBizTaskUnloadVehicleService.listUnloadVehicleTask(entity);
            calculationCount(unloadVehicleTaskRespDto, unloadVehicleTaskDtoList);
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadVehicleTaskRespDto);
        }
        return new InvokeResult(TASK_NO_FOUND_BY_PARAMS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
    }

    private void calculationCount(UnloadVehicleTaskRespDto unloadVehicleTaskRespDto, List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList) {
        unloadVehicleTaskRespDto.setUnloadVehicleTaskDtoList(unloadVehicleTaskDtoList);
        int waitUnloadCount = 0;
        //卸车中数量
        int unloadingCount = 0;
        //已完成卸车数量
        int unloadedCount = 0;
        for (UnloadVehicleTaskDto unloadVehicleTaskDto : unloadVehicleTaskDtoList) {
            switch (unloadVehicleTaskDto.getVehicleStatus()) {
                case 3:
                    waitUnloadCount++;
                    break;
                case 4:
                    unloadingCount++;
                    break;
                case 5:
                    unloadedCount++;
                    break;
                default:
                    log.info("");
            }
        }
        unloadVehicleTaskRespDto.setWaitUnloadCount(waitUnloadCount);
        unloadVehicleTaskRespDto.setUnloadingCount(unloadingCount);
        unloadVehicleTaskRespDto.setUnloadedCount(unloadedCount);
    }

    private List<UnloadVehicleTaskDto> convertUnloadVehicleTaskDto(List<JyVehicleTaskUnloadDetail> unloadDetailList) {
        List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = new ArrayList<>();
        JyVehicleTaskUnloadDetail detail = unloadDetailList.get(0);
        JyBizTaskUnloadVehicleEntity entity = jyBizTaskUnloadVehicleService.findByBizId(detail.getBizId());
        UnloadVehicleTaskDto unloadVehicleTaskDto = jyBizTaskUnloadVehicleService.entityConvertDto(entity);
        unloadVehicleTaskDtoList.add(unloadVehicleTaskDto);
        return unloadVehicleTaskDtoList;
    }

    @Override
    public InvokeResult updateUnloadVehicleTaskProperty(UnloadVehicleTaskDto unloadVehicleTask) {
        JyBizTaskUnloadVehicleEntity entity = BeanUtils.convert(unloadVehicleTask, JyBizTaskUnloadVehicleEntity.class);
        Boolean success = jyBizTaskUnloadVehicleService.saveOrUpdateOfBusinessInfo(entity);
        if (success) {
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    @Override
    public InvokeResult<ScanStatisticsDto> queryStatisticsByDiffDimension(DimensionQueryDto dto) {
        if (UnloadStatisticsQueryTypeEnum.PACKAGE.getCode().equals(dto.getType()) || UnloadStatisticsQueryTypeEnum.WAYBILL.getCode().equals(dto.getType())) {
            ScanStatisticsDto scanStatisticsDto = jyBizTaskUnloadVehicleService.queryStatisticsByDiffDimension(dto);
            return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, scanStatisticsDto);
        }
        return new InvokeResult(NOT_SUPPORT_TYPE_QUERY_CODE, NOT_SUPPORT_TYPE_QUERY_MESSAGE);
    }

    @Override
    public InvokeResult<UnloadVehicleTaskDto> queryTaskDataByBizId(String bizId) {
        UnloadVehicleTaskDto unloadVehicleTaskDto =jyBizTaskUnloadVehicleService.queryTaskDataByBizId(bizId);
        if (ObjectHelper.isNotNull(unloadVehicleTaskDto)){
            return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,unloadVehicleTaskDto);
        }
        return new InvokeResult(TASK_NO_FOUND_BY_PARAMS_CODE,TASK_NO_FOUND_BY_PARAMS_MESSAGE);
    }

    @Override
    public InvokeResult<ScanPackageRespDto> scanAndComBoard(ScanPackageDto scanPackageDto) {
        log.info("invoking jy scanAndComBoard,params: {}", JsonHelper.toJson(scanPackageDto));
        JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(scanPackageDto.getBizId());
        if (!ObjectHelper.isNotNull(unloadVehicleEntity)) {
           return new InvokeResult(TASK_NO_FOUND_BY_PARAMS_CODE,TASK_NO_FOUND_BY_PARAMS_MESSAGE);
        }
        checkPackageCode(scanPackageDto,unloadVehicleEntity);
        //校验一下当前有没有进行中的板，有 直接组板
        Boolean comBoardSuccess =false;
        if (ObjectHelper.isNotNull(scanPackageDto.getBoardCode())) {
            ExecComBoardDto comBoardDto =new ExecComBoardDto(scanPackageDto.getBoardCode(),scanPackageDto.getScanCode());
            comBoardSuccess =execComBoard(comBoardDto,scanPackageDto);
        } else {
            //生成新板
            String boardCode =generateBoard(scanPackageDto);
            if (ObjectHelper.isNotNull(boardCode)){
                ExecComBoardDto comBoardDto =new ExecComBoardDto(boardCode,scanPackageDto.getScanCode());
                comBoardSuccess=execComBoard(comBoardDto,scanPackageDto);
            }
        }
        if (comBoardSuccess){
            UnloadScanDto unloadScanDto = createUnloadDto(scanPackageDto, unloadVehicleEntity);
            unloadScanProducer.sendOnFailPersistent(unloadScanDto.getBarCode(), JsonHelper.toJson(unloadScanDto));
        }
        return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE);
    }

    private UnloadScanDto createUnloadDto(ScanPackageDto request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        Date operateTime = new Date();
        UnloadScanDto unloadScanDto = new UnloadScanDto();
        unloadScanDto.setBizId(request.getBizId());
        // 无任务场景下没有sealCarCode
        unloadScanDto.setSealCarCode(StringUtils.isBlank(request.getSealCarCode()) ? StringUtils.EMPTY : request.getSealCarCode());
        unloadScanDto.setVehicleNumber(taskUnloadVehicle.getVehicleNumber());
        unloadScanDto.setStartSiteId(taskUnloadVehicle.getStartSiteId());
        unloadScanDto.setEndSiteId(taskUnloadVehicle.getEndSiteId());
        unloadScanDto.setManualCreatedFlag(taskUnloadVehicle.getManualCreatedFlag());
        unloadScanDto.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
        unloadScanDto.setBarCode(request.getScanCode());
        unloadScanDto.setOperateTime(operateTime);
        unloadScanDto.setCreateUserErp(request.getUser().getUserErp());
        unloadScanDto.setCreateUserName(request.getUser().getUserName());
        unloadScanDto.setUpdateUserErp(request.getUser().getUserErp());
        unloadScanDto.setUpdateUserName(request.getUser().getUserName());
        unloadScanDto.setCreateTime(operateTime);
        unloadScanDto.setUpdateTime(operateTime);

        //unloadScanDto.setGroupCode(request.getGroupCode());
        unloadScanDto.setTaskId(request.getTaskId());

        return unloadScanDto;
    }

    private void checkPackageCode(ScanPackageDto scanPackageDto,JyBizTaskUnloadVehicleEntity entity) {
        //校验流向
    }

    private Boolean execComBoard(ExecComBoardDto comBoardDto,ScanPackageDto scanPackageDto) {
        AddBoardBox addBoardBox = new AddBoardBox();
        addBoardBox.setBoardCode(comBoardDto.getBoardCode());
        addBoardBox.setBoxCode(comBoardDto.getBarCode());
        addBoardBox.setOperatorErp(scanPackageDto.getUser().getUserErp());
        addBoardBox.setOperatorName(scanPackageDto.getUser().getUserName());
        addBoardBox.setSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
        addBoardBox.setSiteName(scanPackageDto.getCurrentOperate().getSiteName());
        addBoardBox.setSiteType(1);
        addBoardBox.setBizSource(BizSourceEnum.PDA.getValue());

        Response<Integer> rs =groupBoardManager.addBoxToBoard(addBoardBox);
        if (ObjectHelper.isNotNull(rs) &&  ResponseEnum.SUCCESS.getIndex()==rs.getCode()) {
            BoardCommonRequest boardCommonRequest = new BoardCommonRequest();//TODO
            boardCommonManager.sendWaybillTrace(boardCommonRequest, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
        }
        return false;
    }

    private String generateBoard(ScanPackageDto scanPackageDto) {
        AddBoardRequest addBoardRequest = new AddBoardRequest();
        addBoardRequest.setBoardCount(1);
        addBoardRequest.setDestinationId(1);
        addBoardRequest.setDestination("");
        addBoardRequest.setOperatorErp(scanPackageDto.getUser().getUserErp());
        addBoardRequest.setOperatorName(scanPackageDto.getUser().getUserName());
        addBoardRequest.setSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
        addBoardRequest.setSiteName(scanPackageDto.getCurrentOperate().getSiteName());
        addBoardRequest.setBizSource(1);
        Response<List<Board>> createBoardResp = groupBoardManager.createBoards(addBoardRequest);
        if (ObjectHelper.isNotNull(createBoardResp) && ResponseEnum.SUCCESS.getIndex() == createBoardResp.getCode()) {
            Board board = createBoardResp.getData().get(0);
            return board.getCode();
        }
        return null;
    }

    @Override
    public InvokeResult<ScanPackageRespDto> scanAndComBoardForPipelining(ScanPackageDto scanPackageDto) {
        return null;
    }

    @Override
    public InvokeResult<ComBoardDto> queryComBoardDataByBoardCode(String boardCode) {
        return null;
    }

    @Override
    public InvokeResult<List<GoodsCategoryDto>> queryGoodsCategoryByDiffDimension(QueryGoodsCategory queryGoodsCategory) {
        return null;
    }

    @Override
    public InvokeResult<ScanStatisticsInnerDto> queryUnloadDetailByDiffDimension(QueryUnloadDetailDto queryUnloadDetailDto) {
        return null;
    }

    @Override
    public InvokeResult<List<UnloadTaskFlowDto>> queryUnloadTaskFlow(String bizId) {
        return null;
    }

    @Override
    public InvokeResult<TaskFlowComBoardDto> queryComBoarUnderTaskFlow(TaskFlowDto taskFlowDto) {
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

    public static void main(String[] args) {
        System.out.println(BusinessUtil.isSendCode("333833-841276-2022071411736219212"));
    }
}
