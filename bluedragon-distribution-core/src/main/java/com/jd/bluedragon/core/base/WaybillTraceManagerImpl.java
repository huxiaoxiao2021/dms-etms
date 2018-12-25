package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jd.bluedragon.Constants.RESULT_SUCCESS;

/**
 * @author tangchunqing
 * @Description: 全程跟踪工具
 * @date 2018年12月25日 16时:43分
 */
@Service("waybillTraceManager")
public class WaybillTraceManagerImpl implements WaybillTraceManager {
    @Autowired
    private WaybillTraceApi waybillTraceApi;

    /**
     * 查运单的 某个状态的全程跟踪
     *
     * @param waybillCode
     * @param state
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillTraceManagerImpl.getPkStateByWCodeAndState", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<PackageState> getPkStateByWCodeAndState(String waybillCode, String state) {
        BaseEntity<List<PackageState>> baseEntity = waybillTraceApi.getPkStateByWCodeAndState(waybillCode, state);
        if (baseEntity != null && baseEntity.getResultCode() == RESULT_SUCCESS && baseEntity.getData() != null && baseEntity.getData().size() > 0) {
            return baseEntity.getData();
        } else {
            return Lists.newArrayList();
        }
    }
}
