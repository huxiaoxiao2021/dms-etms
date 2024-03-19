package com.jd.bluedragon.core.jsf.tenant;

import com.jdl.basic.api.domain.tenant.JyConfigDictTenant;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bluedragon.core.jsf.tenant
 * @Description:
 * @date Date : 2024年01月18日 21:23
 */
public interface TenantManager {

    /**
     * 根据站点代码获取租户信息
     * @param siteCode 站点代码
     * @return JyConfigDictTenant 返回对应的租户信息
     */
    JyConfigDictTenant getTenantBySiteCode(Integer siteCode);

    /**
     * 获取调用接口别名
     * @param tenantCode 租户代码
     * @param dictCode 字典代码
     * @return 调用接口别名
     */
    String getCallInterfaceAlies(String tenantCode,String dictCode);
}
