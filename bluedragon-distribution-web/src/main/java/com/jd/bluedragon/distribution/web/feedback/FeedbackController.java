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
    private final static Long APP_ID = 9007L;
    private final static Integer ORG_TYPE_ERP = 2;

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping("/addView")
    public String addView(Model model) {
        try {
            ErpUserClient.ErpUser currUser = ErpUserClient.getCurrUser();
            if (currUser != null){
                model.addAttribute("typeMaps", feedbackService.getFeedbackTypeNew(APP_ID,ErpUserClient.getCurrUser().getUserCode(),ORG_TYPE_ERP));
            }
        } catch (Exception e) {
            log.error("获取意见反馈类型时发生异常", e);
        }
        return "feedback/add";
    }

    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping("/index")
    public String index(Model model) {
        try {
            ErpUserClient.ErpUser currUser = ErpUserClient.getCurrUser();
            if (currUser != null){
                boolean res = feedbackService.checkHasFeedBack(APP_ID, currUser.getUserCode());
                if (res){
                    return "feedback/index";
                }else {
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
    PagerResult<FeedBackResponse> listData(@RequestBody BasePagerCondition pagerCondition) {
        PagerResult<FeedBackResponse> pagerResult = null;
//        //todo 测试数据删除
//        pagerResult = new PagerResult<>();
//        pagerResult.setTotal(1);
//        return mockData();
        try {
            if (ErpUserClient.getCurrUser()!=null){
                pagerResult = feedbackService.queryFeedBackPage(pagerCondition, ErpUserClient.getCurrUser().getUserCode(),APP_ID);
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
                    feedback.setAppId(APP_ID);
                    feedback.setImgs(images);
                    feedback.setUserAccount(erpUser.getUserCode());
                    feedback.setUserName(erpUser.getUserName());
                    if (!feedbackService.add(feedback)) {
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
    private  PagerResult<FeedBackResponse> mockData(){
        PagerResult<FeedBackResponse> result = new PagerResult<>();
        List<FeedBackResponse> resList = new ArrayList<>();
        FeedBackResponse response = new FeedBackResponse();
        response.setUserAccount("liuao");
        response.setStatusName("已回复");
        response.setStatus(1);
        response.setCreateTime(new Date());
        response.setUserName("刘奥");
        response.setTypeId(1);
        response.setTypeName("体验问题");
        response.setContent("测试提交问题测试提交问题测试提交问题测试提交问题测试提交问题测试提交问题");
        response.setAttachmentList(Arrays.asList("http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg","http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg","http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg",
        "http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg","http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg"));
        List<ReplyResponse> replyResponses = new ArrayList<>();
        ReplyResponse replyResponse = new ReplyResponse();
        replyResponse.setUserAccount("liuaohuifu");
        replyResponse.setCreateTime(new Date());
        replyResponse.setContent("恢复了很多内容");
        replyResponse.setImgs(Arrays.asList("http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg","http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg","http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg",
                "http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg","http://storage.jd.com/com.jd.mrd.common.image/202004151008226872hxSQ.jpg"));
        replyResponses.add(replyResponse);
        ReplyResponse replyResponse2 = new ReplyResponse();
        replyResponse2.setUserAccount("liuaohuifu");
        replyResponse2.setCreateTime(new Date());
        replyResponse2.setContent("恢复了很多内容");
        replyResponses.add(replyResponse2);
        response.setReplys(replyResponses);
        response.setViewDataJsonStr(JSON.toJSONString(response));
        resList.add(response);
        result.setRows(resList);
        result.setTotal(1);
        return result;
    }
}
