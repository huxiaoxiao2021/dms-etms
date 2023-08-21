package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.feedback.JyFeedbackDto;
import com.jd.bluedragon.common.dto.feedback.JyFeedbackQueryDto;
import com.jd.bluedragon.common.dto.feedback.JyUserInfoDto;
import com.jd.bluedragon.distribution.feedback.domain.FeedBackResponse;
import com.jd.jdwl.feedback.vo.TypeVo;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

public interface FeedbackGatewayService {
    /**
     * 意见反馈类型查询
     * @param userInfoDto
     * @return
     */
    JdCResponse<List<TypeVo>> queryFeedbackType(JyUserInfoDto userInfoDto);

    /**
     * 反馈记录分页查询
     * @param queryDto
     * @return
     */
    JdCResponse<PagerResult<FeedBackResponse>> queryFeedback(JyFeedbackQueryDto queryDto);

    /**
     * 提交意见反馈
     * @param dto
     * @return
     */
    JdCResponse<Boolean> createFeedbackWithUrls(JyFeedbackDto dto);
}
