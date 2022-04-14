package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.mrd.srv.dto.RpcResultDto;
import com.jd.mrd.srv.service.erp.ErpLoginService;
import com.jd.mrd.srv.service.erp.dto.LoginContextDto;
import com.jd.mrd.srv.service.erp.dto.LoginDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 物流网关erp认证登录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-02-17 15:56:52 周四
 */
@Component
public class ErpLoginServiceManagerImpl implements ErpLoginServiceManager {

    @Autowired
    private ErpLoginService erpLoginService;

    /**
     * 物流网关登录接口
     * @param loginDto 登录参数
     * @return 登录结果
     * @author fanggang7
     * @time 2022-02-17 15:58:24 周四
     */
    @JProfiler(jKey = "DMS.BASE.ErpLoginServiceManagerImpl.loginSecurity", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public RpcResultDto<LoginContextDto> loginSecurity(LoginDto loginDto) {
        return erpLoginService.loginSecurity(loginDto);
    }
}
