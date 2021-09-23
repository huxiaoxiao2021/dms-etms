package com.jd.bluedragon.distribution.client.domain;

import java.io.Serializable;

/**
 * 菜单权限验证返回
 * @author wuyoude
 *
 */
public class CheckMenuAuthResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 菜单编码
	 */
	private String menuCode;
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}

}
