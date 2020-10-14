package com.jd.bluedragon.distribution.business.service;

import java.util.List;

import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 * @ClassName: BusinessReturnAdressService
 * @Description: 商家退货地址信息--Service接口
 * @author wuyoude
 * @date 2020年07月28日 16:45:14
 *
 */
public interface BusinessReturnAdressService extends Service<BusinessReturnAdress> {
	/**
	 * 获取导出数据
	 * @param businessReturnAdressCondition
	 * @return
	 */
	List<List<Object>> queryBusinessReturnAdressExcelData(BusinessReturnAdressCondition businessReturnAdressCondition);
	/**
	 * 分页查询
	 * @param businessReturnAdressCondition
	 * @return
	 */
	PagerResult<BusinessReturnAdress> queryBusinessReturnAdressListByPagerCondition(BusinessReturnAdressCondition businessReturnAdressCondition);
	/**
	 * 根据商家编码获取退货维护信息
	 * @param busiId
	 * @return
	 */
	BusinessReturnAdress queryBusinessReturnAdressByBusiId(Integer busiId);
	/**
	 * 添加一条记录
	 * @param businessReturnAdress
	 * @return
	 */
	boolean add(BusinessReturnAdress businessReturnAdress);
	/**
	 * 更新数据
	 * @param businessReturnAdress
	 * @return
	 */
	boolean update(BusinessReturnAdress businessReturnAdress);
}
