package com.jd.bluedragon.distribution.web.WhiteList;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.inspection.InspectionWhiteList;
import com.jd.bluedragon.distribution.inspection.InspectionWhiteListCondition;
import com.jd.bluedragon.distribution.whiteList.service.WhiteListService;
import com.jd.bluedragon.distribution.whitelist.WhiteList;
import com.jd.bluedragon.distribution.whitelist.WhiteListCondition;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * @author lijie
 * @date 2020/3/10 15:39
 */
@Controller
@RequestMapping("/whiteList")
public class WhiteListController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(WhiteListController.class);

    @Autowired
    private WhiteListService whiteListService;

    //todo 加权限
    @Authorization()
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "/whiteList/whiteListIndex";
    }

    @Authorization()
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<WhiteList> listData(@RequestBody WhiteListCondition condition){

        PagerResult<WhiteList> result = whiteListService.queryByCondition(condition);
        return result;
    }

    /**
     * 保存数据
     * @param whiteList
     * @return
     */
    @Authorization()
    @RequestMapping(value = "/save")
    public @ResponseBody
    JdResponse<Boolean> save(@RequestBody WhiteList whiteList) {

        JdResponse<Boolean> rest = new JdResponse<Boolean>();

        if(whiteList == null || whiteList.getMenuId() == null || whiteList.getDimensionId() == null){
            rest.toError("参数有误！");
            return rest;
        }
        whiteList.setCreateTime(new Date());
        whiteList.setCreateUser(getLoginUser().getUserName());
        rest = whiteListService.save(whiteList);

        return rest;
    }

    /**
     * 删除数据
     * @param ids
     * @return
     */
    @Authorization()
    @RequestMapping(value = "/deleteByIds")
    public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
        JdResponse<Integer> rest = new JdResponse<Integer>();
        rest = whiteListService.deleteByIds(ids);
        return rest;
    }

}
