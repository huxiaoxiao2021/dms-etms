package com.jd.bluedragon.distribution.financialForKA.service.impl;

import com.jd.bluedragon.distribution.financialForKA.dao.WaybillCodeCheckDao;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.service.WaybillCodeCheckService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.ReviewWeightSpotCheck;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 单号校验服务实现
 *
 * @author: hujiping
 * @date: 2020/2/26 21:58
 */
@Service("waybillCodeCheckService")
public class WaybillCodeCheckServiceImpl implements WaybillCodeCheckService {

    private Logger log = LoggerFactory.getLogger(WaybillCodeCheckServiceImpl.class);

    @Autowired
    private WaybillCodeCheckDao waybillCodeCheckDao;


    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    @Override
    public PagerResult<ReviewWeightSpotCheck> listData(KaCodeCheckCondition condition) {

        return null;
    }

    /**
     * 获取导出数据
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(KaCodeCheckCondition condition) {

        return null;
    }
}
