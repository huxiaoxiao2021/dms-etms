package com.jd.bluedragon.distribution.weight.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlow;
import com.jd.bluedragon.distribution.weight.domain.DmsWeightFlowCondition;
import com.jd.bluedragon.distribution.weight.service.DmsWeightFlowService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: DmsWeightFlowController
 * @Description: 分拣中心称重操作流水--Controller实现
 * @author wuyoude
 * @date 2018年03月09日 16:02:53
 *
 */
@Controller
@RequestMapping("weight/dmsWeightFlow")
public class DmsWeightFlowController {

	private static final Log logger = LogFactory.getLog(DmsWeightFlowController.class);

	@Autowired
	private DmsWeightFlowService dmsWeightFlowService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/weight/dmsWeightFlow";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<DmsWeightFlow> detail(@PathVariable("id") Long id) {
		JdResponse<DmsWeightFlow> rest = new JdResponse<DmsWeightFlow>();
		rest.setData(dmsWeightFlowService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param dmsWeightFlow
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody DmsWeightFlow dmsWeightFlow) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(dmsWeightFlowService.saveOrUpdate(dmsWeightFlow));
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
			rest.setData(dmsWeightFlowService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param dmsWeightFlowCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DmsWeightFlow> listData(@RequestBody DmsWeightFlowCondition dmsWeightFlowCondition) {
		JdResponse<PagerResult<DmsWeightFlow>> rest = new JdResponse<PagerResult<DmsWeightFlow>>();
		rest.setData(dmsWeightFlowService.queryByPagerCondition(dmsWeightFlowCondition));
		return rest.getData();
	}
}
