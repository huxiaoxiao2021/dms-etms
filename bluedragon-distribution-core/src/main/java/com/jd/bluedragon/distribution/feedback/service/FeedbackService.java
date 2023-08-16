package com.jd.bluedragon.distribution.feedback.service;

import com.jd.bluedragon.distribution.feedback.domain.FeedBackResponse;
import com.jd.bluedragon.distribution.feedback.domain.Feedback;
import com.jd.bluedragon.distribution.feedback.domain.FeedbackNew;
import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.FeedbackQueryDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;
import com.jd.jdwl.feedback.vo.FeedbackVo;
import com.jd.jdwl.feedback.vo.PageVo;
import com.jd.jdwl.feedback.vo.TypeVo;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.io.IOException;
import java.util.List;
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
    boolean add(FeedbackNew feedback) throws IOException;

    /**
     * 新的获取反馈类型的接口
     * @param appId
     * @param userAccount
     * @param orgType
     * @return
     */
    Map<Long, String> getFeedbackTypeNew(Long appId,String userAccount,Integer orgType);

    /**
     * 检查是否有意见反馈
     * @param appId
     * @param userAccount
     * @return
     */
    boolean checkHasFeedBack(Long appId,String userAccount);

    /**
     * 意见反馈分页查询接口
     * @param pagerCondition
     * @param userCode
     * @param appId
     * @return
     */
    PagerResult<FeedBackResponse> queryFeedBackPage(BasePagerCondition pagerCondition, String userCode, Long appId);

    /**
     * 新建意见反馈-带图片urls
     * @param dto
     * @return
     */
    boolean createFeedbackWithUrls(FeedbackDto dto);

    /**
     * 查询反馈类型
     * @param userInfoDto
     * @return
     */
    List<TypeVo> queryFeedBackType(UserInfoDto userInfoDto);

    /**
     * 分页查询用户反馈记录-gateway使用
     * @param queryDto
     * @return
     */
    PagerResult<FeedBackResponse> queryFeedback(FeedbackQueryDto queryDto);
}
