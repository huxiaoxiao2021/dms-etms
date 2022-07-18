package com.jd.bluedragon.distribution.jy.service.unload;

import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.LineTypeStatis;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadStatisticsQueryTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.ws.BasicGoodsAreaWS;
import com.jd.transboard.api.dto.AddBoardBox;
import com.jd.transboard.api.dto.AddBoardRequest;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.BizSourceEnum;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.WAYBILL_ROUTER_SPLIT;
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
    @Autowired
    private WaybillCacheService waybillCacheService;
    @Autowired
    private BasicGoodsAreaWS basicGoodsAreaWS;
    @Autowired
    IJyUnloadVehicleManager jyUnloadVehicleManager;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private JyUnloadDao jyUnloadDao;


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

        condition.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        List<LineTypeStatisDto> lineTypeStatisDtoList=calculationLineTypeStatis(condition);
        respDto.setUnloadVehicleTaskDtoList(unloadVehicleTaskDtoList);
        respDto.setLineTypeStatisDtoList(lineTypeStatisDtoList);
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, respDto);
    }

    private List<LineTypeStatisDto> calculationLineTypeStatis(JyBizTaskUnloadVehicleEntity condition) {
        List<LineTypeStatisDto> lineTypeList = new ArrayList<>();
        JyBizTaskUnloadStatusEnum statusEnum = JyBizTaskUnloadStatusEnum.getEnumByCode(condition.getVehicleStatus());
        List<JyBizTaskUnloadCountDto> lineTypeAgg =jyBizTaskUnloadVehicleService.findStatusCountByCondition4StatusAndLine(condition,null,statusEnum);
        if (CollectionUtils.isNotEmpty(lineTypeAgg)) {
            for (JyBizTaskUnloadCountDto countDto : lineTypeAgg) {
                LineTypeStatisDto lineTypeStatis = convertLineTypeDto(countDto);
                lineTypeList.add(lineTypeStatis);
            }
        }
        return lineTypeList;
    }
    private LineTypeStatisDto convertLineTypeDto(JyBizTaskUnloadCountDto countDto) {
        LineTypeStatisDto lineTypeStatis = new LineTypeStatisDto();
        lineTypeStatis.setLineType(countDto.getLineType());
        lineTypeStatis.setLineTypeName(JyLineTypeEnum.getNameByCode(countDto.getLineType()));
        lineTypeStatis.setCount(countDto.getSum());
        return lineTypeStatis;
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
    public InvokeResult<ScanPackageRespDto> scan(ScanPackageDto scanPackageDto) {
        log.info("invoking jy scanAndComBoard,params: {}", JsonHelper.toJson(scanPackageDto));
        InvokeResult result = new InvokeResult();
        JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(scanPackageDto.getBizId());
        if (!ObjectHelper.isNotNull(unloadVehicleEntity)) {
            result.error(TASK_NO_FOUND_BY_PARAMS_MESSAGE);
            return result;
        }
        if (!checkScanCode(result,scanPackageDto)){
            return result;
        }
        //开始组板
        Integer comBoardCount =comBoard(scanPackageDto);
        if (comBoardCount>0){
            UnloadScanDto unloadScanDto = createUnloadDto(scanPackageDto, unloadVehicleEntity);
            unloadScanProducer.sendOnFailPersistent(unloadScanDto.getBarCode(), JsonHelper.toJson(unloadScanDto));
        }
        return result;
    }

    private Integer comBoard(ScanPackageDto scanPackageDto) {
        Integer comBoardCount=0;
        if (ObjectHelper.isNotNull(scanPackageDto.getBoardCode())) {
            comBoardCount =doComBoard(scanPackageDto.getBoardCode(),scanPackageDto);
        } else {
            String boardCode =generateBoard(scanPackageDto);
            if (ObjectHelper.isNotNull(boardCode)){
                comBoardCount=doComBoard(boardCode,scanPackageDto);
            }
        }
        return comBoardCount;
    }

    private boolean checkScanCode(InvokeResult<ScanPackageRespDto> resp, ScanPackageDto scanPackageDto) {
        if (checkBarCodeScannedAlready(scanPackageDto)){
            resp.hintMessage("单号已扫描！");
            return false;
        }
        String scanCode =scanPackageDto.getScanCode();
        if (WaybillUtil.isPackageCode(scanCode)){
            scanCode =WaybillUtil.getWaybillCode(scanCode);
        }
        Integer nextSiteCode=getWaybillNextRouter(scanCode,scanPackageDto.getCurrentOperate().getSiteCode());
        if (!ObjectHelper.isNotNull(nextSiteCode)){
            resp.hintMessage("未获取到该运单"+scanCode+"的下游站点信息,当前站点为"+scanPackageDto.getCurrentOperate().getSiteCode());
            return false;
        }
        String goodsAreaCode=getGoodsAreaCode(scanPackageDto.getCurrentOperate().getSiteCode(), nextSiteCode);
        if (!ObjectHelper.isNotNull(goodsAreaCode)){
            resp.hintMessage("未获取到该运单"+scanCode+"对应的货区编码信息");
            return false;
        }
        if (ObjectHelper.isNotNull(scanPackageDto.getGoodsAreaCode()) && !goodsAreaCode.equals(scanPackageDto.getGoodsAreaCode())){
            resp.hintMessage("扫描包裹非本货区，请移除本区！");
            return false;
        }
        scanPackageDto.setGoodsAreaCode(goodsAreaCode);
        scanPackageDto.setNextSiteCode(nextSiteCode);
        return true;
    }

    private boolean checkBarCodeScannedAlready(ScanPackageDto request) {
        String barCode = request.getScanCode();
        int siteCode = request.getCurrentOperate().getSiteCode();
        boolean alreadyScanned = false;
        String mutexKey=String.format(CacheKeyConstants.JY_UNLOAD_SCAN_KEY, barCode, siteCode, request.getBizId());
        if (redisClientOfJy.set(mutexKey, String.valueOf(System.currentTimeMillis()), 6, TimeUnit.HOURS, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(barCode, (long) siteCode, request.getBizId());
            if (jyUnloadDao.queryByCodeAndSite(queryDb) != null) {
                alreadyScanned = true;
            }
        }
        else {
            alreadyScanned = true;
        }
        return alreadyScanned;
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



    private Integer doComBoard(String boardCode,ScanPackageDto scanPackageDto) {
        int count =0;
        AddBoardBox addBoardBox = new AddBoardBox();
        addBoardBox.setBoardCode(boardCode);
        addBoardBox.setBoxCode(scanPackageDto.getScanCode());
        addBoardBox.setOperatorErp(scanPackageDto.getUser().getUserErp());
        addBoardBox.setOperatorName(scanPackageDto.getUser().getUserName());
        addBoardBox.setSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
        addBoardBox.setSiteName(scanPackageDto.getCurrentOperate().getSiteName());
        addBoardBox.setSiteType(1);
        addBoardBox.setBizSource(BizSourceEnum.PDA.getValue());

        Response<Integer> rs =groupBoardManager.addBoxToBoard(addBoardBox);
        if (ObjectHelper.isNotNull(rs) &&  ResponseEnum.SUCCESS.getIndex()==rs.getCode()) {
            BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
            boardCommonRequest.setBarCode(scanPackageDto.getScanCode());
            boardCommonRequest.setOperateSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
            boardCommonRequest.setOperateSiteName(scanPackageDto.getCurrentOperate().getSiteName());
            boardCommonRequest.setOperateUserCode(scanPackageDto.getUser().getUserCode());
            boardCommonRequest.setOperateUserName(scanPackageDto.getUser().getUserName());
            boardCommonManager.sendWaybillTrace(boardCommonRequest, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
            count= rs.getData();
        }
        return count;
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
        log.warn("jy卸车岗扫描创建新板异常{}",JsonHelper.toJson(createBoardResp));
        return null;
    }

    @Override
    public InvokeResult<ScanPackageRespDto> scanForPipelining(ScanPackageDto scanPackageDto) {
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

    @Override
    public Integer getWaybillNextRouter(String waybillCode, Integer startSiteId) {
        String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
        return getRouteNextSite(startSiteId, routerStr);
    }

    @Override
    public String getGoodsAreaCode(Integer currentSiteCode, Integer nextSiteCode) {
        if(currentSiteCode == null || nextSiteCode == null) {
            log.warn("jy转运卸车岗查询货区编码参数错误，currentSiteCode={}不可为空，nextSiteCode={}不可为空", currentSiteCode, nextSiteCode);
        }
        return jyUnloadVehicleManager.getGoodsAreaCode(currentSiteCode,nextSiteCode);
    }

    private Integer getRouteNextSite(Integer startSiteId, String routerStr) {
        if (StringUtils.isNotBlank(routerStr)) {
            String [] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLIT);
            for (int i = 0; i < routerNodes.length - 1; i++) {
                Integer curNode = Integer.valueOf(routerNodes[i]);
                Integer nextNode = Integer.valueOf(routerNodes[i + 1]);
                if (startSiteId.equals(curNode)) {
                    return nextNode;
                }
            }
        }
        return null;
    }

}
