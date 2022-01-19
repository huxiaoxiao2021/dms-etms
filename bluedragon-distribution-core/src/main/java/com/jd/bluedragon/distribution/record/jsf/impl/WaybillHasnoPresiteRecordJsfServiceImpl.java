package com.jd.bluedragon.distribution.record.jsf.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.record.dto.WaybillHasnoPresiteRecordQo;
import com.jd.bluedragon.distribution.record.jsf.WaybillHasnoPresiteRecordJsfService;
import com.jd.bluedragon.distribution.record.model.WaybillHasnoPresiteRecord;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.record.vo.WaybillHasnoPresiteRecordVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 无滑道查询
 *
 * @author wuyoude
 * @copyright jd.com 京东物流JDL
 */
@Service("waybillHasnoPresiteRecordJsfService")
public class WaybillHasnoPresiteRecordJsfServiceImpl implements WaybillHasnoPresiteRecordJsfService {

    @Autowired
    private WaybillHasnoPresiteRecordService waybillHasnoPresiteRecordService;

    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<Long> selectCount(WaybillHasnoPresiteRecordQo query) {
        return waybillHasnoPresiteRecordService.selectCount(query);
    }

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<List<WaybillHasnoPresiteRecord>> selectList(WaybillHasnoPresiteRecordQo query) {
        return waybillHasnoPresiteRecordService.selectList(query);
    }

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<PageDto<WaybillHasnoPresiteRecordVo>> selectPageList(WaybillHasnoPresiteRecordQo query) {
        return waybillHasnoPresiteRecordService.selectPageList(query);
    }
}
