package com.jd.bluedragon.distribution.consumable.controller;

import java.util.List;

import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: WaybillConsumableRelationController
 * @Description: 运单耗材关系表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("consumable/waybillConsumableRelation")
public class WaybillConsumableRelationController extends DmsBaseController{

	private static final Log logger = LogFactory.getLog(WaybillConsumableRelationController.class);

	@Autowired
	private WaybillConsumableRelationService waybillConsumableRelationService;

	@Autowired
	private WaybillConsumableRecordService waybillConsumableRecordService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/consumable/waybillConsumableRelation";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<WaybillConsumableRelation> detail(@PathVariable("id") Long id) {
		JdResponse<WaybillConsumableRelation> rest = new JdResponse<WaybillConsumableRelation>();
		rest.setData(waybillConsumableRelationService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param waybillConsumableRelation
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody WaybillConsumableRelation waybillConsumableRelation) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(waybillConsumableRelationService.saveOrUpdate(waybillConsumableRelation));
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
			rest.setData(waybillConsumableRelationService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param waybillConsumableRelationCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<WaybillConsumableRelation> listData(@RequestBody WaybillConsumableRelationCondition waybillConsumableRelationCondition) {
		JdResponse<PagerResult<WaybillConsumableRelation>> rest = new JdResponse<PagerResult<WaybillConsumableRelation>>();
		rest.setData(waybillConsumableRelationService.queryByPagerCondition(waybillConsumableRelationCondition));
		return rest.getData();
	}
}
