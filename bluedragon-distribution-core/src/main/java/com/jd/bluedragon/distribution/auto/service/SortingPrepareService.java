package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * 分拣数据补全
 * Created by wangtingwei on 2014/10/16.
 */
public interface SortingPrepareService {

    /**
     * 准备数据
     * @param entity 任务对象
     * @return
     */
    boolean handler(Task entity);
}
