package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.feedback.domain.FeedBackResponse;
import com.jd.bluedragon.distribution.feedback.service.FeedbackService;
import com.jd.bluedragon.external.gateway.service.FeedbackGatewayService;
import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.FeedbackQueryDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;
import com.jd.jdwl.feedback.vo.TypeVo;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("feedbackGatewayServiceImpl")
public class FeedbackGatewayServiceImpl implements FeedbackGatewayService {

    @Autowired
    private FeedbackService feedbackService;
    @Override
    public JdCResponse<Boolean> createFeedbackWithUrls(FeedbackDto dto) {
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
        if (StringUtils.isEmpty(dto.getUserName())) {
            response.toFail("反馈人姓名不能为空！");
            return response;
        }
        response.setData(feedbackService.createFeedbackWithUrls(dto));
        return response;
    }

    @Override
    public JdCResponse<List<TypeVo>> queryFeedBackType(UserInfoDto userInfoDto) {
        JdCResponse<List<TypeVo>> response = new JdCResponse<>();
        response.toSucceed();
        if (userInfoDto.getAppId() == null) {
            response.toFail("AppId不能为空！");
            return response;
        }
        if (StringUtils.isEmpty(userInfoDto.getUserAccount())) {
            response.toFail("用户erp不能为空！");
            return response;
        }
        if (userInfoDto.getOrgType() == null) {
            response.toFail("orgType不能为空！");
            return response;
        }
        userInfoDto.setOrgType(Constants.ORG_TYPE_ERP);
        List<TypeVo> feedbackType = feedbackService.queryFeedBackType(userInfoDto);
        response.setData(feedbackType);
        return response;
    }

    @Override
    public JdCResponse<PagerResult<FeedBackResponse>> queryFeedback(FeedbackQueryDto queryDto) {
        JdCResponse<PagerResult<FeedBackResponse>> response = new JdCResponse<>();
        response.toSucceed();
        if (StringUtils.isEmpty(queryDto.getUserAccount())) {
            response.toFail("用户erp不能为空！");
            return response;
        }
        if (queryDto.getPageSize() == null) {
            queryDto.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        if (queryDto.getIndex() == null) {
            queryDto.setIndex(Constants.DEFAULT_PAGE_NO);
        }
        queryDto.setAppId(Constants.APP_ID);
        PagerResult<FeedBackResponse> result = feedbackService.queryFeedback(queryDto);
        response.setData(result);
        return response;
    }
}
