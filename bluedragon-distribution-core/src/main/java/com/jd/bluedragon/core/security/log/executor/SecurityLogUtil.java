package com.jd.bluedragon.core.security.log.executor;

import com.alibaba.fastjson.JSONObject;
import com.jd.securitylog.entity.SecurityLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log
 * @ClassName: SecurityLogUtil
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/7 15:57
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SecurityLogUtil {

    private static final Logger log = LoggerFactory.getLogger("security.log");

    public static void log(SecurityLog securityLog) {
        log.info(JSONObject.toJSONString(securityLog));
    }
}
