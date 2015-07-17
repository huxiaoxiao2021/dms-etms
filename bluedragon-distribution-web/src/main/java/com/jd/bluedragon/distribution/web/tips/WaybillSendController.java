package com.jd.bluedragon.distribution.web.tips;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WaybillSendController {

	@RequestMapping(value = "/tips/repail_waybillSend", method = RequestMethod.GET)
	public String index() {
		return "tips/repair_waybillSend";
	}

}
