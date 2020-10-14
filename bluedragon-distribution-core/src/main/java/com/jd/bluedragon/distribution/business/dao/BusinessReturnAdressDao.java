package com.jd.bluedragon.distribution.business.dao;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * @ClassName: BusinessReturnAdressDao
 * @Description: 商家退货地址信息--Dao接口
 * @author wuyoude
 * @date 2020年07月28日 16:45:14
 *
 */
@Repository("businessReturnAdressDao")
public interface BusinessReturnAdressDao extends Dao<BusinessReturnAdress> {
	/**
	 * 根据商家id查询
	 * @param busiId
	 * @return
	 */
	BusinessReturnAdress queryBusinessReturnAdressByBusiId(Integer busiId);
	/**
	 * 分页查询数据,返回当前实体的分页对象
	 * @param businessReturnAdressCondition
	 * @return
	 */
	PagerResult<BusinessReturnAdress> queryListByConditionWithPage(BusinessReturnAdressCondition businessReturnAdressCondition);
}
