package com.jd.bluedragon.distribution.workingConfig;

import com.jd.bluedragon.distribution.businessCode.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.wb.report.api.working.dto.WorkingActionDto;
import com.jd.tys.api.outsource.dto.OutsourceCountRuleDto;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.workingConfig
 * @ClassName: WorkingConfigQueryServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/5/15 23:34
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
public class WorkingConfigQueryServiceImpl implements WorkingConfigQueryService{

    private static final Logger log = LoggerFactory.getLogger(WorkingConfigQueryServiceImpl.class);

    @Autowired
    private WorkingConfigProxy workingConfigProxy;

    @Override
    public Map<String,String> querySupplierCodeWithDaMuJia(Integer siteCode) {
        try {
            Map<String, String> result = new HashMap<>();

            List<WorkingActionDto> dmsResult = workingConfigProxy.queryDMSWorkingConfig(siteCode, 11);//11表述打木架
            log.info("查询该场地【{}】在分拣工作台配置的打木架外包计提供应商结果为：{}", siteCode, JsonHelper.toJson(dmsResult));
            if (!CollectionUtils.isEmpty(dmsResult)) {
                result.put("code", dmsResult.get(0).getGroupId());
                result.put("name", dmsResult.get(0).getGroupName());
                return result;
            }
            List<OutsourceCountRuleDto> tysResult = workingConfigProxy.queryTYSWorkingConfig(siteCode, 105);//105表述打木架
            log.info("查询该场地【{}】在转运工作台配置的打木架外包计提供应商结果为：{}", siteCode, JsonHelper.toJson(tysResult));
            if (!CollectionUtils.isEmpty(tysResult)) {
                result.put("code", tysResult.get(0).getSupplierNo());
                result.put("name", tysResult.get(0).getOutsourceName());
                return result;
            }
        } catch (RuntimeException e) {
            log.error("查询该场地【{}】在分拣工作台配置的打木架外包计提供应商结果发生异常", siteCode, e);
            return Collections.emptyMap();
        }
        return Collections.emptyMap();
    }
}
