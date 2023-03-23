package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;
import java.util.List;

/**
 * 安卓功能是否可使用条件配置
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 17:27:06 周一
 */
public class FuncUsageConditionConfigDto implements Serializable {
    private static final long serialVersionUID = 4547734420178443568L;

    private List<Integer> siteType;
    private List<Integer> siteSubType;
    private List<Integer> siteSortType;
    private List<Integer> siteSortSubType;
    private List<Integer> siteSortThirdType;

    /**
     * 场地编码
     */
    private List<Integer> siteCodes;

    /**
     * 区域编码
     */
    private List<Integer> orgCodes;

    public List<Integer> getSiteType() {
        return siteType;
    }

    public void setSiteType(List<Integer> siteType) {
        this.siteType = siteType;
    }

    public List<Integer> getSiteSubType() {
        return siteSubType;
    }

    public void setSiteSubType(List<Integer> siteSubType) {
        this.siteSubType = siteSubType;
    }

    public List<Integer> getSiteSortType() {
        return siteSortType;
    }

    public void setSiteSortType(List<Integer> siteSortType) {
        this.siteSortType = siteSortType;
    }

    public List<Integer> getSiteSortSubType() {
        return siteSortSubType;
    }

    public void setSiteSortSubType(List<Integer> siteSortSubType) {
        this.siteSortSubType = siteSortSubType;
    }

    public List<Integer> getSiteSortThirdType() {
        return siteSortThirdType;
    }

    public void setSiteSortThirdType(List<Integer> siteSortThirdType) {
        this.siteSortThirdType = siteSortThirdType;
    }

    public List<Integer> getSiteCodes() {
        return siteCodes;
    }

    public void setSiteCodes(List<Integer> siteCodes) {
        this.siteCodes = siteCodes;
    }

    public List<Integer> getOrgCodes() {
        return orgCodes;
    }

    public void setOrgCodes(List<Integer> orgCodes) {
        this.orgCodes = orgCodes;
    }
}
