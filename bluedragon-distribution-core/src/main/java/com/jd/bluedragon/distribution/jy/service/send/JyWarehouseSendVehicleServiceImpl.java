package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleTaskResponse;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSendVehicle;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.jsf.cross.SortCrossJsfManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dto.send.QueryTaskSendDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailQueryEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.ql.erp.util.BeanUtils;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.cross.TableTrolleyJsfDto;
import com.jdl.basic.api.domain.cross.TableTrolleyJsfResp;
import com.jdl.basic.api.domain.cross.TableTrolleyQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("jyWarehouseSendVehicleServiceImpl")
public class JyWarehouseSendVehicleServiceImpl extends JySendVehicleServiceImpl implements JyWarehouseSendVehicleService{

    private static final Logger log = LoggerFactory.getLogger(JyWarehouseSendVehicleServiceImpl.class);

    /**
     * DB不做分页时，查询limit最大数量限制
     */
    public static final Integer DB_LIMIT_DEFAULT_MAX = 100;
    /**
     * DB不做分页时，查询limit默认数量
     */
    public static final Integer DB_LIMIT_DEFAULT = 30;
    /**
     * 混扫任务默认流向数量
     */
    public static final Integer MIX_SCAN_TASK_DEFAULT_FLOW_NUM = 10;


    @Autowired
    private SortCrossJsfManager sortCrossJsfManager;
    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;
    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration ;
    @Autowired
    private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;
    @Autowired
    private JdiQueryWSManager jdiQueryWSManager;

    @Override
    public InvokeResult<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request) {

        InvokeResult<SendVehicleTaskResponse> invokeResult = super.fetchSendVehicleTask(request);
        setMixScanTaskSiteFlowMaxNum(request, invokeResult);
        //车辆状态差异化查询
        if(JyBizTaskSendStatusEnum.TO_SEND.getCode().equals(request.getVehicleStatus())) {
            fillWareHouseFocusField(request, invokeResult);
        }

        return invokeResult;
    }

    /**
     * 混扫任务内配置最大流向数量
     * @param invokeResult
     */
    private void setMixScanTaskSiteFlowMaxNum(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> invokeResult) {
        if(!Objects.isNull(invokeResult) && !Objects.isNull(invokeResult.getData()) && !Objects.isNull(invokeResult.getData().getToSealVehicleData())) {
            invokeResult.getData().getToSealVehicleData().setMixScanTaskSiteFlowMaxNum(getFlowMaxBySiteCode(request.getCurrentOperate().getSiteCode()));
        }
    }

    public Integer getFlowMaxBySiteCode(Integer siteCode) {
        int mixScanTaskFlowNum = MIX_SCAN_TASK_DEFAULT_FLOW_NUM;
        String mixScanTaskFlowNumConfig = uccPropertyConfiguration.getJyWarehouseSendVehicleMixScanTaskFlowNumConfig();

        String configKey = String.format("%s%s%s", Constants.SEPARATOR_COMMA, siteCode, Constants.SEPARATOR_COLON);
        if(StringUtils.isNotBlank(mixScanTaskFlowNumConfig) && mixScanTaskFlowNumConfig.contains(configKey)) {
            String[] configArr = mixScanTaskFlowNumConfig.split(Constants.SEPARATOR_COMMA);
            for(String conf : configArr) {
                String[] keyValue = conf.split(Constants.SEPARATOR_COLON);
                if(siteCode.toString().equals(keyValue[0]) && StringUtils.isNotBlank(keyValue[1])) {
                    mixScanTaskFlowNum = Integer.valueOf(keyValue[1]);
                    break;
                }
            }
        }
        return mixScanTaskFlowNum;
    }
    /**
     * 填充接货仓发货岗关注字段
     * @param request
     * @param invokeResult
     */
    private void fillWareHouseFocusField(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> invokeResult) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.JyWarehouseSendVehicleServiceImpl.fillWareHouseFocusField", false, true);

        if(!Objects.isNull(invokeResult)
                && !Objects.isNull(invokeResult.getData())
                && !Objects.isNull(invokeResult.getData().getToSendVehicleData())
                && CollectionUtils.isNotEmpty(invokeResult.getData().getToSendVehicleData().getData())) {

            List<ToSendVehicle> toSendVehicleList = invokeResult.getData().getToSendVehicleData().getData();
            //K-流向 V-滑道笼车号  map存储滑道笼车号减少不同派车任务中存在相同流向时的资源消耗
            HashMap<Long, String> crossTableTrolleyMap = new HashMap<>();
            //派车任务
            for(ToSendVehicle toSendVehicle : toSendVehicleList) {
                String bizId = toSendVehicle.getSendVehicleBizId();
                if(Objects.isNull(toSendVehicle) || CollectionUtils.isEmpty(toSendVehicle.getSendDestList())) {
                    continue;
                }
                //派车任务明细
                for(SendVehicleDetail sendVehicleDetail : toSendVehicle.getSendDestList()) {
                    //派车任务bizId
                    if(StringUtils.isBlank(sendVehicleDetail.getBizId())) {
                        sendVehicleDetail.setBizId(bizId);
                    }
                    //滑道笼车号
                    if(StringUtils.isBlank(sendVehicleDetail.getCrossTableTrolley())) {
                        sendVehicleDetail.setCrossTableTrolley(this.getCrossTableTrolley(
                                request.getCurrentOperate().getSiteCode(), sendVehicleDetail.getEndSiteId(), crossTableTrolleyMap)
                        );
                    }
                }
            }
        }

        Profiler.registerInfoEnd(info);
    }

    /**
     * 获取滑道笼车号
     * 返回值要么正确，要么null, null由调用方自己处理默认值
     * @return
     */
    private String fetchCrossTableTrolley(Integer currentSiteCode, Integer nextSiteCode) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl.fetchCrossTableTrolley:获取滑道笼车号：";
        try{
            TableTrolleyQuery tableTrolleyQuery = new TableTrolleyQuery();
            tableTrolleyQuery.setDmsId(currentSiteCode);
            tableTrolleyQuery.setSiteCode(nextSiteCode);
            JdResult<TableTrolleyJsfResp> jdResult = sortCrossJsfManager.queryCrossCodeTableTrolleyBySiteFlow(tableTrolleyQuery);
            String crossCode = jdResult.getData().getTableTrolleyDtoJsfList().get(0).getCrossCode();
            String tableTrolleyCode = jdResult.getData().getTableTrolleyDtoJsfList().get(0).getTableTrolleyCode();

            if(StringUtils.isBlank(crossCode) || StringUtils.isBlank(tableTrolleyCode)) {
                if(log.isInfoEnabled()) {
                    log.info("{}查询滑道号或笼车号为空，参数={}|{}，滑道笼车信息返回={}", methodDesc,
                            currentSiteCode, nextSiteCode, JsonHelper.toJson(jdResult));
                }
                return null;
            }
            return String.format("%s-%s", crossCode, tableTrolleyCode);
        }catch (Exception e) {
            log.error("{}服务异常，errMsg={},接货仓发货岗派车任务主页展示滑道笼车号，不卡实操流程，request={]|{}",
                    methodDesc, e.getMessage(), currentSiteCode, nextSiteCode);
        }
        return null;
    }
    //默认滑道笼车号
    private String unknownCrossTableTrolley(Integer currentSiteCode, Integer nextSiteCode) {
        if(Objects.isNull(currentSiteCode) || Objects.isNull(nextSiteCode)) {
            return "未知滑道笼车号";
        }
        return String.format("%s-%s未知滑道笼车号", currentSiteCode, nextSiteCode);
    }

    /**
     * 根据包裹号查询路由下一跳的发货任务
     * 取当前操作机构的下一跳作为发货目的地查询发货流向任务
     *
     * @param result
     * @param queryTaskSendDto
     * @return
     */
    @Override
    public  <T> List<String> resolveSearchKeyword(InvokeResult<T> result, QueryTaskSendDto queryTaskSendDto) {
        if (StringUtils.isBlank(queryTaskSendDto.getKeyword())) {
            return null;
        }

        long startSiteId = queryTaskSendDto.getStartSiteId();
        Long endSiteId = null;

        // 取当前操作网点的路由下一节点
        if (WaybillUtil.isPackageCode(queryTaskSendDto.getKeyword())) {
            endSiteId = getWaybillNextRouter(WaybillUtil.getWaybillCode(queryTaskSendDto.getKeyword()), startSiteId);
        }
//        else if (BusinessUtil.isBoxcode(queryTaskSendDto.getKeyword())
//                && JyComboardLineTypeEnum.TRANSFER.getCode().equals(queryTaskSendDto.getLineType())){
//            endSiteId = getBoxEndSiteId(queryTaskSendDto.getKeyword());
//        }
        else if (BusinessUtil.isSendCode(queryTaskSendDto.getKeyword())) {
            endSiteId = Long.valueOf(BusinessUtil.getReceiveSiteCodeFromSendCode(queryTaskSendDto.getKeyword()));
        }  else {
            //车牌号后四位检索
            if (queryTaskSendDto.getKeyword().length() == VEHICLE_NUMBER_FOUR) {
                List<String> sendVehicleBizList = querySendVehicleBizIdByVehicleFuzzy(queryTaskSendDto);
                if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
                    return sendVehicleBizList;
                }
                result.hintMessage("未检索到相应的发货任务数据！");
            } else if(queryTaskSendDto.getKeyword().contains(Constants.SEPARATOR_HYPHEN)
                    && queryTaskSendDto.getKeyword().split(Constants.SEPARATOR_HYPHEN).length == 2)  {
                List<Long> nextSiteIdList = this.getSiteFlowByCrossCodeTableTrolley(queryTaskSendDto);
                if(CollectionUtils.isEmpty(nextSiteIdList)) {
                    result.hintMessage("根据滑道笼车号未检索到流向！");
                    return null;
                }
                List<String> bizIdList = this.getSendVehicleBizIdList(startSiteId, nextSiteIdList);
                if(CollectionUtils.isEmpty(bizIdList)) {
                    result.hintMessage("根据滑道笼车号未检索到相应的发货任务数据！");
                    return null;
                }
                return bizIdList;

            } else {
                log.info("接货仓发货岗派车任务页按条件查询keyword={},输入错误。请求信息={}", queryTaskSendDto.getKeyword(), JsonHelper.toJson(queryTaskSendDto));
                result.hintMessage("输入位数错误，未检索到发货任务数据！");
            }
            return null;
        }

        if (endSiteId == null) {
            result.hintMessage("运单的路由没有当前场地！");
            return null;
        }

        // 根据路由下一节点查询发货流向的任务
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId, endSiteId);
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findBySiteAndStatus(detailQ, null);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(vehicleDetailList)) {
            String msg = String.format("该包裹没有路由下一站[%s]的发货任务！", endSiteId);
            result.hintMessage(msg);
            return null;
        }
        Set<String> sendVehicleBizSet = new HashSet<>();
        for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
            sendVehicleBizSet.add(entity.getSendVehicleBizId());
        }

        return new ArrayList<>(sendVehicleBizSet);
    }

    /**
     * 按照滑道笼车号查询，必须查当前场地
     * @param queryTaskSendDto
     * @return
     */
    private List<Long> getSiteFlowByCrossCodeTableTrolley(QueryTaskSendDto queryTaskSendDto) {
        final String methodDesc = "JyWarehouseSendVehicleServiceImpl.getSiteFlowByCrossCodeTableTrolley根据滑道笼车号查询流向：";
        String[] keyword = queryTaskSendDto.getKeyword().split(Constants.SEPARATOR_HYPHEN);

        TableTrolleyQuery query = new TableTrolleyQuery();
        query.setDmsId(queryTaskSendDto.getStartSiteId().intValue());
        query.setCrossCode(keyword[0]);
        query.setTabletrolleyCode(keyword[1]);
        JdResult<TableTrolleyJsfResp> jsfRes = sortCrossJsfManager.querySiteFlowByCrossCodeTableTrolley(query);

        if(!jsfRes.isSucceed()) {
            log.error("{}服务异常，参数={}，异常返回={}", methodDesc, JsonHelper.toJson(queryTaskSendDto), JsonHelper.toJson(jsfRes));
            throw new JyBizException("根据滑道笼车号查询流向异常");
        }
        if(Objects.isNull(jsfRes.getData()) || CollectionUtils.isEmpty(jsfRes.getData().getTableTrolleyDtoJsfList())) {
            if(log.isInfoEnabled()) {
                log.info("{}查询为空，参数={}，返回={}", methodDesc, JsonHelper.toJson(queryTaskSendDto), JsonHelper.toJson(jsfRes));
            }
            return null;
        }
        List<Long> res = new ArrayList<>();
        for(TableTrolleyJsfDto dto : jsfRes.getData().getTableTrolleyDtoJsfList()) {
            res.add(dto.getEndSiteId().longValue());
        }
        return res;
    }

    /**
     * 根据流向获取派车任务Id list
     * 接货仓发货岗根据滑道笼车号可能查出多个流向，按照流向list查避免查询结果过多，服务端风险规避，limit数量限制   按关键字查询此处暂时不考虑分页
     * @param startSiteId
     * @param nextSiteIdList
     * @return
     */
    private List<String> getSendVehicleBizIdList(long startSiteId, List<Long> nextSiteIdList) {
        Integer pageSize = DB_LIMIT_DEFAULT;
        Integer defaultLimitSize = uccPropertyConfiguration.getJyWarehouseSendVehicleDetailQueryDefaultLimitSize();
        if(!Objects.isNull(defaultLimitSize) && defaultLimitSize > 0 && defaultLimitSize <= DB_LIMIT_DEFAULT_MAX) {
            pageSize = defaultLimitSize;
        }

        JyBizTaskSendVehicleDetailQueryEntity queryEntity = new JyBizTaskSendVehicleDetailQueryEntity();
        queryEntity.setStartSiteId(startSiteId);
        queryEntity.setEndSiteIdList(nextSiteIdList);
        queryEntity.setPageSize(pageSize);
        List<String> bizIds = taskSendVehicleDetailService.findBizIdsBySiteFlows(queryEntity);
        return bizIds;
    }

    @Override
    public InvokeResult<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq request) {
        InvokeResult<MixScanTaskDetailRes> res = new InvokeResult<>();
        res.success();
        JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
        entity.setGroupCode(request.getGroupCode());
        entity.setTemplateCode(request.getTemplateCode());
        entity.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        List<JyGroupSortCrossDetailEntity> entityList = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(entity);
        if(CollectionUtils.isEmpty(entityList)) {
            res.setMessage("查询为空");
            return res;
        }
        List<MixScanTaskDetailDto> sendVehicleDetailDtoList = new ArrayList<>();
        entityList.forEach(en -> {
            MixScanTaskDetailDto dto = new MixScanTaskDetailDto();
            dto.setStartSiteId(en.getStartSiteId());
            dto.setStartSiteName(en.getStartSiteName());
            dto.setCrossCode(en.getCrossCode());
            dto.setTabletrolleyCode(en.getTabletrolleyCode());
            dto.setTemplateName(en.getTemplateName());
            dto.setTemplateCode(en.getTemplateCode());
            dto.setEndSiteId(en.getEndSiteId());
            dto.setEndSiteName(en.getEndSiteName());
            dto.setSendVehicleDetailBizId(en.getSendVehicleDetailBizId());
            dto.setFocus(en.getFocus());
            sendVehicleDetailDtoList.add(dto);
        });
        MixScanTaskDetailRes resData = new MixScanTaskDetailRes();
        resData.setMixScanTaskDetailDtoList(sendVehicleDetailDtoList);
        res.setData(resData);
        return res;
    }

    @Override
    public InvokeResult<AppendSendVehicleTaskQueryRes> fetchToSendAndSendingTaskPage(AppendSendVehicleTaskQueryReq request) {
        InvokeResult<AppendSendVehicleTaskQueryRes> result = new InvokeResult<>();
        try {
            QueryTaskSendDto queryTaskSendDto = new QueryTaskSendDto();
            queryTaskSendDto.setKeyword(request.getKeyword());
            queryTaskSendDto.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
            List<String> sendVehicleBizList = resolveSearchKeyword(result, queryTaskSendDto);
            if (!result.codeSuccess()) {
                return result;
            }
            List<JyBizTaskSendVehicleEntity> vehiclePageList = getToSendAndSendingSendVehiclePage(request, sendVehicleBizList);

            if (CollectionUtils.isEmpty(vehiclePageList)) {
                result.setMessage("查询数据为空");
                return result;
            }
            AppendSendVehicleTaskQueryRes resData = new AppendSendVehicleTaskQueryRes();
            result.setData(resData);

            List<SendVehicleDto> sendVehicleDtoList = new ArrayList<>();
            //K-流向 V-滑道笼车号  map存储滑道笼车号减少不同派车任务中存在相同流向时的资源消耗
            HashMap<Long, String> crossTableTrolleyMap = new HashMap<>();
            vehiclePageList.forEach(entity -> {
                SendVehicleDto dto = new SendVehicleDto();
                dto.setSendVehicleBizId(entity.getBizId());
                dto.setVehicleStatus(entity.getVehicleStatus());
                //获取车牌号
                dto.setVehicleNumber(this.getVehicleNumber(entity));
                //获取明细
                dto.setSendVehicleDetailDtoList(this.getSendVehicleDetailDto(entity, request, crossTableTrolleyMap));
                sendVehicleDtoList.add(dto);
            });

            result.setData(resData);
        } catch (Exception e) {
            log.error("查询发车任务异常. {}", JsonHelper.toJson(request), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private List<SendVehicleDetailDto> getSendVehicleDetailDto(JyBizTaskSendVehicleEntity entity,
                                                               AppendSendVehicleTaskQueryReq request,
                                                               HashMap<Long, String> crossTableTrolleyMap) {
        // 根据路由下一节点查询发货流向的任务
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(entity.getStartSiteId(), entity.getBizId());

        List<Integer> vehicleStatuses = Arrays.asList(JyBizTaskSendStatusEnum.TO_SEND.getCode(), JyBizTaskSendStatusEnum.SENDING.getCode());

        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findBySiteAndStatus(detailQ, vehicleStatuses);
        List<SendVehicleDetailDto> res = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(vehicleDetailList)) {
            vehicleDetailList.forEach(detailEntity -> {
                SendVehicleDetailDto dto = new SendVehicleDetailDto();
                dto.setBizId(entity.getBizId());
                dto.setSendDetailBizId(detailEntity.getSendVehicleBizId());
                dto.setEndSiteId(detailEntity.getEndSiteId());
                dto.setEndSiteName(detailEntity.getEndSiteName());
                dto.setItemStatus(detailEntity.getVehicleStatus());
                dto.setItemStatusDesc(JyBizTaskSendStatusEnum.getNameByCode(detailEntity.getVehicleStatus()));
                //获取滑道笼车号
                dto.setCrossTableTrolley(this.getCrossTableTrolley(request.getCurrentOperate().getSiteCode(),
                        detailEntity.getEndSiteId(), crossTableTrolleyMap));
                //是否已添加到混扫任务中
                dto.setMixScanTaskProcess(this.isMixScanProcess(detailEntity, request));
                res.add(dto);
            });
        }
        return res;
    }

    /**
     * 获取滑道笼车号
     * @param startSiteId
     * @param endSiteId
     * @param crossTableTrolleyMap  调用方必传对象，不允许为空
     * @return
     */
    private String getCrossTableTrolley(Integer startSiteId, Long endSiteId, HashMap<Long, String> crossTableTrolleyMap) {
        //滑道笼车号
        if(StringUtils.isNotBlank(crossTableTrolleyMap.get(endSiteId))) {
            return crossTableTrolleyMap.get(endSiteId);
        }else {
            String crossTableTrolley = fetchCrossTableTrolley(startSiteId, endSiteId.intValue());
            if(StringUtils.isNotBlank(crossTableTrolley)) {
                crossTableTrolleyMap.put(endSiteId, crossTableTrolley);
                return crossTableTrolley;
            }else {
                return unknownCrossTableTrolley(startSiteId, endSiteId.intValue());
            }
        }
    }

    /**
     * 是否混扫任务进行中
     * @param entity
     * @param request
     * @return
     */
    private Boolean isMixScanProcess(JyBizTaskSendVehicleDetailEntity entity, AppendSendVehicleTaskQueryReq request) {
        JyGroupSortCrossDetailEntity jyGroupSortCrossDetailEntity = new JyGroupSortCrossDetailEntity();
        jyGroupSortCrossDetailEntity.setGroupCode(request.getGroupCode());
        jyGroupSortCrossDetailEntity.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        jyGroupSortCrossDetailEntity.setTemplateCode(request.getMixScanTaskCode());
        jyGroupSortCrossDetailEntity.setSendVehicleDetailBizId(entity.getBizId());
        return jyGroupSortCrossDetailService.isMixScanProcess(jyGroupSortCrossDetailEntity);

    }

    /**
     * 获取派车任务车牌号
     * @param entity
     * @return
     */
    private String getVehicleNumber(JyBizTaskSendVehicleEntity entity){
        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(entity.getTransWorkCode());
        return transWorkBillDto == null ? StringUtils.EMPTY : transWorkBillDto.getVehicleNumber();
    }

    private List<JyBizTaskSendVehicleEntity> getToSendAndSendingSendVehiclePage(AppendSendVehicleTaskQueryReq request, List<String> sendVehicleBizList){
        JyBizTaskSendVehicleEntity queryEntity = new JyBizTaskSendVehicleEntity();
        queryEntity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());

        List<Integer> vehicleStatuses = Arrays.asList(JyBizTaskSendStatusEnum.TO_SEND.getCode(),
                JyBizTaskSendStatusEnum.SENDING.getCode());

         return taskSendVehicleService.querySendTaskOfPage(
                queryEntity, sendVehicleBizList, null, request.getPageNo(), request.getPageSize(), vehicleStatuses);
    }


    @Override
    public JdVerifyResponse<SendScanRes> scan(SendScanReq request, JdVerifyResponse<SendScanRes> response) {
        //
        warehouseSendScanBeforeHandler(request, response);
        if(!response.codeSuccess()) {
            return response;
        }
        JdVerifyResponse<SendScanResponse> scanRes = super.sendScan(request);
        response.setCode(scanRes.getCode());
        response.setMessage(scanRes.getMessage());
        if(!Objects.isNull(scanRes.getData())) {
            SendScanRes resData = new SendScanRes();
            BeanUtils.copyProperties(scanRes.getData(), resData);
            warehouseSendScanResponseHandler(resData);
            response.setData(resData);
        }
        return response;
    }

    /**
     * 接货仓发货岗发货前逻辑
     * @param request
     * @param response
     */
    private void warehouseSendScanBeforeHandler(SendScanReq request, JdVerifyResponse<SendScanRes> response) {
        //根据设备编码

    }

    /**
     * 接货仓发货岗扫描结果集逻辑处理
     * @param resData
     */
    private void warehouseSendScanResponseHandler(SendScanRes resData) {

    }
}
