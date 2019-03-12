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

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleCondition;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: PreSealVehicleController
 * @Description: 预封车数据表--Controller实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Controller
@RequestMapping("newseal/preSealVehicle")
public class PreSealVehicleController {

	private static final Log logger = LogFactory.getLog(PreSealVehicleController.class);

	@Autowired
	private PreSealVehicleService preSealVehicleService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/newseal/preSealVehicle";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<PreSealVehicle> detail(@PathVariable("id") Long id) {
		JdResponse<PreSealVehicle> rest = new JdResponse<PreSealVehicle>();
		rest.setData(preSealVehicleService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param preSealVehicle
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PreSealVehicle preSealVehicle) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(preSealVehicleService.saveOrUpdate(preSealVehicle));
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
			rest.setData(preSealVehicleService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param preSealVehicleCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PreSealVehicle> listData(@RequestBody PreSealVehicleCondition preSealVehicleCondition) {
		JdResponse<PagerResult<PreSealVehicle>> rest = new JdResponse<PagerResult<PreSealVehicle>>();
		rest.setData(preSealVehicleService.queryByPagerCondition(preSealVehicleCondition));
		return rest.getData();
	}
}
