package com.jd.bluedragon.distribution.record.jsf;

import java.util.List;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.record.dto.WaybillHasnoPresiteRecordQo;
import com.jd.bluedragon.distribution.record.model.WaybillHasnoPresiteRecord;
import com.jd.bluedragon.distribution.record.vo.WaybillHasnoPresiteRecordVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 无滑道包裹明细
 *
 * @author wuyoude
 * @copyright jd.com 京东物流JDL
 */
public interface WaybillHasnoPresiteRecordJsfService {

    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     */
    Response<Long> selectCount(WaybillHasnoPresiteRecordQo query);

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     */
    Response<List<WaybillHasnoPresiteRecord>> selectList(WaybillHasnoPresiteRecordQo query);

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     */
    Response<PageDto<WaybillHasnoPresiteRecordVo>> selectPageList(WaybillHasnoPresiteRecordQo query);
}
