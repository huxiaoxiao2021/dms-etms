package com.jd.bluedragon.distribution.web.tips;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WaybillTipController {

	@Authorization(Constants.DMS_WEB_TOOL_WAYBILLCHECK_R)
	@RequestMapping(value = "/tips/waybill", method = RequestMethod.GET)
	public String index() {
		return "tips/waybill";
	}

}
