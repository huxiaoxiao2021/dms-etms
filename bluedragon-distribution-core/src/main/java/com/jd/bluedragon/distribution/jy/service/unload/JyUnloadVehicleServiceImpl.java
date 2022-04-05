package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadVehicleRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanDetail;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionJsfService;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskUnloadVehicleDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName UnloadVehicleServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
@Service
public class JyUnloadVehicleServiceImpl implements IJyUnloadVehicleService {

    private static final Logger log = LoggerFactory.getLogger(JyUnloadVehicleServiceImpl.class);

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private JyUnloadDao jyUnloadDao;

    @Autowired
    private JyBizTaskUnloadVehicleDao unloadVehicleDao;

    @Autowired
    private JyUnloadAggsDao unloadAggsDao;

    @Qualifier("inspectionJsfServiceProvider")
    @Autowired
    private InspectionJsfService inspectionService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private DeliveryService deliveryService;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.unloadScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Integer> unloadScan(UnloadScanRequest request) {

        if (log.isInfoEnabled()) {
            log.info("开始卸车扫描. {}", JsonHelper.toJson(request));
        }

        InvokeResult<Integer> result = new InvokeResult<>();
        if (checkBeforeScan(result, request)) {
            return result;
        }

        JyBizTaskUnloadVehicleEntity taskUnloadVehicle = unloadVehicleDao.findByBizId(request.getBizId());
        if (taskUnloadVehicle == null) {
            result.hintMessage("卸车任务不存在，请刷新卸车任务列表后再扫描！");
            return result;
        }

        JyUnloadEntity dbEntity = createUnloadEntity(request, taskUnloadVehicle);
        try {
            // 保存扫描记录，发运单全程跟踪
            scanCoreLogic(request, dbEntity);

            // 统计本次扫描的包裹数
            calculateScanCount(result, request);

            // TODO 统计卸车任务扫描进度
            recordUnloadProgress(result, request);
        }
        // TODO catch specific exception
//        catch () {
//
//        }
        catch (Exception ex) {
            log.error("卸车扫描失败. {}", JsonHelper.toJson(request), ex);
            result.error("服务器异常，卸车扫描失败，请咚咚联系分拣小秘！");
            return result;
        }

        return result;
    }

    private void recordUnloadProgress(InvokeResult<Integer> result, UnloadScanRequest request) {
        if (NumberHelper.gt0(result.getData())) {
            String pdaOpeCacheKey = String.format(CacheKeyConstants.JY_UNLOAD_PDA_AGG_KEY, request.getBizId());
            redisClientOfJy.hIncrBy(pdaOpeCacheKey, "totalScannedPackageCount", result.getData());
            redisClientOfJy.expire(pdaOpeCacheKey, 10, TimeUnit.MINUTES);
        }
    }

    /**
     * 保存扫描记录，发运单全程跟踪
     * @param request
     * @param dbEntity
     */
    private void scanCoreLogic(UnloadScanRequest request, JyUnloadEntity dbEntity) throws InterruptedException {
        boolean firstScanFromTask = judgeBarCodeIsFirstScanFromTask(request);
        if (jyUnloadDao.insert(dbEntity) > 0) {

            // 插入验货或收货任务，发运单全程跟踪
            com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Boolean> taskResult = addInspectionTask(request, dbEntity);
            if (!taskResult.codeSuccess()) {
                Profiler.businessAlarm("dms.web.IJyUnloadVehicleService.unloadScan.addInspectionTask", "卸车扫描插入验货任务失败，将重试");
                log.warn("卸车扫描插入验货任务失败，将重试. {}，{}", JsonHelper.toJson(request), JsonHelper.toJson(taskResult));
                // 再次尝试插入任务
                Thread.sleep(100);
                addInspectionTask(request, dbEntity);
            }

            // 首次扫描变更任务状态，该小组绑定任务
            if (firstScanFromTask) {
                if (log.isInfoEnabled()) {
                    log.info("任务[{}]首次开始扫描，修改任务状态，锁定任务。{}", request.getBizId(), request.getBarCode());
                }

                // TODO liuduo8 创建任务，分配任务  request.getGroupCode()
            }
        }
    }

    /**
     * 扫描前校验
     * @param result
     * @param request
     * @return
     */
    private boolean checkBeforeScan(InvokeResult<Integer> result, UnloadScanRequest request) {
        String barCode = request.getBarCode();
        if (StringUtils.isBlank(barCode)) {
            result.parameterError("请扫描单号！");
            return false;
        }
        if (!BusinessHelper.isBoxcode(barCode)
                && !WaybillUtil.isWaybillCode(barCode)
                && !WaybillUtil.isPackageCode(barCode)) {
            result.parameterError("扫描单号非法！");
            return false;
        }

        if (StringUtils.isBlank(request.getBizId()) || StringUtils.isBlank(request.getSealCarCode())) {
            result.parameterError("请选择卸车任务！");
            return false;
        }

        int siteCode = request.getCurrentOperate().getSiteCode();
        if (!NumberHelper.gt0(siteCode)) {
            result.parameterError("缺少操作场地！");
            return false;
        }

        // 一个单号只能扫描一次
        if (checkBarScannedAlready(barCode, siteCode)) {
            result.hintMessage("单号已扫描！");
            return false;
        }

        if (StringUtils.isBlank(request.getGroupCode())) {
            result.hintMessage("扫描前请绑定小组！");
            return false;
        }

        return true;
    }

    /**
     * 统计本次扫描的数量
     * @param result
     * @param request
     */
    private void calculateScanCount(InvokeResult<Integer> result, UnloadScanRequest request) {
        String barCode = request.getBarCode();
        Integer scanCount = 0;
        if (WaybillUtil.isPackageCode(barCode)) {
            scanCount = 1;
        }
        else if (WaybillUtil.isWaybillCode(barCode)) {
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(barCode);
            if (waybill != null && NumberHelper.gt0(waybill.getGoodNumber())) {
                scanCount = waybill.getGoodNumber();
            }
        }
        else if (BusinessHelper.isBoxcode(barCode)) {
            CallerInfo inlineUmp = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleService.unloadScan.getCancelSendByBox");
            List<SendDetail> list = deliveryService.getCancelSendByBox(barCode);
            Profiler.registerInfoEnd(inlineUmp);
            if (CollectionUtils.isNotEmpty(list)) {
                scanCount = list.size();
            }
        }

        result.setData(scanCount);
    }

    /**
     * 插入验货或收货任务
     * @param request
     * @param dbEntity
     * @return
     */
    private com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Boolean> addInspectionTask(UnloadScanRequest request, JyUnloadEntity dbEntity) {
        InspectionVO inspectionVO = new InspectionVO();
        inspectionVO.setBarCodes(Collections.singletonList(request.getBarCode()));
        inspectionVO.setSiteCode(request.getCurrentOperate().getSiteCode());
        inspectionVO.setSiteName(request.getCurrentOperate().getSiteName());
        inspectionVO.setUserCode(request.getUser().getUserCode());
        inspectionVO.setUserName(request.getUser().getUserName());
        inspectionVO.setOperateTime(DateHelper.formatDateTime(dbEntity.getOperateTime()));
        return inspectionService.inspection(inspectionVO, InspectionBizSourceEnum.JY_UNLOAD_INSPECTION);
    }

    /**
     * 判断该单号是否是本次卸车任务扫描的第一单
     * @param request
     * @return
     */
    private boolean judgeBarCodeIsFirstScanFromTask(UnloadScanRequest request) {
        boolean firstScanned = false;
        String mutexKey = String.format(CacheKeyConstants.JY_UNLOAD_BIZ_KEY, request.getBizId());
        if (redisClientOfJy.set(mutexKey, "1", 6, TimeUnit.HOURS, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(request.getBizId());
            if (jyUnloadDao.findByBizId(queryDb) == null) {
                firstScanned = true;
            }
        }

        return firstScanned;
    }

    private JyUnloadEntity createUnloadEntity(UnloadScanRequest request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        Date operateTime = new Date();
        JyUnloadEntity unloadEntity = new JyUnloadEntity();
        unloadEntity.setBizId(request.getBizId());
        unloadEntity.setSealCarCode(request.getSealCarCode());
        unloadEntity.setVehicleNumber(taskUnloadVehicle.getVehicleNumber());
        unloadEntity.setStartSiteId(taskUnloadVehicle.getStartSiteId());
        unloadEntity.setEndSiteId(taskUnloadVehicle.getEndSiteId());
        unloadEntity.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
        unloadEntity.setBarCode(request.getBarCode());
        unloadEntity.setOperateTime(operateTime);
        unloadEntity.setCreateUserErp(request.getUser().getUserErp());
        unloadEntity.setCreateUserName(request.getUser().getUserName());
        unloadEntity.setUpdateUserErp(request.getUser().getUserErp());
        unloadEntity.setUpdateUserName(request.getUser().getUserName());
        unloadEntity.setCreateTime(operateTime);
        unloadEntity.setUpdateTime(operateTime);

        return unloadEntity;
    }

    /**
     * 校验卸车是否已经扫描过该单号
     * @param barCode 包裹、运单、箱号
     * @param siteCode 操作场地
     * @return true：扫描过
     */
    private boolean checkBarScannedAlready(String barCode, int siteCode) {
        boolean alreadyScanned = false;
        // 同场地一个单号只能扫描一次
        String mutexKey = String.format(CacheKeyConstants.JY_UNLOAD_SCAN_KEY, barCode, siteCode);
        if (redisClientOfJy.set(mutexKey, "1", 6, TimeUnit.HOURS, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(barCode, (long) siteCode);
            if (jyUnloadDao.queryByCodeAndSite(queryDb) != null) {
                alreadyScanned = true;
            }
        }
        else {
            alreadyScanned = true;
        }

        return alreadyScanned;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.unloadDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<UnloadScanDetail> unloadDetail(UnloadVehicleRequest request) {
        InvokeResult<UnloadScanDetail> result = new InvokeResult<>();



        return result;
    }

    private Boolean refreshUnloadAggCache(String bizId) {

        // TODO jy_unload_aggs flink增加版本，防止进度变小

        JyUnloadAggsEntity queryAgg = new JyUnloadAggsEntity(bizId);
        JyUnloadAggsEntity unloadAggsEntity = unloadAggsDao.aggByBiz(queryAgg);

        // 比较PDA扫描的进度和Flink计算出的进度大小，进度小于PDA则不更新
        String pdaOpeCacheKey = String.format(CacheKeyConstants.JY_UNLOAD_PDA_AGG_KEY, bizId);
        if (redisClientOfJy.exists(pdaOpeCacheKey)) {
            String redisVal = redisClientOfJy.hGet(pdaOpeCacheKey, "totalScannedPackageCount");
            if (StringUtils.isNotBlank(redisVal)) {
                Long scannedPackageCount = Long.valueOf(redisVal);
                if (unloadAggsEntity.getTotalScannedPackageCount() > scannedPackageCount) {
                    String unloadDetailCacheKey = String.format(CacheKeyConstants.JY_UNLOAD_DETAIL_KEY, bizId);
                    redisClientOfJy.hSet(unloadDetailCacheKey, "totalScannedPackageCount", String.valueOf(unloadAggsEntity.getTotalSealPackageCount()));
                }
            }
        }


        return false;
    }
}
