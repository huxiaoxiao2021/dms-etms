package com.jd.bluedragon.distribution.config.jsf.impl;


import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.config.constants.StrandReasonBusinessTagEnum;
import com.jd.tp.common.utils.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.config.jsf.ConfigStrandReasonJsfService;
import com.jd.bluedragon.distribution.config.model.ConfigStrandReason;
import com.jd.bluedragon.distribution.config.query.ConfigStrandReasonQuery;
import com.jd.bluedragon.distribution.config.service.ConfigStrandReasonService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 滞留原因配置表--JsfService接口实现
 * 
 * @author wuyoude
 * @date 2022年01月25日 11:09:42
 *
 */
@Service("configStrandReasonJsfService")
public class ConfigStrandReasonJsfServiceImpl implements ConfigStrandReasonJsfService {

	@Autowired
	@Qualifier("configStrandReasonService")
	private ConfigStrandReasonService configStrandReasonService;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(ConfigStrandReason insertData){
		return configStrandReasonService.insert(insertData);
	 }
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(ConfigStrandReason updateData){
		return configStrandReasonService.updateById(updateData);
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(ConfigStrandReason deleteData){
		return configStrandReasonService.deleteById(deleteData);
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<ConfigStrandReason> queryById(Long id){
		return configStrandReasonService.queryById(id);
	 }
	@Override
	public Result<ConfigStrandReason> queryByReasonCode(Integer reasonCode) {
		return configStrandReasonService.queryByReasonCode(reasonCode);
	}	
	/**
	 * 按条件分页查询
	 * businessTag = 1
	 * 查询默认
	 * @param query
	 * @return
	 */
	public Result<PageDto<ConfigStrandReason>> queryPageList(ConfigStrandReasonQuery query){
		List<Integer> businessTagList = new ArrayList<>(1);
		businessTagList.add(StrandReasonBusinessTagEnum.BUSINESS_TAG_DEFAULT.getCode());
		query.setBusinessTagList(businessTagList);
		return configStrandReasonService.queryPageList(query);
	 }

	/**
	 * 按条件分页查询
	 * businessTag = 1 or 2 分别代表 默认 + 冷链
	 * @param query
	 * @return
	 */
	@Override
	public Result<PageDto<ConfigStrandReason>> queryPageListByBusinessTagList(ConfigStrandReasonQuery query) {
		if(query == null){
			return Result.fail("参数不能为空");
		}
		return configStrandReasonService.queryPageList(query);
	}

	/**
	 * 删除冷链滞留原因
	 * businessTag=2的
	 * @param deleteData
	 * @return
	 */
	@Override
	public Result<Boolean> deleteColdReasonById(ConfigStrandReason deleteData) {
		Result<ConfigStrandReason> reasonResult = configStrandReasonService.queryById(deleteData.getId());
		if(reasonResult.isSuccess() && reasonResult.getData() != null && Objects.equals(reasonResult.getData().getBusinessTag(),StrandReasonBusinessTagEnum.BUSINESS_TAG_COLD.getCode())){
			return configStrandReasonService.deleteById(deleteData);
		}
		Result<Boolean> result = Result.fail("无权限删除或数据不存在");
		return result;
	}
}
