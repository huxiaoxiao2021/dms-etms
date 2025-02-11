package com.jd.bluedragon.distribution.middleend.sorting.service;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.middleend.sorting.domain.DmsCustomSite;
import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.shared.services.sorting.api.dto.Flow;
import com.jd.ql.shared.services.sorting.api.dto.SiteType;
import com.jd.ql.shared.services.sorting.api.dto.SortingDirection;
import com.jd.ql.shared.services.sorting.api.dto.SortingObject;
import com.jd.ql.shared.services.sorting.api.dto.SortingObjectStatus;
import com.jd.ql.shared.services.sorting.api.dto.SortingObjectType;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

public abstract class BaseSortingService {
    protected Logger log = LoggerFactory.getLogger(BaseSortingService.class);

    @Autowired
    @Qualifier("jimdbCacheService")
    protected CacheService cacheService;

    @Autowired
    protected BaseMajorManager baseMajorManager;

    @Autowired
    protected BoxService boxService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    @Qualifier("sortingService")
    protected SortingService dmsSortingService;

    @Resource
    private DmsConfigManager dmsConfigManager;

    @Autowired
    protected WaybillQueryManager waybillQueryManager;


    public static final int WAYBILL_SPLIT_NUM = 100;
    public static final String TASK_SORTING_FINGERPRINT_SORTING_5S = "TASK_SORTING_FP_5S_"; //5前缀
    public static final int TASK_1200_EX_TIME_5_S = 5;

    public static final Integer SORTING_STATUS_IS_LOSS_0 = 0;
    public static final Integer SORTING_STATUS_IS_LOSS_1 = 1;
    public static final Integer SORTING_STATUS_FEATURE_TYPE_2 = 2;


    /**
     * 分拣
     * @param sortingTask
     * @return
     */
    public boolean doSorting(Task sortingTask) {
        CallerInfo info = Profiler.registerInfo("DMSWORKER.BaseSortingService.doSorting", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        String fingerPrintKey = "";
        try {
            //验重
            fingerPrintKey = TASK_SORTING_FINGERPRINT_SORTING_5S + sortingTask.getCreateSiteCode() + "|" + sortingTask.getBoxCode() + "|" + sortingTask.getKeyword2();
            if (check(fingerPrintKey)) {
                //转换成extend对象
                SortingObjectExtend sorting = prepareSorting(sortingTask);
                if(log.isInfoEnabled()){
                    log.info("sorting任务转换成中台理货扩展对象:{}" , JSON.toJSONString(sorting));
                }

                //进行核心的分拣操作：1写sorting表；2发分拣的全称跟踪；3逆向的发送退货一百分和快退MQ
                if (coreSorting(sorting)) {
                    //核心分拣操作成功，则生成分拣成功处理任务用于处理分拣补验货/补发货/写操作日志等动作
                    doSortingSuccess(sorting);
                } else {
                    log.error("核心的分拣操作执行失败，扩展对象：{}" , JSON.toJSONString(sorting));
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("分拣操作异常.参数:{}" , JSON.toJSONString(sortingTask) , e);
            Profiler.functionError(info);
            return false;
        } finally {
            delCache(fingerPrintKey);
            Profiler.registerInfoEnd(info);
        }
        return true;
    }

    /**
     * 判断是否重复分拣, 5秒内如果同操作场地、同目的地、同扫描号码即可判断为重复操作。
     * 立刻置失败，转到下一次执行。只使用key存不存在做防重
     *
     * @param fingerPrintKey
     * @return
     */
    private boolean check(String fingerPrintKey) {
        try {
            Boolean isSuccess = cacheService.setNx(fingerPrintKey, "1", TASK_1200_EX_TIME_5_S, TimeUnit.SECONDS);
            if (!isSuccess) {//说明有重复任务
                this.log.warn("分拣任务重复：{}" , fingerPrintKey);
                return false;
            }
        } catch (Exception e) {
            this.log.error("获得分拣任务指纹失败:{}" , fingerPrintKey, e);
        }
        return true;
    }

    /**
     * 删除缓存
     *
     * @param fingerPrintKey
     */
    private void delCache(String fingerPrintKey) {
        cacheService.del(fingerPrintKey);
    }


    /**
     * sortingTask转换成SortingObjectExtend对象
     *
     * @param sortingTask
     * @return
     */
    protected SortingObjectExtend prepareSorting(Task sortingTask) {
        SortingObjectExtend extendObject = new SortingObjectExtend();

        //组装分拣原sorting对象
        Sorting dmsSorting = getDmsSorting(sortingTask);

        extendObject.setDmsSorting(dmsSorting);

        //处理始发分拣和目的分拣
        DmsCustomSite createSite = null;
        try {
            if(dmsSorting != null){
                createSite = baseMajorManager.getDmsCustomSiteBySiteId(dmsSorting.getCreateSiteCode());
            }
        } catch (Exception e) {
            this.log.error("AbstractSortingService.prepareSorting处理始发分拣异常.createSiteCode:{}" , dmsSorting.getCreateSiteCode(), e);
        }
        extendObject.setCreateSite(createSite);

        //目的分拣以箱号目的地为准
        if (dmsSorting != null && StringUtils.isNotBlank(dmsSorting.getBoxCode()) && BusinessUtil.isBoxcode(dmsSorting.getBoxCode())) {
            Box box = boxService.findBoxByCode(dmsSorting.getBoxCode());
            if (box != null) {
                dmsSorting.setReceiveSiteCode(box.getReceiveSiteCode());
            }
        }

        DmsCustomSite receiveSite = null;
        try {
            if(dmsSorting != null){
                receiveSite = baseMajorManager.getDmsCustomSiteBySiteId(dmsSorting.getReceiveSiteCode());
            }
        } catch (Exception e) {
            this.log.error("AbstractSortingService.prepareSorting处理目的分拣异常.receiveSiteCode:{}" , dmsSorting.getReceiveSiteCode(), e);
        }
        extendObject.setReceiveSite(receiveSite);

        //构建中台的理货对象
        buildMiddleEndSorting(extendObject);

        return extendObject;
    }

    /**
     * sortingTask转换成分拣Sorting对象
     *
     * @param sortingTask
     * @return
     */
    private Sorting getDmsSorting(Task sortingTask) {
        //原分拣任务传入的Body为数组，但实际数组内容都是一个
        String body = sortingTask.getBody().substring(1, sortingTask.getBody().length() - 1);
        SortingRequest request = JsonHelper.jsonToArray(body, SortingRequest.class);
        if (request != null) {
            Sorting dmsSorting = Sorting.toSorting(request);
            dmsSorting.setStatus(Sorting.STATUS_DONE);// 运单回传状态默认为1，以后可以去掉
            return dmsSorting;
        }
        return null;
    }

    /**
     * 构建中台的理货对象
     *
     * @param object
     */
    private void buildMiddleEndSorting(SortingObjectExtend object) {
        Sorting dmsSorting = object.getDmsSorting();
        SortingObject middleEndSorting = new SortingObject();
        object.setMiddleEndSorting(middleEndSorting);

        //IS_LOSS FEATURE_TYPE 合并
        if(null == dmsSorting.getFeatureType() && null == dmsSorting.getIsLoss()){
            middleEndSorting.setStatus(SortingObjectStatus.NORMAL);
        }else if (SORTING_STATUS_FEATURE_TYPE_2.equals(dmsSorting.getFeatureType())) {
            middleEndSorting.setStatus(SortingObjectStatus.TRIPARTITE_RETURN_SPARE_WAREHOUSE);
        } else if (SORTING_STATUS_IS_LOSS_1.equals(dmsSorting.getIsLoss())) {
            middleEndSorting.setStatus(SortingObjectStatus.LOST);
        } else if (SORTING_STATUS_IS_LOSS_0.equals(dmsSorting.getIsLoss())) {
            middleEndSorting.setStatus(SortingObjectStatus.NORMAL);
        }

        //分拣类型
        if (StringUtils.isBlank(dmsSorting.getPackageCode())) {
            middleEndSorting.setObjectType(SortingObjectType.WAYBILL);
            middleEndSorting.setObjectCode(dmsSorting.getWaybillCode());  //运单号/包裹号
        } else {
            middleEndSorting.setObjectType(SortingObjectType.PACKAGE);
            middleEndSorting.setObjectCode(dmsSorting.getPackageCode());  //运单号/包裹号
        }

        //分拣类型 正向/逆向/三方
        middleEndSorting.setSortingDirection(SortingDirection.getEunmByType(dmsSorting.getType()));

        //分拣流向
        Flow flow = new Flow();
        if (object.getCreateSite() != null) {
            flow.setFromSiteId(object.getCreateSite().getSiteId());
            flow.setFromSiteCode(object.getCreateSite().getSiteCode());
            flow.setFromSiteType(SiteType.getEunmByType(object.getCreateSite().getCustomSiteType()));
        }
        if (object.getReceiveSite() != null) {
            flow.setToSiteId(object.getReceiveSite().getSiteId());
            flow.setToSiteCode(object.getReceiveSite().getSiteCode());
            flow.setToSiteType(SiteType.getEunmByType(object.getReceiveSite().getCustomSiteType()));
        }
        middleEndSorting.setFlow(flow);

        middleEndSorting.setWaybillCode(WaybillUtil.getWaybillCode(middleEndSorting.getObjectCode())); //必填
        middleEndSorting.setBatchCode(dmsSorting.getBsendCode());   //理货批次号 BSEND_CODE

        dmsSortingService.fillSortingIfPickup(dmsSorting);
        middleEndSorting.setRelatedCode(dmsSorting.getPickupCode()); //取件单号

        middleEndSorting.setComment(dmsSorting.getSpareReason()); //SPARE_REASON
        middleEndSorting.setIsCancel(dmsSorting.getIsCancel() == 0 ? false : true);
    }


    /**
     * 分拣核心动作：1写sorting表；2发分拣的全称跟踪；3逆向的发送退货一百分和快退MQ
     *
     * @param sorting
     * @return
     */
    protected abstract boolean coreSorting(SortingObjectExtend sorting);

    /**
     * 分拣成功后操作
     * 1.b网补验货，不需要拆分任务，直接补
     * 2.其他操作生成操作成功任务异步处理
     *
     * @param sorting
     */
    public void doSortingSuccess(SortingObjectExtend sorting) {
        addSortingSuccessTask(sorting);
    }

    /**
     * 生成操作成功任务
     *
     * @param sorting
     */
    public void addSortingSuccessTask(SortingObjectExtend sorting) {
        String waybillCode = sorting.getDmsSorting().getWaybillCode();
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(true);
        wChoice.setQueryWaybillE(false);
        wChoice.setQueryWaybillM(false);

        Integer goodNumber = 0;
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, wChoice);
        if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null && baseEntity.getData().getWaybill().getGoodNumber() != null) {
            goodNumber = baseEntity.getData().getWaybill().getGoodNumber();
        }

        //每页容量
        int pageSize = dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize() == 0 ? WAYBILL_SPLIT_NUM : dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize();
        //1.按包裹理货
        if(SortingObjectType.PACKAGE.equals(sorting.getMiddleEndSorting().getObjectType())){
            sorting.setPackagePageIndex(0);
            sorting.setPackagePageSize(pageSize);
            taskService.add(sortingObjectExtend2Task(sorting));
            if(goodNumber == 0){
                log.warn("按包裹理货，运单没有包裹信息：{}", JsonHelper.toJson(sorting));
            }
        }else if (SortingObjectType.WAYBILL.equals(sorting.getMiddleEndSorting().getObjectType()) && goodNumber > 0) {
            //2.按运单理货且运单有包裹信息
            //计算总页数
            int pageCount = Double.valueOf(Math.floor(goodNumber / pageSize)).intValue();
            if (goodNumber % pageSize != 0) {
                pageCount++;
            }
            log.info("AbstractSortingService.addSortingSuccessTask将大运单任务进行拆分，总页数:{},每页容量:{}" ,pageCount, pageSize);
            for (int i = 0; i < pageCount; i++) {
                //发送拆分任务
                sorting.setPackagePageIndex(i + 1);
                sorting.setPackagePageSize(pageSize);
                taskService.add(sortingObjectExtend2Task(sorting));
            }
        } else {
            log.warn("运单分拣包裹数据为空:{}" , JsonHelper.toJson(sorting));
        }
    }

    /**
     * 将SortingObjectExtend对象转换成task任务
     * @param sorting
     * @return
     */
    private Task sortingObjectExtend2Task(SortingObjectExtend sorting) {
        Task task = new Task();

        task.setType(Task.TASK_TYPE_SORTING_CORE_SUCCESS);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SORTING_CORE_SUCCESS));
        task.setSequenceName(Task.getSequenceName(Task.getTableName(Task.TASK_TYPE_SORTING_CORE_SUCCESS)));
        task.setBoxCode(sorting.getDmsSorting().getBoxCode());
        task.setCreateSiteCode(sorting.getDmsSorting().getCreateSiteCode());
        task.setKeyword1(String.valueOf(sorting.getDmsSorting().getCreateSiteCode()));
        task.setKeyword2(sorting.getDmsSorting().getPackageCode());
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setBody(JsonHelper.toJson(sorting));

        return task;
    }
}
