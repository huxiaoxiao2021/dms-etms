package com.jd.bluedragon.distribution.base.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.domain.DmsStorageAreaCondition;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: DmsStorageAreaController
 * @Description: 分拣中心库位--Controller实现
 * @author wuyoude
 * @date 2018年03月13日 16:25:45
 *
 */
@Controller
@RequestMapping("base/dmsStorageArea")
public class DmsStorageAreaController {

	private static final Log logger = LogFactory.getLog(DmsStorageAreaController.class);

	@Autowired
	private DmsStorageAreaService dmsStorageAreaService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/base/dmsStorageArea";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<DmsStorageArea> detail(@PathVariable("id") Long id) {
		JdResponse<DmsStorageArea> rest = new JdResponse<DmsStorageArea>();
		rest.setData(dmsStorageAreaService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param dmsStorageArea
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsStorageArea dmsStorageArea) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(dmsStorageAreaService.saveOrUpdate(dmsStorageArea));
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
			rest.setData(dmsStorageAreaService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param dmsStorageAreaCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DmsStorageArea> listData(@RequestBody DmsStorageAreaCondition dmsStorageAreaCondition) {
		JdResponse<PagerResult<DmsStorageArea>> rest = new JdResponse<PagerResult<DmsStorageArea>>();
		rest.setData(dmsStorageAreaService.queryByPagerCondition(dmsStorageAreaCondition));
		return rest.getData();
	}
}
