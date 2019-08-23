package com.jd.bluedragon.distribution.feedback.service;

import com.jd.bluedragon.distribution.feedback.domain.Feedback;

import java.io.IOException;
import java.util.Map;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName FeedbackService
 * @date 2019/5/27
 */
public interface FeedbackService {

    /**
     * 获取意见反馈类型
     *
     * @param name
     * @return
     */
    Map<Integer, String> getFeedbackType(String name);

    /**
     * 新增意见反馈
     *
     * @param feedback
     * @return
     */
    boolean add(Feedback feedback) throws IOException;

}
