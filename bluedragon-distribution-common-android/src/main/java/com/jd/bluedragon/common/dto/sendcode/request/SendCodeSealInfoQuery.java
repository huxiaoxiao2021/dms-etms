package com.jd.bluedragon.common.dto.sendcode.request;

import java.io.Serializable;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperateUser;

/**
 * @author : wuyoude
 * @date : 2022/10/12
 */
public class SendCodeSealInfoQuery implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    /**
     * 扫描编码
     */
    private String scanCode;
    
    private OperateUser operateUser;
    
    private CurrentOperate currentOperate;
    
	public String getScanCode() {
		return scanCode;
	}
	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}
	public OperateUser getOperateUser() {
		return operateUser;
	}
	public void setOperateUser(OperateUser operateUser) {
		this.operateUser = operateUser;
	}
	public CurrentOperate getCurrentOperate() {
		return currentOperate;
	}
	public void setCurrentOperate(CurrentOperate currentOperate) {
		this.currentOperate = currentOperate;
	}
}
