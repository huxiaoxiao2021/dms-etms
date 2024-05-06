package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.data.api.model.jsf.WaybillMonitorDto;
import com.jd.ql.data.api.model.jsf.WaybillMonitorQuery;
import com.jd.ql.data.api.model.web.GoldShieldResult;
import com.jd.ql.data.api.service.GoldShieldDataService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 金盾判责查询接口
 * @author liwenji3
 * @date 2024/4/8
 */
@Service
@Slf4j
public class GoldShieldDataManagerImpl implements GoldShieldDataManager{

    @Autowired
    private GoldShieldDataService goldShieldDataService;

    /**
     * 根据运单号查询责任单位接口
     * @param query
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.GoldShieldDataManagerImpl.queryGsMonitorInfo" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public GoldShieldResult<WaybillMonitorDto> queryGsMonitorInfo(WaybillMonitorQuery query) {
        try{
            return goldShieldDataService.queryGsMonitorInfo(query);
        }catch (Exception e) {
            log.error("根据运单号查询责任单位异常{}", JsonHelper.toJson(query),e);
            return null;
        }
    }
}
