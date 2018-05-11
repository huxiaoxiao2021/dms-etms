package com.jd.bluedragon.distribution.abnormal.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybillCondition;
import com.jd.bluedragon.distribution.abnormal.service.AbnormalUnknownWaybillService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 *
 * @ClassName: AbnormalUnknownWaybillController
 * @Description: 三无订单申请--Controller实现
 * @author wuyoude
 * @date 2018年05月08日 15:16:15
 *
 */
@Controller
@RequestMapping("abnormal/abnormalUnknownWaybill")
public class AbnormalUnknownWaybillController {

	private static final Log logger = LogFactory.getLog(AbnormalUnknownWaybillController.class);

	@Autowired
	private AbnormalUnknownWaybillService abnormalUnknownWaybillService;

	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/abnormal/abnormalUnknownWaybill";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<AbnormalUnknownWaybill> detail(@PathVariable("id") Long id) {
		JdResponse<AbnormalUnknownWaybill> rest = new JdResponse<AbnormalUnknownWaybill>();
		rest.setData(abnormalUnknownWaybillService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param abnormalUnknownWaybill
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody AbnormalUnknownWaybill abnormalUnknownWaybill) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			return abnormalUnknownWaybillService.queryAndReport(abnormalUnknownWaybill);
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
			rest.setData(abnormalUnknownWaybillService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param abnormalUnknownWaybillCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<AbnormalUnknownWaybill> listData(@RequestBody AbnormalUnknownWaybillCondition abnormalUnknownWaybillCondition) {
		JdResponse<PagerResult<AbnormalUnknownWaybill>> rest = new JdResponse<PagerResult<AbnormalUnknownWaybill>>();
		rest.setData(abnormalUnknownWaybillService.queryByPagerCondition(abnormalUnknownWaybillCondition));
		return rest.getData();
	}
}
