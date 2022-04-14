package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.api
 * @ClassName: DeSealCodeRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/2/23 17:46
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class DeSealCodeRequest implements Serializable {

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 待解封签号
     */
    private List<String> desealCodes;

    /**
     * 解封签站点ID
     */
    private Integer desealSiteId;

    /**
     * 解封签站点名称
     */
    private String desealSiteName;

    /**
     * 解封签用户ERP
     */
    private String desealUserCode;

    /**
     * 解封签用户名
     */
    private String desealUserName;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public List<String> getDesealCodes() {
        return desealCodes;
    }

    public void setDesealCodes(List<String> desealCodes) {
        this.desealCodes = desealCodes;
    }

    public Integer getDesealSiteId() {
        return desealSiteId;
    }

    public void setDesealSiteId(Integer desealSiteId) {
        this.desealSiteId = desealSiteId;
    }

    public String getDesealSiteName() {
        return desealSiteName;
    }

    public void setDesealSiteName(String desealSiteName) {
        this.desealSiteName = desealSiteName;
    }

    public String getDesealUserCode() {
        return desealUserCode;
    }

    public void setDesealUserCode(String desealUserCode) {
        this.desealUserCode = desealUserCode;
    }

    public String getDesealUserName() {
        return desealUserName;
    }

    public void setDesealUserName(String desealUserName) {
        this.desealUserName = desealUserName;
    }
}
