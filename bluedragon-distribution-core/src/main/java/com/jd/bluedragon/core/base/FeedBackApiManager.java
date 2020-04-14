package com.jd.bluedragon.core.base;

import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;

import java.util.Map;

/**
 * 新版意见反馈
 * @author liuao
 */
public interface FeedBackApiManager {

    Boolean createFeedBack(FeedbackDto feedbackDto);

    public Map<Long,String> queryFeedBackType(UserInfoDto userInfoDto);
}
