package com.jd.bluedragon.distribution.kuaiyun.weight.domain;

import java.io.Serializable;

import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeUploadResult;

public class WaybillWeightCheckResult implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Boolean isExists;
    
    private Integer verifyCode;
    
    private String verifyMessage;
    
    private WeightVolumeUploadResult weightVolumeCheckResult;

	public Boolean getIsExists() {
		return isExists;
	}

	public void setIsExists(Boolean isExists) {
		this.isExists = isExists;
	}

	public Integer getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(Integer verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getVerifyMessage() {
		return verifyMessage;
	}

	public void setVerifyMessage(String verifyMessage) {
		this.verifyMessage = verifyMessage;
	}

	public WeightVolumeUploadResult getWeightVolumeCheckResult() {
		return weightVolumeCheckResult;
	}

	public void setWeightVolumeCheckResult(WeightVolumeUploadResult weightVolumeCheckResult) {
		this.weightVolumeCheckResult = weightVolumeCheckResult;
	}
    
}
