package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.feedback.domain.FeedBackResponse;
import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.FeedbackQueryDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;
import com.jd.jdwl.feedback.vo.TypeVo;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;
import java.util.Map;

public interface FeedbackGatewayService {
    JdCResponse<List<TypeVo>> queryFeedBackType(UserInfoDto userInfoDto);
    JdCResponse<PagerResult<FeedBackResponse>> queryFeedback(FeedbackQueryDto queryDto);
    JdCResponse<Boolean> createFeedbackWithUrls(FeedbackDto dto);
}
