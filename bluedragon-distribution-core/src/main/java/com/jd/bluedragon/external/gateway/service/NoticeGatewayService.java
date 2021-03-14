package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.notice.request.NoticePdaQuery;
import com.jd.bluedragon.distribution.notice.response.NoticeH5Dto;
import com.jd.bluedragon.distribution.notice.response.NoticeLastNewDto;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-10 17:06:09 周三
 */
public interface NoticeGatewayService {

    /**
     * 查询未读通知数
     * @param noticePdaQuery 查询参数
     * @return Long 未读通知数
     * @author fanggang7
     * @date 2021-02-24 20:21:42 周三
     */
    JdCResponse<Long> getNoticeUnreadCount(NoticePdaQuery noticePdaQuery);

    /**
     * 查询最新一条未读通知
     * @param noticePdaQuery 查询参数
     * @return NoticeH5Dto 通知详情
     * @author fanggang7
     * @date 2021-02-24 20:25:38 周三
     */
    JdCResponse<NoticeLastNewDto> getLastNewNotice(NoticePdaQuery noticePdaQuery);
}
