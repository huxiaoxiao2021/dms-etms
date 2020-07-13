package com.jd.bluedragon.distribution.notice.jsf.impl;

import com.jd.bluedragon.distribution.notice.domain.AttachmentDownloadDto;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;
import com.jd.bluedragon.distribution.notice.service.NoticeAttachmentJsfService;
import com.jd.bluedragon.distribution.notice.service.NoticeAttachmentService;
import com.jd.ql.dms.common.domain.JdResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知附件jsf实现层
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @date 2020-07-02 14:36:08 周四
 */
@Service
public class NoticeAttachmentJsfServiceImpl implements NoticeAttachmentJsfService {

    @Autowired
    private NoticeAttachmentService noticeAttachmentService;

    /**
     * 根据ID获取
     *
     * @param id 附件ID
     * @return NoticeAttachment
     * @author fanggang7
     * @date 2020-07-02 14:21:43 周四
     */
    @Override
    public JdResponse<NoticeAttachment> getById(Long id) {
        JdResponse<NoticeAttachment> result = new JdResponse<>();
        NoticeAttachment noticeAttach = noticeAttachmentService.getById(id);
        result.setData(noticeAttach);
        return result;
    }

    /**
     * 根据通知id获取附件信息列表
     *
     * @param noticeId 通知主键ID
     * @return 附件列表
     * @author fanggang7
     * @date 2020-07-02 14:23:33 周四
     */
    @Override
    public JdResponse<List<NoticeAttachment>> getByNoticeId(Long noticeId) {
        JdResponse<List<NoticeAttachment>> result = new JdResponse<>();
        List<NoticeAttachment> noticeAttachList = noticeAttachmentService.getByNoticeId(noticeId);
        result.setData(noticeAttachList);
        return result;
    }

    /**
     * 批量新增
     *
     * @param attachments 附件数据
     * @return 批量新增结果
     * @author fanggang7
     * @date 2020-07-02 14:28:06 周四
     */
    @Override
    public JdResponse<Boolean> batchAdd(List<NoticeAttachment> attachments) {
        JdResponse<Boolean> result = new JdResponse<>();
        noticeAttachmentService.batchAdd(attachments);
        result.setData(true);
        return result;
    }

    /**
     * 按通知主键删除附件
     *
     * @param noticeId 通知ID
     * @return 删除影响条数
     * @author fanggang7
     * @date 2020-07-02 14:29:14 周四
     */
    @Override
    public JdResponse<Integer> deleteByNoticeId(Long noticeId) {
        JdResponse<Integer> result = new JdResponse<>();
        Integer integer = noticeAttachmentService.deleteByNoticeId(noticeId);
        result.setData(integer);
        return result;
    }
}
