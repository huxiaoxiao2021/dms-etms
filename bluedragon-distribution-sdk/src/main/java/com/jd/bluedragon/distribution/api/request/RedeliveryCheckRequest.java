package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * 协商再投状态校验请求对象
 */
public class RedeliveryCheckRequest extends JdRequest {
    /**
     * 条码类型 1：包裹 2：运单 3：箱号
     */
    private Integer codeType;

    /**
     * 条码内容
     */
    private String code;
    
    /**
     * 异常id
     */
    private Integer exceptionId;
    /**
     * 异常上级id
     */
    private Integer supExceptionId;    
    

    public Integer getCodeType() {
        return codeType;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

	public Integer getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(Integer exceptionId) {
		this.exceptionId = exceptionId;
	}

	public Integer getSupExceptionId() {
		return supExceptionId;
	}

	public void setSupExceptionId(Integer supExceptionId) {
		this.supExceptionId = supExceptionId;
	}

}
