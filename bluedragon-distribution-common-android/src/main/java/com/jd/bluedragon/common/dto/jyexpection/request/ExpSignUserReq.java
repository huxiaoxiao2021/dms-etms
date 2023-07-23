package com.jd.bluedragon.common.dto.jyexpection.request;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/31 10:26
 * @Description: 异常岗签到人
 */
public class ExpSignUserReq extends ExpBaseReq implements Serializable {

    /**
     * 异常签到用户erp
     */
    private String expUserErp;

    /**
     * 异常签到用户id
     */
    private String expUserCode;


    private Integer pageNumber;

    private Integer pageSize;

    private Integer offSet;


    public String getExpUserErp() {
        return expUserErp;
    }

    public void setExpUserErp(String expUserErp) {
        this.expUserErp = expUserErp;
    }

    public String getExpUserCode() {
        return expUserCode;
    }

    public void setExpUserCode(String expUserCode) {
        this.expUserCode = expUserCode;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffSet() {
        if (pageNumber == null || pageSize == null) {
            return 0;
        }
        return (pageNumber - 1) * pageSize;
    }

    public void setOffSet(Integer offSet) {
        if (pageNumber == null || pageSize == null) {
            this.offSet = 0;
        }else {
            this.offSet = (pageNumber - 1) * pageSize;
        }
    }
}
