package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.feedback.JyFeedbackDto;
import com.jd.bluedragon.common.dto.feedback.JyFeedbackQueryDto;
import com.jd.bluedragon.common.dto.feedback.JyUserInfoDto;
import com.jd.bluedragon.distribution.feedback.domain.FeedBackResponse;
import com.jd.bluedragon.distribution.feedback.service.FeedbackService;
import com.jd.bluedragon.external.gateway.service.FeedbackGatewayService;
import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.FeedbackQueryDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;
import com.jd.jdwl.feedback.vo.TypeVo;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("feedbackGatewayServiceImpl")
public class FeedbackGatewayServiceImpl implements FeedbackGatewayService {

    @Autowired
    private FeedbackService feedbackService;
    @Override
    public JdCResponse<Boolean> createFeedbackWithUrls(JyFeedbackDto dto) {
        JdCResponse<Boolean> response = new JdCResponse<>();
        response.toSucceed();
        if (dto.getAppId() == null) {
            response.toFail("AppId不能为空！");
            return response;
        }
        if (dto.getTypeId() == null) {
            response.toFail("反馈类型id不能为空！");
            return response;
        }
        if (StringUtils.isEmpty(dto.getTypeName())) {
            response.toFail("反馈类型名称不能为空！");
            return response;
        }
        if (StringUtils.isEmpty(dto.getUserAccount())) {
            response.toFail("反馈人erp不能为空！");
            return response;
        }
        if (StringUtils.isEmpty(dto.getContent())) {
            response.toFail("反馈描述不能为空！");
            return response;
        }
        FeedbackDto feedbackDto = new FeedbackDto();
        BeanUtils.copyProperties(dto, feedbackDto);
        response.setData(feedbackService.createFeedbackWithUrls(feedbackDto));
        return response;
    }

    @Override
    public JdCResponse<List<TypeVo>> queryFeedbackType(JyUserInfoDto dto) {
        JdCResponse<List<TypeVo>> response = new JdCResponse<>();
        response.toSucceed();
        if (dto.getAppId() == null) {
            response.toFail("AppId不能为空！");
            return response;
        }
        if (StringUtils.isEmpty(dto.getUserAccount())) {
            response.toFail("用户erp不能为空！");
            return response;
        }
        if (dto.getOrgType() == null) {
            response.toFail("orgType不能为空！");
            return response;
        }
        dto.setOrgType(Constants.ORG_TYPE_ERP);
        UserInfoDto userInfoDto = new UserInfoDto();
        BeanUtils.copyProperties(dto, userInfoDto);
        List<TypeVo> feedbackType = feedbackService.queryFeedBackType(userInfoDto);
        response.setData(feedbackType);
        return response;
    }

    @Override
    public JdCResponse<PagerResult<FeedBackResponse>> queryFeedback(JyFeedbackQueryDto dto) {
        JdCResponse<PagerResult<FeedBackResponse>> response = new JdCResponse<>();
        response.toSucceed();
        if (StringUtils.isEmpty(dto.getUserAccount())) {
            response.toFail("用户erp不能为空！");
            return response;
        }
        if (dto.getAppId() == null) {
            response.toFail("AppId不能为空！");
            return response;
        }
        if (dto.getPageSize() == null) {
            dto.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        if (dto.getIndex() == null) {
            dto.setIndex(Constants.DEFAULT_PAGE_NO);
        }
        FeedbackQueryDto feedbackQueryDto = new FeedbackQueryDto();
        BeanUtils.copyProperties(dto, feedbackQueryDto);
        PagerResult<FeedBackResponse> result = feedbackService.queryFeedback(feedbackQueryDto);
        response.setData(result);
        return response;
    }
}
