package com.jd.bluedragon.distribution.notice.service.impl;

import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.notice.dao.NoticeAttachmentDao;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;
import com.jd.bluedragon.distribution.notice.service.NoticeAttachmentService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName NoticeAttachmentServiceImpl
 * @date 2019/4/18
 */
@Service
public class NoticeAttachmentServiceImpl implements NoticeAttachmentService {

    private final Logger logger = Logger.getLogger(NoticeAttachmentServiceImpl.class);

    @Autowired
    private NoticeAttachmentDao noticeAttachmentDao;

    @Autowired
    private JssService jssService;

    @Value("${jss.notice.bucket}")
    private String bucket;

    @Override
    public NoticeAttachment getById(Long id) {
        if (id != null) {
            return noticeAttachmentDao.getById(id);
        } else {
            return null;
        }
    }

    @Override
    public List<NoticeAttachment> getByNoticeId(Long noticeId) {
        Map<String, Object> parameter = new HashMap<>(1);
        parameter.put("noticeId", noticeId);
        return noticeAttachmentDao.getByNoticeId(parameter);
    }

    @Override
    public void batchAdd(List<NoticeAttachment> attachments) {
        if (attachments != null && attachments.size() > 0) {
            noticeAttachmentDao.batchAdd(attachments);
        }
    }

    @Override
    public void download(Long attachmentId, HttpServletResponse response) throws IOException {
        NoticeAttachment attachment = this.getById(attachmentId);
        if (attachment != null) {
            String keyName = attachment.getKeyName();
            if (StringUtils.isNotEmpty(keyName)) {
                InputStream inputStream = jssService.downloadFile(bucket, keyName);
                response.addHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(attachment.getFileName(), "UTF-8"));
                this.writeResponse(inputStream, response.getOutputStream());
            }
        }
    }

    @Override
    public Integer delete(Long noticeId) {
        if (noticeId != null) {
            List<NoticeAttachment> attachments = this.getByNoticeId(noticeId);
            if (attachments.size() > 0) {
                for (NoticeAttachment attachment : attachments) {
                    if (StringUtils.isNotEmpty(attachment.getKeyName())) {
                        // 删除附件
                        try {
                            jssService.deleteFile(bucket, attachment.getKeyName());
                        } catch (JssStorageException e) {
                            logger.error("调用JSS服务删除附件失败", e);
                        }
                    }
                }
                Map<String, Object> parameter = new HashMap<>(1);
                parameter.put("noticeId", noticeId);
                return noticeAttachmentDao.deleteByNoticeId(parameter);
            }
        }
        return 0;
    }

    private void writeResponse(InputStream is, OutputStream os) {
        try {
            byte[] b = new byte[1024];
            int i;
            while ((i = is.read(b)) > 0) {
                os.write(b, 0, i);
            }
            os.flush();
        } catch (Exception e) {
            logger.error("导出失败", e);
        }
    }

}
