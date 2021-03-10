package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.notice.request.NoticePdaQuery;
import com.jd.bluedragon.distribution.notice.response.NoticeH5Dto;
import com.jd.bluedragon.distribution.notice.service.NoticeH5Service;
import com.jd.bluedragon.external.gateway.service.NoticeGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-03-10 17:07:50 周三
 */
@Service
public class NoticeGatewayServiceImpl implements NoticeGatewayService {

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
    public JdCResponse<Long> getNoticeUnreadCount(NoticePdaQuery noticePdaQuery) {
        JdCResponse<Long> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, "");
        Response<Long> rawResult = noticeH5Service.getNoticeUnreadCount(noticePdaQuery);
        if(rawResult.isSucceed()){
            result.setData(rawResult.getData());
        } else {
            result.toFail(rawResult.getMessage());
        }
        return result;
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
    public JdCResponse<NoticeH5Dto> getLastNewNotice(NoticePdaQuery noticePdaQuery) {
        JdCResponse<NoticeH5Dto> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, "");
        Response<NoticeH5Dto> rawResult = noticeH5Service.getLastNewNotice(noticePdaQuery);
        if(rawResult.isSucceed()){
            result.setData(rawResult.getData());
        } else {
            result.toFail(rawResult.getMessage());
        }
        return result;
    }
}
