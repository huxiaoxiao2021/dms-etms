package com.jd.bluedragon.core.security.log.builder;

import com.jd.securitylog.entity.RespInfo;
import com.jd.securitylog.entity.UniqueIdentifier;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.core.security.log
 * @ClassName: SecurityLogRespInfoBuilder
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/6 10:30
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class SecurityLogRespInfoBuilder {

    private final RespInfo respInfo = new RespInfo();

    public SecurityLogRespInfoBuilder status (Integer status){
        this.respInfo.setStatus(status);
        return this;
    }

    public SecurityLogRespInfoBuilder recordCnt (Integer recordCnt){
        this.respInfo.setRecordCnt(recordCnt);
        return this;
    }

    public SecurityLogRespInfoBuilder uniqueIdentifier (UniqueIdentifier uniqueIdentifier){
        if (CollectionUtils.isEmpty(this.respInfo.getUniqueIdentifier())) {
            this.respInfo.setUniqueIdentifier(new ArrayList<UniqueIdentifier>());
        }
        this.respInfo.getUniqueIdentifier().add(uniqueIdentifier);
        return this;
    }

    public RespInfo build() {
        return this.respInfo;
    }


}
