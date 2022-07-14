package com.jd.bluedragon.common.dto.identity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.common.dto.identity
 * @ClassName: IdentityContentEntity
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/7/7 00:50
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class IdentityContentEntity {

    private String name;

    private String idNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
