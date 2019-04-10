package com.jd.bluedragon.distribution.batch.domain;

import java.util.Date;
import java.util.List;

public class BatchSendRequest {

    /**
     * 波次号
     */
    private String batchCode;

    /**
     * 创建站点编号
     */
    private Integer createSiteCode;
    
    /**
     * 目的站点编号
     */
    private String receiveCodes;
    
    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 最后修改时间
     */
    private Date updateTime;

    private List<Integer> receiveCodeList;


	public String getBatchCode() {
		return batchCode;
	}


	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}


	public Integer getCreateSiteCode() {
		return createSiteCode;
	}


	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public String getReceiveCodes() {
		return receiveCodes;
	}


	public void setReceiveCodes(String receiveCodes) {
		this.receiveCodes = receiveCodes;
	}


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public Date getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

    public void setReceiveCodeList(List<Integer> receiveCodeList) {
        this.receiveCodeList = receiveCodeList;
    }

    public List<Integer> getReceiveCodeList() {
        return receiveCodeList;
    }
}
