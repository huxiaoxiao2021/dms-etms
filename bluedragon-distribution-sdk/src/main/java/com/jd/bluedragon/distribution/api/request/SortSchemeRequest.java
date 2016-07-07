package com.jd.bluedragon.distribution.api.request;

/**
 * Created by yangbo7 on 2016/6/22.
 */
public class SortSchemeRequest {


    private static final long serialVersionUID = 5799267676878153722L;

    /**
     * 主键
     */
    private Long id;

    private String name;

    /**
     * 中转场代码，比如010X
     */
    private String siteNo;

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

    private Integer pageNo;

    private Integer pageSize;

    public static boolean validateSortMode(String sortMode) {
        if (sortMode == null || sortMode.length() <= 0) {
            return false;
        }
        if ("1".equals(sortMode) || "2".equals(sortMode) || "3".equals(sortMode)) {
            return true;
        }
        return false;
    }

    public static boolean validateReceFlag(Integer receFlag) {
        if (receFlag == null || receFlag <= 0) {
            return false;
        }
        if (receFlag == 1 || receFlag == 2) {
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
