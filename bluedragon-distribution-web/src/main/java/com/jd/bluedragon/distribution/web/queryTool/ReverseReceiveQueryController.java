package com.jd.bluedragon.distribution.web.queryTool;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.response.QueryBaseResponse;
import com.jd.bluedragon.distribution.queryTool.domain.QueryReverseReceiveDomain;
import com.jd.bluedragon.distribution.queryTool.service.QueryReverseReceiveService;
import com.jd.bluedragon.utils.ObjectMapHelper;
import org.apache.commons.jexl2.UnifiedJEXL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
@RequestMapping("/query_reverse")
public class ReverseReceiveQueryController {
    @Autowired
    QueryReverseReceiveService queryReverseReceiveService;

    private final Log logger = LogFactory.getLog(this.getClass());

    @RequestMapping("/index")
    public String index(Model model) {
        return "queryTool/reverse_receive";
    }

    @RequestMapping(value = "/receive", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public QueryBaseResponse<Pager<List<QueryReverseReceiveDomain>>> query(QueryReverseReceiveDomain reverseReceiveDomain, Pager<List<QueryReverseReceiveDomain>> pager) {
        QueryBaseResponse<Pager<List<QueryReverseReceiveDomain>>> queryBaseResponse = new QueryBaseResponse<Pager<List<QueryReverseReceiveDomain>>>();
        try {
            logger.info("reverse_receive 表查询");

            Map<String, Object> params = ObjectMapHelper.makeObject2Map(reverseReceiveDomain);
            List<QueryReverseReceiveDomain> resultList = queryReverseReceiveService.queryByCondition(params, pager);

            if (pager == null) {
                pager = new Pager<List<QueryReverseReceiveDomain>>(Pager.DEFAULT_PAGE_NO);
            }

            pager.setData(resultList);
            queryBaseResponse.setData(pager);
            queryBaseResponse.setCode(queryBaseResponse.CODE_NORMAL);
            queryBaseResponse.setMessage("ok");
        } catch (Exception e) {
            queryBaseResponse.setData(null);
            queryBaseResponse.setCode(queryBaseResponse.CODE_EXCEPTION);
            logger.info("查询reverse_service表失败. "+e.getMessage());
            queryBaseResponse.setMessage("查询reverse_service表失败");
        }

        return queryBaseResponse;
    }
}
