package com.jd.bluedragon.distribution.config.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.config.dao.ConfigStrandReasonDao;
import com.jd.bluedragon.distribution.config.model.ConfigStrandReason;
import com.jd.bluedragon.distribution.config.query.ConfigStrandReasonQuery;
import com.jd.bluedragon.distribution.config.service.ConfigStrandReasonService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @ClassName: ConfigStrandReasonServiceImpl
 * @Description: 滞留原因配置表--Service接口实现
 * @author liuduo8
 * @date 2022年02月18日 16:12:56
 *
 */
@Service("configStrandReasonService")
public class ConfigStrandReasonServiceImpl implements ConfigStrandReasonService {

	@Autowired
	@Qualifier("configStrandReasonDao")
	private ConfigStrandReasonDao configStrandReasonDao;

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	public Result<Boolean> insert(ConfigStrandReason insertData){
		Result<Boolean> result = Result.success();
		if(insertData.getReasonCode() == null) {
			return result.toFail("原因编码不能为空！");
		}
		if(configStrandReasonDao.queryByReasonCode(insertData.getReasonCode()) != null) {
			return result.toFail("原因编码已存在，请修改！");
		}
		result.setData(configStrandReasonDao.insert(insertData) == 1);
		return result;
	 }
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	public Result<Boolean> updateById(ConfigStrandReason updateData){
		Result<Boolean> result = Result.success();
		result.setData(configStrandReasonDao.updateById(updateData) == 1);
		return result;
	 }
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	public Result<Boolean> deleteById(ConfigStrandReason deleteData){
		Result<Boolean> result = Result.success();
		result.setData(configStrandReasonDao.deleteById(deleteData) == 1);
		return result;
	 }
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	public Result<ConfigStrandReason> queryById(Long id){
		Result<ConfigStrandReason> result = Result.success();
		result.setData(configStrandReasonDao.queryById(id));
		return result;
	 }
	@Override
	public Result<ConfigStrandReason> queryByReasonCode(Integer reasonCode) {
		Result<ConfigStrandReason> result = Result.success();
		result.setData(configStrandReasonDao.queryByReasonCode(reasonCode));
		return result;
	}	
	/**
	 * 按条件分页查询
	 * businessTag = 1 or 2 分别代表 默认 + 冷链
	 * 不传businessTagList，代表查询全部
	 * @param query
	 * @return
	 */
	public Result<PageDto<ConfigStrandReason>> queryPageList(ConfigStrandReasonQuery query){
		Result<PageDto<ConfigStrandReason>> result = Result.success();
		Result<Boolean> checkResult = this.checkParamForQueryPageList(query);
		if(!checkResult.isSuccess()){
		    return Result.fail(checkResult.getMessage());
		}
		PageDto<ConfigStrandReason> pageData = new PageDto<>(query.getPageNumber(), query.getLimit());
		int totalCount = 0;
		Long count = configStrandReasonDao.queryCount(query);
		if(count != null) {
			totalCount = count.intValue();
		}
		if(totalCount > 0){
			pageData.setResult(configStrandReasonDao.queryList(query));
		}else {
			pageData.setResult(new ArrayList<ConfigStrandReason>());
		}
		pageData.setTotalRow(totalCount);
		result.setData(pageData);
		return result;
	 }
	/**
	 * 查询参数校验
	 * @param query
	 * @return
	 */
	public Result<Boolean> checkParamForQueryPageList(ConfigStrandReasonQuery query){
		Result<Boolean> result = Result.success();
		if(query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getLimit());
		};
		return result;
	 }

}
