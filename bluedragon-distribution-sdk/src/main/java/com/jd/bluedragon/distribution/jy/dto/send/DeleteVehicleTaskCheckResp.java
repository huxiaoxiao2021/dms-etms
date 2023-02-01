package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;

/**
 * 任务删除-校验返回实体
 */
public class DeleteVehicleTaskCheckResp implements Serializable {
	
	private static final long serialVersionUID = -3233317634380491930L;
	/**
	 * 是否需要密码校验
	 */
	private Boolean needCheckPassword;

	public Boolean getNeedCheckPassword() {
		return needCheckPassword;
	}

	public void setNeedCheckPassword(Boolean needCheckPassword) {
		this.needCheckPassword = needCheckPassword;
	}
}
