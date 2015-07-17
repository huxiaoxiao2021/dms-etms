package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;

public class MessageResult implements Serializable {
    
    
    private String transferId;
    private String returnFlag;
    private String errorMessage;
	public String getTransferId() {
		return transferId;
	}
	public void setTransferId(String transferId) {
		this.transferId = transferId;
	}
	public String getReturnFlag() {
		return returnFlag;
	}
	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}