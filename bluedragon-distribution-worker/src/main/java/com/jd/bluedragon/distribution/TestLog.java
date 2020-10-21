package com.jd.bluedragon.distribution;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.site.dao.SiteMapper;
import com.jd.bluedragon.distribution.ver.domain.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestLog {

    private Logger log = LoggerFactory.getLogger(TestLog.class);

    @Autowired
    SiteMapper siteMapper;


    @RequestMapping("/testLogLevel")
    public @ResponseBody
    String testLogLevel() {

        log.debug("DEBUG Level in TestLog");
        log.info("INFO Level in TestLog");
        log.warn("WARN Level in TestLog");
        log.error("ERROR Level in TestLog");

        return "logLevelTest";
    }

    @RequestMapping("/siteMapperLogTest")
    public @ResponseBody
    String siteMapperLogTest() {
        Site site = siteMapper.get(1);
        return JSONObject.toJSONString(site);
    }


}
