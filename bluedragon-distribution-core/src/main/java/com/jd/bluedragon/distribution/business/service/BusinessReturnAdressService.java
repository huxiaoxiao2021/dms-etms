package com.jd.bluedragon.distribution.business.service;

import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdress;
import com.jd.bluedragon.distribution.business.entity.BusinessReturnAdressCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.io.BufferedWriter;
import java.util.List;

/**
 * @ClassName: BusinessReturnAdressService
 * @Description: 商家退货地址信息--Service接口
 * @author wuyoude
 * @date 2020年07月28日 16:45:14
 *
 */
public interface BusinessReturnAdressService extends Service<BusinessReturnAdress> {
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

    /**
     * 根据商家ID查询未维护退货地址数据
     * @param businessId
     * @return
     */
    List<BusinessReturnAdress> queryByBusinessIdWithNoMaintain(Integer businessId);

    /**
     * 根据商家ID站点ID查询退货地址
     * @param dmsSiteCode
     * @param businessId
     * @return
     */
    BusinessReturnAdress queryBySiteAndBusinessId(Integer dmsSiteCode, Integer businessId);

    /**
     * 根据id更新退货量
     * @param businessReturnAddress
     * @return
     */
    int updateReturnQuantity(BusinessReturnAdress businessReturnAddress);

    /**
     * 根据商家ID更新状态
     * @param businessId
     */
    int updateStatusByBusinessId(Integer businessId);

	/**
	 * 数据导出
	 * @param businessReturnAdressCondition
	 * @param bfw
	 */
	void export(BusinessReturnAdressCondition businessReturnAdressCondition, BufferedWriter bfw);
}
