package com.jd.bluedragon.common.dto.operation.workbench.unload.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName UnsealVehicleTaskRequest
 * @Description 解封车任务请求
 * @Author wyd
 * @Date 2022/5/20 7:16
 **/
public class UnsealVehicleTaskRequest implements Serializable {

	private static final long serialVersionUID = 1L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 目的分拣中心
     */
    private Integer endSiteCode;

    /**
     * 状态列表
     */
    private List<Integer> statusCodeList;

    /**
     * 车牌号
     */
    private String vehicleNumber;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CurrentOperate getCurrentOperate() {
		return currentOperate;
	}

	public void setCurrentOperate(CurrentOperate currentOperate) {
		this.currentOperate = currentOperate;
	}

	public Integer getEndSiteCode() {
		return endSiteCode;
	}

	public void setEndSiteCode(Integer endSiteCode) {
		this.endSiteCode = endSiteCode;
	}

	public List<Integer> getStatusCodeList() {
		return statusCodeList;
	}

	public void setStatusCodeList(List<Integer> statusCodeList) {
		this.statusCodeList = statusCodeList;
	}

	public String getVehicleNumber() {
		return vehicleNumber;
	}

	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
}
