package com.jd.bluedragon.distribution.spotcheck.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.spotcheck.entity.SpotCheckAppealAppendixResult;
import com.jd.bluedragon.distribution.spotcheck.entity.SpotCheckAppealDto;
import com.jd.bluedragon.distribution.spotcheck.entity.SpotCheckAppealResult;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

public interface SpotCheckAppealJsfService {

    /**
     * 根据条件分页查询
     */
    Response<PagerResult<SpotCheckAppealResult>> findByCondition(SpotCheckAppealDto spotCheckAppealEntity);

    /**
     * 根据ID更新
     */
    Response<Void> updateById(SpotCheckAppealDto spotCheckAppealEntity);

    /**
     * 根据ID列表批量更新
     */
    Response<Void> batchUpdateByIds(SpotCheckAppealDto spotCheckAppealEntity);

    /**
     * 根据BizId查询申诉附件
     */
    Response<List<SpotCheckAppealAppendixResult>> findAppendixByBizId(SpotCheckAppealDto spotCheckAppealEntity);

}
