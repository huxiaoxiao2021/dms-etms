package com.jd.bluedragon.distribution.notice.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;
import com.jd.bluedragon.distribution.notice.request.NoticeQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;

/**
 * 通知栏jsf服务
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @date 2020-07-01 15:34:55 周三
 */
public interface NoticeJsfService {

    /**
     * 分页查询
     *
     * @param query 请求参数
     * @return 分页数据
     * @author fanggang7
     * @date 2020-07-01 15:58:11 周三
     */
    Response<PageDto<Notice>> queryPageList(NoticeQuery query);

    /**
     * 按主键查询
     * @param id 主键ID
     * @return Notice 通知详情
     * @author fanggang7
     * @date 2020-07-07 19:52:00 周二
     */
    Response<Notice> getByPrimaryKey(Long id);

    /**
     * 新增
     *
     * @param notice @see Notice
     * @return 新增结果
     * @author fanggang7
     * @date 2020-07-02 10:03:53 周四
     */
    Response<Notice> add(Notice notice);

    /**
     * 按主键更新
     *
     * @param notice Notice
     * @return Response 更新条数
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    Response<Boolean> updateByPrimaryKey(Notice notice);

    /**
     * 按主键伪删除
     *
     * @param notice Notice
     * @return Response 更新条数
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    Response<Boolean> deleteByPrimaryKey(Notice notice);

    /**
     * 根据主键id获取附件信息列表
     *
     * @param id 主键ID
     * @return Response 附件列表
     * @author fanggang7
     * @date 2020-07-05 19:48:12 周日
     */
    Response<NoticeAttachment> getAttachmentById(Long id);

    /**
     * 根据通知id获取附件信息列表
     *
     * @param noticeId 通知主键ID
     * @return Response 附件列表
     * @author fanggang7
     * @date 2020-07-05 19:48:12 周日
     */
    Response<List<NoticeAttachment>> getAttachmentByNoticeId(Long noticeId);

    /**
     * 批量新增
     *
     * @param attachments 附件数据
     * @return 新增结果
     * @author fanggang7
     * @date 2020-07-05 19:51:44 周日
     */
    Response<List<NoticeAttachment>> batchAddAttachment(List<NoticeAttachment> attachments);

    /**
     * 删除附件
     *
     * @param id 附件主键ID
     * @return Response
     * @author fanggang7
     * @date 2020-07-05 19:46:59 周日
     */
    Response<Integer> deleteAttachmentById(Long id);

    /**
     * 删除附件
     *
     * @param noticeId 通知主键ID
     * @return Response
     * @author fanggang7
     * @date 2020-07-05 19:46:59 周日
     */
    Response<Integer> deleteAttachmentByNoticeId(Long noticeId);

}
