package com.jd.bluedragon.common.dto.sendcode.request;

import java.io.Serializable;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperateUser;

/**
 * @author : wuyoude
 * @date : 2022/10/12
 */
public class SendCodeSealInfoQuery extends BaseReq implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    /**
     * 扫描编码
     */
    private String scanCode;
    
	public String getScanCode() {
		return scanCode;
	}
	public void setScanCode(String scanCode) {
		this.scanCode = scanCode;
	}
}
