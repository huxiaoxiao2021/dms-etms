package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.ql.dms.common.domain.BusType;

import java.util.List;

/**
 * Created by xumei3 on 2017/12/28.
 */
public interface BusTypeService {
    /**
     * 获取所有的车型信息
     * @return
     */
    public List<BusType> getAllBusType();
}
