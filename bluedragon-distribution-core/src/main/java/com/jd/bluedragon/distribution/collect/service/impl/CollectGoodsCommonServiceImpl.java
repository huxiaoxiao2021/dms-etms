package com.jd.bluedragon.distribution.collect.service.impl;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.service.impl.WaybillCommonServiceImpl;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.*;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsAreaService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsCommonService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsDetailService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 集货相关功能业务逻辑实现
 */
@Service("collectGoodsCommonService")
public class CollectGoodsCommonServiceImpl implements CollectGoodsCommonService{


    private Logger logger = LoggerFactory.getLogger(CollectGoodsCommonServiceImpl.class);

    public static final int COLLECT_ALL_TIP_CODE = 201;

    public static final int COLLECT_NOT_TIP_CODE = 202;


    public static final String COLLECT_LOCK_BEGIN = "COLLECT_LOCK_";

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Autowired
    private CollectGoodsDetailService collectGoodsDetailService;

    @Autowired
    private CollectGoodsAreaService collectGoodsAreaService;

    @Autowired
    private CollectGoodsPlaceService collectGoodsPlaceService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected InspectionDao inspectionDao;

    /**
     * 集货作业
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    @JProfiler(jKey = "DMSWEB.CollectGoodsCommonServiceImpl.put", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<CollectGoodsDTO> put(CollectGoodsDTO req) {

        InvokeResult<CollectGoodsDTO> result = new InvokeResult<>();

        //校验数据是否正常
        if (com.jd.common.util.StringUtils.isBlank(req.getCollectGoodsAreaCode()) ||
                com.jd.common.util.StringUtils.isBlank(req.getOperateUserErp()) ||
                req.getOperateSiteCode() == null || com.jd.common.util.StringUtils.isBlank(req.getPackageCode())) {

            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage(InvokeResult.PARAM_ERROR);
            return result;
        }
        //校验包裹是否真实存在
        if(!waybillCommonService.checkPackExist(req.getPackageCode())){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("包裹不存在！");
            return result;
        }
        //一单一件直接返回
        if(WaybillUtil.getPackNumByPackCode(req.getPackageCode()) == 1){
            result.setCode(COLLECT_NOT_TIP_CODE);
            result.setMessage("一单一件无需操作集货功能！");
            //补验货
            inspection(req);
            return result;
        }
        //校验包裹是否已操作过
        if(collectGoodsDetailService.checkExist(req.getPackageCode(),null
                ,null,req.getOperateSiteCode())){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("此包裹已操作集货！");
            return result;
        }
        // 获取推荐货位和 更新状态 记录明细 加锁 集货区 防止并发出现推荐集货位已经溢出存储运单上限
        if(!lock(req)){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("此集货区繁忙，请稍后重试！");
            return result;
        }
        //获取推荐货位 并更新状态
        CollectGoodsPlace recommendPlace = collectGoodsPlaceService.recommendPlace(req.getPackageCode(),
                req.getCollectGoodsAreaCode(),req.getOperateSiteCode(),result);

        //无空闲储位则提示现场 不需要弹框 这种情况会很多容易弹框容易影响生产效率
        if(recommendPlace == null){
            result.setCode(COLLECT_NOT_TIP_CODE);
            result.setMessage("无空闲货位！");
            return result;
        }

        //记录集货明细
        CollectGoodsDetail collectGoodsDetail = convertToDetail(req,recommendPlace);
        //如果集齐 则 修改以前运单的集货数据 如果未集齐则直接记录
        if(result.getCode() ==  COLLECT_ALL_TIP_CODE ){
            CollectGoodsDetail cleanParam = new CollectGoodsDetail();
            cleanParam.setCreateSiteCode(collectGoodsDetail.getCreateSiteCode());
            cleanParam.setCollectGoodsAreaCode(collectGoodsDetail.getCollectGoodsAreaCode());
            cleanParam.setCollectGoodsPlaceCode(collectGoodsDetail.getCollectGoodsPlaceCode());
            cleanParam.setWaybillCode(collectGoodsDetail.getWaybillCode());
            if(!collectGoodsDetailService.clean(cleanParam)){
                throw new CollectException("集齐后,释放明细数据失败！");
            }
        }else{
            if(!collectGoodsDetailService.saveOrUpdate(collectGoodsDetail)){
                throw new CollectException("记录集货明细失败！");
            }
        }
        //释放
        unLock(req);

        //补验货
        inspection(req);

        //记录操作日志
        collectLog(req,collectGoodsDetail);

        //组装返回对象
        CollectGoodsDTO collectGoodsDTO = new CollectGoodsDTO();
        collectGoodsDTO.setCollectGoodsPlaceCode(recommendPlace.getCollectGoodsPlaceCode());
        if(StringUtils.isBlank(result.getMessage())){
            result.setMessage("当前运单共"+WaybillUtil.getPackNumByPackCode(req.getPackageCode())+"件，已扫描1件");
        }

        result.setData(collectGoodsDTO);
        return result;
    }

    /**
     * 集货释放
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    @JProfiler(jKey = "DMSWEB.CollectGoodsCommonServiceImpl.clean", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> clean(CollectGoodsDTO req) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        result.setData(Boolean.TRUE);
        CollectGoodsPlace param = new CollectGoodsPlace();
        if(StringUtils.isBlank(req.getCollectGoodsPlaceCode()) && StringUtils.isBlank(req.getCollectGoodsAreaCode())){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("未传入集货区或集货位编码");
            result.setData(Boolean.FALSE);
            return result;
        }
        if(req.getOperateSiteCode() == null || Integer.valueOf(0).equals(req.getOperateSiteCode())){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("未传入操作分拣中心编码");
            result.setData(Boolean.FALSE);
            return result;
        }
        //校验是否存在
        if(!collectGoodsDetailService.checkExist(req.getPackageCode(),req.getCollectGoodsAreaCode()
                ,req.getCollectGoodsPlaceCode(),req.getOperateSiteCode())){
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("无包裹存在货位无需释放");
            result.setData(Boolean.FALSE);
            return result;
        }
        param.setCollectGoodsPlaceCode(req.getCollectGoodsPlaceCode());
        param.setCollectGoodsAreaCode(req.getCollectGoodsAreaCode());
        param.setCreateSiteCode(req.getOperateSiteCode());
        //记录日志
        operateLogOfQuerySelf(req,"手动释放");

        //初始化参数
        param.setCollectGoodsPlaceStatus(Integer.valueOf(CollectGoodsPlaceStatusEnum.FREE_0.getCode()));
        //释放 集货区
        //释放集货位
        if(collectGoodsPlaceService.updateStatus(param)){
            //释放集货明细
            CollectGoodsDetail cleanParam = new CollectGoodsDetail();
            cleanParam.setCreateSiteCode(param.getCreateSiteCode());
            cleanParam.setCollectGoodsAreaCode(param.getCollectGoodsAreaCode());
            cleanParam.setCollectGoodsPlaceCode(param.getCollectGoodsPlaceCode());

            if(!collectGoodsDetailService.clean(cleanParam)){
                throw new CollectException("释放集货明细失败");
            }
        }else{
            throw new CollectException("释放集货位失败");
        }

        return result;

    }

    /**
     * 集货转移
     * 默认转移至异常集货位
     * @param req
     * @return
     */
    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
    @JProfiler(jKey = "DMSWEB.CollectGoodsCommonServiceImpl.transfer", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public InvokeResult<Boolean> transfer(CollectGoodsDTO req) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.setData(Boolean.TRUE);
        //是否将运单转移
        boolean isTransferWaybill = false;
        //校验是否存在
        CollectGoodsDetail param = new CollectGoodsDetail();
        param.setCollectGoodsPlaceCode(req.getCollectGoodsPlaceCode());
        param.setCollectGoodsAreaCode(req.getCollectGoodsAreaCode());
        if(StringUtils.isNotBlank(req.getPackageCode())){
            param.setWaybillCode(WaybillUtil.getWaybillCode(req.getPackageCode()));
        }
        param.setCreateSiteCode(req.getOperateSiteCode());
        List<CollectGoodsDetailCondition> scanWaybillList = collectGoodsDetailService.findScanWaybill(param);
        if(scanWaybillList == null || scanWaybillList.isEmpty()){
            result.setCode(InvokeResult.RESULT_INTERCEPT_CODE);
            result.setMessage("无数据可转移！");
            result.setData(Boolean.FALSE);
            return result;
        }
        String sourceCollectGoodsPlaceCode = req.getCollectGoodsPlaceCode();
        String targetCollectGoodsPlaceCode = req.getTargetCollectGoodsPlaceCode();
        String sourceCollectGoodsAreaCode = req.getCollectGoodsAreaCode();
        Integer targetCollectGoodsPlaceType = null;
        if(StringUtils.isNotBlank(req.getPackageCode())){
            //是否转移运单
            isTransferWaybill = true;
            //根据包裹获取源货位 //此时scanWaybillList肯定非空
            sourceCollectGoodsPlaceCode = scanWaybillList.get(0).getCollectGoodsPlaceCode();

        }

        //获取源货区
        CollectGoodsPlace sourceCollectGoodsPlace = collectGoodsPlaceService.findPlaceByCode(req.getOperateSiteCode(),sourceCollectGoodsPlaceCode);
        if(sourceCollectGoodsPlaceCode==null){
            throw new CollectException("未获取到源货位");
        }
        sourceCollectGoodsAreaCode = sourceCollectGoodsPlace.getCollectGoodsAreaCode();



        //默认获取 异常货位
        CollectGoodsPlace targetCollectGoodsPlace = collectGoodsPlaceService.findAbnormalPlace(req.getOperateSiteCode(),sourceCollectGoodsAreaCode);
        if(targetCollectGoodsPlace==null){
            throw new CollectException("未获取到目的货位");
        }
        targetCollectGoodsPlaceCode = targetCollectGoodsPlace.getCollectGoodsPlaceCode();
        targetCollectGoodsPlaceType = targetCollectGoodsPlace.getCollectGoodsPlaceType();


        //转移  集货明细
        collectGoodsDetailService.transfer(sourceCollectGoodsPlaceCode,targetCollectGoodsPlaceCode,targetCollectGoodsPlaceType,req.getOperateSiteCode(),req.getPackageCode());

        //还原 源货位状态
        // 需判断是不是所有包裹都已经转移走

        CollectGoodsPlace updatesourceCollectGoodsPlace = new CollectGoodsPlace();
        updatesourceCollectGoodsPlace.setCollectGoodsPlaceCode(sourceCollectGoodsPlaceCode);
        updatesourceCollectGoodsPlace.setCreateSiteCode(req.getOperateSiteCode());
        //未转移全部运单
        if(isTransferWaybill && (sourceCollectGoodsPlace.getScanWaybillNum() - 1) > 0){
            updatesourceCollectGoodsPlace.setCollectGoodsPlaceStatus(Integer.valueOf(CollectGoodsPlaceStatusEnum.NOT_FREE_1.getCode()));
            updatesourceCollectGoodsPlace.setScanWaybillNum(sourceCollectGoodsPlace.getScanWaybillNum() - 1);
        }else{
            updatesourceCollectGoodsPlace.setCollectGoodsPlaceStatus(Integer.valueOf(CollectGoodsPlaceStatusEnum.FREE_0.getCode()));
            updatesourceCollectGoodsPlace.setScanWaybillNum(0);

        }
        if(!collectGoodsPlaceService.updateStatus(updatesourceCollectGoodsPlace)){
            throw new CollectException("还原源货位状态失败");
        }

        //变更 目的货位状态
        CollectGoodsPlace updateTargetCollectGoodsPlace = new CollectGoodsPlace();
        updateTargetCollectGoodsPlace.setCollectGoodsPlaceCode(targetCollectGoodsPlaceCode);
        updateTargetCollectGoodsPlace.setCreateSiteCode(req.getOperateSiteCode());
        updateTargetCollectGoodsPlace.setCollectGoodsPlaceStatus(Integer.valueOf(CollectGoodsPlaceStatusEnum.NOT_FREE_1.getCode()));
        if(!collectGoodsPlaceService.updateStatus(updateTargetCollectGoodsPlace)){
            throw new CollectException("变更目的货位状态失败");
        }

        //记录日志
        operateLogOfQuerySelf(req,"异常转移，原货位"+sourceCollectGoodsPlaceCode+",新货位"+targetCollectGoodsPlaceCode);

        return result;
    }



    /**
     * 释放日志
     * @param req
     */
    private void operateLogOfQuerySelf(CollectGoodsDTO req,String remark){
        CollectGoodsDetailCondition param = new CollectGoodsDetailCondition();
        param.setCollectGoodsPlaceCode(req.getCollectGoodsPlaceCode());
        param.setCollectGoodsAreaCode(req.getCollectGoodsAreaCode());
        if(StringUtils.isNotBlank(req.getPackageCode())){
            param.setWaybillCode(WaybillUtil.getWaybillCode(req.getPackageCode()));
        }
        param.setCreateSiteCode(req.getOperateSiteCode());
        List<CollectGoodsDetail> list = collectGoodsDetailService.queryByCondition(param);
        for(CollectGoodsDetail c : list){
            this.operationLogService.add(parseOperationLog(c.getWaybillCode(),c.getPackageCode(),
                    c.getCreateSiteCode(),req.getOperateSiteName(),req.getOperateUserErp()+"|"+req.getOperateUserName()
                    ,new Date(Long.valueOf(req.getOperateTime())),remark));
        }
    }

    /**
     * 集货日志
     * @param req
     */
    private void collectLog(CollectGoodsDTO collectGoodsDTO,CollectGoodsDetail req){

        this.operationLogService.add(parseOperationLog(req.getWaybillCode(),req.getPackageCode(),
                req.getCreateSiteCode(),req.getCreateSiteName(),req.getCreateUser()
                    ,new Date(Long.valueOf(collectGoodsDTO.getOperateTime())),"集货作业，货位号"+req.getCollectGoodsPlaceCode()));

    }

    private OperationLog parseOperationLog(String waybillCode, String packCode, Integer siteCode , String siteName, String userErp , Date opeareTime , String remark) {
        OperationLog operationLog = new OperationLog();
        operationLog.setWaybillCode(waybillCode);
        operationLog.setPackageCode(packCode);
        operationLog.setCreateSiteCode(siteCode);
        operationLog.setCreateSiteName(siteName);
        operationLog.setCreateUser(userErp);
        operationLog.setCreateTime(new Date() );
        operationLog.setOperateTime(opeareTime);
        operationLog.setLogType(OperationLog.LOG_TYPE_COLLECT);
        operationLog.setRemark(remark);
        return operationLog;
    }

    private CollectGoodsDetail convertToDetail(CollectGoodsDTO collectGoodsDTO,CollectGoodsPlace collectGoodsPlace){
        CollectGoodsDetail collectGoodsDetail = new CollectGoodsDetail();
        collectGoodsDetail.setCreateSiteCode(collectGoodsDTO.getOperateSiteCode());
        collectGoodsDetail.setCreateSiteName(collectGoodsDTO.getOperateSiteName());
        collectGoodsDetail.setCreateUser(collectGoodsDTO.getOperateUserErp()+"|"+collectGoodsDTO.getOperateUserName());
        collectGoodsDetail.setPackageCode(collectGoodsDTO.getPackageCode());
        collectGoodsDetail.setCollectGoodsPlaceCode(collectGoodsPlace.getCollectGoodsPlaceCode());
        collectGoodsDetail.setCollectGoodsAreaCode(collectGoodsPlace.getCollectGoodsAreaCode());
        collectGoodsDetail.setCollectGoodsPlaceType(collectGoodsPlace.getCollectGoodsPlaceType());
        collectGoodsDetail.setWaybillCode(WaybillUtil.getWaybillCode(collectGoodsDTO.getPackageCode()));
        int packCount = waybillCommonService.getPackNum(WaybillUtil.getWaybillCode(collectGoodsDTO.getPackageCode())).getData();
        collectGoodsDetail.setPackageCount(packCount);


        return collectGoodsDetail;
    }

    private void inspection(CollectGoodsDTO req){

        Inspection inspectionQ=new Inspection();
        inspectionQ.setWaybillCode(WaybillUtil.getWaybillCode(req.getPackageCode()));
        inspectionQ.setPackageBarcode(req.getPackageCode());
        inspectionQ.setCreateSiteCode(req.getOperateSiteCode());
        inspectionQ.setYn(Integer.valueOf(1));
        if(inspectionDao.haveInspectionByPackageCode(inspectionQ)){
            //如果已经验过货  就不用补了
            return;
        }
        InspectionRequest inspection=new InspectionRequest();
        TaskRequest request=new TaskRequest();

        inspection.setUserCode(req.getOperateUserId());
        inspection.setUserName(req.getOperateUserName());
        inspection.setSiteCode(req.getOperateSiteCode());
        inspection.setSiteName(req.getOperateSiteName());

        //验货操作提前5秒
        inspection.setOperateTime(DateHelper.formatDateTime(new Date(req.getOperateTime())));
        inspection.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        inspection.setPackageBarOrWaybillCode(req.getPackageCode());
        request.setKeyword2(req.getPackageCode());
        request.setBusinessType(Constants.BUSSINESS_TYPE_POSITIVE);
        request.setKeyword1(req.getOperateUserErp());
        request.setType(Task.TASK_TYPE_INSPECTION);
        request.setOperateTime(inspection.getOperateTime());
        request.setSiteCode(req.getOperateSiteCode());
        request.setSiteName(req.getOperateSiteName());
        request.setUserCode(req.getOperateUserId());
        request.setUserName(req.getOperateUserName());
        //request.setBody();
        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(inspection)
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task=this.taskService.toTask(request, eachJson);

        this.taskService.add(task, true);

    }

    private boolean lock(CollectGoodsDTO req){
        try{
            String lockKey = COLLECT_LOCK_BEGIN+req.getOperateSiteCode()+"_"+req.getCollectGoodsAreaCode();
            if(!cacheService.setNx(lockKey,StringUtils.EMPTY,60, TimeUnit.SECONDS)){
                Thread.sleep(100);
                return cacheService.setNx(lockKey,StringUtils.EMPTY,60, TimeUnit.SECONDS);
            }
        }catch (Exception e){
            logger.error("集货lock异常"+JsonHelper.toJson(req),e);
        }
        return true;
    }

    private void unLock(CollectGoodsDTO req){
        try{
            String lockKey = COLLECT_LOCK_BEGIN+req.getOperateSiteCode()+"_"+req.getCollectGoodsAreaCode();
            cacheService.del(lockKey);
        }catch (Exception e){
            logger.error("集货unLock异常"+JsonHelper.toJson(req),e);
        }

    }
}
