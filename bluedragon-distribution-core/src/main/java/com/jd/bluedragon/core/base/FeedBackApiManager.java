package com.jd.bluedragon.core.base;

import com.jd.jdwl.feedback.common.dto.Result;
import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.FeedbackQueryDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;
import com.jd.jdwl.feedback.vo.FeedbackVo;
import com.jd.jdwl.feedback.vo.PageVo;

import java.util.Map;

/**
 * 新版意见反馈
 * @author liuao
 */
public interface FeedBackApiManager {

    Boolean createFeedBack(FeedbackDto feedbackDto);

    public Map<Long,String> queryFeedBackType(UserInfoDto userInfoDto);

    PageVo<FeedbackVo> queryFeedback(FeedbackQueryDto queryDto);

    boolean checkHasFeedBack(String accountCode,Long appId);
}
