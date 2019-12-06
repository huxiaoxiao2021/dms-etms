package com.jd.bluedragon.distribution.web.uploadimage;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.external.gateway.service.AbnormalReportingGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName UploadController
 * @date 2019/12/5
 */
@Controller
@RequestMapping("/upload")
public class UploadController {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private AbnormalReportingGatewayService abnormalReportingGatewayService;

    @RequestMapping(value = "/uploadImage", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JdCResponse<String> uploadImage(HttpServletRequest request, HttpServletResponse response) {
        logger.info("图片上传入口");
        return abnormalReportingGatewayService.uploadExceptionImage(request, response);
    }

}
