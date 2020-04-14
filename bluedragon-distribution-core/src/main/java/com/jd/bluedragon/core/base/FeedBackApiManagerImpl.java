package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.jdwl.feedback.api.FeedbackApi;
import com.jd.jdwl.feedback.api.TypeApi;
import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;
import com.jd.jdwl.feedback.vo.TypeVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author liuao
 */
@Service("FeedBackApiManager")
public class FeedBackApiManagerImpl implements FeedBackApiManager {
    private Logger log = LoggerFactory.getLogger(FeedBackApiManagerImpl.class);

    @Autowired
    private FeedbackApi feedbackApi;
    @Autowired
    private TypeApi typeApi;

    @Override
    public Boolean createFeedBack(FeedbackDto feedbackDto) {
        log.info("FeedBackApiManagerImpl.createFeedBack begin param is {}", JSON.toJSONString(feedbackDto));
        com.jd.jdwl.feedback.common.dto.Result feedback = feedbackApi.createFeedback(feedbackDto);
        log.info("FeedBackApiManagerImpl.createFeedBack begin param is {} 调用接口返回:{}", JSON.toJSONString(feedbackDto),JSON.toJSONString(feedback));
        return null;
    }

    @Override
    public Map<Long,String> queryFeedBackType(UserInfoDto userInfoDto) {
        log.info("FeedBackApiManagerImpl.queryFeedBackType begin param is {}", JSON.toJSONString(userInfoDto));
        com.jd.jdwl.feedback.common.dto.Result<List<TypeVo>> feedback = typeApi.queryType(userInfoDto);
        log.info("FeedBackApiManagerImpl.queryFeedBackType begin param is {} 调用接口返回:{}", JSON.toJSONString(userInfoDto),JSON.toJSONString(feedback));
        if (feedback == null || feedback.getCode() !=1){
            return new HashedMap<>();
        }
        List<TypeVo> data = feedback.getData();
        if (CollectionUtils.isEmpty(data)){
            return new HashedMap<>();
        }
        HashedMap<Long,String> result = new HashedMap<>();
        for (TypeVo datum : data) {
            try {
                result.put(Long.valueOf(datum.getFeedbackType()),datum.getFeedbackTypeName());
            } catch (NumberFormatException e) {
                log.error("FeedBackApiManagerImpl.queryFeedBackType error",e);
            }
        }
        return result;
    }
}
