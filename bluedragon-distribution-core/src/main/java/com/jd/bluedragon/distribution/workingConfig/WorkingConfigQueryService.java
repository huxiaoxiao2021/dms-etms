package com.jd.bluedragon.distribution.workingConfig;

import com.jd.dms.wb.report.sdk.model.vo.working.data.SupplierVO;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.workingConfig
 * @ClassName: WorkingConfigQueryService
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/5/15 23:22
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface WorkingConfigQueryService {

    /**
     * 从转运和分拣的工作台中获取打木架的外包计提配置，查询供应商
     * @param siteCode 站点
     * @return
     */
    Map<String,String> querySupplierCodeWithDaMuJia(Integer siteCode);

    /**
     * 查询场地外包商
     * @param siteId
     * @return
     */
    List<SupplierVO> querySupplierBySiteCode(Integer siteId);
}
