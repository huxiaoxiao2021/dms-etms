package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.base.domain.BasePdaUserDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.ssa.domain.UserInfo;

/**
 * @author yangwenshu
 * @Description: 类描述信息
 * @date 2018年10月19日 10时:32分
 */
public interface UserVerifyManager {
    /**
     * 自营校验
     * @param name
     * @param password
     * @return
     */
    InvokeResult<UserInfo> baseVerify(String name, String password);

    /**
     * 三方校验
     * @param pin
     * @param password
     * @param clientInfo
     * @return
     */
    BasePdaUserDto passportVerify(String pin, String password, ClientInfo clientInfo);
}
