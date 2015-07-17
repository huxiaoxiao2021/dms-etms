package com.jd.bluedragon.distribution.web.tips;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WaybillTipController {

	@RequestMapping(value = "/tips/waybill", method = RequestMethod.GET)
	public String index() {
		return "tips/waybill";
	}

}
