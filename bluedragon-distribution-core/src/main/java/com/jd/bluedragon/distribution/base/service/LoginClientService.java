package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
/**
 * @description: erp信息登录客户端（新、旧）接口
 * @author: lql
 * @date: 2020/8/17 13:18
 **/
public interface LoginClientService {

    /**
     * 账号密码是否存在
     *
     * @param String
     *            erpcode erp账号
     * @param String
     *            password erp密码
     *
     * @param loginVersion 登录接口的版本号
     *
     * @return StaffDto 是否登录成功
     */
    PdaStaff login(String erpcode, String password, ClientInfo clientInfo, Byte loginVersion);

    /**
     * @description: 选择使用的校验服务
     * @param erpAccount: erp账号
     * @author: lql
     * @date: 2020/8/21 11:37
     **/
    void selectErpValidateService(String erpAccount);
}
