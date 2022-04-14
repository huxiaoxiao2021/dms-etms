package com.jd.bluedragon.core.base;

import com.jd.mrd.srv.dto.RpcResultDto;
import com.jd.mrd.srv.service.erp.dto.LoginContextDto;
import com.jd.mrd.srv.service.erp.dto.LoginDto;

/**
 * 物流网关外网erp认证插件
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-02-17 15:55:10 周四
 */
public interface ErpLoginServiceManager {

    /**
     * 物流网关登录接口
     * @param loginDto 登录参数
     * @return 登录结果
     * @author fanggang7
     * @time 2022-02-17 15:58:24 周四
     */
    RpcResultDto<LoginContextDto> loginSecurity(LoginDto loginDto);
}
