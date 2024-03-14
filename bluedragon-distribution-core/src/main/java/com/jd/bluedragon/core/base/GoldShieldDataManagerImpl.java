package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.data.api.model.jsf.WaybillMonitorDto;
import com.jd.ql.data.api.model.jsf.WaybillMonitorQuery;
import com.jd.ql.data.api.model.web.GoldShieldResult;
import com.jd.ql.data.api.service.GoldShieldDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoldShieldDataManagerImpl implements GoldShieldDataManager{

    @Autowired
    private GoldShieldDataService goldShieldDataService;


    @Override
    public GoldShieldResult<WaybillMonitorDto> queryGsMonitorInfo(WaybillMonitorQuery query) {
        try{
            return goldShieldDataService.queryGsMonitorInfo(query);
        }catch (Exception e) {
            log.error("根据运单号查询责任单位异常{}", JsonHelper.toJson(query),e);
            return null;
        }
    }
}
