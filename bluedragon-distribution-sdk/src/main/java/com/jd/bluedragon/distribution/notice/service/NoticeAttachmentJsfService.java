package com.jd.bluedragon.distribution.notice.service;

import com.jd.bluedragon.distribution.notice.domain.AttachmentDownloadDto;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.List;

/**
 * 通知附件jsf接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @date 2020-07-02 10:31:36 周四
 */
public interface NoticeAttachmentJsfService {

    /**
     * 根据ID获取
     * @param id 附件ID
     * @return NoticeAttachment
     * @author fanggang7
     * @date 2020-07-02 14:21:43 周四
     */
    JdResponse<NoticeAttachment> getById(Long id);

    /**
     * 根据通知id获取附件信息列表
     * @param noticeId 通知主键ID
     * @return 附件列表
     * @author fanggang7
     * @date 2020-07-02 14:23:33 周四
     */
    JdResponse<List<NoticeAttachment>> getByNoticeId(Long noticeId);

    /**
     * 批量新增
     * @param attachments 附件数据
     * @return 批量新增结果
     * @author fanggang7
     * @date 2020-07-02 14:28:06 周四
     */
    JdResponse<Boolean> batchAdd(List<NoticeAttachment> attachments);

    /**
     * 按通知主键删除附件
     *
     * @param noticeId 通知ID
     * @return 删除影响条数
     * @author fanggang7
     * @date 2020-07-02 14:29:14 周四
     */
    JdResponse<Integer> deleteByNoticeId(Long noticeId);
}
