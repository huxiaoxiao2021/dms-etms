package com.jd.bluedragon.core.base;

import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.ql.dms.report.weightVolumeFlow.domain.WeightVolumeFlowEntity;

import java.util.List;
import java.util.Set;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/21 09:02
 * @Description:
 */
public interface WeightVolumeFlowJSFManager {

    /**
     * 获取已称重数据
     * @param barCodes
     * @param siteCode
     * @return
     */
    Set<String> findExistFlow(List<String> barCodes, Integer siteCode);

    /**
     * 检查箱号是否称重
     * @param boxCode
     * @param siteCode
     * @return
     */
    WeightVolumeFlowEntity findExistFlowOfBox(String boxCode, Integer siteCode);

}
