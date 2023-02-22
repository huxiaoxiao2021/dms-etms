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
	
    /**
     * 是否可以使用功能
     */
    private Integer canUse;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 提示类型
     */
    private Integer msgType;
	
	public String getMenuCode() {
		return menuCode;
	}
	public void setMenuCode(String menuCode) {
		this.menuCode = menuCode;
	}
	public Integer getCanUse() {
		return canUse;
	}
	public void setCanUse(Integer canUse) {
		this.canUse = canUse;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Integer getMsgType() {
		return msgType;
	}
	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}
}
