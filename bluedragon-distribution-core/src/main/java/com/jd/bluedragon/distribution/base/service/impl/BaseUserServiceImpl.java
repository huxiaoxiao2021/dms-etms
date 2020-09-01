package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.service.AbstractClient;
import com.jd.bluedragon.distribution.base.service.ErpValidateService;
import com.jd.bluedragon.distribution.base.service.LoginClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @description:  新的登录逻辑调入口
 * @author: lql
 * @date: 2020/8/27 16:46
 **/
@Service("baseUserService")
@Deprecated
public class BaseUserServiceImpl extends AbstractClient{

    @Autowired
    @Qualifier("thirdValidateService")
    private ErpValidateService thirdValidateService;
    @Autowired
    @Qualifier("erpValidateService")
    private ErpValidateService erpValidateService;

    @Override
    protected ErpValidateService setOwnErpValidateService() {
        return erpValidateService;
    }

    @Override
    protected ErpValidateService setThirdErpValidateService() {
        return thirdValidateService;
    }

    @Override
    protected LoginClientService selectLoginClient() {
        return this;
    }
}
