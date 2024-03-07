package com.jd.bluedragon.distribution.jy.exception.query;

/**
 * 根据包裹和拦截类型查询上次处理过的场地处理记录查询参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-01-21 21:01:27 周日
 */
public class PackageWithInterceptTypeLastHandleSiteQuery {

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 拦截类型
     */
    private Integer interceptType;

    /**
     * 任务状态
     */
    private Integer taskStatus;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getInterceptType() {
        return interceptType;
    }

    public void setInterceptType(Integer interceptType) {
        this.interceptType = interceptType;
    }
}
