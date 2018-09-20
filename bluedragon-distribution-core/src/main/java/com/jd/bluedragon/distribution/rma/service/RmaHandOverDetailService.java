package com.jd.bluedragon.distribution.rma.service;

import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
public interface RmaHandOverDetailService {

    /**
     * 新增
     *
     * @param detail
     */
    void add(RmaHandoverDetail detail);

    /**
     * 批量新增
     *
     * @param details
     */
    void batchAdd(List<RmaHandoverDetail> details);

    /**
     * 根据主表ID获取明细列表
     *
     * @param waybillId
     * @return
     */
    List<RmaHandoverDetail> getListByWaybillId(Long waybillId);

}
