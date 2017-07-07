package com.jd.bluedragon.distribution.sortscheme.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yangbo7 on 2016/6/22.
 */
public class SortScheme implements Serializable {

    private static final long serialVersionUID = 7402935664670381056L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 分拣方案名称
     */
    private String name;

    /**
     * 中转场代码，比如010X
     */
    private String siteNo;

    private String siteName;

    /**
     * 分拣机代码
     */
    private String machineCode;

    /**
     * 分拣模式:1最近,2瀑布,3循环
     */
    private String sortMode;

    /**
     * 接收标识 0：未接收 1：已接收
     */
    private Integer receFlag;

    /**
     * 数据接收时间
     */
    private String receTime;

    /**
     * 是否自动发货 0：否 1：是
     */
    private Integer autoSend;

    /**
     * 数据库时间
     */
    private Date timesTamp;

    /**
     * 0不生效, 1生效
     */
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(String siteNo) {
        this.siteNo = siteNo;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getSortMode() {
        return sortMode;
    }

    public void setSortMode(String sortMode) {
        this.sortMode = sortMode;
    }

    public Integer getReceFlag() {
        return receFlag;
    }

    public void setReceFlag(Integer receFlag) {
        this.receFlag = receFlag;
    }

    public String getReceTime() {
        return receTime;
    }

    public void setReceTime(String receTime) {
        this.receTime = receTime;
    }

    public Integer getAutoSend() {
        return autoSend;
    }

    public void setAutoSend(Integer autoSend) {
        this.autoSend = autoSend;
    }

    public Date getTimesTamp() {
        return timesTamp;
    }

    public void setTimesTamp(Date timesTamp) {
        this.timesTamp = timesTamp;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
