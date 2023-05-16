package com.jd.bluedragon.distribution.weightVolume.domain;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *     分拣称重量方上传结果对象
 *
 * @author wuyoude
 * @since 2023/05/04
 **/
public class WeightVolumeUploadResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * 校验结果
	 */
	private Boolean checkResult;
	
	private Boolean overLengthAndWeightFlag;
	
	private Boolean needConfirm;
	/**
	 * 是否已有-超重信息
	 */
	private Boolean hasOverLengthAndWeight = Boolean.FALSE;	
	
	private List<OverLengthAndWeightType> overLengthAndWeightTypesToSelect;

	public Boolean getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(Boolean checkResult) {
		this.checkResult = checkResult;
	}

	public Boolean getOverLengthAndWeightFlag() {
		return overLengthAndWeightFlag;
	}

	public void setOverLengthAndWeightFlag(Boolean overLengthAndWeightFlag) {
		this.overLengthAndWeightFlag = overLengthAndWeightFlag;
	}

	public Boolean getNeedConfirm() {
		return needConfirm;
	}

	public void setNeedConfirm(Boolean needConfirm) {
		this.needConfirm = needConfirm;
	}

	public List<OverLengthAndWeightType> getOverLengthAndWeightTypesToSelect() {
		return overLengthAndWeightTypesToSelect;
	}

	public void setOverLengthAndWeightTypesToSelect(List<OverLengthAndWeightType> overLengthAndWeightTypesToSelect) {
		this.overLengthAndWeightTypesToSelect = overLengthAndWeightTypesToSelect;
	}

	public Boolean getHasOverLengthAndWeight() {
		return hasOverLengthAndWeight;
	}

	public void setHasOverLengthAndWeight(Boolean hasOverLengthAndWeight) {
		this.hasOverLengthAndWeight = hasOverLengthAndWeight;
	}

}
