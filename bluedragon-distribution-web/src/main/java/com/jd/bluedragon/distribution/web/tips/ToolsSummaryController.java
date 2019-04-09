package com.jd.bluedragon.distribution.web.tips;

import com.jd.bluedragon.Constants;
import com.jd.uim.annotation.Authorization;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/toolsSummary")
public class ToolsSummaryController {

	@Authorization(Constants.DMS_WEB_DEVELOP_TOOLS_SUMMARY_R)
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String goListPage() {
		return "tips/tools_summary";
	}

}
