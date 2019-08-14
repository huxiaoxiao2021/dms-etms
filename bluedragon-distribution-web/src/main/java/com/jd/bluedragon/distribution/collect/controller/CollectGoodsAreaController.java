package com.jd.bluedragon.distribution.collect.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsArea;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsAreaCondition;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsAreaService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: CollectGoodsAreaController
 * @Description: 集货区表--Controller实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Controller
@RequestMapping("collect/collectGoodsArea")
public class CollectGoodsAreaController {

	private static final Log logger = LogFactory.getLog(CollectGoodsAreaController.class);

	@Autowired
	private CollectGoodsAreaService collectGoodsAreaService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/collect/collectGoodsArea";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<CollectGoodsArea> detail(@PathVariable("id") Long id) {
		JdResponse<CollectGoodsArea> rest = new JdResponse<CollectGoodsArea>();
		rest.setData(collectGoodsAreaService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param collectGoodsArea
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody CollectGoodsArea collectGoodsArea) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(collectGoodsAreaService.saveOrUpdate(collectGoodsArea));
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
			rest.setData(collectGoodsAreaService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param collectGoodsAreaCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<CollectGoodsArea> listData(@RequestBody CollectGoodsAreaCondition collectGoodsAreaCondition) {
		JdResponse<PagerResult<CollectGoodsArea>> rest = new JdResponse<PagerResult<CollectGoodsArea>>();
		rest.setData(collectGoodsAreaService.queryByPagerCondition(collectGoodsAreaCondition));
		return rest.getData();
	}
}
