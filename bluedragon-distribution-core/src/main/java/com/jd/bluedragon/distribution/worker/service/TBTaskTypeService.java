package com.jd.bluedragon.distribution.worker.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.worker.domain.TBTaskType;

import java.util.List;

/**
 * Created by wangtingwei on 2015/10/8.
 */
public interface TBTaskTypeService {

    /**
     * 分页查询任务数据
     * @param pagerInnerTaskTypeName 分页内嵌任务名称
     * @return
     */
    Pager<List<TBTaskType>> readByName(Pager<String> pagerInnerTaskTypeName);

    /**
     * 插入单条记录
     * @param domain
     * @return
     */
    int inserSingle(TBTaskType domain) throws Exception;

    /**
     * 更新记录
     * @param domain
     * @return
     */
    int updateSingleById(TBTaskType domain);

    /**
     * 读取单条记录
     * @param id
     * @return
     */
    TBTaskType readById(int id);
}
