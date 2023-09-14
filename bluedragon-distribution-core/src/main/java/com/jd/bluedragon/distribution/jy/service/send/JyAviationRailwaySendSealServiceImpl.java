package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarSourceEnum;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
import com.jd.bluedragon.common.dto.operation.workbench.seal.SealCarSendCodeResp;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;
import com.jd.bluedragon.common.dto.seal.request.CheckTransportReq;
import com.jd.bluedragon.common.dto.seal.request.SealVehicleReq;
import com.jd.bluedragon.common.dto.seal.request.ValidSendCodeReq;
import com.jd.bluedragon.common.dto.seal.response.TransportResp;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CarrierQueryWSManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.constants.TaskBindTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.send.AviationNextSiteStatisticsDto;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizSendTaskAssociationDto;
import com.jd.bluedragon.distribution.jy.dto.send.QueryTaskSendDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.exception.JyDemotionException;
import com.jd.bluedragon.distribution.jy.manager.IJySendVehicleJsfManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.seal.JySeaCarlCacheService;
import com.jd.bluedragon.distribution.jy.service.seal.JySealVehicleService;
import com.jd.bluedragon.distribution.jy.service.seal.JySendSealCodeService;
import com.jd.bluedragon.distribution.jy.service.summary.JyStatisticsSummaryService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskBindService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendAviationPlanService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.summary.BusinessKeyTypeEnum;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryCondition;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryEntity;
import com.jd.bluedragon.distribution.jy.summary.SummarySourceEnum;
import com.jd.bluedragon.distribution.jy.task.*;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.api.resource.req.AirlineReq;
import com.jd.etms.api.resource.resp.AirLineResp;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.dms.common.constants.CodeConstants;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.job.SendBoxAgg;
import com.jdl.jy.realtime.model.es.job.SendPackageEsDto;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendTaskQueryEnum.TASK_RECOMMEND;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 14:55
 * @Description
 */
@Service
public class JyAviationRailwaySendSealServiceImpl extends JySendVehicleServiceImpl implements JyAviationRailwaySendSealService{

    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwaySendSealServiceImpl.class);

    public static final Integer BIND_TASK_NUM = 100;


    @Value("${jyAviationRailwayShuttleSendTaskPlanTimeBeginHour:2}")
    private int shuttlePlanTimeBeginHour;
    @Value("${jyAviationRailwayShuttleSendTaskPlanTimeEndHour:24}")
    private int shuttlePlanTimeEndHour;
    @Value("${jyAviationSendSealListQueryCreateTimeStartDay:7}")
    private int listQueryCreateTimeStartDay;
    @Value("${jyAviationSendSealToSendQueryTakeOffTimeStartHour:24}")
    private int toSendQueryTakeOffTimeStartHour;

    @Autowired
    private JySeaCarlCacheService jySeaCarlCacheService;
    @Autowired
    private JyBizTaskSendAviationPlanService jyBizTaskSendAviationPlanService;
    @Autowired
    private JyBizTaskBindService jyBizTaskBindService;
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
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private JyVehicleSendRelationService jyVehicleSendRelationService;
    @Autowired
    private NewSealVehicleService newSealVehicleService;
    @Autowired
    @Qualifier("sendVehicleTransactionManager")
    private SendVehicleTransactionManager sendVehicleTransactionManager;
    @Autowired
    private JyStatisticsSummaryService statisticsSummaryService;
    @Autowired
    private JySendSealCodeService jySendSealCodeService;
    @Autowired
    private IJySendVehicleJsfManager sendVehicleJsfManager;
    @Autowired
    private SortingService sortingService;
    @Autowired
    private UccPropertyConfiguration uccConfig;
    @Autowired
    private JySealVehicleService jySealVehicleService;
    @Autowired
    private JdiQueryWSManager jdiQueryWSManager;
    @Autowired
    private SendDatailDao sendDatailDao;
    @Autowired
    private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;

    @Override
    public InvokeResult<Void> sendTaskBinding(SendTaskBindReq request) {
        final String methodDesc = "JyAviationRailwaySendSealServiceImpl:sendTaskBinding:任务绑定：";
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        //并发锁（和封车同一把锁，避免封车期间绑定并发问题）
        if(!jySeaCarlCacheService.lockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode())) {
            res.error("多人操作中，请稍后重试");
            return res;
        }
        try{
            //校验被绑摆渡任务是否封车，已封车禁绑
            JyBizTaskSendVehicleDetailEntity sendTaskInfo = jyBizTaskSendVehicleDetailService.findByBizId(request.getDetailBizId());
            if(Objects.isNull(sendTaskInfo)) {
                res.error("未找到派车任务.bizId:" + request.getBizId());
                return res;
            }
            if(JyBizTaskSendStatusEnum.SEALED.getCode().equals(sendTaskInfo.getVehicleStatus())) {
                res.error("已封车不支持绑定");
                return res;
            }
            if(JyBizTaskSendStatusEnum.CANCEL.getCode().equals(sendTaskInfo.getVehicleStatus())) {
                res.error("已作废不支持绑定");
                return res;
            }
            Integer count = jyBizTaskBindService.countBind(request.getDetailBizId());
            if(NumberHelper.gt((count + request.getSendTaskBindDtoList().size()), JyAviationRailwaySendSealServiceImpl.BIND_TASK_NUM)) {
                String msg = String.format("该车已绑定任务%s,本次绑定%s,超出最大绑定数量%s", count, request.getSendTaskBindDtoList().size(), JyAviationRailwaySendSealServiceImpl.BIND_TASK_NUM);
                res.error(msg);
                return res;
            }
            //
            Map<String, SendTaskBindDto> needBindSendTaskMap = new HashMap<>();
            request.getSendTaskBindDtoList().forEach(sendTaskBindDto -> {
                if(StringUtils.isBlank(sendTaskBindDto.getBizId()) || StringUtils.isBlank(sendTaskBindDto.getDetailBizId()) || StringUtils.isEmpty(sendTaskBindDto.getFlightNumber())) {
                    throw new JyBizException("需绑航空任务集合中字段缺失");
                }
                needBindSendTaskMap.put(sendTaskBindDto.getDetailBizId(), sendTaskBindDto);
            });
            List<String> needDetailBizIdList = needBindSendTaskMap.keySet().stream().collect(Collectors.toList());

            //校验需绑空铁任务中是否已经绑定其他摆渡车辆
            List<JyBizTaskBindEntity> existBindEntityList = jyBizTaskBindService.queryBindTaskByBindDetailBizIds(needDetailBizIdList, request.getType());
            if(CollectionUtils.isNotEmpty(existBindEntityList)) {
                //todo 体验优化点 批量操作时部分拦截如何提示更友好
                String flightNumber = needBindSendTaskMap.get(existBindEntityList.get(0).getBindDetailBizId()).getFlightNumber();
                if(request.getDetailBizId().equals(existBindEntityList.get(0).getDetailBizId())) {
                    res.error(String.format("任务%s已经绑定当前车辆，请勿重复操作", flightNumber));
                }else {
                    res.error(String.format("任务%s已经被其他摆渡车辆绑定", flightNumber));
                }
                return res;
            }
            //校验需绑空铁任务是否封车，仅绑已封车
            List<JyBizTaskSendAviationPlanEntity> entityList = jyBizTaskSendAviationPlanService.findNoSealTaskByBizIds(needDetailBizIdList);
            if(CollectionUtils.isNotEmpty(entityList)) {
                res.error(String.format("绑定任务中存在未封车任务%s,请先操作封车", entityList.get(0).getFlightNumber()));
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
            jySeaCarlCacheService.unlockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode());
        }
    }


    //绑定摆渡任务
    private void taskBinding(SendTaskBindReq request) {
        List<JyBizTaskBindEntity> bindEntityList = new ArrayList<>();
        request.getSendTaskBindDtoList().forEach(sendTaskBindDto -> {
            JyBizTaskBindEntity bindEntity = new JyBizTaskBindEntity();
            bindEntity.setBizId(request.getBizId());
            bindEntity.setDetailBizId(request.getDetailBizId());
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
        if(!jySeaCarlCacheService.lockSendTaskSeal(request.getDetailBizId(), SendTaskTypeEnum.VEHICLE.getCode())) {
            res.error("多人操作中，请稍后重试");
            return res;
        }
        try{
            //校验被绑摆渡任务是否封车，已封车禁绑
            JyBizTaskSendVehicleDetailEntity sendTaskInfo = jyBizTaskSendVehicleDetailService.findByBizId(request.getDetailBizId());
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
            entity.setDetailBizId(request.getDetailBizId());
            entity.setBindBizId(request.getUnbindBizId());
            entity.setBindDetailBizId(request.getUnbindDetailBizId());
            entity.setUpdateTime(new Date());
            entity.setUpdateUserErp(request.getUser().getUserErp());
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
            jySeaCarlCacheService.unlockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode());
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
        condition.setPageSize(100);
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
        InvokeResult<TransportInfoQueryRes> res = new InvokeResult<>();

        JyBizTaskSendAviationPlanEntity entity = jyBizTaskSendAviationPlanService.findByBizId(request.getBizId());
        AirlineReq airlineReq = new AirlineReq();
        if(!Objects.isNull(request.getAirTransportType())) {
            airlineReq.setAirTransportType(request.getAirTransportType());
        }else {
            if(AirTypeEnum.AIR_TYPE_BULK.getCode().equals(entity.getAirType())) {
                airlineReq.setAirTransportType(2);
            }else {
                airlineReq.setAirTransportType(1);
            }
        }
        airlineReq.setEndAirportCode(entity.getEndNodeCode());
        airlineReq.setStartAirportCode(entity.getBeginNodeCode());
        airlineReq.setValidDate(new Date());
        InvokeResult<List<AirLineResp>> invokeResult = vrsRouteTransferRelationManager.queryAirLineByAirLineReq(airlineReq);

        if(!invokeResult.codeSuccess()) {
            res.error(invokeResult.getMessage());
            return res;
        }
        TransportInfoQueryRes resData = new TransportInfoQueryRes();
        res.setData(resData);

        List<TransportDataDto> transportDataDtoList = new ArrayList<>();
        for(AirLineResp airLineResp : invokeResult.getData()){
            TransportDataDto transportDataDto = new TransportDataDto();
            if(StringUtils.isBlank(airLineResp.getLineCode())) {
                continue;
            }
            transportDataDto.setTransportCode(airLineResp.getLineCode());
            String departTime = "";//todo zcf 离场时间字段还没有
            transportDataDto.setDepartureTimeStr(departTime);
            transportDataDto.setFocusFlag(false);
            transportDataDtoList.add(transportDataDto);
        };

        String focusLineCode = this.getFocusLineCode(transportDataDtoList);

        if(StringUtils.isNotBlank(focusLineCode)) {
            for(TransportDataDto data : transportDataDtoList) {
                if(focusLineCode.equals(data.getTransportCode())) {
                    data.setFocusFlag(true);
                }
                break;
            }
        }
        return res;
    }

    private String getFocusLineCode(List<TransportDataDto> transportDataDtoList) {
        if(CollectionUtils.isEmpty(transportDataDtoList)) {
            return null;
        }
        Long curTime = System.currentTimeMillis();
        String transportCode = null;
        Long focusTransportCodeTime = null;

        for(TransportDataDto data : transportDataDtoList){
            if(StringUtils.isBlank(data.getDepartureTimeStr())) {
                continue;
            }
            String[] todayTimeStr = data.getDepartureTimeStr().split(Constants.SEPARATOR_COLON);
            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(todayTimeStr[0]));
            todayCalendar.set(Calendar.MINUTE, Integer.valueOf(todayTimeStr[1]));
            if(todayCalendar.getTime().getTime() > curTime) {
                if(focusTransportCodeTime == null) {
                    focusTransportCodeTime = todayCalendar.getTime().getTime();
                    transportCode = data.getTransportCode();
                }else if(todayCalendar.getTime().getTime() < focusTransportCodeTime){
                    focusTransportCodeTime = todayCalendar.getTime().getTime();
                    transportCode = data.getTransportCode();
                }
            } else {
                String[] tomorrowTimeStr = data.getDepartureTimeStr().split(Constants.SEPARATOR_COLON);
                Calendar tomorrowCalendar = Calendar.getInstance();
                tomorrowCalendar.add(Calendar.DAY_OF_YEAR, 1);
                tomorrowCalendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(tomorrowTimeStr[0]));
                tomorrowCalendar.set(Calendar.MINUTE, Integer.valueOf(tomorrowTimeStr[1]));
                Long tomorrowStamp = tomorrowCalendar.getTime().getTime();
                if(focusTransportCodeTime == null) {
                    focusTransportCodeTime = tomorrowStamp;
                    transportCode = data.getTransportCode();
                }else if(tomorrowStamp < focusTransportCodeTime){
                    focusTransportCodeTime = tomorrowStamp;
                    transportCode = data.getTransportCode();
                }
            }
        };

        return transportCode;
    }


    @Override
    public InvokeResult<TransportDataDto> scanAndCheckTransportInfo(ScanAndCheckTransportInfoReq request) {
        InvokeResult<TransportDataDto> res = new InvokeResult<>();
        TransportDataDto resData = new TransportDataDto();
        res.setData(resData);

        CheckTransportReq param = new CheckTransportReq();
        param.setUser(request.getUser());
        param.setCurrentOperate(request.getCurrentOperate());
        param.setTransportCode(request.getTransportCode());
        param.setEndSiteId(request.getNextSiteId());

        InvokeResult<TransportResp> transportResp = jySealVehicleService.checkTransCode(param);
        if(!transportResp.codeSuccess()) {
            res.setCode(transportResp.getCode());
            res.setMessage(transportResp.getMessage());
            return res;
        }
        TransportResp transportInfo = transportResp.getData();
        if(Objects.isNull(transportResp.getData())) {
            res.error("未查到运力信息");
            return res;
        }

        resData.setTransWay(transportInfo.getTransWay());
        resData.setTransWayName(transportInfo.getTransWayName());
        resData.setTransType(transportInfo.getTransType());
        resData.setTransTypeName(transportInfo.getTransTypeName());
        resData.setTransportCode(request.getTransportCode());
        resData.setDepartureTimeStr(transportInfo.getSendCarHourMin());
        return res;

    }

    @Override
    public InvokeResult<ShuttleTaskSealCarQueryRes> fetchShuttleTaskSealCarInfo(ShuttleTaskSealCarQueryReq request) {
        InvokeResult<ShuttleTaskSealCarQueryRes> res = new InvokeResult<>();
        ShuttleTaskSealCarQueryRes resData = new ShuttleTaskSealCarQueryRes();
        res.setData(resData);

        JyStatisticsSummaryCondition summaryCondition = new JyStatisticsSummaryCondition();
        summaryCondition.setBusinessKeyType(BusinessKeyTypeEnum.JY_SEND_TASK_DETAIL.getCode());
        summaryCondition.setSource(SummarySourceEnum.SEAL.getCode());
        summaryCondition.setBusinessKey(request.getDetailBizId());
        summaryCondition.setOperateSiteCode(request.getCurrentOperate().getSiteCode());
        JyStatisticsSummaryEntity entity = statisticsSummaryService.queryByBusinessKeyAndType(summaryCondition);

        if(Objects.isNull(entity)) {
            res.setMessage("查询为空");
            return res;
        }

        resData.setWeight(entity.getWeight());
        resData.setVolume(entity.getVolume());
        resData.setItemNum(entity.getItemNum());
        resData.setPalletCount(entity.getPalletCount());
        resData.setTransportCode(entity.getTransportCode());
        resData.setDepartureTimeStr(entity.getDepartTime());
        resData.setTaskNum(entity.getSealBindAviationTaskNum());

        resData.setSealCodes(jySendSealCodeService.selectSealCodeByBizId(request.getDetailBizId()));
        return res;
    }


    @Override
    public InvokeResult<Void> aviationTaskSealCar(AviationTaskSealCarReq request) {
        InvokeResult<Void> res = new InvokeResult<>();
        if(!jySeaCarlCacheService.lockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.AVIATION.getCode())) {
            res.error("多人操作中，请稍后重试");
            return res;
        }
        try{
            if(jyBizTaskSendAviationPlanService.aviationPlanIntercept(request.getBizId())) {
                res.error("该航空任务已经取消，无法进行后续操作，请迁移任务操作数据");
                return res;
            }
            JyBizTaskSendAviationPlanEntity entity = jyBizTaskSendAviationPlanService.findByBizId(request.getBizId());
            if (ObjectHelper.isNotNull(entity) && JyBizTaskSendStatusEnum.SEALED.getCode().equals(entity.getTaskStatus())){
                throw new JyBizException("该航空任务已封车！");
            }

            SealVehicleReq sealVehicleReq = new SealVehicleReq();
            sealVehicleReq.setSendVehicleBizId(request.getBizId());
            sealVehicleReq.setSendVehicleDetailBizId(request.getBookingCode());
            sealVehicleReq.setTransportCode(request.getTransportCode());
            sealVehicleReq.setWeight(request.getWeight());
            sealVehicleReq.setVolume(request.getVolume());
            sealVehicleReq.setItemNum(request.getItemNum());
            sealVehicleReq.setSealCarType(SealCarTypeEnum.SEAL_BY_TRANSPORT_CAPABILITY.getType());
            sealVehicleReq.setUser(request.getUser());
            sealVehicleReq.setCurrentOperate(request.getCurrentOperate());
            sealVehicleReq.setDepartureTimeStr(request.getDepartureTimeStr());
            sealVehicleReq.setFuncType(request.getPost());
            //选填字段
            sealVehicleReq.setTransWay(request.getTransWay());
            sealVehicleReq.setTransWayName(request.getTransWayName());
            return jySealVehicleService.sealVehicle(sealVehicleReq);

        }catch (Exception e) {
            log.error("航空任务发货提交封车异常：request={},errMsg={}", JsonHelper.toJson(request), e.getMessage(), e);
            throw new JyBizException("航空任务发货提交封车异常");
        }finally {
            jySeaCarlCacheService.unlockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.AVIATION.getCode());
        }
    }

    @Override
    public InvokeResult<AviationToSendAndSendingListRes> fetchAviationToSendAndSendingList(AviationSendTaskListReq request) {
        InvokeResult<AviationToSendAndSendingListRes> res = new InvokeResult<>();
        AviationToSendAndSendingListRes resData = new AviationToSendAndSendingListRes();
        res.setData(resData);

        resData.setTaskStatus(request.getStatusCode());
        resData.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.getNameByCode(request.getStatusCode()));

        JyBizTaskSendAviationPlanQueryCondition condition = this.convertListQueryCondition(
                request.getCurrentOperate().getSiteCode(),
                request.getStatusCode(),
                request.getFilterConditionDto(),
                request.getKeyword());

        List<JyBizTaskAviationStatusStatistics> taskStatusStatisticsList = jyBizTaskSendAviationPlanService.toSendAndSendingStatusStatistics(condition);
        List<TaskStatusStatistics> taskStatusStatistics = this.convertFillStatusDefaultValue(taskStatusStatisticsList, true);
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
                break;
            }
        }

        return res;
    }


    //列表查询条件转换
    private JyBizTaskSendAviationPlanQueryCondition convertListQueryCondition(Integer siteCode, Integer statusCode, FilterConditionDto filterConditionDto, String keyword) {
        JyBizTaskSendAviationPlanQueryCondition condition = new JyBizTaskSendAviationPlanQueryCondition();
        condition.setStartSiteId(siteCode);
        if(!Objects.isNull(statusCode)) {
            condition.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.getSendTaskStatusByCode(statusCode));
        }
        if(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode().equals(statusCode)) {
            //起飞时间
            condition.setTakeOffTimeStart(DateHelper.newTimeRangeHoursAgo(new Date(), toSendQueryTakeOffTimeStartHour));
        }
        condition.setCreateTimeStart(DateHelper.getZeroFromDay(new Date(), listQueryCreateTimeStartDay));
        //条件筛选
        this.parseQueryCondition(filterConditionDto, condition);
        //关键词搜索
        this.parseKeyword(keyword, siteCode,  condition);
        return condition;
    }


    //状态统计默认值处理
    private  List<TaskStatusStatistics> convertFillStatusDefaultValue(List<JyBizTaskAviationStatusStatistics> dbQuery, boolean flag) {
        List<TaskStatusStatistics> statusAggList = new ArrayList<>();
        if(flag) {

            TaskStatusStatistics toSend = new TaskStatusStatistics();
            toSend.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode());
            toSend.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getName());
            toSend.setTotal(0);
            statusAggList.add(toSend);

            TaskStatusStatistics sending = new TaskStatusStatistics();
            sending.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.SENDING.getCode());
            sending.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.SENDING.getName());
            sending.setTotal(0);
            statusAggList.add(sending);
        }else {
            TaskStatusStatistics trunkN = new TaskStatusStatistics();
            trunkN.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_N.getCode());
            trunkN.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_N.getName());
            trunkN.setTotal(0);
            statusAggList.add(trunkN);

            TaskStatusStatistics trunkY = new TaskStatusStatistics();
            trunkY.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_Y.getCode());
            trunkY.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_Y.getName());
            trunkY.setTotal(0);
            statusAggList.add(trunkY);

        }

        Map<Integer, Integer> map = dbQuery.stream().collect(Collectors.toMap(
                k -> k.getTaskStatus(),
                v -> v.getTotal()
        ));
        for (TaskStatusStatistics statistics : statusAggList) {
            Integer taskStatus = JyAviationRailwaySendVehicleStatusEnum.getSendTaskStatusByCode(statistics.getTaskStatus());
            if(!Objects.isNull(map.get(taskStatus))) {
                statistics.setTotal(map.get(JyAviationRailwaySendVehicleStatusEnum.getSendTaskStatusByCode(statistics.getTaskStatus())));
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
        else if (NumberUtils.isDigits(keyword)) {
            entity.setNextSiteId(Integer.valueOf(keyword));
        }
        else {
            throw new JyBizException("不支持当前关键字类型查询");
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

        Integer statusCode = null;
        if (!TASK_RECOMMEND.getCode().equals(request.getSource())) {
            statusCode = request.getStatusCode();
        }

        JyBizTaskSendAviationPlanQueryCondition condition = this.convertListQueryCondition(
                request.getCurrentOperate().getSiteCode(),
                statusCode,
                request.getFilterConditionDto(),
                request.getKeyword()
                );
        condition.setNextSiteId(request.getNextSiteId());
        condition.setOffset((request.getPageNo() - 1) * request.getPageSize());
        condition.setPageSize(request.getPageSize());

        List<JyBizTaskSendAviationPlanEntity> taskDtoList;
        
       if (!TASK_RECOMMEND.getCode().equals(request.getSource())) {
           //  查询发车任务列表
           taskDtoList = jyBizTaskSendAviationPlanService.pageFetchAviationTaskByNextSite(condition);
       }else {
           // 查询推荐任务列表
           taskDtoList = jyBizTaskSendAviationPlanService.pageQueryRecommendTaskByNextSiteId(condition);
        }

        List<AviationSendTaskDto> sendTaskDtoList = this.convertAviationSendTaskDtoList(taskDtoList);
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
    private List<AviationSendTaskDto> convertAviationSendTaskDtoList(List<JyBizTaskSendAviationPlanEntity> taskDtoList) {
        if(CollectionUtils.isEmpty(taskDtoList)) {
            return null;
        }
        List<String> bizIdList = new ArrayList<>();
        List<AviationSendTaskDto> res = new ArrayList<>();
        taskDtoList.forEach(dbQueryDto -> {
            
            if (JyAviationRailwaySendVehicleStatusEnum.SENDING.getSendTaskStatus().equals(dbQueryDto.getTaskStatus())) {
                bizIdList.add(dbQueryDto.getBizId());
            }

            AviationSendTaskDto taskDto = new AviationSendTaskDto();
            taskDto.setBizId(dbQueryDto.getBizId());
            taskDto.setDetailBizId(dbQueryDto.getBookingCode());
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
        if(CollectionUtils.isNotEmpty(bizIdList)) {
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
    public InvokeResult<AviationToSealAndSealedListRes> pageFetchAviationToSealAndSealedList(AviationSendTaskSealListReq request) {
        InvokeResult<AviationToSealAndSealedListRes> res = new InvokeResult<>();
        AviationToSealAndSealedListRes resData = new AviationToSealAndSealedListRes();
        res.setData(resData);

        resData.setTaskStatus(request.getStatusCode());
        resData.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.getNameByCode(request.getStatusCode()));

        JyBizTaskSendAviationPlanQueryCondition condition = this.convertListQueryCondition(
                request.getCurrentOperate().getSiteCode(),
                request.getStatusCode(),
                request.getFilterConditionDto(),
                request.getKeyword());
        condition.setOffset((request.getPageNo() - 1) * request.getPageSize());
        condition.setPageSize(request.getPageSize());

        List<JyBizTaskAviationStatusStatistics> taskStatusStatisticsList = jyBizTaskSendAviationPlanService.statusStatistics(condition);
        List<TaskStatusStatistics> taskStatusStatistics = this.convertFillStatusDefaultValue(taskStatusStatisticsList, false);
        resData.setTaskStatusStatisticsList(taskStatusStatistics);

        boolean existTaskFlag = false;
        for (TaskStatusStatistics tss : taskStatusStatistics) {
            if(tss.getTaskStatus().equals(request.getStatusCode()) && NumberHelper.gt0(tss.getTaskStatus())) {
                existTaskFlag = true;
                break;
            }
        }

        if(existTaskFlag) {
            //当前状态统计>0 查具体流向
            List<JyBizTaskSendAviationPlanEntity> aviationPlanEntityList = jyBizTaskSendAviationPlanService.pageQueryAviationPlanByCondition(condition);
            if(CollectionUtils.isNotEmpty(aviationPlanEntityList)) {
                List<AviationSealListDto> sealListDtoArrayList = new ArrayList<>();
                List<String> bizIdList = new ArrayList<>();
                aviationPlanEntityList.forEach(entity -> {
                    bizIdList.add(entity.getBizId());
                    sealListDtoArrayList.add(this.convertAviationSealListDto(entity));
                });

                //重量、体积、件数及运力
                this.fillAviationSealListStatistics(sealListDtoArrayList, bizIdList, request.getStatusCode(), request.getCurrentOperate().getSiteCode());

                //获取运力信息
                this.fillFocusTransportInfo(sealListDtoArrayList.get(0));

                resData.setAviationSealListDtoList(sealListDtoArrayList);
            }else {
                if(log.isInfoEnabled()) {
                    log.info("航空发货计划查询封车相关数据为空，request={},queryCondition={}", JsonHelper.toJson(request), JsonHelper.toJson(condition));
                }
            }
        }
        return res;
    }

    @Override
    public InvokeResult<ShuttleSendTaskRes> pageFetchShuttleSendTaskList(ShuttleSendTaskReq request) {
        InvokeResult<ShuttleSendTaskRes> res = new InvokeResult<>();
        ShuttleSendTaskRes resData = new ShuttleSendTaskRes();
        res.setData(resData);
        //关键词解析bizId
        List<String> bizIdList = null;
        if(StringUtils.isNotBlank(request.getKeyword())) {
            QueryTaskSendDto queryTaskSendDto = new QueryTaskSendDto();
            queryTaskSendDto.setKeyword(request.getKeyword());
            queryTaskSendDto.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
            bizIdList = super.resolveSearchKeyword(res, queryTaskSendDto);
            if (!res.codeSuccess()) {
                return res;
            }
        }
        //查询条件
        JyBizTaskSendVehicleDetailQueryEntity detailEntity = this.generateShuttleTask(request, bizIdList);
        //
        Integer count = taskSendVehicleService.countDetailSendTaskByCondition(detailEntity);
        if(count > 0) {
            List<JyBizSendTaskAssociationDto> sendTaskList = jyBizTaskSendVehicleService.pageFindDetailSendTaskByCondition(detailEntity, request.getPageNo(), request.getPageSize());
            if(CollectionUtils.isNotEmpty(sendTaskList)) {
                List<ShuttleSendTaskDto> shuttleSendTaskDtoList = new ArrayList<>();
                List<String> detailBizId = new ArrayList<>();

                sendTaskList.forEach(sendTask -> {
                    ShuttleSendTaskDto taskDto = new ShuttleSendTaskDto();
                    taskDto.setBizId(sendTask.getSendVehicleBizId());
                    taskDto.setDetailBizId(sendTask.getBizId());
                    String vehicleNumber = this.getVehicleNumber(sendTask.getTransWorkCode());
                    taskDto.setVehicleNumber(StringUtils.isBlank(vehicleNumber) ? "未知车牌号" : vehicleNumber);
                    taskDto.setNextSiteId(sendTask.getEndSiteId());
                    taskDto.setNextSiteName(sendTask.getEndSiteName());
                    taskDto.setTaskNum(0);
                    taskDto.setTotalItemNum(0);
                    shuttleSendTaskDtoList.add(taskDto);
                    detailBizId.add(taskDto.getDetailBizId());
                });
                //封车汇总数据
                if(ShuttleQuerySourceEnum.SEAL_Y.getCode().equals(request.getShuttleQuerySource())) {
                    //任务绑定信息查询， 流向维度传detailBizId
                    Map<String, JyStatisticsSummaryEntity> map = this.shuttleSendTaskSummary(detailBizId, request.getCurrentOperate().getSiteCode());
                    if(CollectionUtils.isNotEmpty(map.entrySet())) {
                        shuttleSendTaskDtoList.forEach(dto -> {
                        if(!Objects.isNull(map.get(dto.getDetailBizId()))) {
                            JyStatisticsSummaryEntity summaryEntity = map.get(dto.getDetailBizId());
                            dto.setTaskNum(summaryEntity.getSealBindAviationTaskNum());
                            dto.setTotalItemNum(summaryEntity.getItemNum());
                        }
                        });
                    }
                }
                resData.setShuttleSendTaskDtoList(shuttleSendTaskDtoList);
            }else {
                count = 0;
            }
        }

        //封车统计值
        if(ShuttleQuerySourceEnum.SEAL_Y.getCode().equals(request.getShuttleQuerySource())) {
            List<TaskStatusStatistics> statusAggList = new ArrayList<>();
            TaskStatusStatistics shuttle = new TaskStatusStatistics();
            shuttle.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.SHUTTLE_SEAL_Y.getCode());
            shuttle.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.SHUTTLE_SEAL_Y.getName());
            shuttle.setTotal(count);
            statusAggList.add(shuttle);
            resData.setTaskStatusStatisticsList(statusAggList);
        }
        return res;
    }

    private String getVehicleNumber(String transWorkCode){
        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(transWorkCode);
        if(!Objects.isNull(transWorkBillDto) && StringUtils.isNotBlank(transWorkBillDto.getVehicleNumber())) {
            return transWorkBillDto.getVehicleNumber();
        }
        if(log.isInfoEnabled()) {
            log.info("根据派车任务编码【{}】获取车牌号为空,运输返回={}", transWorkCode, JsonHelper.toJson(transWorkBillDto));
        }
        return StringUtils.EMPTY;
    }

    private JyBizTaskSendVehicleDetailQueryEntity generateShuttleTask(ShuttleSendTaskReq request, List<String> bizIdList) {
        JyBizTaskSendVehicleDetailQueryEntity detailEntity = new JyBizTaskSendVehicleDetailQueryEntity();
        detailEntity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        detailEntity.setLineTypeList(Arrays.asList(JyLineTypeEnum.SHUTTLE.getCode()));
        if(ShuttleQuerySourceEnum.SEAL_Y.getCode().equals(request.getShuttleQuerySource())) {
            detailEntity.setStatusList(Arrays.asList(JyBizTaskSendStatusEnum.SEALED.getCode()));
        } else if(ShuttleQuerySourceEnum.SEAL_N.getCode().equals(request.getShuttleQuerySource())) {
            detailEntity.setStatusList(Arrays.asList(
                    JyBizTaskSendStatusEnum.TO_SEND.getCode(),
                    JyBizTaskSendStatusEnum.SENDING.getCode(),
                    JyBizTaskSendStatusEnum.TO_SEAL.getCode()));
        }
        if(CollectionUtils.isNotEmpty(bizIdList)) {
            detailEntity.setBizIdList(bizIdList);
        }

        Date curTime = new Date();
        detailEntity.setLastPlanDepartTimeBegin(DateHelper.addHours(curTime, -shuttlePlanTimeBeginHour));
        detailEntity.setLastPlanDepartTimeEnd(DateHelper.addHours(curTime, shuttlePlanTimeEndHour));
        detailEntity.setCreateTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -uccConfig.getJySendTaskCreateTimeBeginDay()));

        return detailEntity;
    }

    private Map<String, JyStatisticsSummaryEntity> shuttleSendTaskSummary(List<String> detailBizIdList, Integer siteId) {
        //封车汇总记录
        JyStatisticsSummaryCondition summaryCondition = new JyStatisticsSummaryCondition();
        summaryCondition.setBusinessKeyType(BusinessKeyTypeEnum.JY_SEND_TASK_DETAIL.getCode());
        summaryCondition.setSource(SummarySourceEnum.SEAL.getCode());
        summaryCondition.setBusinessKeyList(detailBizIdList);
        summaryCondition.setOperateSiteCode(siteId);
        List<JyStatisticsSummaryEntity> summaryEntityList = statisticsSummaryService.queryByBusinessKeysAndType(summaryCondition);
        //K-bizId V-汇总结果
        Map<String, JyStatisticsSummaryEntity> map = summaryEntityList.stream().collect(Collectors.toMap(k -> k.getBusinessKey(), v -> v));
        return map;
    }

    private Map<String, JyStatisticsSummaryEntity> aviationSendTaskSummary(List<String> bizIdList, Integer siteId) {
        //封车汇总记录
        JyStatisticsSummaryCondition summaryCondition = new JyStatisticsSummaryCondition();
        summaryCondition.setBusinessKeyType(BusinessKeyTypeEnum.JY_SEND_TASK.getCode());
        summaryCondition.setSource(SummarySourceEnum.SEAL.getCode());
        summaryCondition.setBusinessKeyList(bizIdList);
        summaryCondition.setOperateSiteCode(siteId);
        List<JyStatisticsSummaryEntity> summaryEntityList = statisticsSummaryService.queryByBusinessKeysAndType(summaryCondition);
        //K-bizId V-汇总结果
        Map<String, JyStatisticsSummaryEntity> map = summaryEntityList.stream().collect(Collectors.toMap(k -> k.getBusinessKey(), v -> v));
        return map;
    }


    @Override
    public InvokeResult<SendTaskBindQueryRes> queryBindTaskList(SendTaskBindQueryReq request) {
        InvokeResult<SendTaskBindQueryRes> res = new InvokeResult<>();
        SendTaskBindQueryRes resData = new SendTaskBindQueryRes();
        resData.setBindTaskNum(0);
        res.setData(resData);

        JyBizTaskBindEntityQueryCondition condition = new JyBizTaskBindEntityQueryCondition();
        condition.setBizId(request.getBizId());
        condition.setDetailBizId(request.getDetailBizId());
        condition.setOperateSiteCode(request.getCurrentOperate().getSiteCode());
        condition.setType(request.getType());
        List<JyBizTaskBindEntity> bindEntityList = jyBizTaskBindService.queryBindTaskList(condition);
        if(CollectionUtils.isEmpty(bindEntityList)) {
            res.setMessage("查询为空");
            return res;
        }
        resData.setBindTaskNum(bindEntityList.size());

        List<String> bindBizList = bindEntityList.stream().map(JyBizTaskBindEntity::getBindBizId).collect(Collectors.toList());
        List<JyBizTaskSendAviationPlanEntity> aviationPlanEntityList = jyBizTaskSendAviationPlanService.findByBizIdList(bindBizList);

        List<SendTaskBindQueryDto> sendTaskBindQueryDtoList = new ArrayList<>();
        List<String> bizIdList = new ArrayList<>();

        aviationPlanEntityList.forEach(entity -> {
            SendTaskBindQueryDto queryDto = new SendTaskBindQueryDto();
            queryDto.setBindBizId(entity.getBizId());
            queryDto.setFlightNumber(entity.getFlightNumber());
            queryDto.setBindDetailBizId(entity.getBookingCode());
            queryDto.setNextSiteId(entity.getNextSiteId());
            queryDto.setNextSiteName(entity.getNextSiteName());
            queryDto.setCargoType(entity.getCargoType());
            queryDto.setAirType(entity.getAirType());
            queryDto.setWeight(0d);
            queryDto.setVolume(0d);
            queryDto.setItemNum(0);
            sendTaskBindQueryDtoList.add(queryDto);
            bizIdList.add(entity.getBizId());
        });
        //封车汇总数据
        if(ShuttleQuerySourceEnum.SEAL_Y.getCode().equals(request.getShuttleQuerySource())) {
            Map<String, JyStatisticsSummaryEntity> map =  this.aviationSendTaskSummary(bizIdList, request.getCurrentOperate().getSiteCode());
            if(CollectionUtils.isNotEmpty(map.entrySet())) {
                sendTaskBindQueryDtoList.forEach(queryDto -> {
                if (!Objects.isNull(map.get(queryDto.getBindBizId()))) {
                    JyStatisticsSummaryEntity summaryEntity = map.get(queryDto.getBindBizId());
                    queryDto.setWeight(summaryEntity.getWeight());
                    queryDto.setVolume(summaryEntity.getVolume());
                    queryDto.setItemNum(summaryEntity.getItemNum());
                    queryDto.setTransportCode(summaryEntity.getTransportCode());
                    queryDto.setDepartureTimeStr(summaryEntity.getDepartTime());
                }
                });
            }
        }

        resData.setSendTaskBindQueryDtoList(sendTaskBindQueryDtoList);
        return res;
    }

    @Override
    public InvokeResult<AviationSealedListRes> pageFetchAviationSealedList(AviationSealedListReq request) {
        InvokeResult<AviationSealedListRes> res = new InvokeResult<>();
        AviationSealedListRes resData = new AviationSealedListRes();
        res.setData(resData);


        JyBizTaskSendAviationPlanQueryCondition condition = new JyBizTaskSendAviationPlanQueryCondition();
        condition.setStartSiteId(request.getCurrentOperate().getSiteCode());
        condition.setTaskStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());
        this.parseKeyword(request.getKeyword(), request.getCurrentOperate().getSiteCode(),  condition);
        condition.setOffset((request.getPageNo() - 1) * request.getPageSize());
        condition.setPageSize(request.getPageSize());

        //当前状态统计>0 查具体流向
        List<JyBizTaskSendAviationPlanEntity> aviationPlanEntityList = jyBizTaskSendAviationPlanService.pageQueryAviationPlanByCondition(condition);
        if(CollectionUtils.isNotEmpty(aviationPlanEntityList)) {
            List<AviationSealListDto> sealListDtoList = new ArrayList<>();
            List<String> bizIdList = new ArrayList<>();
            aviationPlanEntityList.forEach(entity -> {
                bizIdList.add(entity.getBizId());
                AviationSealListDto sealListDto = this.convertAviationSealListDto(entity);
                sealListDto.setBindFlag(false);
                sealListDtoList.add(sealListDto);
            });

            //重量、体积、件数、运力
            this.fillAviationSealListStatistics(sealListDtoList, bizIdList, JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_Y.getCode(), request.getCurrentOperate().getSiteCode());

            //是否已绑定
            this.bindFlagHandler(sealListDtoList, request.getSendVehicleBizId(), request.getSendVehicleDetailBizId());

            resData.setAviationSealListDtoList(sealListDtoList);
        }
        return res;
    }

    @Override
    public InvokeResult<AviationBarCodeDetailResp> sendBoxDetail(AviationBoxDetailReq request) {
        InvokeResult<AviationBarCodeDetailResp> invokeResult = new InvokeResult<>();
        AviationBarCodeDetailResp resp = new AviationBarCodeDetailResp();
        List<SendScanBarCodeDto> sendScanBarCodeDtoList = new ArrayList<>();
        resp.setSendScanBarCodeDtoList(sendScanBarCodeDtoList);
        invokeResult.setData(resp);
        
        SortingPageRequest sortingPageRequest = new SortingPageRequest();
        sortingPageRequest.setOffset((request.getPageNumber() - 1) * request.getPageSize());
        sortingPageRequest.setLimit(request.getPageSize());
        sortingPageRequest.setBoxCode(request.getBoxCode());
        sortingPageRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        List<String> pagePackageNoByBoxCode = sortingService.getPagePackageNoByBoxCode(sortingPageRequest);
        if (CollectionUtils.isNotEmpty(pagePackageNoByBoxCode)) {
            for (String barCode : pagePackageNoByBoxCode) {
                SendScanBarCodeDto scanBarCodeDto = new SendScanBarCodeDto();
                scanBarCodeDto.setBarCode(barCode);
                sendScanBarCodeDtoList.add(scanBarCodeDto);
            }
        }
        return invokeResult;
    }

    @Override
    public InvokeResult<Void> aviationSendComplete(AviationSendCompleteReq req) {
        InvokeResult<Void> invokeResult = new InvokeResult<>();
        
        // 查询发货任务数据
        JyBizTaskSendAviationPlanEntity sendAviationPlan = jyBizTaskSendAviationPlanService.findByBizId(req.getSendVehicleBizId());
        JyBizTaskSendVehicleEntity sendVehicleTask = jyBizTaskSendVehicleService.findByBizId(req.getSendVehicleBizId());
        JyBizTaskSendVehicleDetailEntity sendVehicleDetailTask = jyBizTaskSendVehicleDetailService.findByBizId(sendAviationPlan.getBookingCode());
        sendAviationPlan.setTaskStatus(JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode());
        
        sendVehicleTask.setUpdateUserName(req.getUser().getUserName());
        sendVehicleTask.setUpdateUserErp(req.getUser().getUserErp());
        sendAviationPlan.setUpdateUserErp(req.getUser().getUserErp());
        sendAviationPlan.setUpdateUserName(req.getUser().getUserName());
        sendVehicleDetailTask.setUpdateUserName(req.getUser().getUserName());
        sendVehicleDetailTask.setUpdateUserErp(req.getUser().getUserErp());
        // 更新任务状态
        if (sendVehicleTransactionManager.updateAviationTaskStatus(sendAviationPlan,
                        sendVehicleTask,
                        sendVehicleDetailTask,
                        null,
                        JyBizTaskSendDetailStatusEnum.TO_SEAL)) {
            return invokeResult;
        }else {
            invokeResult.error("任务完成失败，请联系分拣小秘！");
            return invokeResult;
        }
    }

    //任务添加页增加已绑定标签
    private void bindFlagHandler(List<AviationSealListDto> sealListDtoList, String sendVehicleBizId, String sendVehicleDetailBizId) {
        JyBizTaskBindEntityQueryCondition condition = new JyBizTaskBindEntityQueryCondition();
        condition.setBizId(sendVehicleBizId);
        condition.setDetailBizId(sendVehicleDetailBizId);
        condition.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION.getCode());
        List<JyBizTaskBindEntity> bindEntityList = jyBizTaskBindService.queryBindTaskList(condition);
        if(CollectionUtils.isEmpty(bindEntityList)) {
            return;
        }
        //K-bizId V 任意
        Map<String, Integer> map = bindEntityList.stream().collect(Collectors.toMap(
                k -> k.getBindBizId(),
                v -> 1));
        sealListDtoList.forEach(sealListDto -> {
            if(!Objects.isNull(map.get(sealListDto.getBizId()))) {
                sealListDto.setBindFlag(true);
            }
        });
    }

    @Override
    public JdVerifyResponse<AviationSendScanResp> scan(AviationSendScanReq request) {
        JdVerifyResponse<AviationSendScanResp> result = new JdVerifyResponse<>();
        SendScanRequest sendScanRequest = new SendScanRequest();
        com.jd.ql.erp.util.BeanUtils.copyProperties(request, sendScanRequest);
        // 空铁发货 前置校验
        if (AviationCheckBeforeSendScan(request, result)) {
            return result;
        }
        
        JdVerifyResponse<SendScanResponse> response = super.sendScan(sendScanRequest);
        AviationSendScanResp resp = BeanUtils.copy(response,AviationSendScanResp.class);
        result.setCode(response.getCode());
        result.setMessage(response.getMessage());
        result.setMsgBoxes(response.getMsgBoxes());
        result.setData(resp);
        return result;
    }

    private boolean AviationCheckBeforeSendScan(AviationSendScanReq request, JdVerifyResponse<AviationSendScanResp> result) {
        
        // 校验是否超载
        double scanWeight = 0L;
        List<JySendAggsEntity> sendAggList = jySendAggsService.findBySendVehicleBiz(request.getSendVehicleBizId());
        if (!CollectionUtils.isEmpty(sendAggList)) {
            JySendAggsEntity sendAgg = sendAggList.get(0);
            scanWeight = Objects.isNull(sendAgg.getTotalScannedWeight()) ? 0d : sendAgg.getTotalScannedWeight().doubleValue();
        }
        
        JyBizTaskSendAviationPlanEntity entity= jyBizTaskSendAviationPlanService.findByBizId(request.getSendVehicleBizId());
        if (entity == null) {
            result.toFail("未获取到发货任务！");
            return false;
        }
        double bookingWeight = entity.getBookingWeight();
        
        if (scanWeight > bookingWeight) {
            result.setMessage(InvokeResult.AVIATION_TASK_OUT_WEIGHT_MESSAGE);
            result.setCode(InvokeResult.AVIATION_TASK_OUT_WEIGHT_CODE);
            return false;
        }
        return true;
    }


    //封车列表查询结果集转换
    private AviationSealListDto convertAviationSealListDto(JyBizTaskSendAviationPlanEntity entity) {
        AviationSealListDto res = new AviationSealListDto();
        res.setBizId(entity.getBizId());
        res.setBookingCode(entity.getBookingCode());
        res.setDetailBizId(entity.getBookingCode());
        res.setFlightNumber(entity.getFlightNumber());
        res.setCargoType(entity.getCargoType());
        res.setAirType(entity.getAirType());
        res.setNextSiteId(entity.getNextSiteId());
        res.setNextSiteName(entity.getNextSiteName());

        return res;
    }

    /**
     * 首次扫描更新任务
     * @param request
     * @param taskSend
     * @param curSendDetail
     */
    public void updateSendVehicleStatus(SendScanRequest request, JyBizTaskSendVehicleEntity taskSend, JyBizTaskSendVehicleDetailEntity curSendDetail) {
        // 发货任务表更新
        taskSend.setUpdateTime(new Date());
        taskSend.setUpdateUserErp(request.getUser().getUserErp());
        taskSend.setUpdateUserName(request.getUser().getUserName());
        curSendDetail.setUpdateTime(taskSend.getUpdateTime());
        curSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        curSendDetail.setUpdateUserName(taskSend.getUpdateUserName());
        sendVehicleTransactionManager.updateTaskStatus(taskSend, curSendDetail, JyBizTaskSendDetailStatusEnum.SENDING);
        
        //空铁任务表更新
        JyBizTaskSendAviationPlanEntity aviationPlan = new JyBizTaskSendAviationPlanEntity();
        aviationPlan.setTaskStatus(JyBizTaskSendDetailStatusEnum.SENDING.getCode());
        aviationPlan.setUpdateTime(taskSend.getUpdateTime());
        aviationPlan.setUpdateUserErp(taskSend.getUpdateUserErp());
        aviationPlan.setUpdateUserName(taskSend.getUpdateUserName());
        aviationPlan.setBizId(taskSend.getBizId());
        aviationPlan.setStartSiteId(taskSend.getStartSiteId().intValue());
        int updateStatus = jyBizTaskSendAviationPlanService.updateStatus(aviationPlan);
        log.info("更新结果：{}", updateStatus);
    }
    
    private void fillAviationSealListStatistics(List<AviationSealListDto> sealListDtoList, List<String> bizIdList, Integer statusCode, Integer siteCode) {
        if(CollectionUtils.isEmpty(sealListDtoList) || CollectionUtils.isEmpty(bizIdList) || Objects.isNull(statusCode) || !NumberHelper.gt0(siteCode)) {
            return;
        }
        //未封查aggs统计数据
        if(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_N.getCode().equals(statusCode)) {
            List<JySendAggsEntity> aggs = jySendAggsService.getSendStatisticsByBizList(bizIdList);
            Map<String, JySendAggsEntity> map = aggs.stream().collect(Collectors.toMap(k -> k.getSendVehicleBizId(), v -> v));
            sealListDtoList.forEach(sealListDto -> {
                Double weight = 0d;
                Double volume = 0d;
                Integer itemNum = 0;
                JySendAggsEntity aggsEntity = map.get(sealListDto.getBizId());
                if(!Objects.isNull(aggsEntity)) {
                    if(!Objects.isNull(aggsEntity.getTotalScannedWeight())) {
                        weight = aggsEntity.getTotalScannedWeight().doubleValue();
                    }
                    if(!Objects.isNull(aggsEntity.getTotalScannedVolume())) {
                        volume = aggsEntity.getTotalScannedVolume().doubleValue();
                    }
                    if(!Objects.isNull(aggsEntity.getTotalScannedPackageCodeCount()) || !Objects.isNull(aggsEntity.getTotalScannedBoxCodeCount())) {
                        int scanPackageNum = Objects.isNull(aggsEntity.getTotalScannedPackageCodeCount()) ? 0 : aggsEntity.getTotalScannedPackageCodeCount();
                        int scanBoxNum = Objects.isNull(aggsEntity.getTotalScannedBoxCodeCount()) ? 0 : aggsEntity.getTotalScannedBoxCodeCount();
                        itemNum = scanPackageNum + scanBoxNum;
                    }
                }
                //
                sealListDto.setWeight(weight);
                sealListDto.setVolume(volume);
                sealListDto.setItemNum(itemNum);

            });

        }
        //已封查实际封车数据
        else if(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_Y.getCode().equals(statusCode)) {
            //封车汇总记录  K-bizId V-汇总结果
            Map<String, JyStatisticsSummaryEntity> map =  this.aviationSendTaskSummary(bizIdList, siteCode);
            if(CollectionUtils.isNotEmpty(map.entrySet())) {
                sealListDtoList.forEach(sealListDto -> {
                    if (!Objects.isNull(map.get(sealListDto.getBizId()))) {
                        JyStatisticsSummaryEntity summaryEntity = map.get(sealListDto.getBizId());
                        sealListDto.setWeight(summaryEntity.getWeight());
                        sealListDto.setVolume(summaryEntity.getVolume());
                        sealListDto.setItemNum(summaryEntity.getItemNum());
                        //获取运力信息
                        sealListDto.setDepartureTimeStr(summaryEntity.getDepartTime());
                        sealListDto.setTransportCode(summaryEntity.getTransportCode());
                    }
                });
            }
        }
    }

    private void fillFocusTransportInfo(AviationSealListDto sealListDto) {
        TransportCodeQueryReq param = new TransportCodeQueryReq();
        param.setBizId(sealListDto.getBizId());
        param.setDetailBizId(sealListDto.getDetailBizId());
        InvokeResult<TransportInfoQueryRes> invokeResult = this.fetchTransportCodeList(param);
        if(!invokeResult.codeSuccess()) {
            //辅助功能不卡实操
            log.warn("航空任务查询推荐运力信息出错，sealListDto={}, res={}", JsonHelper.toJson(sealListDto), JsonHelper.toJson(invokeResult));
            return;
        }
        if(!Objects.isNull(invokeResult.getData()) && CollectionUtils.isNotEmpty(invokeResult.getData().getTransportInfoDtoList())) {
            for (TransportDataDto data : invokeResult.getData().getTransportInfoDtoList()) {
                if(!Objects.isNull(data) && data.getFocusFlag()) {
                    sealListDto.setDepartureTimeStr(data.getDepartureTimeStr());
                    sealListDto.setTransportCode(data.getTransportCode());
                    break;
                }
            }
        }
    }

    public JyBizTaskSendVehicleEntity getSendVehicleByBizId(String bizId) {
        JyBizTaskSendAviationPlanEntity entity= jyBizTaskSendAviationPlanService.findByBizId(bizId);
        if (entity != null) {
            JyBizTaskSendVehicleEntity sendVehicle = new JyBizTaskSendVehicleEntity();
            sendVehicle.setBizId(bizId);
            sendVehicle.setStartSiteId(Long.valueOf(entity.getStartSiteId()));
            sendVehicle.setVehicleStatus(entity.getTaskStatus());
            return sendVehicle;
        }
        return null;
    }

    public List<JyBizTaskSendVehicleDetailEntity> getSendVehicleDetail(JyBizTaskSendVehicleDetailEntity detail) {
        JyBizTaskSendAviationPlanEntity entity= jyBizTaskSendAviationPlanService.findByBizId(detail.getSendVehicleBizId());
        if (entity != null) {
            JyBizTaskSendVehicleDetailEntity detailEntity = new JyBizTaskSendVehicleDetailEntity();
            detailEntity.setBizId(entity.getBookingCode());
            detailEntity.setSendVehicleBizId(detail.getBizId());
            detailEntity.setEndSiteId(Long.valueOf(entity.getNextSiteId()));
            detailEntity.setVehicleStatus(entity.getTaskStatus());
            detailEntity.setExcepLabel(entity.getIntercept());
            detailEntity.setStartSiteId(entity.getStartSiteId().longValue());
            return Collections.singletonList(detailEntity);
        }
        return null;
    }

    @Override
    public InvokeResult<AviationSendVehicleProgressResp> getAviationSendVehicleProgress(AviationSendVehicleProgressReq request) {
        InvokeResult<AviationSendVehicleProgressResp> result = new InvokeResult<>();
        AviationSendVehicleProgressResp taskDto = new AviationSendVehicleProgressResp();
        result.setData(taskDto);
        
        // 查询任务基础信息
        JyBizTaskSendAviationPlanEntity entity = jyBizTaskSendAviationPlanService.findByBizId(request.getBizId());
        if (entity == null){
            return new InvokeResult<>(InvokeResult.RESULT_NULL_CODE, "未找到航空计划发货任务！");
        }
        convertProgressResp(taskDto, entity);
        
        // 查询统计数据
        List<JySendAggsEntity> sendAggList = jySendAggsService.findBySendVehicleBiz(request.getBizId());
        if (!CollectionUtils.isEmpty(sendAggList)) {
            JySendAggsEntity sendAgg = sendAggList.get(0);
            taskDto.setScanWeight(Objects.isNull(sendAgg.getTotalScannedWeight()) ? 0d : sendAgg.getTotalScannedWeight().doubleValue());
            int scanPackageNum = Objects.isNull(sendAgg.getTotalScannedPackageCodeCount()) ? 0 : sendAgg.getTotalScannedPackageCodeCount();
            int scanBoxNum = Objects.isNull(sendAgg.getTotalScannedBoxCodeCount()) ? 0 : sendAgg.getTotalScannedBoxCodeCount();
            taskDto.setScannedBoxCount(scanBoxNum);
            taskDto.setScannedPackCount(scanPackageNum);
            taskDto.setScannedCount(scanBoxNum + scanPackageNum);
            taskDto.setInterceptedPackCount(sendAgg.getTotalInterceptCount());
            taskDto.setForceSendPackCount(sendAgg.getTotalForceSendCount());
        }

        taskDto.setTaskId(getJyScheduleTaskId(request.getBizId()));
        return result;
    }

    @Override
    public InvokeResult<AviationSendAbnormalPackResp> abnormalBarCodeDetail(AviationSendAbnormalPackReq request) {
        InvokeResult<AviationSendAbnormalPackResp> invokeResult = new InvokeResult<>();
        if (!NumberHelper.gt0(request.getPageNumber())
                || !NumberHelper.gt0(request.getPageSize())
                || org.apache.commons.lang3.StringUtils.isBlank(request.getBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            queryAbnormalSendBarCodeList(invokeResult, request);
        }
        catch (JyDemotionException e){
            invokeResult.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_SEND_INTERCEPT, false));
        }
        catch (Exception ex) {
            log.error("查询发车拦截包裹记录异常. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，查询拦截包裹记录失败，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    @Override
    public InvokeResult<AviationBarCodeDetailResp> sendBarCodeDetail(AviationBarCodeDetailReq request) {
        InvokeResult<AviationBarCodeDetailResp> invokeResult = new InvokeResult<>();
        AviationBarCodeDetailResp resp = new AviationBarCodeDetailResp();
        List<SendScanBarCodeDto> sendScanBarCodeDtoList = new ArrayList<>();
        resp.setSendScanBarCodeDtoList(sendScanBarCodeDtoList);
        invokeResult.setData(resp);
        checkAviationBarCodeDetailReq(request,invokeResult);
        if (!invokeResult.codeSuccess()) {
            return invokeResult;
        }
        
        // 判断查询包裹号还是箱号
        boolean queryBoxCount = true;
        if (Objects.equals(request.getScanedType(), AviationScanedTypeEnum.PACKAGE.getCode())) {
            queryBoxCount = false;
        }
        
        if (StringUtils.isNotEmpty(request.getBarCode())) {
            if (WaybillUtil.isPackageCode(request.getBarCode())) {
                queryBoxCount = false;
            }
        }
        //查询箱号明细
        if (queryBoxCount) {
            Pager<SendVehicleTaskQuery> queryPager = getSendBarCodeDetailQueryPager(request);
            Pager<SendBoxAgg> boxAggPager = sendVehicleJsfManager.querySendBoxAgg(queryPager);
            if (boxAggPager != null && CollectionUtils.isNotEmpty(boxAggPager.getData())) {
                for (SendBoxAgg sendBoxAgg : boxAggPager.getData()) {
                    SendScanBarCodeDto scanBarCodeDto = new SendScanBarCodeDto();
                    scanBarCodeDto.setBarCode(sendBoxAgg.getBoxCode());
                    scanBarCodeDto.setPackCount(sendBoxAgg.getPackageCount());
                    sendScanBarCodeDtoList.add(scanBarCodeDto);
                }
            }
        }else {
            // 查询包裹明细
            Pager<SendVehicleTaskQuery> queryPager = getSendBarCodeDetailQueryPager(request);
            Pager<SendPackageEsDto> sendPackageDtoPager = sendVehicleJsfManager.querySendPackageDetailNoBox(queryPager);
            if (sendPackageDtoPager != null && CollectionUtils.isNotEmpty(sendPackageDtoPager.getData())) {
                for (SendPackageEsDto sendBoxAgg : sendPackageDtoPager.getData()) {
                    SendScanBarCodeDto scanBarCodeDto = new SendScanBarCodeDto();
                    scanBarCodeDto.setBarCode(sendBoxAgg.getPackageCode());
                    sendScanBarCodeDtoList.add(scanBarCodeDto);
                }
            }
        }
        return invokeResult;
    }

    private Pager<SendVehicleTaskQuery> getSendBarCodeDetailQueryPager(AviationBarCodeDetailReq request) {
        Pager<SendVehicleTaskQuery> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());

        SendVehicleTaskQuery searchVo = new SendVehicleTaskQuery();
        pager.setSearchVo(searchVo);
        searchVo.setSendVehicleBizId(request.getBizId());
        searchVo.setBarCode(request.getBarCode());
        return pager;
    }

    private void checkAviationBarCodeDetailReq(AviationBarCodeDetailReq request, InvokeResult<AviationBarCodeDetailResp> invokeResult) {
        if (!NumberHelper.gt0(request.getPageNumber())
                || !NumberHelper.gt0(request.getPageSize())
                || org.apache.commons.lang3.StringUtils.isBlank(request.getBizId()) ||
                (StringUtils.isEmpty(request.getBarCode()) && request.getScanedType() == null)) {
            invokeResult.parameterError();
        }

        // 校验条码
        if (StringUtils.isNotEmpty(request.getBarCode())
                && !WaybillUtil.isPackageCode(request.getBarCode())
                && !BusinessHelper.isBoxcode((request.getBarCode()))) {
            invokeResult.parameterError("请扫描正确的包裹号或者箱号！");
        }
        
        if (request.getScanedType() != null 
                && !Objects.equals(request.getScanedType(),AviationScanedTypeEnum.PACKAGE.getCode()) 
                && !Objects.equals(request.getScanedType(),AviationScanedTypeEnum.BOX.getCode())) {
            invokeResult.parameterError("扫描类型错误！");
        }
    }

    private void queryAbnormalSendBarCodeList(InvokeResult<AviationSendAbnormalPackResp> invokeResult, AviationSendAbnormalPackReq request) {
        Pager<SendVehicleTaskQuery> queryPager = getAbnormalQueryPager(request);

        AviationSendAbnormalPackResp packResp = new AviationSendAbnormalPackResp();
        invokeResult.setData(packResp);
        List<String> barCodeList = new ArrayList<>();
        packResp.setBarCodeList(barCodeList);
        
        Pager<SendBarCodeDetailVo> retPager = sendVehicleJsfManager.queryByCondition(queryPager);
        if (retPager != null && org.apache.commons.collections4.CollectionUtils.isNotEmpty(retPager.getData())) {
            retPager.getData().forEach(item -> barCodeList.add(item.getPackageCode()));
        }
    }

    private Pager<SendVehicleTaskQuery> getAbnormalQueryPager(AviationSendAbnormalPackReq request) {
        Pager<SendVehicleTaskQuery> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());

        SendVehicleTaskQuery searchVo = new SendVehicleTaskQuery();
        pager.setSearchVo(searchVo);
        searchVo.setSendVehicleBizId(request.getBizId());
        searchVo.setQueryBarCodeFlag(request.getQueryBarCodeFlag());
        return pager;
    }

    private void convertProgressResp(AviationSendVehicleProgressResp taskDto, JyBizTaskSendAviationPlanEntity entity) {
        taskDto.setBizId(entity.getBizId());
        taskDto.setBookingCode(entity.getBookingCode());
        taskDto.setFlightNumber(entity.getFlightNumber());
        taskDto.setTakeOffTime(Objects.isNull(entity.getTakeOffTime()) ? null : entity.getTakeOffTime().getTime());
        taskDto.setAirCompanyCode(entity.getAirCompanyCode());
        taskDto.setAirCompanyName(entity.getAirCompanyName());
        taskDto.setBeginNodeCode(entity.getBeginNodeCode());
        taskDto.setBeginNodeName(entity.getBeginNodeName());
        taskDto.setCarrierCode(entity.getCarrierCode());
        taskDto.setCarrierName(entity.getCarrierName());
        taskDto.setBookingWeight(entity.getBookingWeight());
        taskDto.setCargoType(entity.getCargoType());
        taskDto.setAirType(entity.getAirType());
        taskDto.setNextSiteId(entity.getNextSiteId());
        taskDto.setNextSiteName(entity.getNextSiteName());
    }


    @Override
    public InvokeResult<ScanSendCodeValidRes> validateTranCodeAndSendCode(ScanSendCodeValidReq request) {
        InvokeResult<ScanSendCodeValidRes> res = new InvokeResult<>();

        ValidSendCodeReq sealVehicleReq = new ValidSendCodeReq();
        sealVehicleReq.setSendCode(request.getSendCode());
        sealVehicleReq.setTransportCode(request.getTransportCode());
        sealVehicleReq.setVehicleNumber(request.getVehicleNumber());
        sealVehicleReq.setSealCarType(SealCarTypeEnum.SEAL_BY_TASK.getType());
        sealVehicleReq.setSealCarSource(SealCarSourceEnum.COMMON_SEAL_CAR.getCode());
        InvokeResult<SealCarSendCodeResp> weightVolumeRes = jySealVehicleService.validateTranCodeAndSendCode(sealVehicleReq);
        if(!weightVolumeRes.codeSuccess()) {
            res.setCode(weightVolumeRes.getCode());
            res.setMessage(weightVolumeRes.getMessage());
            return res;
        }

        ScanSendCodeValidRes resData = new ScanSendCodeValidRes();
        resData.setBatchCode(request.getSendCode());
        resData.setWeight(0d);
        resData.setVolume(0d);
        resData.setItemNum(0);
        res.setData(resData);

        if(!Objects.isNull(weightVolumeRes) && !Objects.isNull(weightVolumeRes.getData())) {
            BigDecimal bdWeight = weightVolumeRes.getData().getPackageWeightTotal();
            BigDecimal bdVolume = weightVolumeRes.getData().getPackageVolumeTotal();
            if(!Objects.isNull(bdWeight) && NumberHelper.gt0(bdWeight)) {
                resData.setWeight(bdWeight.doubleValue());
            }
            if(!Objects.isNull(bdVolume) && NumberHelper.gt0(bdVolume)) {
                resData.setVolume(bdVolume.doubleValue());
            }
        }
        SendDetail sendDetail = new SendDetail();
        sendDetail.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        sendDetail.setSendCode(request.getSendCode());
        Integer itemNum = sendDatailDao.countBoxCodeSingleBySendCode(sendDetail);
        if(NumberHelper.gt0(itemNum)) {
            resData.setItemNum(itemNum);
        }
        return res;
    }
}
