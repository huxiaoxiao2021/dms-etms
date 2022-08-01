package com.jd.bluedragon.distribution.weightVolume.service;

import com.google.common.collect.Sets;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeRuleConstant;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 称重校验实现
 *
 * @author hujiping
 * @date 2022/7/25 6:18 PM
 */
@Service("dmsWeightVolumeCheckService")
public class DMSWeightVolumeCheckServiceImpl implements DMSWeightVolumeCheckService {

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 校验城配集配场地是否可以称重
     *  1、站点发货后城配集配不可操作称重
     *  2、下游场地已操作验货则集配城配场地不可操作称重
     * @param barCode
     * @param siteCode
     * @return
     */
    @Override
    public InvokeResult<Boolean> checkJPIsCanWeight(String barCode, Integer siteCode) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
        BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(siteCode);
        if(!BusinessUtil.isCPSite(operateSite.getSubType()) && !BusinessUtil.isJPSite(operateSite.getSubType())){
            return result;
        }
        Set<Integer> stateSet = Sets.newHashSet();
        stateSet.add(Integer.parseInt(Constants.WAYBILL_TRACE_STATE_SEND_BY_SITE));
        stateSet.add(Integer.parseInt(Constants.WAYBILL_TRACE_STATE_INSPECTION_BY_CENTER));
        List<PackageState> packStateList = waybillTraceManager.getAllOperationsByOpeCodeAndState(barCode, stateSet);
        if(CollectionUtils.isNotEmpty(packStateList)){
            // 操作过站点发货 || 分拣中心验货就不能操作称重了
            result.parameterError(WeightVolumeRuleConstant.RESULT_WEIGHT_INTERCEPT_AFTER_LL);
            return result;
        }
        return result;
    }
}
