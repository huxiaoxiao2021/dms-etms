package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.blockcar.enumeration.SealCarTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.BookingTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.JyAviationRailwaySendVehicleStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendTaskTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.ShuttleQuerySourceEnum;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req.*;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res.*;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.CarrierQueryWSManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.constants.TaskBindTypeEnum;
import com.jd.bluedragon.distribution.jy.dto.send.AviationNextSiteStatisticsDto;
import com.jd.bluedragon.distribution.jy.dto.send.QueryTaskSendDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.summary.JyStatisticsSummaryService;
import com.jd.bluedragon.distribution.jy.service.task.*;
import com.jd.bluedragon.distribution.jy.summary.BusinessKeyTypeEnum;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryCondition;
import com.jd.bluedragon.distribution.jy.summary.JyStatisticsSummaryEntity;
import com.jd.bluedragon.distribution.jy.task.*;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.wss.dto.SealCarDto;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.util.SiteSignTool;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.jd.bluedragon.utils.TimeUtils.yyyy_MM_dd_HH_mm_ss;

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
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Autowired
    private JyVehicleSendRelationService jyVehicleSendRelationService;
    @Autowired
    private NewSealVehicleService newSealVehicleService;
    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;
    @Autowired
    private JyStatisticsSummaryService statisticsSummaryService;


    @Override
    public InvokeResult<Void> sendTaskBinding(SendTaskBindReq request) {
        final String methodDesc = "JyAviationRailwaySendSealServiceImpl:sendTaskBinding:任务绑定：";
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        //并发锁（和封车同一把锁，避免封车期间绑定并发问题）
        if(!jyAviationRailwaySendSealCacheService.lockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode())) {
            res.error("多人操作中，请稍后重试");
            return res;
        }
        try{
            //校验被绑摆渡任务是否封车，已封车禁绑
            JyBizTaskSendAviationPlanEntity sendTaskInfo = jyBizTaskSendAviationPlanService.findByBizId(request.getBizId());
            if(Objects.isNull(sendTaskInfo)) {
                res.error("未找到不支持解绑.bizId:" + request.getBizId());
                return res;
            }
            if(JyBizTaskSendStatusEnum.SEALED.getCode().equals(sendTaskInfo.getTaskStatus())) {
                res.error("不支持解绑已封车，不支持绑定");
                return res;
            }
            if(JyBizTaskSendStatusEnum.CANCEL.getCode().equals(sendTaskInfo.getTaskStatus())) {
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
            jyAviationRailwaySendSealCacheService.unlockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode());
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
        if(!jyAviationRailwaySendSealCacheService.lockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode())) {
            res.error("多人操作中，请稍后重试");
            return res;
        }
        try{
            //校验被绑摆渡任务是否封车，已封车禁绑
            JyBizTaskSendAviationPlanEntity sendTaskInfo = jyBizTaskSendAviationPlanService.findByBizId(request.getBizId());
            if(Objects.isNull(sendTaskInfo)) {
                res.error("未找到摆渡任务.bizId:" + request.getBizId());
                return res;
            }
            if(JyBizTaskSendStatusEnum.SEALED.getCode().equals(sendTaskInfo.getTaskStatus())) {
                res.error("摆渡任务已封车，不支持解绑");
                return res;
            }
            if(JyBizTaskSendStatusEnum.CANCEL.getCode().equals(sendTaskInfo.getTaskStatus())) {
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
            jyAviationRailwaySendSealCacheService.unlockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.VEHICLE.getCode());
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
        //todo zcf
        return null;
    }

    @Override
    public InvokeResult<TransportDataDto> scanAndCheckTransportInfo(ScanAndCheckTransportInfoReq request) {
        InvokeResult<TransportDataDto> res = new InvokeResult<>();
        TransportDataDto resData = new TransportDataDto();
        res.setData(resData);

        CommonDto<TransportResourceDto> commonDto = carrierQueryWSManager.getTransportResourceByTransCode(request.getTransportCode());
        if (Objects.isNull(commonDto) || Objects.isNull(commonDto.getData())) {
            res.error("查询运力信息结果为空");
            return res;
        }

        if (Constants.RESULT_SUCCESS == commonDto.getCode()) {
            TransportResourceDto data = commonDto.getData();
            Integer endNodeId = data.getEndNodeId();
            if (!request.getNextSiteId().equals(endNodeId)) {
                //不分传摆和运力都去校验目的地类型是中转场的时候 跳过目的地不一致逻辑
                BaseStaffSiteOrgDto endNodeSite = baseMajorManager.getBaseSiteBySiteId(endNodeId);
                if (!(endNodeSite != null && SiteSignTool.supportTemporaryTransfer(endNodeSite.getSiteSign()))) {
                    res.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    res.setMessage(NewSealVehicleResponse.TIPS_RECEIVE_DIFF_ERROR);
                    return res;
                }
            }
            resData.setTransWay(data.getTransWay());
            resData.setTransTypeName(data.getTransTypeName());
            resData.setTransportCode(request.getTransportCode());
            //todo zcf 发车时间取那个字段
//            resData.setDepartureTime();
            resData.setDepartureTimeStr("待确认");
            return res;
        } else if (Constants.RESULT_WARN == commonDto.getCode()) {
            res.error(commonDto.getMessage());
            return res;
        } else {
            res.error("查询运力信息出错！");
            log.warn("scanAndCheckTransportInfo：根据运力编码：【{}】查询运力信息出错,出错原因:{}", request.getTransportCode(), commonDto.getMessage());
            return res;
        }
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
        //todo zcf 这个接口写错了。航空封车待确认，这个逻辑走的是摆渡封车
        InvokeResult<Void> res = new InvokeResult<>();

        //拦截
        if(jyBizTaskSendAviationPlanService.aviationPlanIntercept(request.getBizId())) {
            res.error("该航空任务已经取消，无法进行后续操作，请迁移任务操作数据");
            return res;
        }

        if(!jyAviationRailwaySendSealCacheService.lockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.AVIATION.getCode())) {
            res.error("多人操作中，请稍后重试");
            return res;
        }

        try{
            List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleBizId(request.getBizId());
            if(CollectionUtils.isEmpty(sendCodeList)) {
                res.error("未查到该任务发货批次");
                return res;
            }
            JyBizTaskSendAviationPlanEntity entity = jyBizTaskSendAviationPlanService.findByBizId(request.getBizId());
            if (ObjectHelper.isNotNull(entity) && JyBizTaskSendStatusEnum.SEALED.getCode().equals(entity.getTaskStatus())){
                throw new JyBizException("该航空任务已封车！");
            }

//            SealCarDto sealCarDto = BeanUtils.copy(sealVehicleReq, SealCarDto.class);
            SealCarDto sealCarDto = new SealCarDto();
            sealCarDto.setSealCarType(SealCarTypeEnum.SEAL_BY_TRANSPORT_CAPABILITY.getType());
            sealCarDto.setSealCarTime(TimeUtils.date2string(new Date(), yyyy_MM_dd_HH_mm_ss));
            sealCarDto.setSealSiteId(request.getCurrentOperate().getSiteCode());
            sealCarDto.setSealSiteName(request.getCurrentOperate().getSiteName());
            sealCarDto.setSealUserCode(request.getUser().getUserErp());
            sealCarDto.setSealUserName(request.getUser().getUserName());
            sealCarDto.setBatchCodes(sendCodeList);
            //转换体积单位 立方厘米转换为立方米
            sealCarDto.setVolume(request.getVolume());
            sealCarDto.setWeight((request.getWeight()));
            sealCarDto.setItemNum(request.getItemNum());

            List<SealCarDto> sealCarDtoList = Arrays.asList(sealCarDto);
            //批次为空的列表信息
            Map<String, String> emptyBatchCode = new HashMap<String,String>();
            NewSealVehicleResponse sealResp = newSealVehicleService.doSealCarWithVehicleJob(sealCarDtoList,emptyBatchCode);
            if(!JdResponse.CODE_OK.equals(sealResp.getCode())) {
                String msg = StringUtils.isEmpty(sealResp.getMessage()) ? "封车异常" : sealResp.getMessage();
                res.setMessage(msg);
                return res;
            }
            //保存封车个数

            //航空任务没有主子任务一说，沿用历史发货任务数据模型，生成任务时以订舱号为detailBizId，直接使用
            updateTaskStatusAndSummary(request, sendCodeList, entity.getBookingCode(),  sealCarDto);
            return res;
        }catch (Exception e) {
            log.error("航空任务发货提交封车异常：request={},errMsg={}", JsonHelper.toJson(request), e.getMessage(), e);
            throw new JyBizException("航空任务发货提交封车异常");
        }finally {
            jyAviationRailwaySendSealCacheService.unlockSendTaskSeal(request.getBizId(), SendTaskTypeEnum.AVIATION.getCode());
        }
    }

    //修改状态并落封车汇总数据
    private void updateTaskStatusAndSummary(AviationTaskSealCarReq request, List<String> batchCodes, String detailBizId, SealCarDto sealCarDto) {
        //航空计划
        Date time = new Date();
        JyBizTaskSendAviationPlanEntity aviationPlanEntity = new JyBizTaskSendAviationPlanEntity();
        aviationPlanEntity.setUpdateTime(time);
        aviationPlanEntity.setUpdateUserErp(request.getUser().getUserErp());
        aviationPlanEntity.setUpdateUserName(request.getUser().getUserName());
        //发货任务主表
        JyBizTaskSendVehicleDetailEntity taskSendDetail = jyBizTaskSendVehicleDetailService.findByBizId(detailBizId);
        JyBizTaskSendVehicleEntity taskSend = jyBizTaskSendVehicleService.findByBizId(request.getBizId());
        taskSend.setUpdateTime(time);
        taskSend.setUpdateUserErp(request.getUser().getUserErp());
        taskSend.setUpdateUserName(request.getUser().getUserName());
        //发货任务子表
        taskSendDetail.setSealCarTime(DateHelper.parseDate(sealCarDto.getSealCarTime(), Constants.DATE_TIME_FORMAT));
        taskSendDetail.setUpdateTime(time);
        taskSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        taskSendDetail.setUpdateUserName(taskSend.getUpdateUserName());
        //航空任务封车汇总
        JyStatisticsSummaryEntity summaryEntity = new JyStatisticsSummaryEntity(
                request.getBizId(), BusinessKeyTypeEnum.JY_SEND_TASK.getCode(), request.getCurrentOperate().getSiteCode());
        summaryEntity.setWeight(request.getWeight());
        summaryEntity.setVolume(request.getVolume());
        summaryEntity.setItemNum(request.getItemNum());
        summaryEntity.setBatchCodeNum(batchCodes.size());
        summaryEntity.setSubBusinessNum(1);
        summaryEntity.setTransportCode(request.getTransportCode());
        summaryEntity.setCreateTime(time);
        summaryEntity.setUpdateTime(time);
        summaryEntity.setCreateUserErp(request.getUser().getUserErp());
        summaryEntity.setCreateUserName(request.getUser().getUserName());
        summaryEntity.setUpdateUserErp(request.getUser().getUserErp());
        summaryEntity.setUpdateUserName(request.getUser().getUserName());

        sendVehicleTransactionManager.updateAviationTaskStatus(aviationPlanEntity, taskSend, taskSendDetail, summaryEntity, JyBizTaskSendDetailStatusEnum.SEALED);
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

        List<JyBizTaskAviationStatusStatistics> taskStatusStatisticsList = jyBizTaskSendAviationPlanService.statusStatistics(condition);
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
            }
        }

        return res;
    }


    //列表查询条件转换
    private JyBizTaskSendAviationPlanQueryCondition convertListQueryCondition(Integer siteCode, Integer statusCode, FilterConditionDto filterConditionDto, String keyword) {
        JyBizTaskSendAviationPlanQueryCondition condition = new JyBizTaskSendAviationPlanQueryCondition();
        condition.setStartSiteId(siteCode);
        condition.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.getSendTaskStatusByCode(statusCode));
        if(JyAviationRailwaySendVehicleStatusEnum.TO_SEND.getCode().equals(statusCode)) {
            //起飞时间
            condition.setTakeOffTime(DateHelper.newTimeRangeHoursAgo(new Date(), 24));
        }
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
            toSend.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.SENDING.getCode());
            toSend.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.SENDING.getName());
            toSend.setTotal(0);
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
            if(tss.getTaskStatus().equals(request.getStatusCode()) && NumberHelper.gt0(tss.getTotal())) {
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
        JyBizTaskSendVehicleEntity queryEntity = new JyBizTaskSendVehicleEntity();
        queryEntity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        queryEntity.setLineType(JyLineTypeEnum.SHUTTLE.getCode());

        List<Integer> vehicleStatuses = null;
        if(ShuttleQuerySourceEnum.SEAL_Y.getCode().equals(request.getShuttleQuerySource())) {
            vehicleStatuses = Arrays.asList(JyBizTaskSendStatusEnum.TO_SEAL.getCode());
        } else if(ShuttleQuerySourceEnum.SEAL_N.getCode().equals(request.getShuttleQuerySource())) {
            //todo zcf 确认车辆列表查询未封车的，  逻辑待确认，确认最终展示的是以及还是二级列表
            vehicleStatuses = Arrays.asList(JyBizTaskSendStatusEnum.TO_SEND.getCode(), JyBizTaskSendStatusEnum.SENDING.getCode());
        }
        //状态统计
        Integer count = taskSendVehicleService.countByCondition(queryEntity, bizIdList, vehicleStatuses);

        if(count > 0) {
            List<JyBizTaskSendVehicleEntity> sendTaskList = jyBizTaskSendVehicleService.querySendTaskOfPage(
                    queryEntity, bizIdList, null, request.getPageNo(), request.getPageSize(), vehicleStatuses);
            if(CollectionUtils.isNotEmpty(sendTaskList)) {
                List<ShuttleSendTaskDto> shuttleSendTaskDtoList = new ArrayList<>();
                sendTaskList.forEach(sendTask -> {
                    ShuttleSendTaskDto taskDto = new ShuttleSendTaskDto();
                    taskDto.setBizId(sendTask.getBizId());
                    taskDto.setVehicleNumber(sendTask.getVehicleNumber());
                    //todo zcf 封车总件数和封车绑定总任务数待定
//                    taskDto.setTotalItemNum();
//                    taskDto.setTotalItemNum();
                    shuttleSendTaskDtoList.add(taskDto);
                });
                resData.setShuttleSendTaskDtoList(shuttleSendTaskDtoList);
            }
        }
        //统计值
        int total = CollectionUtils.isEmpty(resData.getShuttleSendTaskDtoList()) ? 0 : count;
        List<TaskStatusStatistics> statusAggList = new ArrayList<>();
        TaskStatusStatistics shuttleY = new TaskStatusStatistics();
        shuttleY.setTaskStatus(JyAviationRailwaySendVehicleStatusEnum.SHUTTLE_SEAL_Y.getCode());
        shuttleY.setTaskStatusName(JyAviationRailwaySendVehicleStatusEnum.SHUTTLE_SEAL_Y.getName());
        shuttleY.setTotal(total);
        statusAggList.add(shuttleY);
        resData.setTaskStatusStatisticsList(statusAggList);

        return res;
    }

    @Override
    public InvokeResult<SendTaskBindQueryRes> queryBindTaskList(SendTaskBindQueryReq request) {
        InvokeResult<SendTaskBindQueryRes> res = new InvokeResult<>();
        SendTaskBindQueryRes resData = new SendTaskBindQueryRes();
        res.setData(resData);

        JyBizTaskBindEntityQueryCondition condition = new JyBizTaskBindEntityQueryCondition();
        condition.setBizId(request.getBizId());
        condition.setOperateSiteCode(request.getCurrentOperate().getSiteCode());
        condition.setType(TaskBindTypeEnum.BIND_TYPE_AVIATION.getCode());
        List<JyBizTaskBindEntity> bindEntityList = jyBizTaskBindService.queryBindTaskList(condition);
        if(CollectionUtils.isEmpty(bindEntityList)) {
            res.setMessage("查询为空");
            return res;
        }
        List<String> bindBizList = bindEntityList.stream().map(JyBizTaskBindEntity::getBindBizId).collect(Collectors.toList());

        List<JyBizTaskSendAviationPlanEntity> aviationPlanEntityList = jyBizTaskSendAviationPlanService.findByBizIdList(bindBizList);

        List<SendTaskBindQueryDto> sendTaskBindQueryDtoList = new ArrayList<>();
        aviationPlanEntityList.forEach(entity -> {
            SendTaskBindQueryDto queryDto = new SendTaskBindQueryDto();
            queryDto.setBindBizId(entity.getBizId());
            queryDto.setFlightNumber(entity.getFlightNumber());
            queryDto.setBindDetailBizId(entity.getBookingCode());
            sendTaskBindQueryDtoList.add(queryDto);
        });
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
            this.bindFlagHandler(sealListDtoList, request.getSendVehicleBizId());

            resData.setAviationSealListDtoList(sealListDtoList);
        }
        return res;
    }
    //任务添加页增加已绑定标签
    private void bindFlagHandler(List<AviationSealListDto> sealListDtoList, String sendVehicleBizId) {
        JyBizTaskBindEntityQueryCondition condition = new JyBizTaskBindEntityQueryCondition();
        condition.setBizId(sendVehicleBizId);
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


    //封车列表查询结果集转换
    private AviationSealListDto convertAviationSealListDto(JyBizTaskSendAviationPlanEntity entity) {
        AviationSealListDto res = new AviationSealListDto();
        res.setBizId(entity.getBizId());
        res.setBookingCode(entity.getBookingCode());
        res.setFlightNumber(entity.getFlightNumber());
        res.setCargoType(entity.getCargoType());
        res.setAirType(entity.getAirType());
        res.setNextSiteId(entity.getNextSiteId());
        res.setNextSiteName(entity.getNextSiteName());

        return res;
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

                //获取运力信息  todo zcf 待确认
                //        res.setDepartureTime();
                //        res.setDepartureTimeStr();
                //        res.setTransportCode();
            });

        }
        //已封查实际封车数据
        else if(JyAviationRailwaySendVehicleStatusEnum.TRUNK_LINE_SEAL_Y.getCode().equals(statusCode)) {
            //封车汇总记录
            JyStatisticsSummaryCondition summaryCondition = new JyStatisticsSummaryCondition();
            summaryCondition.setBusinessKeyType(BusinessKeyTypeEnum.JY_SEND_TASK.getCode());
            summaryCondition.setBusinessKeyList(bizIdList);
            summaryCondition.setOperateSiteCode(siteCode);
            List<JyStatisticsSummaryEntity> summaryEntityList = statisticsSummaryService.queryByBusinessKeysAndType(summaryCondition);
            //K-bizId V-汇总结果
            Map<String, JyStatisticsSummaryEntity> map = summaryEntityList.stream().collect(Collectors.toMap(k -> k.getBusinessKey(), v -> v));

            sealListDtoList.forEach(sealListDto -> {
                Double weight = 0d;
                Double volume = 0d;
                Integer itemNum = 0;
                if(!Objects.isNull(map.get(sealListDto.getBizId()))) {
                    JyStatisticsSummaryEntity summaryEntity = map.get(sealListDto.getBizId());
                    sealListDto.setWeight(summaryEntity.getWeight());
                    sealListDto.setVolume(summaryEntity.getVolume());
                    sealListDto.setItemNum(summaryEntity.getItemNum());
                    //获取运力信息
                    sealListDto.setDepartureTime(Objects.isNull(summaryEntity.getDepartTime()) ? null : summaryEntity.getDepartTime().getTime());
                    sealListDto.setDepartureTimeStr(this.convertDepartureTimeStr(summaryEntity.getDepartTime()));
                    sealListDto.setTransportCode(summaryEntity.getTransportCode());
                }


            });
        }
    }

    //todo zcf 待确认
    private String convertDepartureTimeStr(Date departureTime) {
        if(Objects.isNull(departureTime)) {
            return "";
        }

        return "1";
    }


}
