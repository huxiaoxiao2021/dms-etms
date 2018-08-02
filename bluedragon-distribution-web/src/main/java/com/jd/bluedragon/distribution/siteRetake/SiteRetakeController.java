package com.jd.bluedragon.distribution.siteRetake;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.siteRetake.service.SiteRetakeService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ldop.middle.api.basic.domain.BasicTraderQueryDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author tangchunqing
 * @Description: 驻厂批量再取
 * @date 2018年08月02日 14时:53分
 */
@Controller
@RequestMapping("siteRetake")
public class SiteRetakeController {
    private static final Log logger = LogFactory.getLog(SiteRetakeController.class);
    @Autowired
    private SiteRetakeService retakeService;

    @ResponseBody
    @RequestMapping(value = "/queryBasicTraderInfoByKey/{key}", method = RequestMethod.POST)
    public List<BasicTraderQueryDTO> queryBasicTraderInfoByKey(@PathVariable("key") String key) {
        List<BasicTraderQueryDTO> result = Lists.newArrayList();
        if (StringHelper.isEmpty(key)) {
            return result;
        }
        List<BasicTraderQueryDTO> basicTraderQueryDTOS = retakeService.queryBasicTraderInfoByKey(key);
        if (basicTraderQueryDTOS != null && basicTraderQueryDTOS.size() > 0) {
            for (BasicTraderQueryDTO dto : basicTraderQueryDTOS) {
                dto.setTraderName(dto.getTraderName() + Constants.PUNCTUATION_OPEN_BRACKET_SMALL + dto.getTraderCode() + Constants.PUNCTUATION_CLOSE_BRACKET_SMALL);
                result.add(dto);
            }
        }
        if (result.size() == 0) {
            BasicTraderQueryDTO emptyText = new BasicTraderQueryDTO();
            emptyText.setId("00000");
            emptyText.setTraderCode("");
            emptyText.setTraderName("无查询结果");
            result.add(emptyText);
        }
        return result;
    }
}
