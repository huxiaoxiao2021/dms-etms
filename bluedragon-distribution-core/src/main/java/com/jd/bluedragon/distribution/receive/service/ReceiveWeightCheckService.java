package com.jd.bluedragon.distribution.receive.service;

import com.jd.bluedragon.distribution.receive.domain.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.WeightAndVolumeCheck;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckService
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/2/28 17:54
 */
public interface ReceiveWeightCheckService {

    /**
     * 新增一条记录
     * */
    public int insert(WeightAndVolumeCheck weightAndVolumeCheck);

    /**
     * 按条件查询
     * */
    public PagerResult<WeightAndVolumeCheck> queryByCondition(WeightAndVolumeCheckCondition condition);

    /**
     * 根据条件查询数据条数
     * */
    public Integer queryNumByCondition(WeightAndVolumeCheckCondition condition);

    /**
     * 整理导出数据
     * */
    public List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition);
}
