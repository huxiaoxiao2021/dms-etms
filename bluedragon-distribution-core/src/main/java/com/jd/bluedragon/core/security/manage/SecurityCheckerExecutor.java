package com.jd.bluedragon.core.security.manage;

import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import org.springframework.beans.factory.annotation.Autowired;

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
public class SecurityCheckerExecutor {

    private final String waybillAMToken;

    @Autowired
    private SecurityCheckManager securityCheckManager;

    public SecurityCheckerExecutor(String waybillAMToken) {
        this.waybillAMToken = waybillAMToken;
    }

    public InvokeResult<Boolean> verifyWaybillDetailPermission(String erpCode, String waybillNo) {
        return securityCheckManager.verifyWaybillDetailPermissionByErp(erpCode, waybillNo, waybillAMToken);
    }

    public InvokeResult<Boolean> verifyOrderDetailPermission(String erpCode, String waybillNo) {
        return securityCheckManager.verifyOrderDetailPermissionByErp(erpCode, waybillNo, waybillAMToken);
    }

}
