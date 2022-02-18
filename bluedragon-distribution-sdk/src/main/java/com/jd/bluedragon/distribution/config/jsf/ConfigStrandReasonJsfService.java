package com.jd.bluedragon.distribution.config.jsf;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.config.model.ConfigStrandReason;
import com.jd.bluedragon.distribution.config.query.ConfigStrandReasonQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 无滑道包裹明细
 *
 * @author wuyoude
 * @copyright jd.com 京东物流JDL
 */
public interface ConfigStrandReasonJsfService {
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	Result<Boolean> insert(ConfigStrandReason insertData);
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	Result<Boolean> updateById(ConfigStrandReason updateData);
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	Result<Boolean> deleteById(ConfigStrandReason deleteData);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	Result<ConfigStrandReason> queryById(Long id);
	/**
	 * 根据reasonCode查询
	 * @param id
	 * @return
	 */
	Result<ConfigStrandReason> queryByReasonCode(Integer reasonCode);	
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	Result<PageDto<ConfigStrandReason>> queryPageList(ConfigStrandReasonQuery query);    
}
