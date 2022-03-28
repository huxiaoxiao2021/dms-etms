package com.jd.bluedragon.distribution.api.domain;

import java.io.Serializable;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.api.domain
 * @ClassName: TransAbnormalTypeDto
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/2/25 16:43
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class TransAbnormalTypeDto implements Serializable {

    private String typeCode;

    private String typeName;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
