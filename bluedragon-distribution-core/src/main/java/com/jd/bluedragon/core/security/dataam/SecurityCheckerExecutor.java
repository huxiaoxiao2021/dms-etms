package com.jd.bluedragon.core.security.dataam;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.security.dataam.enums.SecurityDataMapFuncEnum;
import com.jd.bluedragon.core.security.dataam.manage.SecurityCheckManager;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Objects;

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

    // 提示语模板
    private final String TEMPLATE_HINT = "当前操作人%s次数过多，请更换操作人";

    private final Map<SecurityDataMapFuncEnum, String> securityCheckEnumsStringMap;

    @Autowired
    private SecurityCheckManager securityCheckManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;


    public SecurityCheckerExecutor(Map<SecurityDataMapFuncEnum, String> securityCheckEnumsStringMap) {
        this.securityCheckEnumsStringMap = securityCheckEnumsStringMap;
    }

    public InvokeResult<Boolean> verifyWaybillDetailPermission(SecurityDataMapFuncEnum securityCheckEnums, String erpCode, String waybillNo) {
        if(!uccPropertyConfiguration.getSecuritySwitch()){
            return new InvokeResult<>();
        }
        InvokeResult<Boolean> result = securityCheckManager.verifyWaybillDetailPermissionByErp(erpCode, waybillNo, securityCheckEnumsStringMap.get(securityCheckEnums));
        if(Objects.equals(result.getCode(), 400)){
            result.setMessage(String.format(TEMPLATE_HINT, securityCheckEnums.getSecurityFuncEnum().getName()));
        }
        return result;
    }

    public InvokeResult<Boolean> verifyOrderDetailPermission(SecurityDataMapFuncEnum securityCheckEnums, String erpCode, String waybillNo) {
        return securityCheckManager.verifyOrderDetailPermissionByErp(erpCode,waybillNo, securityCheckEnumsStringMap.get(securityCheckEnums));
    }

}
