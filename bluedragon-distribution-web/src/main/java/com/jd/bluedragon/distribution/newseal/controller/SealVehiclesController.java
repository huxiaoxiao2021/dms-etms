package com.jd.bluedragon.distribution.newseal.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.domain.SealVehiclesCondition;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: SealVehiclesController
 * @Description: 封车数据表--Controller实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Controller
@RequestMapping("newseal/sealVehicles")
public class SealVehiclesController {

	private static final Log logger = LogFactory.getLog(SealVehiclesController.class);

	@Autowired
	private SealVehiclesService sealVehiclesService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/newseal/sealVehicles";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<SealVehicles> detail(@PathVariable("id") Long id) {
		JdResponse<SealVehicles> rest = new JdResponse<SealVehicles>();
		rest.setData(sealVehiclesService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param sealVehicles
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody SealVehicles sealVehicles) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(sealVehiclesService.saveOrUpdate(sealVehicles));
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
			rest.setData(sealVehiclesService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param sealVehiclesCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<SealVehicles> listData(@RequestBody SealVehiclesCondition sealVehiclesCondition) {
		JdResponse<PagerResult<SealVehicles>> rest = new JdResponse<PagerResult<SealVehicles>>();
		rest.setData(sealVehiclesService.queryByPagerCondition(sealVehiclesCondition));
		return rest.getData();
	}
}
