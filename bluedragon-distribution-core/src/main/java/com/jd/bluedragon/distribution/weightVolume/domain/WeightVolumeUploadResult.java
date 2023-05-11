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

}
