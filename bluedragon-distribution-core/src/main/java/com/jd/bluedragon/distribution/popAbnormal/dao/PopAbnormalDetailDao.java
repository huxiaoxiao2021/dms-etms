package com.jd.bluedragon.distribution.popAbnormal.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-21 下午02:10:00
 *
 * POP差异订单Dao
 */
public class PopAbnormalDetailDao extends BaseDao<PopAbnormalDetail> {

	public static final String NAME_SPACE = PopAbnormalDetailDao.class.getName();

	public PopAbnormalDetail findByObj(PopAbnormalDetail popAbnormalDetail) {
		Object obj = this.getSqlSession().selectOne(NAME_SPACE + ".findByObj", popAbnormalDetail);
		popAbnormalDetail = (obj == null) ? null : (PopAbnormalDetail)obj;
		return popAbnormalDetail;
	}

	@SuppressWarnings("unchecked")
	public List<PopAbnormalDetail> findListByAbnormalId(Long abnormalId) {
		Object obj = this.getSqlSession().selectList(NAME_SPACE + ".findListByAbnormalId", abnormalId);
		List<PopAbnormalDetail> popAbnormalDetails = (obj == null) ? null : (List<PopAbnormalDetail>)obj;
		return popAbnormalDetails;
	}
}
