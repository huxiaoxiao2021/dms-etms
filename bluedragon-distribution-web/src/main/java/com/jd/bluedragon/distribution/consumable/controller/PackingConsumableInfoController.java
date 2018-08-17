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

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfoCondition;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: PackingConsumableInfoController
 * @Description: 包装耗材信息表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("consumable/packingConsumableInfo")
public class PackingConsumableInfoController {

	private static final Log logger = LogFactory.getLog(PackingConsumableInfoController.class);

	@Autowired
	private PackingConsumableInfoService packingConsumableInfoService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/consumable/packingConsumableInfo";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<PackingConsumableInfo> detail(@PathVariable("id") Long id) {
		JdResponse<PackingConsumableInfo> rest = new JdResponse<PackingConsumableInfo>();
		rest.setData(packingConsumableInfoService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param packingConsumableInfo
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PackingConsumableInfo packingConsumableInfo) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(packingConsumableInfoService.saveOrUpdate(packingConsumableInfo));
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
			rest.setData(packingConsumableInfoService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param packingConsumableInfoCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PackingConsumableInfo> listData(@RequestBody PackingConsumableInfoCondition packingConsumableInfoCondition) {
		JdResponse<PagerResult<PackingConsumableInfo>> rest = new JdResponse<PagerResult<PackingConsumableInfo>>();
		rest.setData(packingConsumableInfoService.queryByPagerCondition(packingConsumableInfoCondition));
		return rest.getData();
	}
}
