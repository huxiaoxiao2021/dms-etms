package com.jd.bluedragon.distribution.web.sqlkit;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/dataCompare")
public class DataSourceDataCompareController {

	private final Logger log = LoggerFactory.getLogger(DataSourceDataCompareController.class);

    @Authorization(Constants.DMS_WEB_DEVELOP_SQLKIT_R)
    @RequestMapping("/compareList")
    public String toView(Model model) {
        return "sqlkit/comparePage";
    }


    @Authorization(Constants.DMS_WEB_SORTING_SORTMACHINEAUTOSEND_R)
    @RequestMapping(value = "/compare", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult<List<String>> findSortMachineByErp(){

    }
}
