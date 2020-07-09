package com.jd.bluedragon.distribution.notice.jsf.impl;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;
import com.jd.bluedragon.distribution.notice.request.NoticeQuery;
import com.jd.bluedragon.distribution.notice.service.NoticeAttachmentService;
import com.jd.bluedragon.distribution.notice.service.NoticeJsfService;
import com.jd.bluedragon.distribution.notice.service.NoticeService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知栏jsf服务实现层
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @date 2020-07-02 14:31:53 周四
 */
@Service("dmsNoticeJsfService")
public class NoticeJsfServiceImpl implements NoticeJsfService {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private NoticeAttachmentService noticeAttachmentService;

    /**
     * 分页查询
     *
     * @param query 请求参数
     * @return 分页数据
     * @author fanggang7
     * @date 2020-07-01 15:58:11 周三
     */
    @Override
    public Response<PageDto<Notice>> queryPageList(NoticeQuery query) {
        Response<PageDto<Notice>> response = new Response<>();
        response.toSucceed();
        PageDto<Notice> noticePageDto = noticeService.queryPageList(query);
        response.setData(noticePageDto);
        return response;
    }

    /**
     * 按主键查询
     *
     * @param id 主键ID
     * @return Notice 通知详情
     * @author fanggang7
     * @date 2020-07-07 19:52:00 周二
     */
    @Override
    public Response<Notice> getByPrimaryKey(Long id) {
        return noticeService.getByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param notice @see Notice
     * @return 新增结果
     * @author fanggang7
     * @date 2020-07-02 10:03:53 周四
     */
    @Override
    public Response<Notice> add(Notice notice) {
        Response<Notice> response = new Response<>();
        response.toSucceed();
        noticeService.add(notice);
        response.setData(notice);
        return response;
    }

    /**
     * 按主键更新
     *
     * @param notice Notice
     * @return Response 更新条数
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    @Override
    public Response<Boolean> updateByPrimaryKey(Notice notice) {
        return noticeService.updateByPrimaryKey(notice);
    }

    /**
     * 按主键伪删除
     *
     * @param notice Notice
     * @return Response 更新条数
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    @Override
    public Response<Boolean> deleteByPrimaryKey(Notice notice) {
        return noticeService.deleteByPrimaryKey(notice);
    }

    /**
     * 根据主键id获取附件信息列表
     *
     * @param id 主键ID
     * @return Response 附件列表
     * @author fanggang7
     * @date 2020-07-05 19:48:12 周日
     */
    @Override
    public Response<NoticeAttachment> getAttachmentById(Long id) {
        Response<NoticeAttachment> response = new Response<>();
        response.toSucceed();
        NoticeAttachment attachment = noticeAttachmentService.getById(id);
        response.setData(attachment);
        return response;
    }

    /**
     * 根据主键id获取附件信息列表
     *
     * @param noticeId 通知主键ID
     * @return Response 附件列表
     * @author fanggang7
     * @date 2020-07-05 19:48:12 周日
     */
    @Override
    public Response<List<NoticeAttachment>> getAttachmentByNoticeId(Long noticeId) {
        Response<List<NoticeAttachment>> response = new Response<>();
        response.toSucceed();
        List<NoticeAttachment> attachmentList = noticeAttachmentService.getByNoticeId(noticeId);
        response.setData(attachmentList);
        return response;
    }

    /**
     * 批量新增
     *
     * @param attachments 附件数据
     * @return 新增结果
     * @author fanggang7
     * @date 2020-07-05 19:51:44 周日
     */
    @Override
    public Response<List<NoticeAttachment>> batchAddAttachment(List<NoticeAttachment> attachments) {
        Response<List<NoticeAttachment>> response = new Response<>();
        response.toSucceed();
        noticeAttachmentService.batchAdd(attachments);
        response.setData(attachments);
        return response;
    }

    /**
     * 删除附件
     *
     * @param id 附件主键ID
     * @return Response
     * @author fanggang7
     * @date 2020-07-05 19:46:59 周日
     */
    @Override
    public Response<Integer> deleteAttachmentById(Long id) {
        Response<Integer> response = new Response<>();
        response.toSucceed();
        Integer deleteCount = noticeAttachmentService.deleteById(id);
        response.setData(deleteCount);
        return response;
    }

    /**
     * 删除附件
     *
     * @param noticeId 通知主键ID
     * @return Response
     * @author fanggang7
     * @date 2020-07-05 19:46:59 周日
     */
    @Override
    public Response<Integer> deleteAttachmentByNoticeId(Long noticeId) {
        Response<Integer> response = new Response<>();
        response.toSucceed();
        Integer deleteCount = noticeAttachmentService.deleteByNoticeId(noticeId);
        response.setData(deleteCount);
        return response;
    }

}
