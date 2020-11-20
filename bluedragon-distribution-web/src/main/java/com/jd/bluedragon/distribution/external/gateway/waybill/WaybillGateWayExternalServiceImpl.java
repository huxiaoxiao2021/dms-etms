package com.jd.bluedragon.distribution.external.gateway.waybill;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.external.gateway.base.GateWayBaseResponse;
import com.jd.bluedragon.external.gateway.dto.request.ThirdBoxCodeMessageVO;
import com.jd.bluedragon.external.gateway.dto.request.WaybillSyncRequest;
import com.jd.bluedragon.external.gateway.waybill.WaybillGateWayExternalService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

import static com.jd.bluedragon.Constants.THIRD_ENET_BOX_WAYBILL_PREFIX;

/**
 * 运单相关 发布物流网关
 * @author : xumigen
 * @date : 2020/1/2
 */
public class WaybillGateWayExternalServiceImpl implements WaybillGateWayExternalService {
    private final Logger logger = LoggerFactory.getLogger(WaybillGateWayExternalServiceImpl.class);

    @Autowired
    private ThirdBoxDetailService thirdBoxDetailService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private TaskService taskService;

    @Autowired
    @Qualifier(value = "thirdBoxWeightProducer")
    private DefaultJMQProducer thirdBoxWeightDealProducer;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    private static final Integer OPERATION_SORTING = 1;//集包
    private static final Integer OPERATION_CANCEL = 2;//取消集包
    private static final Integer BOX_MAX_PACKAGE = 20000;//经济网集包上限
    private static final Integer OPERATOR_ID= -1;//经济网操作人回传全称跟踪默认ID：-1

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillGateWayExternalServiceImpl.syncWaybillCodeAndBoxCode", jAppName = Constants.UMP_APP_NAME_DMSWEB , mState = {JProEnum.TP, JProEnum.FunctionError})
    public GateWayBaseResponse<Void> syncWaybillCodeAndBoxCode(WaybillSyncRequest request,String pin) {
        logger.info("同步运单与箱号信息waybillCode[{}]boxCode[{}]",request.getWaybillCode(),request.getBoxCode());
        GateWayBaseResponse<Void> response = null;
        //参数校验
        response = coreParamCheck(request);
        if(!GateWayBaseResponse.CODE_SUCCESS.equals(response.getResultCode())){
            return response;
        }
        //获取基础信息
        BaseStaffSiteOrgDto startSite = null;
        Box box = null;
        try{
            //获取始发地
            startSite = baseMajorManager.getBaseSiteByDmsCode(request.getStartSiteCode());
            if(startSite == null || startSite.getSiteCode() == null){
                response.toConfirm(GateWayBaseResponse.MESSAGE_START_SITE_CONFIRM);
                return response;
            }
            //校验始发为经济网站点
            if(!Constants.THIRD_ENET_SITE_TYPE.equals(startSite.getSiteType())){
                response.toConfirm(GateWayBaseResponse.MESSAGE_START_SITE_TYPE_CONFIRM);
                return response;
            }
            //查询箱号
            box = boxService.findBoxByCode(request.getBoxCode());
            if(box == null || !request.getBoxCode().equals(box.getCode())){
                response.toConfirm(GateWayBaseResponse.MESSAGE_BOX_CONFIRM);
                return response;
            }
        }catch (Exception e){
            logger.error("经济网查询始发地或箱号异常：{}-{}", request.getStartSiteCode(), request.getBoxCode(), e);
            response.toFail(GateWayBaseResponse.MESSAGE_FAIL);
            return response;
        }
        //业务操作
        if(OPERATION_SORTING.equals(request.getOperationType())){
            //处理箱号明细：如果箱号已经操作了称重，则需要将明细也进行分拣内部称重，不回传给运单
            thirdBoxWeightDealProducer.sendOnFailPersistent(request.getWaybillCode(),JsonHelper.toJson(request));
            return sorting(request, startSite, box);
        }else if(OPERATION_CANCEL.equals(request.getOperationType())){
            return cancelSorting(request, startSite.getSiteCode(), box);
        }else{
            response.toError(GateWayBaseResponse.MESSAGE_OPERATION_TYPE_ERROR);
            return response;
        }
    }


    /**
     * 核心参数校验
     * @param request 请求体
     * @return 结果
     */
    private GateWayBaseResponse<Void> coreParamCheck(WaybillSyncRequest request){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();
        if(!Constants.TENANT_CODE_ECONOMIC.equals(request.getTenantCode())){
            response.toError("请传入正确的租户");
            return response;
        }
        if(StringUtils.isBlank(request.getStartSiteCode())){
            response.toError(GateWayBaseResponse.MESSAGE_START_SITE_ERROR);
            return response;
        }
        if(StringUtils.isBlank(request.getBoxCode())){
            response.toError(GateWayBaseResponse.MESSAGE_BOX_ERROR);
            return response;
        }
        if(StringUtils.isBlank(request.getPackageCode())){
            response.toError(GateWayBaseResponse.MESSAGE_PACKAGECODE_ERROR);
            return response;
        }
        if(StringUtils.isBlank(request.getEndSiteCode()) || StringUtils.isBlank(request.getOperatorId())
                || StringUtils.isBlank(request.getOperatorName()) || StringUtils.isBlank(request.getOperatorUnitName())
                || StringUtils.isBlank(request.getWaybillCode()) || request.getOperatorTime() == null ){
            response.toError("endSiteCode,operatorId,operatorName,operatorUnitName,waybillCode,operatorTime 都不能为空");
            return response;
        }
        return response;
    }


    private GateWayBaseResponse<Void> sorting(WaybillSyncRequest request, BaseStaffSiteOrgDto startSite, Box box){
        CallerInfo info = Profiler.registerInfo("DMSWORKER.WaybillGateWayExternalServiceImpl.sorting", false, true);
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();
        String redisKey = THIRD_ENET_BOX_WAYBILL_PREFIX.concat(request.getBoxCode()).concat(Constants.SEPARATOR_HYPHEN)
                .concat(request.getOperationType().toString()).concat(Constants.SEPARATOR_HYPHEN)
                .concat(request.getPackageCode());
        try {
            if(jimdbCacheService.exists(redisKey)){
                if(logger.isInfoEnabled()){
                    logger.info("经济网运单明细回传重复：{}", JsonHelper.toJson(request));
                }
                response.setMessage("运单明细回传重复");
                return response;
            }
            //集包校验
            response = sortingCheck(startSite, box);
            if(!GateWayBaseResponse.CODE_SUCCESS.equals(response.getResultCode())){
                return response;
            }
            //获取目的地
            BaseStaffSiteOrgDto endSite = baseMajorManager.getBaseSiteByDmsCode(request.getEndSiteCode());
            if(endSite == null || endSite.getSiteCode() == null){
                response.toConfirm(GateWayBaseResponse.MESSAGE_END_SITE_CONFIRM);
                return response;
            }
            //集包
            ThirdBoxDetail detail = convertRequest(request, startSite.getSiteCode(), endSite.getSiteCode());
            boolean result = thirdBoxDetailService.sorting(detail);
            if(!result){
                response.toFail(GateWayBaseResponse.MESSAGE_FAIL);
            }else {
                jimdbCacheService.setEx(redisKey, request.getPackageCode(), DateHelper.ONE_DAY_SECONDS);
                addSortingAdditionalTask(detail, startSite, endSite);
            }
        }catch (Exception e){
            logger.error("经济网集包异常：{}", JsonHelper.toJson(request), e);
            response.toFail(GateWayBaseResponse.MESSAGE_FAIL);
            Profiler.functionError(info);
            jimdbCacheService.del(redisKey);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return response;
    }


    /**
     * 添加回传分拣的运单状态
     * @param detail 明细
     * @param createSite 始发
     * @param receiveSite 目的
     */
    public void addSortingAdditionalTask(ThirdBoxDetail detail, BaseStaffSiteOrgDto createSite, BaseStaffSiteOrgDto receiveSite) {
        CallerInfo info = Profiler.registerInfo("DMSWORKER.WaybillGateWayExternalServiceImpl.addSortingAdditionalTask", false, true);
        try{
            WaybillStatus waybillStatus = this.parseWaybillStatus(detail, createSite, receiveSite);
            String jsonStr = JsonHelper.toJson(waybillStatus);
            Task task = new Task();
            task.setBody(jsonStr);
            task.setFingerprint(Md5Helper.encode(createSite.getSiteCode()+ "_" + receiveSite.getSiteCode() + "_10_"
                    + detail.getWaybillCode() + "_" + detail.getPackageCode() + "_" + System.currentTimeMillis()));
            task.setBoxCode(detail.getBoxCode());
            task.setCreateSiteCode(createSite.getSiteCode());
            task.setCreateTime(detail.getOperatorTime());
            task.setKeyword1(detail.getWaybillCode());
            task.setKeyword2(detail.getPackageCode());
            task.setReceiveSiteCode(receiveSite.getSiteCode());
            task.setType(WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_SORTING);
            task.setTableName(Task.TABLE_NAME_WAYBILL);
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setOwnSign(BusinessHelper.getOwnSign());
            this.taskService.add(task);
        }catch (Exception e){
            logger.error("经济网回传集包全称跟踪异常：{}", JsonHelper.toJson(detail), e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }

    private WaybillStatus parseWaybillStatus(ThirdBoxDetail detail, BaseStaffSiteOrgDto createSite,
                                             BaseStaffSiteOrgDto receiveSite) {

        WaybillStatus waybillStatus = new WaybillStatus();

        waybillStatus.setWaybillCode(detail.getWaybillCode());
        waybillStatus.setPackageCode(detail.getPackageCode());
        waybillStatus.setBoxCode(detail.getBoxCode());

        waybillStatus.setOrgId(createSite.getOrgId());
        waybillStatus.setOrgName(createSite.getOrgName());

        waybillStatus.setCreateSiteCode(createSite.getSiteCode());
        waybillStatus.setCreateSiteName(createSite.getSiteName());
        waybillStatus.setCreateSiteType(createSite.getSiteType());

        waybillStatus.setReceiveSiteCode(receiveSite.getSiteCode());
        waybillStatus.setReceiveSiteName(receiveSite.getSiteName());
        waybillStatus.setReceiveSiteType(receiveSite.getSiteType());

        waybillStatus.setOperatorId(OPERATOR_ID);
        waybillStatus.setOperator(detail.getOperatorName());
        waybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_SITE_SORTING);
        waybillStatus.setOperateTime(detail.getOperatorTime());
        return waybillStatus;
    }

    /**
     * 经济网集包校验
     * @param site 始发站点
     * @param box 箱子
     * @return 结果
     */
    private GateWayBaseResponse<Void> sortingCheck(BaseStaffSiteOrgDto site, Box box){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();

        //校验箱子是否已发货
        if(Box.BOX_STATUS_SEND.equals(box.getStatus())){
            response.toFail(GateWayBaseResponse.MESSAGE_BOX_SEND_FAIL);
            return response;
        }
        //校验箱子始发地是否和集包始发地一致
        if(!site.getSiteCode().equals(box.getCreateSiteCode())){
            response.toError(GateWayBaseResponse.MESSAGE_BOX_SITE_ERROR);
            return response;
        }
        //校验箱子集包数量是否达到上限
        if(box.getPackageNum() > BOX_MAX_PACKAGE){
            response.toFail(GateWayBaseResponse.MESSAGE_BOX_MAX_FAIL);
            return response;
        }

        return response;
    }

    /**
     * 转换请求体为实体对象
     * @param request 请求体
     * @param startSiteId 始发站点ID
     * @param endSiteId 目的站点ID
     * @return 实体
     */
    private ThirdBoxDetail convertRequest(WaybillSyncRequest request, Integer startSiteId, Integer endSiteId){
        ThirdBoxDetail detail = new ThirdBoxDetail();
        detail.setTenantCode(request.getTenantCode());
        detail.setStartSiteId(startSiteId);
        detail.setStartSiteCode(request.getStartSiteCode());
        detail.setEndSiteId(endSiteId);
        detail.setEndSiteCode(request.getEndSiteCode());
        detail.setOperatorId(request.getOperatorId());
        detail.setOperatorName(request.getOperatorName());
        detail.setOperatorUnitName(request.getOperatorUnitName());
        detail.setOperatorTime(request.getOperatorTime());
        detail.setBoxCode(request.getBoxCode());
        detail.setWaybillCode(request.getWaybillCode());
        detail.setPackageCode(request.getPackageCode());

        return detail;
    }

    /**
     * 取消集包
     * @param request 请求体
     * @param startSiteId 始发站点ID
     * @param box 箱子
     * @return 返回值
     */
    private GateWayBaseResponse<Void> cancelSorting(WaybillSyncRequest request, Integer startSiteId, Box box){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();
        CallerInfo info = Profiler.registerInfo("DMSWEB.WaybillGateWayExternalServiceImpl.cancelSorting", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            //取消集包校验
            response = cancelSortingCheck(box);
            if(!GateWayBaseResponse.CODE_SUCCESS.equals(response.getResultCode())){
                return response;
            }
            //取消集包
            ThirdBoxDetail detail = convertRequest(request, startSiteId, null);
            boolean result = thirdBoxDetailService.cancel(detail);
            if(!result){
                response.toConfirm(GateWayBaseResponse.MESSAGE_BOX_PACKAGE_CONFIRM);
            }else{
                sendSortingCancelWaybillTrace(request, startSiteId);
            }
        }catch (Exception e){
            logger.error("经济网取消集包异常：{}", JsonHelper.toJson(request), e);
            response.toFail(GateWayBaseResponse.MESSAGE_FAIL);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }

        return response;
    }

    /**
     * 发送取消全称跟踪
     *
     */
    private void sendSortingCancelWaybillTrace(WaybillSyncRequest request, Integer createSiteCode) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.WaybillGateWayExternalServiceImpl.sendSortingCancelWaybillTrace", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            String boxCode = request.getBoxCode();
            String packageCode = request.getPackageCode();

            WaybillStatus waybillStatus = new WaybillStatus();
            //设置站点相关属性
            waybillStatus.setPackageCode(packageCode);
            waybillStatus.setWaybillCode(request.getWaybillCode());
            waybillStatus.setBoxCode(boxCode);
            waybillStatus.setCreateSiteCode(createSiteCode);
            waybillStatus.setOperatorId(OPERATOR_ID);
            waybillStatus.setOperator(request.getOperatorName());
            waybillStatus.setOperateTime(request.getOperatorTime() == null ? new Date() : request.getOperatorTime());
            waybillStatus.setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_SITE_CANCEL_SORTING);
            waybillStatus.setRemark("站点取消装箱，箱号：" + boxCode);

            Task task = new Task();
            task.setTableName(Task.TABLE_NAME_POP);
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setKeyword1(packageCode);
            task.setKeyword2(WaybillStatus.WAYBILL_STATUS_CODE_SITE_CANCEL_SORTING.toString());
            task.setCreateSiteCode(createSiteCode);
            task.setBody(JsonHelper.toJson(waybillStatus));
            task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
            task.setOwnSign(BusinessHelper.getOwnSign());

            // 添加到task表
            taskService.add(task);

        } catch (Exception e) {
            Profiler.functionError(info);
            logger.error("经济网取消分拣发送全称跟踪失败:{}",JsonHelper.toJson(request), e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 取消集包校验：箱子是否已发货
     * @param box 箱子
     * @return 返回值
     */
    private GateWayBaseResponse<Void> cancelSortingCheck(Box box){
        GateWayBaseResponse<Void> response = new GateWayBaseResponse<Void>();
        //校验箱号状态
        if(Box.BOX_STATUS_SEND.equals(box.getStatus())){
            response.toFail(GateWayBaseResponse.MESSAGE_BOX_SEND_FAIL);
            return response;
        }
        return response;
    }
}
