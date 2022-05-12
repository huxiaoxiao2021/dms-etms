package com.jd.bluedragon.distribution.config.service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.config.model.ConfigStrandReason;
import com.jd.bluedragon.distribution.config.query.ConfigStrandReasonQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
/**
 * @ClassName: ConfigStrandReasonService
 * @Description: 滞留原因配置表--Service接口
 * @author liuduo8
 * @date 2022年02月18日 16:12:56
 *
 */
public interface ConfigStrandReasonService {
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
	 * businessTag = 1 or 2 分别代表 默认 + 冷链
	 * 不传businessTagList，代表查询全部
	 * @param query
	 * @return
	 */
	Result<PageDto<ConfigStrandReason>> queryPageList(ConfigStrandReasonQuery query);
}
