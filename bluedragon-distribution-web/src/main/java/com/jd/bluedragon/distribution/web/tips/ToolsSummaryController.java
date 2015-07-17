package com.jd.bluedragon.distribution.web.tips;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/toolsSummary")
public class ToolsSummaryController {
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String goListPage() {
		return "tips/tools_summary";
	}

}
