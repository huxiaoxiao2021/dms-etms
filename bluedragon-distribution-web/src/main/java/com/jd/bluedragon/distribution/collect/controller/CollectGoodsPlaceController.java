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

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlace;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceCondition;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: CollectGoodsPlaceController
 * @Description: 集货位表--Controller实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Controller
@RequestMapping("collect/collectGoodsPlace")
public class CollectGoodsPlaceController {

	private static final Log logger = LogFactory.getLog(CollectGoodsPlaceController.class);

	@Autowired
	private CollectGoodsPlaceService collectGoodsPlaceService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/collect/collectGoodsPlace";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<CollectGoodsPlace> detail(@PathVariable("id") Long id) {
		JdResponse<CollectGoodsPlace> rest = new JdResponse<CollectGoodsPlace>();
		rest.setData(collectGoodsPlaceService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param collectGoodsPlace
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody CollectGoodsPlace collectGoodsPlace) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(collectGoodsPlaceService.saveOrUpdate(collectGoodsPlace));
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
			rest.setData(collectGoodsPlaceService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param collectGoodsPlaceCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<CollectGoodsPlace> listData(@RequestBody CollectGoodsPlaceCondition collectGoodsPlaceCondition) {
		JdResponse<PagerResult<CollectGoodsPlace>> rest = new JdResponse<PagerResult<CollectGoodsPlace>>();
		rest.setData(collectGoodsPlaceService.queryByPagerCondition(collectGoodsPlaceCondition));
		return rest.getData();
	}
}
