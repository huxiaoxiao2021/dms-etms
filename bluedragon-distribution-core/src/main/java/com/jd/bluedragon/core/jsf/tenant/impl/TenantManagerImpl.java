package com.jd.bluedragon.core.jsf.tenant.impl;

import com.jd.bluedragon.core.jsf.tenant.TenantManager;
import com.jdl.basic.api.domain.tenant.JyConfigDictTenant;
import com.jdl.basic.api.enums.TenantEnum;
import com.jdl.basic.api.service.tenant.JyConfigDictTenantJsfService;
import com.jdl.basic.common.utils.ObjectHelper;
import com.jdl.basic.common.utils.Result;
import com.jdl.basic.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: ql-dms-distribution
 * @Package com.jd.bluedragon.core.jsf.tenant.impl
 * @Description:
 * @date Date : 2024年01月18日 21:48
 */
@Slf4j
@Service
public class TenantManagerImpl implements TenantManager {

    @Resource
    private JyConfigDictTenantJsfService jyConfigDictTenantJsfService;

    /**
     * 根据站点代码获取租户信息
     * @param siteCode 站点代码
     * @return JyConfigDictTenant 返回租户信息
     * @throws Exception 异常
     */
    @Override
    public JyConfigDictTenant getTenantBySiteCode(Integer siteCode) {
        if(siteCode == null){
            return null;
        }
        try {
            Result<JyConfigDictTenant> rs = jyConfigDictTenantJsfService.getTenantBySiteCode(siteCode);
            if (ObjectHelper.isNotNull(rs) && rs.isSuccess() && ObjectHelper.isNotNull(rs.getData())) {
                return rs.getData();
            }
        }catch (Exception e){
            log.error("根据场地:{}获取租户信息异常",siteCode,e);
        }
        JyConfigDictTenant defaultResult = new JyConfigDictTenant();
        defaultResult.setBelongTenantCode(TenantEnum.TENANT_JY.getCode());
        return defaultResult;
    }

    /**
     * 获取调用接口别名
     * @param tenantCode 租户编码
     * @param dictCode 字典编码
     * @return 调用接口别名
     * @throws Exception 异常
     */
    @Override
    public String getCallInterfaceAlies(String tenantCode, String dictCode) {
        if(StringUtils.isBlank(tenantCode) || StringUtils.isBlank(dictCode)){
            return null;
        }
        try {
            Result<String> rs = jyConfigDictTenantJsfService.getCallInterfaceAlies(tenantCode,dictCode);
            if (ObjectHelper.isNotNull(rs) && rs.isSuccess() && StringUtils.isNotBlank(rs.getData())) {
                return rs.getData();
            }
        }catch (Exception e){
            log.error("租户{}获取接口回调别名异常",tenantCode,e);
        }
        return null;
    }
}
