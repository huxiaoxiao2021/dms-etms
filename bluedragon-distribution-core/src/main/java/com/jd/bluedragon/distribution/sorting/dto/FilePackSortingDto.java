package com.jd.bluedragon.distribution.sorting.dto;

import java.io.Serializable;

/**
 * @ClassName FilePackSortingDto
 * @Description 文件包裹分拣消息
 * @Author wyh
 * @Date 2020/12/16 14:58
 **/
public class FilePackSortingDto implements Serializable {

    private static final long serialVersionUID = 615820984839191641L;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 操作分拣中心
     */
    private Integer siteCode;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }
}
