package com.jd.bluedragon.distribution.web.notice;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.NoticeRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.basic.FileUtils;
import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.bluedragon.distribution.notice.domain.AttachmentDownloadDto;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;
import com.jd.bluedragon.distribution.notice.request.NoticeAttachmentRequest;
import com.jd.bluedragon.distribution.notice.service.NoticeAttachmentService;
import com.jd.bluedragon.distribution.notice.service.NoticeService;
import com.jd.bluedragon.distribution.notice.utils.NoticeLevelEnum;
import com.jd.bluedragon.distribution.notice.utils.NoticeTypeEnum;
import com.jd.bluedragon.distribution.notice.utils.TopDisplayEnum;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSONObject;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName NoticeController
 * @date 2019/4/16
 */
@Controller
@RequestMapping("/notice")
public class NoticeController {

    private final Logger logger = Logger.getLogger(NoticeController.class);

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeAttachmentService noticeAttachmentService;

    /**
     * 跳转到主界面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping("/list")
    public String index() {
        return "notice/list";
    }

    /**
     * 通知展示
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping(value = "/show", method = RequestMethod.POST)
    @ResponseBody
    public PagerResult<Notice> show() {
        PagerResult<Notice> result = new PagerResult();
        List<Notice> allNotice = noticeService.getAll(null);
        result.setRows(allNotice);
        result.setTotal(allNotice.size());
        return result;
    }

    /**
     * 跳转附件查看页
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping("/attachment/view/{noticeId}")
    public String attachmentView(@PathVariable("noticeId") Long noticeId, Model model) {
        model.addAttribute("noticeId", noticeId);
        return "notice/attachment";
    }

    /**
     * 通知附件展示
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping(value = "/attachment/detail", method = RequestMethod.POST)
    @ResponseBody
    public PagerResult<NoticeAttachment> attachmentDetail(@RequestBody NoticeAttachmentRequest request) {
        PagerResult<NoticeAttachment> result = new PagerResult();
        Long noticeId = request.getNoticeId();
        if (noticeId != null) {
            List<NoticeAttachment> attachments = noticeAttachmentService.getByNoticeId(noticeId);
            result.setRows(attachments);
            result.setTotal(attachments.size());
        } else {
            result.setRows(Collections.<NoticeAttachment>emptyList());
            result.setTotal(0);
        }
        return result;
    }

    /**
     * 附件下载页
     *
     * @param attachmentId
     * @param response
     */
    @Authorization(Constants.DMS_WEB_INDEX_R)
    @RequestMapping(value = "/attachment/download", method = RequestMethod.GET)
    @ResponseBody
    public InvokeResult download(@RequestParam Long attachmentId, HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        try {
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                AttachmentDownloadDto downloadDto = this.noticeAttachmentService.download(attachmentId);
                if (downloadDto != null && downloadDto.getInputStream() != null) {
                    response.addHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(downloadDto.getFileName(), "UTF-8"));
                    FileUtils.write(downloadDto.getInputStream(), response.getOutputStream());
                } else {
                    result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                    result.setMessage("参数错误，附件不存在，下载失败");
                }
            } else {
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage("获取当前登录用户信息失败，请重新登录ERP后尝试");
            }
        } catch (Exception ex) {
            logger.error("附件下载异常", ex);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 跳转到管理页
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_NOTICE_MANAGE)
    @RequestMapping("/manage")
    public String manage() {
        return "notice/manage";
    }

    /**
     * 跳转新增页
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_NOTICE_MANAGE)
    @RequestMapping("/addView")
    public String addView(Model model) {
        model.addAttribute("levelValues", NoticeLevelEnum.values());
        model.addAttribute("typeValues", NoticeTypeEnum.values());
        model.addAttribute("topDisplayValues", TopDisplayEnum.values());
        return "notice/add";
    }

    /**
     * 保存
     *
     * @param request
     * @return
     */
    @Authorization(Constants.DMS_WEB_NOTICE_MANAGE)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public InvokeResult add(MultipartHttpServletRequest request) {
        InvokeResult result = new InvokeResult();
        try {
            String paramJson = request.getParameter("noticeRequest");
            List<MultipartFile> files = request.getFiles("files");
            if (JsonHelper.isJsonString(paramJson)) {
                ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
                if (erpUser != null) {
                    NoticeRequest noticeRequest = JSONObject.parseObject(paramJson, NoticeRequest.class);
                    noticeService.addAndUploadFile(files, noticeRequest, erpUser.getUserCode());
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
            logger.error("新增通知时发生异常", e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

    /**
     * 根据id删除通知和附件
     *
     * @param ids
     * @return
     */
    @Authorization(Constants.DMS_WEB_NOTICE_MANAGE)
    @RequestMapping(value = "/deleteByIds")
    @ResponseBody
    public InvokeResult<Integer> deleteByIds(@RequestBody List<Integer> ids) {
        InvokeResult<Integer> result = new InvokeResult<>();
        try {
            /**
             * 类型转换 默认接受为Integer 需要转换成Long
             */
            List<Long> idsLong = new ArrayList<>();
            for (Integer id : ids) {
                idsLong.add(Long.valueOf(id));
            }
            ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
            if (erpUser != null) {
                result.setData(noticeService.deleteByIds(idsLong, erpUser.getUserCode()));
            } else {
                result.setCode(InvokeResult.SERVER_ERROR_CODE);
                result.setMessage("删除失败，获取ERP信息失败，请重新登录后再操作！");
            }
        } catch (Exception e) {
            logger.error("删除通知时发生异常！" + e.getMessage(), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

}
