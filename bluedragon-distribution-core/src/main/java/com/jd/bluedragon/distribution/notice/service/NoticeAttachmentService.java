package com.jd.bluedragon.distribution.notice.service;

import com.jd.bluedragon.distribution.notice.domain.AttachmentDownloadDto;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;

import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName NoticeAttachmentService
 * @date 2019/4/18
 */
public interface NoticeAttachmentService {

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    NoticeAttachment getById(Long id);

    /**
     * 根据通知id获取附件信息列表
     *
     * @param noticeId
     * @return
     */
    List<NoticeAttachment> getByNoticeId(Long noticeId);

    /**
     * 批量新增
     *
     * @param attachments
     */
    void batchAdd(List<NoticeAttachment> attachments);

    /**
     * 附件下载
     *
     * @param attachmentId
     */
    AttachmentDownloadDto download(Long attachmentId);

    /**
     * 删除附件
     *
     * @param noticeId
     * @return
     */
    Integer deleteByNoticeId(Long noticeId);

    /**
     * 按主键ID删除附件
     *
     * @param id 附件主键ID
     * @return 影响条数
     * @author fanggang7
     * @date 2020-07-08 16:02:56 周三
     */
    Integer deleteById(Long id);
}
