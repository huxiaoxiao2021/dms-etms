package com.jd.bluedragon.distribution.config.query;

import java.io.Serializable;
import java.util.List;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 滞留原因配置表-查询条件实体类
 * 
 * @author wuyoude
 * @date 2022年01月25日 11:09:42
 *
 */
public class ConfigStrandReasonQuery extends BasePagerCondition implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 原因编码
	 */
	private Integer reasonCode;
	/**
	 * 业务条线
	 * 1：默认
	 * 2：冷链
	 */
	private List<Integer> businessTagList;

	public Integer getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(Integer reasonCode) {
		this.reasonCode = reasonCode;
	}

	public List<Integer> getBusinessTagList() {
		return businessTagList;
	}

	public void setBusinessTagList(List<Integer> businessTagList) {
		this.businessTagList = businessTagList;
	}
}
