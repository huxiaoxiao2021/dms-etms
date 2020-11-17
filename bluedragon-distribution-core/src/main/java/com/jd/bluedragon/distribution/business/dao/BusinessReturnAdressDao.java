package com.jd.bluedragon.distribution.business.dao;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

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

    /**
     * 根据商家ID查询未维护退货地址的数据
     * @param businessId
     * @return
     */
    List<BusinessReturnAdress> queryByBusinessIdWithNoMaintain(Integer businessId);

    /**
     * 根据商家ID站点ID查询退货地址
     * @param businessReturnAddress
     * @return
     */
    BusinessReturnAdress queryBySiteAndBusinessId(BusinessReturnAdress businessReturnAddress);

    /**
     * 批量更新退货地址状态
     * @param list
     * @return
     */
    int batchUpdateStatus(List<BusinessReturnAdress> list);

    /**
     * 根据id更新退货量
     * @param businessReturnAddress
     * @return
     */
    int updateReturnQuantity(BusinessReturnAdress businessReturnAddress);
}
