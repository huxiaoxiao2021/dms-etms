package com.jd.bluedragon.distribution.consumable.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: DmsConsumableRelationController
 * @Description: 分拣中心耗材关系表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("consumable/dmsConsumableRelation")
public class DmsConsumableRelationController {

	private static final Log logger = LogFactory.getLog(DmsConsumableRelationController.class);

	@Autowired
	private DmsConsumableRelationService dmsConsumableRelationService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/consumable/dmsConsumableRelation";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<DmsConsumableRelation> detail(@PathVariable("id") Long id) {
		JdResponse<DmsConsumableRelation> rest = new JdResponse<DmsConsumableRelation>();
		rest.setData(dmsConsumableRelationService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param dmsConsumableRelation
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsConsumableRelation dmsConsumableRelation) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(dmsConsumableRelationService.saveOrUpdate(dmsConsumableRelation));
	} catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(dmsConsumableRelationService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param dmsConsumableRelationCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DmsConsumableRelation> listData(@RequestBody DmsConsumableRelationCondition dmsConsumableRelationCondition) {
		JdResponse<PagerResult<DmsConsumableRelation>> rest = new JdResponse<PagerResult<DmsConsumableRelation>>();
		rest.setData(dmsConsumableRelationService.queryByPagerCondition(dmsConsumableRelationCondition));
		return rest.getData();
	}
}
