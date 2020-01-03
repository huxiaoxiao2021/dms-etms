package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadedPackage;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by wangtingwei on 2014/10/16.
 */
public abstract class AbstractSortingPrepareService implements SortingPrepareService{
    private static final Logger log = LoggerFactory.getLogger(AbstractSortingPrepareService.class);
    /**
     * 处理分拣数据
     * @param task      任务
     * @return 任务处理成功否
     */
    @Override
    public boolean handler(Task task) {
        log.info("自动分拣机开始处理 插入到 task_sorting 任务 task = {}" , task);
        Sorting entity=new Sorting();
        dataConvert(task, entity);
        if(!prepareSite(entity))
            return false;
        if(filter(entity))
            return true;
        if(!prepareSorting(entity))
            return false;
        if(!intercept(entity))
            return false;

        return  push(entity);
    }

    private static void dataConvert(Task source,Sorting target){
        log.info("dataConvert");
        target.setType(10);/*正向分拣*/
        UploadedPackage pack= JsonHelper.fromJson(source.getBody(),UploadedPackage.class);
        target.setPackageCode(pack.getBarcode());
        target.setCreateSiteCode(pack.getSortCenterNo());
        target.setOperateTime(DateHelper.parseDateTime(pack.getTimeStamp()));
        target.setWaybillCode(WaybillUtil.getWaybillCode(target.getPackageCode()));
        target.setCreateUser(pack.getOperatorName());
        target.setCreateUserCode(pack.getOperatorID());

    }


    /**
     * 准备分拣站点
     * @param entity
     * @return
     */
    protected abstract boolean prepareSite(Sorting entity);

    /**
     * 准备批次、波次、箱号数据
     * @param entity
     * @return
     */
    protected abstract boolean prepareSorting(Sorting entity);

    /**
     * 拦截处理
     * @param entity
     * @return
     */
    protected abstract boolean intercept(Sorting entity);

    /**
     * 推送数据
     * @param entity
     * @return
     */
    protected abstract boolean push(Sorting entity);

    /**
     * 过滤器
     * @param entity
     * @return 是否需要过滤【是：无需分拣；否：需要分拣】
     */
    protected abstract boolean filter(Sorting entity);



}
