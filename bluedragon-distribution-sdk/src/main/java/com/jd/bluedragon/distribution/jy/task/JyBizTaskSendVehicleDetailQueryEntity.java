package com.jd.bluedragon.distribution.jy.task;


import java.io.Serializable;
import java.util.List;

/**
 * 发车任务明细表
 *
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JyBizTaskSendVehicleDetailQueryEntity extends JyBizTaskSendVehicleDetailEntity implements Serializable {

	private static final long serialVersionUID = 4089383783438643445L;

	private List<Long> endSiteIdList;

	private Integer pageSize;

    private Integer manualCreatedFlag;
	private List<String> bizIdList;


	public List<Long> getEndSiteIdList() {
		return endSiteIdList;
	}

	public void setEndSiteIdList(List<Long> endSiteIdList) {
		this.endSiteIdList = endSiteIdList;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

    public Integer getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Integer manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }

	public List<String> getBizIdList() {
		return bizIdList;
	}

	public void setBizIdList(List<String> bizIdList) {
		this.bizIdList = bizIdList;
	}
}
