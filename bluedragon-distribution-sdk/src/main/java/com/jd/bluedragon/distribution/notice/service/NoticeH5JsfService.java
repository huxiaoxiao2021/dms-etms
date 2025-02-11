package com.jd.bluedragon.distribution.notice.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.notice.request.NoticePdaQuery;
import com.jd.bluedragon.distribution.notice.response.NoticeH5Dto;
import com.jd.bluedragon.distribution.notice.response.NoticeLastNewDto;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 通知相关h5页面接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2020-07-01 16:01:29 周三
 */
public interface NoticeH5JsfService {

    /**
     * 查询未读通知数
     * @param noticePdaQuery 查询参数
     * @return Long 未读通知数
     * @author fanggang7
     * @date 2021-02-24 20:21:42 周三
     */
    Response<Long> getNoticeUnreadCount(NoticePdaQuery noticePdaQuery);

    /**
     * 查询最新一条未读通知
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 通知详情
     * @author fanggang7
     * @date 2021-02-24 20:25:38 周三
     */
    Response<NoticeLastNewDto> getLastNewNotice(NoticePdaQuery noticePdaQuery);

    /**
     * 查询通知列表
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 通知里列表分页数据
     * @author fanggang7
     * @date 2021-02-24 20:30:10 周三
     */
    Response<PageDto<NoticeH5Dto>> getNoticeList(NoticePdaQuery noticePdaQuery);

    /**
     * 查询通知详情
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 通知详情
     * @author fanggang7
     * @date 2021-02-24 20:33:49 周三
     */
    Response<NoticeH5Dto> getNoticeDetail(NoticePdaQuery noticePdaQuery);

    /**
     * 根据关键字查询
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 查询结果分页数据
     * @author fanggang7
     * @date 2021-02-24 20:33:49 周三
     */
    Response<PageDto<NoticeH5Dto>> searchByKeyword(NoticePdaQuery noticePdaQuery);
}
