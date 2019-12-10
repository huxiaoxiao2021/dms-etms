package com.jd.bluedragon.distribution.notice.service.impl;

import com.jd.bluedragon.distribution.exception.jss.JssStorageException;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.notice.dao.NoticeAttachmentDao;
import com.jd.bluedragon.distribution.notice.domain.AttachmentDownloadDto;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;
import com.jd.bluedragon.distribution.notice.service.NoticeAttachmentService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    private final Logger log = LoggerFactory.getLogger(NoticeAttachmentServiceImpl.class);

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
    public AttachmentDownloadDto download(Long attachmentId) {
        NoticeAttachment attachment = this.getById(attachmentId);
        if (attachment != null) {
            AttachmentDownloadDto dto = new AttachmentDownloadDto();
            String keyName = attachment.getKeyName();
            if (StringUtils.isNotEmpty(keyName)) {
                dto.setInputStream(jssService.downloadFile(bucket, keyName));
                dto.setFileName(attachment.getFileName());
            }
            return dto;
        }
        return null;
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
                            log.error("调用JSS服务删除附件失败", e);
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

}
