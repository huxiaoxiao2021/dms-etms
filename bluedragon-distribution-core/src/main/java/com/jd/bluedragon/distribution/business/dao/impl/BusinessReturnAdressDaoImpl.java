package com.jd.bluedragon.distribution.business.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.bluedragon.distribution.business.dao.BusinessReturnAdressDao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

/**
 * @ClassName: BusinessReturnAdressDaoImpl
 * @Description: 商家退货地址信息--Dao接口实现
 * @author wuyoude
 * @date 2020年07月28日 16:45:14
 *
 */
@Repository("businessReturnAdressDao")
public class BusinessReturnAdressDaoImpl extends BaseDao<BusinessReturnAdress> implements BusinessReturnAdressDao {

	@Override
	public BusinessReturnAdress queryBusinessReturnAdressByBusiId(Integer busiId) {
		return sqlSession.selectOne(this.nameSpace+".queryBusinessReturnAdressByBusiId", busiId);
	}

	@Override
	public PagerResult<BusinessReturnAdress> queryListByConditionWithPage(BusinessReturnAdressCondition businessReturnAdressCondition) {
		return super.queryByPagerCondition("queryListByConditionWithPage", businessReturnAdressCondition);
	}
}
