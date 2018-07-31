package com.jd.bluedragon.distribution.abnormal.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.abnormal.dao.DmsOperateHintDao;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintCondition;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

/**
 *
 * @ClassName: DmsOperateHintDaoImpl
 * @Description: 运单操作提示--Dao接口实现
 * @author wuyoude
 * @date 2018年03月19日 10:10:39
 *
 */
@Repository("dmsOperateHintDao")
public class DmsOperateHintDaoImpl extends BaseDao<DmsOperateHint> implements DmsOperateHintDao {

	@Override
	public DmsOperateHint queryNeedReprintHintByWaybillCode(String waybillCode) {
		DmsOperateHintCondition condition = new DmsOperateHintCondition();
		condition.setWaybillCode(waybillCode);
		condition.setHintType(DmsOperateHint.HINT_TYPE_SYS);
		condition.setHintCode(DmsOperateHint.HINT_CODE_NEED_REPRINT);
		return sqlSession.selectOne(nameSpace+".queryNeedReprintHintByWaybillCode", condition);
	}


	/**
	 * 根据运单号获取出去开启状态的提示信息
	 * @param dmsOperateHint
	 * @return
	 */
	@Override
	public DmsOperateHint getEnabledOperateHint(DmsOperateHint dmsOperateHint){
		return sqlSession.selectOne(nameSpace+".getEnabledOperateHint", dmsOperateHint);
	}

}
