package com.jd.bluedragon.distribution.config.dao;

import java.util.List;

import com.jd.bluedragon.distribution.config.model.ConfigStrandReason;
import com.jd.bluedragon.distribution.config.query.ConfigStrandReasonQuery;

/**
 * @ClassName: ConfigStrandReasonDao
 * @Description: 滞留原因配置表--Dao接口
 * @author liuduo8
 * @date 2022年02月18日 16:12:56
 *
 */
public interface ConfigStrandReasonDao {
	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	int insert(ConfigStrandReason insertData);
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	int updateById(ConfigStrandReason updateData);
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	int deleteById(ConfigStrandReason deleteData);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	ConfigStrandReason queryById(Long id);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	List<ConfigStrandReason> queryList(ConfigStrandReasonQuery query);
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	Long queryCount(ConfigStrandReasonQuery query);
	/**
	 * 根据reasonCode查询
	 * @param id
	 * @return
	 */
	ConfigStrandReason queryByReasonCode(Integer reasonCode);

}
