package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.distribution.base.domain.BasePdaUserDto;
import com.jd.bluedragon.distribution.base.service.AbstractClient;
import com.jd.bluedragon.distribution.base.service.ErpValidateService;
import com.jd.bluedragon.distribution.base.service.LoginClientService;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import org.springframework.stereotype.Service;

/**
 * @description: erp登录校验处理类
 * @author: lql
 * @date: 2020/8/17 13:22
 **/
@Service("erpValidateService")
public class ErpValidateServiceImpl implements ErpValidateService {
    @Override
    public BasePdaUserDto pdaUserLogin(String userid, String password, ClientInfo clientInfo, Byte loginVersion) {
        System.out.println("=======新接口======");
        return null;
    }

}
