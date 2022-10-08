package com.jd.bluedragon.core.security.log.domain;

import com.jd.bluedragon.core.security.log.enums.SecurityAccountEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogOpEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogReqInfoKeyEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogUniqueIdentifierKeyEnums;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log.domain
 * @ClassName: SecurityLogEntity
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/7 16:51
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Builder
@Getter
public class SecurityLogEntity {

    private final String interfaceName;

    private final SecurityAccountEnums accountType;

    private final String accountName;

    private final SecurityLogOpEnums op;

    private final Map<SecurityLogReqInfoKeyEnums, String> reqKeyMapping;

    private final Object businessRequest;

    private final Map<SecurityLogUniqueIdentifierKeyEnums,String> respKeyMapping;

    private final Object businessResponse;

    private final List<?> businessResponses;

    private final Integer resultNum;
}
