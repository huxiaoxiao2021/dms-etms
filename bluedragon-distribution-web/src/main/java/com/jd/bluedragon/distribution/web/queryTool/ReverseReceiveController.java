package com.jd.bluedragon.distribution.web.queryTool;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.response.QueryBaseResponse;
import com.jd.bluedragon.distribution.queryTool.domain.ReverseReceive;
import com.jd.bluedragon.distribution.queryTool.domain.ReverseReceiveRequest;
import com.jd.bluedragon.distribution.queryTool.service.ReverseReceiveService;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by xumei1 on 2016/8/30.
 */

@Controller
@RequestMapping("/reverseReceive")
public class ReverseReceiveController {
    @Autowired
    ReverseReceiveService queryReverseReceiveService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Authorization(Constants.DMS_WEB_TOOL_REVERSERECEIVE_R)
    @RequestMapping("/index")
    public String index(Model model) {
        return "queryTool/reversereceive";
    }

    @Authorization(Constants.DMS_WEB_TOOL_REVERSERECEIVE_R)
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    @ResponseBody
    public QueryBaseResponse<Pager<List<ReverseReceive>>> query(ReverseReceiveRequest request, Pager<List<ReverseReceive>> pager) {
        QueryBaseResponse<Pager<List<ReverseReceive>>> queryBaseResponse = new QueryBaseResponse<Pager<List<ReverseReceive>>>();
        try {
            log.info("reverse_receive 表查询");

            Map<String, Object> params = ObjectMapHelper.makeObject2Map(request);
            List<ReverseReceive> resultList = queryReverseReceiveService.queryByCondition(params, pager);

            if (pager == null) {
                pager = new Pager<List<ReverseReceive>>(Pager.DEFAULT_PAGE_NO);
            }

            pager.setData(resultList);
            queryBaseResponse.setData(pager);
            queryBaseResponse.setCode(queryBaseResponse.CODE_NORMAL);
            queryBaseResponse.setMessage("ok");
        } catch (Exception e) {
            queryBaseResponse.setData(null);
            queryBaseResponse.setCode(queryBaseResponse.CODE_EXCEPTION);
            log.error("查询reverse_service表失败. ", e);
            queryBaseResponse.setMessage("查询reverse_service表失败");
        }

        return queryBaseResponse;
    }
}
