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

    /**
     * 指定运单数据再发送(为终端修数专用)
     */
    Response<Void> dataSend(String waybillCodes);

    /**
     * 指定运单数据删除(为称重抽检软包体积超标功能优化脏数据专用)
     */
    Response<Void> dataDelete(String waybillCodes);

    /**
     * 根据分区键查询库和表下标
     */
    Response<String> getDbIndexAndTableIndex(String operateBizKey);

}
