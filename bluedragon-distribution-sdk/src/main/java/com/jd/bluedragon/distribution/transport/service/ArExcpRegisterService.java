package com.jd.bluedragon.distribution.transport.service;

import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.transport.domain.ArExcpRegister;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 *
 * @ClassName: ArExcpRegisterService
 * @Description: 异常登记表--Service接口
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
public interface ArExcpRegisterService extends Service<ArExcpRegister> {

    /**
     * 保存或修改
     * @param arExcpRegister
     * @param userCode 登录人账户
     * @param userName 登录人名称

     * @return
     */
    public boolean saveOrUpdate(ArExcpRegister arExcpRegister, String userCode, String userName);
}
