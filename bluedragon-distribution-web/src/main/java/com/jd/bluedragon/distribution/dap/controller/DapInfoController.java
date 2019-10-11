package com.jd.bluedragon.distribution.dap.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfoCondition;
import com.jd.bluedragon.distribution.consumable.domain.PackingTypeEnum;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;
import com.jd.bluedragon.distribution.rest.dap.DapInfo;
import com.jd.bluedragon.distribution.rest.dap.DapResource;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 *
 * @ClassName: PackingConsumableInfoController
 * @Description: 包装耗材信息表--Controller实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Controller
@RequestMapping("dap/info")
public class DapInfoController extends DmsBaseController {

	private static final Log logger = LogFactory.getLog(DapInfoController.class);

	@Autowired
	private DapResource dapResource;

	/**
	 * 返回主页面
	 * @return
	 */
	@Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/dap/dapInfo";
	}

	@Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<DapInfo> listData(@RequestBody BasePagerCondition basePagerCondition) {

		PagerResult<DapInfo> pagerResult = new PagerResult<>();
		pagerResult.setRows(dapResource.getUndiv().getData());
		pagerResult.setTotal(dapResource.getUndiv().getData().size());

		return pagerResult;
	}
}
