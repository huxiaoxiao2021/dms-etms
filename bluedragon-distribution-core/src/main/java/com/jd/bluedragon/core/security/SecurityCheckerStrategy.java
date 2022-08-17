package com.jd.bluedragon.core.security;

import com.jd.bluedragon.core.security.manage.SecurityCheckEnums;
import com.jd.bluedragon.core.security.manage.SecurityCheckerExecutor;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;

import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security
 * @ClassName: VerifyPermissChecker
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/8/17 14:13
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SecurityCheckerStrategy {

    private final Map<SecurityCheckEnums, SecurityCheckerExecutor> securityCheckEnumsAbstractVerifyPermissionMap = new HashMap<>();

    public SecurityCheckerStrategy(Map<SecurityCheckEnums, String> securityCheckEnumsStringMap) {
        for (Map.Entry<SecurityCheckEnums, String> entry : securityCheckEnumsStringMap.entrySet()) {
            SecurityCheckerExecutor verifyPermission = new SecurityCheckerExecutor(entry.getValue());
            this.securityCheckEnumsAbstractVerifyPermissionMap.put(entry.getKey(), verifyPermission);
        }
    }

    public InvokeResult<Boolean> verifyWaybillDetailPermission(SecurityCheckEnums securityCheckEnums, String erpCode, String waybillNo) {
        return securityCheckEnumsAbstractVerifyPermissionMap.get(securityCheckEnums).verifyWaybillDetailPermission(erpCode,waybillNo);
    }

    public InvokeResult<Boolean> verifyOrderDetailPermission(SecurityCheckEnums securityCheckEnums, String erpCode, String waybillNo) {
        return securityCheckEnumsAbstractVerifyPermissionMap.get(securityCheckEnums).verifyOrderDetailPermission(erpCode,waybillNo);
    }

}
