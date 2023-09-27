package com.jd.bluedragon.distribution.jy.dto.common;

import java.io.Serializable;

import com.jd.bluedragon.distribution.api.domain.OperatorData;

/**
 * @ClassName: JyOperateFlowData
 * @Description: 分拣-业务操作流水数据-实体类
 * @author wuyoude
 * @date 2023年09月12日 17:07:43
 *
 */
public class JyOperateFlowData implements Serializable {

	private static final long serialVersionUID = 1L;
	
    /**
     * 操作信息对象
     */
	private OperatorData operatorData;

	public OperatorData getOperatorData() {
		return operatorData;
	}

	public void setOperatorData(OperatorData operatorData) {
		this.operatorData = operatorData;
	}
}
