package com.jd.bluedragon.distribution.sysloginlog.service;

import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.distribution.sysloginlog.domain.SysLoginLog;

/**
 * 登录日志服务接口
 * Created by shipeilin on 2018/1/16.
 */
public interface SysLoginLogService {
    /**
     * 新增异常操作记录
     * @param siteInfo
     * @param clientInfo
     * @return
     */
    public SysLoginLog insert(PdaStaff siteInfo, ClientInfo clientInfo);
}
