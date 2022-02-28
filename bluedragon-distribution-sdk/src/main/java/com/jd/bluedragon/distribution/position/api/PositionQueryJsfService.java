package com.jd.bluedragon.distribution.position.api;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.query.PositionQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 岗位查询对外jsf服务
 *
 * @author hujiping
 * @date 2022/2/26 8:47 PM
 */
public interface PositionQueryJsfService {

    /**
     * 按条件分页查询
     *
     * @param query
     * @return
     */
    Result<PageDto<PositionDetailRecord>> queryPageList(PositionQuery query);

    /**
     * 根据岗位编码更新
     *
     * @param positionCode
     * @return
     */
    Result<Boolean> updateByPositionCode(String positionCode);
}
