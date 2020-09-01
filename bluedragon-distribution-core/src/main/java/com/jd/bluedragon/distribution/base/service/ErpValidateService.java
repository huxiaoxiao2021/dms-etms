package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;

/**
 * @description: erp校验接口
 * @author: lql
 * @date: 2020/8/21 11:17
 */
public interface ErpValidateService{

    /***
     * @description:
     * @param userid:
     * @param password:
     * @param clientInfo:
     * @param loginVersion:
     * @return: com.jd.bluedragon.distribution.base.domain.BasePdaUserDto
     * @author: lql
     * @date: 2020/8/21 11:19
     **/
    com.jd.bluedragon.distribution.base.domain.BasePdaUserDto pdaUserLogin(String userid, String password, ClientInfo clientInfo, Byte loginVersion);
}
