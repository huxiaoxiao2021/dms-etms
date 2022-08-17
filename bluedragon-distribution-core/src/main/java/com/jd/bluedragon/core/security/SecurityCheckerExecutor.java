package com.jd.bluedragon.core.security;

import com.jd.bluedragon.core.security.manage.SecurityCheckEnums;
import com.jd.bluedragon.core.security.manage.SecurityCheckManager;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security
 * @ClassName: AbstractVerifyPermission
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/8/17 14:04
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SecurityCheckerExecutor{

    private final Map<SecurityCheckEnums, String> securityCheckEnumsStringMap;

    @Autowired
    private SecurityCheckManager securityCheckManager;


    public SecurityCheckerExecutor(Map<SecurityCheckEnums, String> securityCheckEnumsStringMap) {
        this.securityCheckEnumsStringMap = securityCheckEnumsStringMap;
    }

    public InvokeResult<Boolean> verifyWaybillDetailPermission(SecurityCheckEnums securityCheckEnums, String erpCode, String waybillNo) {
        return securityCheckManager.verifyWaybillDetailPermissionByErp(erpCode, waybillNo, securityCheckEnumsStringMap.get(securityCheckEnums));
    }

    public InvokeResult<Boolean> verifyOrderDetailPermission(SecurityCheckEnums securityCheckEnums, String erpCode, String waybillNo) {
        return securityCheckManager.verifyOrderDetailPermissionByErp(erpCode,waybillNo, securityCheckEnumsStringMap.get(securityCheckEnums));
    }

}
