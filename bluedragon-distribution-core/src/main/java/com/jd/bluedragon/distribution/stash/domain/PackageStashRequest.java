package com.jd.bluedragon.distribution.stash.domain;

/**
 * <P>
 *     收纳暂存业务的请求dto
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/6/10
 */
public class PackageStashRequest {

    /**
     * EMG码
     */
    private String emgCode;

    /**
     * 请求数量
     */
    private Integer number;

    /**
     * 操作人ERP
     */
    private String userCode;

    /**
     * 毫秒时间戳
     */
    private Long createTime;

    public String getEmgCode() {
        return emgCode;
    }

    public void setEmgCode(String emgCode) {
        this.emgCode = emgCode;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
