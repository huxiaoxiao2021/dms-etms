package com.jd.bluedragon.distribution.web.feedback;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.bluedragon.distribution.feedback.domain.Feedback;
import com.jd.bluedragon.distribution.feedback.service.FeedbackService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName FeedbackController
 * @date 2019/5/24
 */
@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    private final Logger logger = Logger.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    private final static String APP_PACKAGE_NAME = "dms.etms";

    @RequestMapping("/addView")
    public String addView(Model model) {
        model.addAttribute("typeMaps", feedbackService.getFeedbackType(APP_PACKAGE_NAME));
        return "feedback/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult add(MultipartHttpServletRequest request) {
        InvokeResult result = new InvokeResult();
        try {
            String paramJson = request.getParameter("feedbackRequest");
            List<MultipartFile> images = request.getFiles("images");
            if (JsonHelper.isJsonString(paramJson)) {
                ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
                if (erpUser != null) {
                    Feedback feedback = JSONObject.parseObject(paramJson, Feedback.class);
                    feedback.setAppPackageName(APP_PACKAGE_NAME);
                    feedback.setImages(images);
                    feedback.setUserErp(erpUser.getUserCode());
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
            logger.error("调用JSS服务进行附件存储时发生异常", je);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage("调用JSS服务进行附件存储时发生异常");
        } catch (Exception e) {
            logger.error("提交意见反馈时发生异常", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }
}
