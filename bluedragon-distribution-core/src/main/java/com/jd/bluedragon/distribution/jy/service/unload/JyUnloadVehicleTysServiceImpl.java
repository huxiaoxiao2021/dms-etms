package com.jd.bluedragon.distribution.jy.service.unload;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadVehicleBoardDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadVehicleBoardEntity;
import com.jd.bluedragon.distribution.ministore.enums.SiteTypeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.enums.BizSourceEnum;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JyUnloadTaskWaybillAgg;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.WAYBILL_ROUTER_SPLIT;
import static com.jd.bluedragon.core.base.BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum.*;


/**
 * @author weixiaofeng12
 * @date 2022-07-01
 * 转运卸车岗相关服务实现
 */
@Slf4j
@Service("jyUnloadVehicleTysService")
@UnifiedExceptionProcess
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
    IJyUnloadVehicleManager jyUnloadVehicleManager;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private JyUnloadDao jyUnloadDao;
    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;
    @Autowired
    JyUnloadVehicleBoardDao jyUnloadVehicleBoardDao;
    @Autowired
    BaseMajorManager baseMajorManager;

    private static final int SCAN_EXPIRE_TIME_HOUR = 6;


    @Override
    public InvokeResult<UnloadVehicleTaskRespDto> listUnloadVehicleTask(@Validated UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        if (ObjectHelper.isNotNull(unloadVehicleTaskReqDto.getPackageCode())) {
            return queryUnloadVehicleTaskByVehicleNumOrPackage(unloadVehicleTaskReqDto);
        }
        //查询状态统计数据(按状态分组聚合)
        JyBizTaskUnloadStatusEnum[] statusEnums = {WAIT_UN_LOAD, UN_LOADING, UN_LOAD_DONE};
        JyBizTaskUnloadVehicleEntity statusStatisticsQueryParams = assembleQueryStatusStatisticsCondition(unloadVehicleTaskReqDto);
        List<JyBizTaskUnloadCountDto> unloadCountDtos = jyBizTaskUnloadVehicleService.findStatusCountByCondition4Status(statusStatisticsQueryParams, null, statusEnums);
        if (!ObjectHelper.isNotNull(unloadCountDtos)) {
            return new InvokeResult(TASK_NO_FOUND_BY_STATUS_CODE, TASK_NO_FOUND_BY_STATUS_MESSAGE);
        }
        UnloadVehicleTaskRespDto respDto = new UnloadVehicleTaskRespDto();
        initCountToResp(respDto, unloadCountDtos);
        //查询卸车任务列表
        PageHelper.startPage(unloadVehicleTaskReqDto.getPageNo(), unloadVehicleTaskReqDto.getPageSize());
        JyBizTaskUnloadVehicleEntity unloadTaskQueryParams = assembleQueryTaskCondition(unloadVehicleTaskReqDto);
        List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = jyBizTaskUnloadVehicleService.listUnloadVehicleTask(unloadTaskQueryParams);
        respDto.setUnloadVehicleTaskDtoList(unloadVehicleTaskDtoList);

        statusStatisticsQueryParams.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        List<LineTypeStatisDto> lineTypeStatisDtoList = calculationLineTypeStatis(statusStatisticsQueryParams);
        respDto.setLineTypeStatisDtoList(lineTypeStatisDtoList);

        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, respDto);
    }

    private JyBizTaskUnloadVehicleEntity assembleQueryTaskCondition(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        condition.setEndSiteId(Long.valueOf(unloadVehicleTaskReqDto.getCurrentOperate().getSiteCode()));
        condition.setLineType(unloadVehicleTaskReqDto.getLineType());
        condition.setFuzzyVehicleNumber(unloadVehicleTaskReqDto.getVehicleNumber());
        return condition;
    }

    private JyBizTaskUnloadVehicleEntity assembleQueryStatusStatisticsCondition(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setLineType(unloadVehicleTaskReqDto.getLineType());
        condition.setEndSiteId(Long.valueOf(unloadVehicleTaskReqDto.getCurrentOperate().getSiteCode()));
        condition.setFuzzyVehicleNumber(unloadVehicleTaskReqDto.getVehicleNumber());
        return condition;
    }

    private List<LineTypeStatisDto> calculationLineTypeStatis(JyBizTaskUnloadVehicleEntity condition) {
        List<LineTypeStatisDto> lineTypeList = new ArrayList<>();
        JyBizTaskUnloadStatusEnum statusEnum = JyBizTaskUnloadStatusEnum.getEnumByCode(condition.getVehicleStatus());
        List<JyBizTaskUnloadCountDto> lineTypeAgg = jyBizTaskUnloadVehicleService.findStatusCountByCondition4StatusAndLine(condition, null, statusEnum);
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
            detail.setEndSiteId(queryUnloadTaskDto.getCurrentOperate().getSiteCode());
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
        UnloadVehicleTaskDto unloadVehicleTaskDto = jyBizTaskUnloadVehicleService.queryTaskDataByBizId(bizId);
        if (ObjectHelper.isNotNull(unloadVehicleTaskDto)) {
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadVehicleTaskDto);
        }
        return new InvokeResult(TASK_NO_FOUND_BY_PARAMS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
    }

    @Override
    public InvokeResult<ScanPackageRespDto> scan(ScanPackageDto scanPackageDto) {
        log.info("invoking jy scanAndComBoard,params: {}", JsonHelper.toJson(scanPackageDto));
        checkScan(scanPackageDto);

        JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(scanPackageDto.getBizId());
        if (!ObjectHelper.isNotNull(unloadVehicleEntity)) {
            return new InvokeResult<>(TASK_NO_FOUND_BY_PARAMS_CODE,TASK_NO_FOUND_BY_PARAMS_MESSAGE);
        }
        //开始组板
        Integer comBoardCount = comBoard(scanPackageDto);
        if (ObjectHelper.isNotNull(comBoardCount) && comBoardCount > 0) {
            UnloadScanDto unloadScanDto = createUnloadDto(scanPackageDto, unloadVehicleEntity);
            unloadScanProducer.sendOnFailPersistent(unloadScanDto.getBarCode(), JsonHelper.toJson(unloadScanDto));

            ScanPackageRespDto scanPackageRespDto = new ScanPackageRespDto();
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(scanPackageDto.getNextSiteCode());
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto)) {
                scanPackageRespDto.setEndSiteId(Long.valueOf(baseStaffSiteOrgDto.getSiteCode()));
                scanPackageRespDto.setEndSiteName(baseStaffSiteOrgDto.getSiteName());
            }
            scanPackageRespDto.setBizId(scanPackageDto.getBizId());
            scanPackageRespDto.setBoardCode(scanPackageDto.getBoardCode());
            scanPackageRespDto.setGoodsAreaCode(scanPackageDto.getGoodsAreaCode());
            scanPackageRespDto.setComBoardCount(comBoardCount);
            return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,scanPackageRespDto);
        }
        return new InvokeResult(UNLOAD_SCAN_EXCEPTION_CODE, UNLOAD_SCAN_EXCEPTION_MESSAGE);
    }

    private Integer comBoard(ScanPackageDto scanPackageDto) {
        Integer comBoardCount = 0;
        if (ObjectHelper.isNotNull(scanPackageDto.getBoardCode())) {
            comBoardCount = doComBoard(scanPackageDto.getBoardCode(), scanPackageDto);
        } else {
            String boardCode = generateBoard(scanPackageDto);
            scanPackageDto.setBoardCode(boardCode);
            if (ObjectHelper.isNotNull(boardCode)) {
                comBoardCount = doComBoard(boardCode, scanPackageDto);
            }
            JyUnloadVehicleBoardEntity entity = convertUnloadVehicleBoard(scanPackageDto);
            jyUnloadVehicleBoardDao.insertSelective(entity);
        }
        return comBoardCount;
    }

    private JyUnloadVehicleBoardEntity convertUnloadVehicleBoard(ScanPackageDto scanPackageDto) {
        Date now = new Date();
        JyUnloadVehicleBoardEntity entity = new JyUnloadVehicleBoardEntity();
        entity.setUnloadVehicleBizId(scanPackageDto.getBizId());
        entity.setBoardCode(scanPackageDto.getBoardCode());
        entity.setEndSiteId(Long.valueOf(scanPackageDto.getNextSiteCode()));
        entity.setGoodsAreaCode(scanPackageDto.getGoodsAreaCode());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setCreateUserErp(scanPackageDto.getUser().getUserErp());
        entity.setCreateUserName(scanPackageDto.getUser().getUserName());
        return entity;
    }

    private void checkScan(ScanPackageDto scanPackageDto) {
        if (checkBarCodeScannedAlready(scanPackageDto)) {
            throw new JyBizException("单号已扫描！");
        }
        String scanCode = scanPackageDto.getScanCode();
        if (WaybillUtil.isPackageCode(scanCode)) {
            scanCode = WaybillUtil.getWaybillCode(scanCode);
        }
        Integer nextSiteCode = getWaybillNextRouter(scanCode, scanPackageDto.getCurrentOperate().getSiteCode());
        if (!ObjectHelper.isNotNull(nextSiteCode)) {
            throw new JyBizException("未获取到该运单" + scanCode + "的下游站点信息,当前站点为" + scanPackageDto.getCurrentOperate().getSiteCode());
        }
        String goodsAreaCode = getGoodsAreaCode(scanPackageDto.getCurrentOperate().getSiteCode(), nextSiteCode);
        if (!ObjectHelper.isNotNull(goodsAreaCode)) {
            throw new JyBizException("未获取到该运单" + scanCode + "对应的货区编码信息");
        }
        if (ObjectHelper.isNotNull(scanPackageDto.getGoodsAreaCode()) && !goodsAreaCode.equals(scanPackageDto.getGoodsAreaCode())) {
            throw new JyBizException("扫描包裹非本货区，请移除本区！");
        }
        scanPackageDto.setGoodsAreaCode(goodsAreaCode);
        scanPackageDto.setNextSiteCode(nextSiteCode);
    }

    private boolean checkBarCodeScannedAlready(ScanPackageDto request) {
        String barCode = request.getScanCode();
        int siteCode = request.getCurrentOperate().getSiteCode();
        boolean alreadyScanned = false;
        String mutexKey = String.format(CacheKeyConstants.JY_UNLOAD_SCAN_KEY, barCode, siteCode, request.getBizId());
        if (redisClientOfJy.set(mutexKey, String.valueOf(System.currentTimeMillis()), SCAN_EXPIRE_TIME_HOUR, TimeUnit.HOURS, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(barCode, (long) siteCode, request.getBizId());
            if (jyUnloadDao.queryByCodeAndSite(queryDb) != null) {
                alreadyScanned = true;
            }
        } else {
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
        unloadScanDto.setTaskType(2);
        return unloadScanDto;
    }


    private Integer doComBoard(String boardCode, ScanPackageDto scanPackageDto) {
        Integer count = 0;
        AddBoardBox addBoardBox = new AddBoardBox();
        addBoardBox.setBoardCode(boardCode);
        addBoardBox.setBoxCode(scanPackageDto.getScanCode());
        addBoardBox.setOperatorErp(scanPackageDto.getUser().getUserErp());
        addBoardBox.setOperatorName(scanPackageDto.getUser().getUserName());
        addBoardBox.setSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
        addBoardBox.setSiteName(scanPackageDto.getCurrentOperate().getSiteName());
        addBoardBox.setSiteType(BOARD_COMBINATION_SITE_TYPE);
        addBoardBox.setBizSource(BizSourceEnum.PDA.getValue());
        String waybillCode =WaybillUtil.isPackageCode(scanPackageDto.getScanCode())?WaybillUtil.getWaybillCode(scanPackageDto.getScanCode()):scanPackageDto.getScanCode();
        addBoardBox.setWaybillCode(waybillCode);

        Response<Integer> rs = groupBoardManager.addBoxToBoard(addBoardBox);
        if (ObjectHelper.isNotNull(rs) && ResponseEnum.SUCCESS.getIndex() == rs.getCode()) {
            BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
            boardCommonRequest.setBarCode(scanPackageDto.getScanCode());
            boardCommonRequest.setOperateSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
            boardCommonRequest.setOperateSiteName(scanPackageDto.getCurrentOperate().getSiteName());
            boardCommonRequest.setOperateUserCode(scanPackageDto.getUser().getUserCode());
            boardCommonRequest.setOperateUserName(scanPackageDto.getUser().getUserName());
            boardCommonManager.sendWaybillTrace(boardCommonRequest, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
            count = rs.getData();
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
        log.warn("jy卸车岗扫描创建新板异常{}", JsonHelper.toJson(createBoardResp));
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
        JyUnloadAggsEntity entity = new JyUnloadAggsEntity();
        entity.setBizId(queryGoodsCategory.getBizId());
        entity.setBoardCode(queryGoodsCategory.getBoardCode());
        List<GoodsCategoryDto> goodsCategoryDtoList = jyUnloadAggsService.queryGoodsCategoryStatistics(entity);
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, goodsCategoryDtoList);
    }

    @Override
    public InvokeResult<ScanStatisticsInnerDto> queryUnloadDetailByDiffDimension(QueryUnloadDetailDto queryUnloadDetailDto) {
        //统计数据查询
        ScanStatisticsInnerDto scanStatisticsInnerDto = new ScanStatisticsInnerDto();
        JyUnloadAggsEntity condition = new JyUnloadAggsEntity();
        condition.setBizId(queryUnloadDetailDto.getBizId());

        if (!queryUnloadDetailDto.getExpFlag()) {
            List<GoodsCategoryDto> goodsCategoryDtoList = jyUnloadAggsService.queryGoodsCategoryStatistics(condition);
            scanStatisticsInnerDto.setGoodsCategoryDtoList(goodsCategoryDtoList);
        } else {
            List<ExcepScanDto> excepScanDtoList = jyUnloadAggsService.queryExcepScanStatistics(condition);
            scanStatisticsInnerDto.setExcepScanDtoList(excepScanDtoList);
        }
        //运单列表查询
        List<UnloadWaybillDto> unloadWaybillDtoList;
        Pager<JyVehicleTaskUnloadDetail> query = assembleQueryUnloadTaskWaybillCondition(queryUnloadDetailDto);
        Pager<JyUnloadTaskWaybillAgg> waybillAggPager = jyUnloadVehicleManager.queryUnloadTaskWaybill(query);
        if (ObjectHelper.isNotNull(waybillAggPager) && ObjectHelper.isNotNull(waybillAggPager.getData())) {
            unloadWaybillDtoList = BeanUtils.copy(waybillAggPager.getData(), UnloadWaybillDto.class);
            scanStatisticsInnerDto.setUnloadWaybillDtoList(unloadWaybillDtoList);
        }
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, scanStatisticsInnerDto);
    }

    private Pager<JyVehicleTaskUnloadDetail> assembleQueryUnloadTaskWaybillCondition(QueryUnloadDetailDto queryUnloadDetailDto) {
        JyVehicleTaskUnloadDetail condition = new JyVehicleTaskUnloadDetail();
        condition.setBizId(queryUnloadDetailDto.getBizId());
        if (queryUnloadDetailDto.getExpFlag()) {
            switch (queryUnloadDetailDto.getExpType()) {
                case 1:
                    condition.setScannedFlag(0);
                    break;
                case 2:
                    condition.setInterceptFlag(1);
                    break;
                case 3:
                    condition.setManualCreatedFlag(1);
                    break;
                default:
                    log.info("");
            }
        } else {
            condition.setProductType(queryUnloadDetailDto.getGoodsType());
        }
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(queryUnloadDetailDto.getPageNo());
        pager.setPageSize(queryUnloadDetailDto.getPageSize());
        pager.setSearchVo(condition);
        return pager;
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
        if (ObjectHelper.isNotNull(queryWaybillDto.getBoardCode())){
            //search DB
            List<PackageDto> packageDtos=groupBoardManager.getPackageCodeUnderComBoard(queryWaybillDto.getBoardCode(),queryWaybillDto.getWaybillCode());
            List<UnloadPackageDto> unloadPackageDtoList =BeanUtils.copy(packageDtos,UnloadPackageDto.class);
            return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,unloadPackageDtoList);
        }
        else {
            Pager<JyVehicleTaskUnloadDetail> condition = assembleScanQueryCondition(queryWaybillDto);
            Pager<JyVehicleTaskUnloadDetail> pagerResp = null;
            if (ObjectHelper.isNotNull(queryWaybillDto.getGoodsType())) {
                pagerResp = iJyUnloadVehicleManager.queryToScanBarCodeDetail(condition);
            } else if (UnloadBarCodeQueryEntranceEnum.TO_SCAN.getCode().equals(queryWaybillDto.getExpType())) {
                pagerResp = iJyUnloadVehicleManager.queryToScanBarCodeDetail(condition);
            } else if (UnloadBarCodeQueryEntranceEnum.MORE_SCAN.getCode().equals(queryWaybillDto.getExpType())) {
                pagerResp = iJyUnloadVehicleManager.queryMoreScanBarCodeDetail(condition);
            } else if (UnloadBarCodeQueryEntranceEnum.INTERCEPT.getCode().equals(queryWaybillDto.getExpType())) {
                pagerResp = iJyUnloadVehicleManager.queryInterceptBarCodeDetail(condition);
            }
            if (ObjectHelper.isNotNull(pagerResp.getData())){
                List<UnloadPackageDto>  unloadPackageDtoList =new ArrayList<>();
                List<JyVehicleTaskUnloadDetail> unloadDetailList =pagerResp.getData();
                for (JyVehicleTaskUnloadDetail unloadDetail:unloadDetailList){
                    UnloadPackageDto unloadPackageDto =new UnloadPackageDto();
                    unloadPackageDto.setPackageCode(unloadDetail.getPackageCode());
                    unloadPackageDtoList.add(unloadPackageDto);
                }
                return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,unloadPackageDtoList);
            }
        }
        return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
    }

    private Pager<JyVehicleTaskUnloadDetail> assembleScanQueryCondition(QueryWaybillDto queryWaybillDto) {
        JyVehicleTaskUnloadDetail condition = new JyVehicleTaskUnloadDetail();
        condition.setBizId(queryWaybillDto.getBizId());
        condition.setWaybillCode(queryWaybillDto.getWaybillCode());
        if (ObjectHelper.isNotNull(queryWaybillDto.getBoardCode())){
            condition.setBoardCode(queryWaybillDto.getBoardCode());
        }
        else {
            if (ObjectHelper.isNotNull(queryWaybillDto.getGoodsType())){
                condition.setProductType(queryWaybillDto.getGoodsType());
            }
            else {
                switch (queryWaybillDto.getExpType()) {
                    case 1:
                        condition.setScannedFlag(0);
                        break;
                    case 2:
                        condition.setInterceptFlag(1);
                        break;
                    case 3:
                        condition.setManualCreatedFlag(1);
                        break;
                    default:
                        log.info("");
                }
            }
        }
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(queryWaybillDto.getPageNo());
        pager.setPageSize(queryWaybillDto.getPageSize());
        pager.setSearchVo(condition);
        return pager;
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
        if (currentSiteCode == null || nextSiteCode == null) {
            log.warn("jy转运卸车岗查询货区编码参数错误，currentSiteCode={}不可为空，nextSiteCode={}不可为空", currentSiteCode, nextSiteCode);
        }
        return jyUnloadVehicleManager.getGoodsAreaCode(currentSiteCode, nextSiteCode);
    }

    private Integer getRouteNextSite(Integer startSiteId, String routerStr) {
        if (StringUtils.isNotBlank(routerStr)) {
            String[] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLIT);
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
