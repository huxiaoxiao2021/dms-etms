package com.jd.bluedragon.distribution.notice.jsf.impl;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.request.NoticePdaQuery;
import com.jd.bluedragon.distribution.notice.response.NoticeH5Dto;
import com.jd.bluedragon.distribution.notice.service.NoticeH5JsfService;
import com.jd.bluedragon.distribution.notice.service.NoticeH5Service;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * h5页面通知接口实现
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2020-07-01 16:01:29 周三
 */
@Service("noticeH5JsfService")
public class NoticeH5JsfServiceImpl implements NoticeH5JsfService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NoticeH5Service noticeH5Service;

    /**
     * 查询未读通知数
     *
     * @param noticePdaQuery 查询参数
     * @return Long 未读通知数
     * @author fanggang7
     * @date 2021-02-24 20:21:42 周三
     */
    @Override
    public Response<Long> getNoticeUnreadCount(NoticePdaQuery noticePdaQuery) {
        return noticeH5Service.getNoticeUnreadCount(noticePdaQuery);
    }

    /**
     * 查询最新一条未读通知
     *
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 通知详情
     * @author fanggang7
     * @date 2021-02-24 20:25:38 周三
     */
    @Override
    public Response<NoticeH5Dto> getLastNewNotice(NoticePdaQuery noticePdaQuery) {
        return noticeH5Service.getLastNewNotice(noticePdaQuery);
    }

    /**
     * 查询通知列表
     *
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 通知里列表分页数据
     * @author fanggang7
     * @date 2021-02-24 20:30:10 周三
     */
    @Override
    public Response<PageDto<NoticeH5Dto>> getNoticeList(NoticePdaQuery noticePdaQuery) {
        return noticeH5Service.getNoticeList(noticePdaQuery);
    }

    /**
     * 查询通知列表
     *
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 通知里列表分页数据
     * @author fanggang7
     * @date 2021-02-24 20:30:10 周三
     */
    @Override
    public Response<NoticeH5Dto> getNoticeDetail(NoticePdaQuery noticePdaQuery) {
        return noticeH5Service.getNoticeDetail(noticePdaQuery);
    }

    /**
     * 根据关键字查询
     *
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 查询结果分页数据
     * @author fanggang7
     * @date 2021-02-24 20:33:49 周三
     */
    @Override
    public Response<PageDto<NoticeH5Dto>> searchByKeyword(NoticePdaQuery noticePdaQuery) {
        return noticeH5Service.searchByKeyword(noticePdaQuery);
    }
}
