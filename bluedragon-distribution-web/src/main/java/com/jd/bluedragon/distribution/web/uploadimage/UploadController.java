package com.jd.bluedragon.distribution.web.uploadimage;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.external.gateway.service.AbnormalReportingGatewayService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
    public JdCResponse<String> uploadImage(MultipartHttpServletRequest request, HttpServletResponse response) {
        logger.info("图片上传入口");
        JdCResponse<String> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        try {
            List<MultipartFile> images = request.getFiles("file");
            if (images.size() > 0) {
                String url = abnormalReportingGatewayService.uploadExceptionMedia(images.get(0).getInputStream(), images.get(0).getOriginalFilename());
                if (StringUtils.isEmpty(url)) {
                    jdCResponse.toFail("文件上传失败，请重试");
                } else {
                    jdCResponse.setData(url);
                }
            }
        } catch (Exception e) {
            logger.error("uploadExceptionImage error", e);
            jdCResponse.toError("图片上传时发生异常");
        }
        return jdCResponse;
    }

}
