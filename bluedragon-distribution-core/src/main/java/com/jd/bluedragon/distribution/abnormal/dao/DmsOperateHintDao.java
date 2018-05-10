package com.jd.bluedragon.distribution.abnormal.dao;

import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.ql.dms.common.web.mvc.api.Dao;

/**
 *
 * @ClassName: DmsOperateHintDao
 * @Description: 运单操作提示--Dao接口
 * @author wuyoude
 * @date 2018年03月19日 10:10:39
 *
 */
public interface DmsOperateHintDao extends Dao<DmsOperateHint> {
	/**
	 * 根据运单号查询，补打提示信息
	 * @param waybillCode
	 * @return
	 */
	DmsOperateHint queryNeedReprintHintByWaybillCode(String waybillCode);
}
