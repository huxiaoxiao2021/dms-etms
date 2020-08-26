package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.service.AbstractClient;
import com.jd.bluedragon.distribution.base.service.ErpValidateService;
import com.jd.bluedragon.distribution.base.service.LoginClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("baseUserService")
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
