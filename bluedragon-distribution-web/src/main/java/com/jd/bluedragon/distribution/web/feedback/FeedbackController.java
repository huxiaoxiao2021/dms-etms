package com.jd.bluedragon.distribution.web.feedback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.bluedragon.distribution.feedback.domain.FeedBackResponse;
import com.jd.bluedragon.distribution.feedback.domain.FeedbackNew;
import com.jd.bluedragon.distribution.feedback.domain.ReplyResponse;
import com.jd.bluedragon.distribution.feedback.service.FeedbackService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author lixin39
 * @Description
 * @ClassName FeedbackController
 * @date 2019/5/24
 */
@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final Logger log = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    private final static String APP_PACKAGE_NAME = "dms.etms";
    private final static Long APP_ID = 8181L;
    private final static Integer ORG_TYPE_ERP = 2;

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping("/addView")
    public String addView(Model model, Long appId) {
        try {
            ErpUserClient.ErpUser currUser = ErpUserClient.getCurrUser();
            if (currUser != null && appId != null) {
                model.addAttribute("appId", appId);
                model.addAttribute("typeMaps", feedbackService.getFeedbackTypeNew(appId, ErpUserClient.getCurrUser().getUserCode(), ORG_TYPE_ERP));
            }
        } catch (Exception e) {
            log.error("获取意见反馈类型时发生异常", e);
        }
        return "feedback/add";
    }

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping("/index")
    public String index(Model model, Long appId) {
        try {
            ErpUserClient.ErpUser currUser = ErpUserClient.getCurrUser();
            if (currUser != null && appId != null) {
                model.addAttribute("appId", appId);
                boolean res = feedbackService.checkHasFeedBack(appId, currUser.getUserCode());
                if (res) {
                    return "feedback/index";
                } else {
                    model.addAttribute("typeMaps", feedbackService.getFeedbackTypeNew(appId, ErpUserClient.getCurrUser().getUserCode(), ORG_TYPE_ERP));
                    return "feedback/add";
                }
            }
        } catch (Exception e) {
            log.error("获取意见反馈类型时发生异常", e);
        }
        return "feedback/index";
    }

    @RequestMapping(value = "/listData")
    public @ResponseBody
    PagerResult<FeedBackResponse> listData(@RequestBody BasePagerCondition pagerCondition, Long appId) {
        PagerResult<FeedBackResponse> pagerResult = null;
        try {
            if (ErpUserClient.getCurrUser()!=null && appId != null){
                pagerResult = feedbackService.queryFeedBackPage(pagerCondition, ErpUserClient.getCurrUser().getUserCode(),appId);
            }else {
                pagerResult = new PagerResult<>();
                pagerResult.setTotal(0);
                pagerResult.setRows(new ArrayList<FeedBackResponse>());
            }
        } catch (Exception e) {
            log.error("FeedbackController.listData error",e);
            pagerResult = new PagerResult<>();
            pagerResult.setTotal(0);
            pagerResult.setRows(new ArrayList<FeedBackResponse>());
        }
        return pagerResult;
    }
    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult add(MultipartHttpServletRequest request) {
        InvokeResult result = new InvokeResult();
        try {
            String paramJson = request.getParameter("feedbackRequest");
            List<MultipartFile> images = request.getFiles("images");
            if (JsonHelper.isJsonString(paramJson)) {
                ErpUserClient.ErpUser erpUser =  ErpUserClient.getCurrUser();
                if (erpUser != null) {
                    FeedbackNew feedback = JSONObject.parseObject(paramJson, FeedbackNew.class);
                    feedback.setImgs(images);
                    feedback.setUserAccount(erpUser.getUserCode());
                    feedback.setUserName(erpUser.getUserName());
                    if (feedback.getAppId() == null) {
                        result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                        result.setMessage("获取系统类别ID失败，请提交正确的appId");
                    }else if (!feedbackService.add(feedback)) {
                        result.setCode(InvokeResult.SERVER_ERROR_CODE);
                        result.setMessage("提交反馈意见失败，请重试");
                    }
                } else {
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setMessage("获取当前登录用户信息失败，请重新登录ERP后尝试");
                }
            } else {
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage(InvokeResult.PARAM_ERROR);
            }
        } catch (JssStorageException je) {
            log.error("调用JSS服务进行附件存储时发生异常", je);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage("调用JSS服务进行附件存储时发生异常");
        } catch (Exception e) {
            log.error("提交意见反馈时发生异常", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}
