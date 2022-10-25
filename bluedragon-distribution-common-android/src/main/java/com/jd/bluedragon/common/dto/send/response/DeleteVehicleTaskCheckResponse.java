package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;
/**
 * 任务删除-校验返回实体
 * @author wuyoude
 *
 */
public class DeleteVehicleTaskCheckResponse  implements Serializable {
	
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
