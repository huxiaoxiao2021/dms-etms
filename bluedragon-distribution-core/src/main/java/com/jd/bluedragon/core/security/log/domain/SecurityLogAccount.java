package com.jd.bluedragon.core.security.log.domain;

import lombok.Data;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log.domain
 * @ClassName: SecurityLogAccount
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/7 16:06
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class SecurityLogAccount {

    private String clientIp;

    /**
     * @see com.jd.bluedragon.core.security.log.enums.SecurityAccountEnums
     */
    private Integer accountType;

    private String accountName;
}
