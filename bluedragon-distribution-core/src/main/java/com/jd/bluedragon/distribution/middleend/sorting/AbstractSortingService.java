package com.jd.bluedragon.distribution.middleend.sorting;

import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.middleend.sorting.domain.SortingObjectExtend;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.shared.services.sorting.api.dto.SortingObject;
import com.jd.ql.shared.services.sorting.api.dto.SortingObjectStatus;
import com.jd.ql.shared.services.sorting.api.dto.SortingObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.TimeUnit;

public abstract class AbstractSortingService {

    protected Logger logger = LoggerFactory.getLogger(AbstractSortingService.class);

    public final static String TASK_SORTING_FINGERPRINT_SORTING_5S = "TASK_SORTING_FP_5S_"; //5前缀

    public static final int TASK_1200_EX_TIME_5_S = 5;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    public boolean doSorting(Task sortingTask){
        //转换
        SortingObjectExtend sorting = prepareSorting(sortingTask);

        String fingerPrintKey = "";
        try {
//            fingerPrintKey = TASK_SORTING_FINGERPRINT_SORTING_5S + sorting.getDmsSorting().getCreateSiteCode() +"|"+ sorting.getBoxCode()
//                    +"|"+sorting.getWaybillCode()+"|"+sorting.getDmsSorting().getPackageCode()+"|"+ sorting.getPageNo()+"|"+sorting.getPageSize();
            if (check(fingerPrintKey)) {
                //组装数据
                coreSorting(sorting);
            }
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            delCache(fingerPrintKey);
        }
    }


    private boolean check(String fingerPrintKey) {
        try {
            //判断是否重复分拣, 5秒内如果同操作场地、同目的地、同扫描号码即可判断为重复操作。
            // 立刻置失败，转到下一次执行。只使用key存不存在做防重
            Boolean isSucdess = cacheService.setNx(fingerPrintKey, "1", TASK_1200_EX_TIME_5_S, TimeUnit.SECONDS);
            if (!isSucdess) {//说明有重复任务
                this.logger.warn("分拣任务重复：" + fingerPrintKey);
                return false;
            }
        } catch (Exception e) {
            this.logger.error("获得分拣任务指纹失败" + fingerPrintKey, e);
        }
        return true;
    }

    private boolean delCache(String fingerPrintKey) {
        cacheService.del(fingerPrintKey);
    }


    protected SortingObjectExtend prepareSorting(Task sortingTask) {
        SortingObjectExtend extendObject = new SortingObjectExtend();

        Sorting dmsSorting = getDmsSorting(sortingTask);

        buildMiddleEndSorting(extendObject,dmsSorting);

        //填充扩展
        extendObject.setDmsSorting(dmsSorting);
        //始发和目的


        return extendObject;
    }

    private Sorting getDmsSorting(Task sortingTask) {
        String body = sortingTask.getBody().substring(1, sortingTask.getBody().length() - 1);
        SortingRequest request = JsonHelper.jsonToArray(body, SortingRequest.class);
        if (request != null) {
            Sorting sorting = Sorting.toSorting(request);
            sorting.setStatus(Sorting.STATUS_DONE);// 运单回传状态默认为1，以后可以去掉
            //补上始发和目的
            return sorting;
        }
        return null;
    }

    private void buildMiddleEndSorting(SortingObject object, Sorting dmsSorting){
        object.setObjectCode();
        object.setObjectCodeAlias();

        object.setStatus(SortingObjectStatus.NORMAL);
        if (StringUtils.isBlank(dmsSorting.getPackageCode())) {
            object.setObjectType(SortingObjectType.WAYBILL);
        } else {
            object.setObjectType(SortingObjectType.PACKAGE);
        }
        object.setFlow();
        object.setWaybillCode(dmsSorting.getWaybillCode());
        object.setBatchCode();
        object.setRelatedCode();
        object.setComment();
        object.setIsCancel(false);
    }

    protected abstract void coreSorting(SortingObjectExtend sorting);
}
