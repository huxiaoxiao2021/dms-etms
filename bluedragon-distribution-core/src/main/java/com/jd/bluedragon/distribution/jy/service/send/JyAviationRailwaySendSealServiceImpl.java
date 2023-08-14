package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.BookingTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyAviationRailwaySendVehicleStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
import com.jd.bluedragon.core.base.CarrierQueryWSManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.send.AviationNextSiteStatisticsDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.task.*;
import com.jd.bluedragon.distribution.jy.task.*;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.jd.bluedragon.Constants.LONG_ZERO;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:55
 * @Description
 */
public class JyAviationRailwaySendSealServiceImpl extends JySendVehicleServiceImpl implements JyAviationRailwaySendSealService{

    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwaySendSealServiceImpl.class);

    @Autowired
    private JyAviationRailwaySendSealCacheService jyAviationRailwaySendSealCacheService;
    @Autowired
    private JyBizTaskSendAviationPlanService jyBizTaskSendAviationPlanService;
    @Autowired
    private JyBizTaskBindService jyBizTaskBindService;
    @Autowired
    private JyBizTaskBindCacheService jyBizTaskBindCacheService;
    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private RouterService routerService;
    @Autowired
    private CarrierQueryWSManager carrierQueryWSManager;
    @Autowired
    private JySendAggsService jySendAggsService;

    @Override
    public InvokeResult<Void> sendTaskBinding(SendTaskBindReq request) {
        final String methodDesc = "JyAviationRailwaySendSealServiceImpl:sendTaskBinding:任务绑定：";
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        //并发锁（和封车同一把锁，避免封车期间绑定并发问题）
        if(!jyAviationRailwaySendSealCacheService.lockShuttleTaskSealCarBizId(request.getBizId())) {
            res.error("多人操作中，请重试");
            return res;
        }
        try{
            //校验被绑摆渡任务是否封车，已封车禁绑
            JyBizTaskSendVehicleEntity sendTaskInfo = jyBizTaskSendVehicleService.findByBizId(request.getBizId());
            if(Objects.isNull(sendTaskInfo)) {
                res.error("未找到不支持解绑.bizId:" + request.getBizId());
                return res;
            }
            if(JyBizTaskSendStatusEnum.SEALED.getCode().equals(sendTaskInfo.getVehicleStatus())) {
                res.error("不支持解绑已封车，不支持绑定");
                return res;
            }
            if(JyBizTaskSendStatusEnum.CANCEL.getCode().equals(sendTaskInfo.getVehicleStatus())) {
                res.error("不支持解绑已作废，不支持绑定");
                return res;
            }

            Map<String, SendTaskBindDto> needBindSendTaskMap = new HashMap<>();
            request.getSendTaskBindDtoList().forEach(sendTaskBindDto -> {
                if(StringUtils.isBlank(sendTaskBindDto.getBizId()) || StringUtils.isBlank(sendTaskBindDto.getDetailBizId())) {
                    throw new JyBizException("需绑任务集合中字段缺失");
                }
                needBindSendTaskMap.put(sendTaskBindDto.getDetailBizId(), sendTaskBindDto);
            });
            Set<String> needDetailBizIds = needBindSendTaskMap.keySet();
            List<String> needDetailBizIdList = needDetailBizIds.stream().collect(Collectors.toList());

            //校验需绑空铁任务中是否已经绑定其他摆渡车辆
            List<JyBizTaskBindEntity> existBindEntityList = jyBizTaskBindService.queryBindTaskByBindDetailBizIds(needDetailBizIdList, request.getType());
            if(CollectionUtils.isNotEmpty(existBindEntityList)) {
                //todo 体验优化点 批量操作时部分拦截如何提示更友好
                if(request.getBizId().equals(existBindEntityList.get(0).getBizId())) {
                    res.error(String.format("任务%s已经绑定当前车辆，请勿重复操作", existBindEntityList.get(0).getBindDetailBizId()));
                }else {
                    res.error(String.format("任务%s已经被其他摆渡车辆绑定", existBindEntityList.get(0).getBindDetailBizId()));
                }
                return res;
            }
            //校验需绑空铁任务是否封车，仅绑已封车
            List<JyBizTaskSendVehicleDetailEntity> entityList = jyBizTaskSendVehicleDetailService.findNoSealTaskByBizIds(needDetailBizIdList);
            if(CollectionUtils.isNotEmpty(entityList)) {
                res.error(String.format("绑定任务中存在未封车任务（bizId=%s）请先操作封车", entityList.get(0).getBizId()));
                return res;
            }
            //绑定
            this.taskBinding(request);
            return res;
        }catch (Exception e) {
            log.error("{}空铁绑定服务异常，request={],errMsg={}", methodDesc, JsonHelper.toJson(request), e.getMessage(), e);
            res.error("空铁绑定服务异常");
            return res;
        }finally {
            //释放锁
            jyAviationRailwaySendSealCacheService.unlockShuttleTaskSealCarBizId(request.getBizId());
        }
    }


    //绑定摆渡任务
    private void taskBinding(SendTaskBindReq request) {
        List<JyBizTaskBindEntity> bindEntityList = new ArrayList<>();
        request.getSendTaskBindDtoList().forEach(sendTaskBindDto -> {
            JyBizTaskBindEntity bindEntity = new JyBizTaskBindEntity();
            bindEntity.setBizId(request.getBizId());
            bindEntity.setBindBizId(sendTaskBindDto.getBizId());
            bindEntity.setBindDetailBizId(sendTaskBindDto.getDetailBizId());
            bindEntity.setOperateSiteCode(request.getCurrentOperate().getSiteCode());
            bindEntity.setType(request.getType());
            bindEntity.setCreateUserErp(request.getUser().getUserErp());
            bindEntity.setCreateUserName(request.getUser().getUserName());
            bindEntity.setCreateTime(new Date());
            bindEntityList.add(bindEntity);
        });
        jyBizTaskBindService.taskBinding(bindEntityList);
    }


    @Override
    public InvokeResult<Void> sendTaskUnbinding(SendTaskUnbindReq request) {
        final String methodDesc = "JyAviationRailwaySendSealServiceImpl:sendTaskUnbinding:任务解绑：";
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        //并发锁（和封车同一把锁，避免封车期间并发问题）
        if(!jyAviationRailwaySendSealCacheService.lockShuttleTaskSealCarBizId(request.getBizId())) {
            res.error("多人操作中，请重试");
            return res;
        }
        try{
            //校验被绑摆渡任务是否封车，已封车禁绑
            JyBizTaskSendVehicleEntity sendTaskInfo = jyBizTaskSendVehicleService.findByBizId(request.getBizId());
            if(Objects.isNull(sendTaskInfo)) {
                res.error("未找到摆渡任务.bizId:" + request.getBizId());
                return res;
            }
            if(JyBizTaskSendStatusEnum.SEALED.getCode().equals(sendTaskInfo.getVehicleStatus())) {
                res.error("摆渡任务已封车，不支持解绑");
                return res;
            }
            if(JyBizTaskSendStatusEnum.CANCEL.getCode().equals(sendTaskInfo.getVehicleStatus())) {
                res.error("摆渡任务已作废，不支持解绑");
                return res;
            }

            //解绑
            JyBizTaskBindEntity entity = new JyBizTaskBindEntity();
            entity.setBizId(request.getBizId());
            entity.setBindBizId(request.getUnbindBizId());
            entity.setBindDetailBizId(request.getUnbindDetailBizId());
            entity.setUpdateTime(new Date());
            entity.setCreateUserErp(request.getUser().getUserErp());
            entity.setUpdateUserName(request.getUser().getUserName());
            entity.setType(request.getType());
            jyBizTaskBindService.taskUnbinding(entity);

            return res;
        }catch (Exception e) {
            log.error("{}空铁解绑服务异常，request={],errMsg={}", methodDesc, JsonHelper.toJson(request), e.getMessage(), e);
            res.error("空铁解绑服务异常");
            return res;
        }finally {
            //释放锁
            jyAviationRailwaySendSealCacheService.unlockShuttleTaskSealCarBizId(request.getBizId());
        }
    }

    @Override
    public InvokeResult<FilterConditionQueryRes> pageFetchCurrentSiteStartAirport(FilterConditionQueryReq request) {

        InvokeResult<FilterConditionQueryRes> res = new InvokeResult<>();
        FilterConditionQueryRes resData = new FilterConditionQueryRes();
        res.setData(resData);
        //订舱类型统计
        List<JyBizTaskAviationAirTypeStatistics>  statisticsList = jyBizTaskSendAviationPlanService.airTypeStatistics(request.getCurrentOperate().getSiteCode());
        resData.setBookingTypeDtoList(this.convertAirType(statisticsList));

        //当前场地始发机场
        JyBizTaskSendAviationPlanQueryCondition condition = new JyBizTaskSendAviationPlanQueryCondition();
        condition.setStartSiteId(request.getCurrentOperate().getSiteCode());
        condition.setPageSize(request.getPageSize());
        condition.setOffset((request.getPageNo() - 1) * request.getPageSize());
        List<JyBizTaskSendAviationPlanEntity> entityList = jyBizTaskSendAviationPlanService.pageFindAirportInfoByCurrentSite(condition);
        if(CollectionUtils.isNotEmpty(entityList)) {
            List<AirportDataDto> airportDataDtoList = new ArrayList<>();
            entityList.forEach(entity -> {
                AirportDataDto dto = new AirportDataDto();
                dto.setAirportCode(entity.getBeginNodeCode());
                dto.setAirportName(entity.getBeginNodeName());
                airportDataDtoList.add(dto);
            });
            resData.setAirportDataDtoList(airportDataDtoList);
        }

        return res;
    }

    //机场类型转换
    private List<BookingTypeDto> convertAirType(List<JyBizTaskAviationAirTypeStatistics> statisticsList) {
        List<BookingTypeDto> res = new ArrayList<>();

        BookingTypeDto bulkAircraft = new BookingTypeDto();
        bulkAircraft.setCode(BookingTypeEnum.BULK_AIRCRAFT.getCode());
        bulkAircraft.setDesc(BookingTypeEnum.BULK_AIRCRAFT.getName());
        bulkAircraft.setCount(0);
        res.add(bulkAircraft);

        BookingTypeDto allCargo = new BookingTypeDto();
        allCargo.setCode(BookingTypeEnum.ALL_CARGO_AIRCRAFT.getCode());
        allCargo.setDesc(BookingTypeEnum.ALL_CARGO_AIRCRAFT.getName());
        allCargo.setCount(0);
        res.add(allCargo);

        if(CollectionUtils.isEmpty(statisticsList)) {
            return res;
        }

        Map<Integer,Integer> map = statisticsList.stream().collect(Collectors.toMap(
                k -> k.getAirType(),
                v -> v.getTotal()
        ));

        res.forEach(obj ->{
            if(NumberHelper.gt0(map.get(obj.getCode()))) {
                obj.setCount(map.get(obj.getCode()));
            }
        });

        return res;
    }


    @Override
    public InvokeResult<TransportInfoQueryRes> fetchTransportCodeList(TransportCodeQueryReq request) {
        //todo zcf
        return null;
    }

    @Override
    public InvokeResult<TransportDataDto> scanAndCheckTransportInfo(ScanAndCheckTransportInfoReq request) {
        //todo zcf


        CommonDto<TransportResourceDto> dto = carrierQueryWSManager.getTransportResourceByTransCode(request.getTransportCode());


        return null;
    }

    @Override
    public InvokeResult<ShuttleTaskSealCarQueryRes> fetchShuttleTaskSealCarInfo(ShuttleTaskSealCarQueryReq request) {
        //todo zcf
        return null;
    }

    @Override
    public InvokeResult<Void> shuttleTaskSealCar(ShuttleTaskSealCarReq request) {
        //todo zcf
        return null;
    }

    @Override
    public InvokeResult<Void> aviationTaskSealCar(AviationTaskSealCarReq request) {
        //todo zcf
        return null;
    }

    @Override
    public InvokeResult<AviationToSendAndSendingListRes> fetchAviationToSendAndSendingList(AviationSendTaskListReq request) {
        InvokeResult<AviationToSendAndSendingListRes> res = new InvokeResult<>();
        AviationToSendAndSendingListRes resData = new AviationToSendAndSendingListRes();
        res.setData(resData);

        resData.setTaskStatus(request.getStatusCode());
        resData.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.getNameByCode(request.getStatusCode()));


        JyBizTaskSendAviationPlanQueryCondition condition = new JyBizTaskSendAviationPlanQueryCondition();
        condition.setStartSiteId(request.getCurrentOperate().getSiteCode());
        condition.setTaskStatus(request.getStatusCode());
        if(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode().equals(request.getStatusCode())) {
            //起飞时间
            condition.setTakeOffTime(DateHelper.newTimeRangeHoursAgo(new Date(), 24));
        }
        //条件筛选
        this.parseQueryCondition(request.getFilterConditionDto(), condition);
        //关键词搜索
        this.parseKeyword(request.getKeyword(), request.getCurrentOperate().getSiteCode(),  condition);


        List<JyBizTaskAviationStatusStatistics> taskStatusStatisticsList = jyBizTaskSendAviationPlanService.statusStatistics(condition);
        List<TaskStatusStatistics> taskStatusStatistics = this.convertFillStatusDefaultValue(taskStatusStatisticsList);
        resData.setTaskStatusStatisticsList(taskStatusStatistics);

        for (TaskStatusStatistics tss : taskStatusStatistics) {
            if(tss.getTaskStatus().equals(request.getStatusCode()) && NumberHelper.gt0(tss.getTotal())) {
                //当前状态统计>0 查具体流向
                List<AviationNextSiteStatisticsDto> nextSiteListList = jyBizTaskSendAviationPlanService.queryNextSitesByStartSite(condition);
                if(CollectionUtils.isNotEmpty(nextSiteListList)) {
                    List<AviationSendTaskNextSiteList> aviationSendTaskNextSiteLists = new ArrayList<>();
                    nextSiteListList.forEach(dto -> {
                        AviationSendTaskNextSiteList obj = new AviationSendTaskNextSiteList();
                        obj.setNextSiteId(dto.getNextSiteId());
                        obj.setNextSiteName(dto.getNextSiteName());
                        obj.setTotalNum(dto.getTotalNum());
                        aviationSendTaskNextSiteLists.add(obj);
                    });
                    resData.setNextSiteListList(aviationSendTaskNextSiteLists);
                }else {
                    if(log.isInfoEnabled()) {
                        log.info("航空发货计划查询发货流向数据为空，request={},queryCondition={}", JsonHelper.toJson(request), JsonHelper.toJson(condition));
                    }
                }
            }
        }

        return res;
    }

    //状态统计默认值处理
    private  List<TaskStatusStatistics> convertFillStatusDefaultValue(List<JyBizTaskAviationStatusStatistics> dbQuery) {

        List<TaskStatusStatistics> statusAggList = new ArrayList<>();
        TaskStatusStatistics toSend = new TaskStatusStatistics();
        toSend.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode());
        toSend.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getName());
        toSend.setTotal(0);
        statusAggList.add(toSend);

        TaskStatusStatistics sending = new TaskStatusStatistics();
        toSend.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.SENDING.getCode());
        toSend.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.SENDING.getName());
        toSend.setTotal(0);
        statusAggList.add(sending);

        Map<Integer, Integer> map = dbQuery.stream().collect(Collectors.toMap(
                k -> k.getTaskStatus(),
                v -> v.getTotal()
        ));
        for (TaskStatusStatistics statistics : statusAggList) {
            if(!Objects.isNull(map.get(statistics.getTaskStatus()))) {
                statistics.setTotal(map.get(statistics.getTaskStatus()));
            }
        }
        return statusAggList;
    }



    //列表关键词转换
    private void parseKeyword(String keyword, Integer currentSiteId, JyBizTaskSendAviationPlanQueryCondition entity) {
        if(StringUtils.isBlank(keyword) || Objects.isNull(entity) || Objects.isNull(currentSiteId)) {
            return;
        }
        //扫描包裹号或者单号，查路由流向
        if(WaybillUtil.isPackageCode(keyword) || WaybillUtil.isWaybillCode(keyword)) {

            RouteNextDto routeNextDto = routerService.matchRouterNextNode(currentSiteId, WaybillUtil.getWaybillCode(keyword));
            if(Objects.isNull(routeNextDto) || Objects.isNull(routeNextDto.getFirstNextSiteId())) {
                throw new JyBizException(String.format("没有找到{}的流向场地", keyword));
            }
            // 入参有则取入参的，没有则取路由
            entity.setNextSiteId(Integer.valueOf(keyword));
        }
        //航班号(拼音开头)
        else if(Character.isLetter(keyword.charAt(0))) {
            entity.setFlightNumber(keyword);
        }
        //默认流向
        else {
            entity.setNextSiteId(Integer.valueOf(keyword));
        }

    }

    //列表过滤条件转换
    private void parseQueryCondition(FilterConditionDto filterConditionDto, JyBizTaskSendAviationPlanQueryCondition entity) {
        if(Objects.isNull(filterConditionDto) || Objects.isNull(entity)) {
            return;
        }
        //航空类型
        if(Objects.isNull(filterConditionDto.getBookingType())) {
            entity.setAirType(filterConditionDto.getBookingType());
        }
        //始发机场
        if(Objects.isNull(filterConditionDto.getAirportCode())) {
            entity.setBeginNodeCode(filterConditionDto.getAirportCode());
        }
    }

    @Override
    public InvokeResult<AviationSendTaskQueryRes> pageFetchAviationTaskByNextSite(AviationSendTaskQueryReq request) {
        InvokeResult<AviationSendTaskQueryRes> res = new InvokeResult<>();

        JyBizTaskSendAviationPlanQueryCondition condition = new JyBizTaskSendAviationPlanQueryCondition();
        condition.setStartSiteId(request.getCurrentOperate().getSiteCode());
        condition.setNextSiteId(request.getNextSiteId());
        condition.setTaskStatus(request.getStatusCode());
        condition.setOffset((request.getPageNo() - 1) * request.getPageSize());
        condition.setPageSize(request.getPageSize());
        List<JyBizTaskSendAviationPlanEntity> taskDtoList = jyBizTaskSendAviationPlanService.pageFetchAviationTaskByNextSite(condition);

        List<AviationSendTaskDto> sendTaskDtoList = this.convertAviationSendTaskDtoList(taskDtoList, request.getStatusCode());
        if(CollectionUtils.isEmpty(sendTaskDtoList)) {
            res.setMessage("查询为空");
            return res;
        }

        AviationSendTaskQueryRes resData = new AviationSendTaskQueryRes();
        res.setData(resData);
        resData.setAviationSendTaskDtoList(sendTaskDtoList);
        return res;

    }
    //航空任务结果转换
    private List<AviationSendTaskDto> convertAviationSendTaskDtoList(List<JyBizTaskSendAviationPlanEntity> taskDtoList, Integer statusCode) {
        if(CollectionUtils.isEmpty(taskDtoList)) {
            return null;
        }
        List<String> bizIdList = new ArrayList<>();
        List<AviationSendTaskDto> res = new ArrayList<>();
        taskDtoList.forEach(dbQueryDto -> {
            bizIdList.add(dbQueryDto.getBizId());

            AviationSendTaskDto taskDto = new AviationSendTaskDto();
            taskDto.setBizId(dbQueryDto.getBizId());
            taskDto.setBookingCode(dbQueryDto.getBookingCode());
            taskDto.setFlightNumber(dbQueryDto.getFlightNumber());
            taskDto.setTakeOffTime(Objects.isNull(dbQueryDto.getTakeOffTime()) ? null : dbQueryDto.getTakeOffTime().getTime());
            taskDto.setAirCompanyCode(dbQueryDto.getAirCompanyCode());
            taskDto.setAirCompanyName(dbQueryDto.getAirCompanyName());
            taskDto.setBeginNodeCode(dbQueryDto.getBeginNodeCode());
            taskDto.setBeginNodeName(dbQueryDto.getBeginNodeName());
            taskDto.setCarrierCode(dbQueryDto.getCarrierCode());
            taskDto.setCarrierName(dbQueryDto.getCarrierName());
            taskDto.setBookingWeight(dbQueryDto.getBookingWeight());
            taskDto.setCargoType(dbQueryDto.getCargoType());
            taskDto.setAirType(dbQueryDto.getAirType());
            taskDto.setNextSiteId(dbQueryDto.getNextSiteId());
            taskDto.setNextSiteName(dbQueryDto.getNextSiteName());
            res.add(taskDto);
        });
        //发货中状态补充已扫重量
        if(JyAviationRailwaySendVehicleStatusEnum.SENDING.getCode().equals(statusCode)) {
            List<JySendAggsEntity> aggs = jySendAggsService.getSendStatisticsByBizList(bizIdList);
            Map<String, BigDecimal> map = aggs.stream().collect(Collectors.toMap(k -> k.getSendVehicleBizId(), v-> v.getTotalScannedWeight()));
            res.forEach(obj -> {
                BigDecimal totalScannedWeight = map.get(obj.getBizId());
                obj.setScanWeight(Objects.isNull(totalScannedWeight) ? 0d : totalScannedWeight.doubleValue());
            });
        }
        return res;
    }

    @Override
    public InvokeResult<AviationToSealAndSealedListRes> fetchAviationToSealAndSealedList(AviationSendTaskSealListReq request) {
        //todo zcf
        return null;
    }


}
