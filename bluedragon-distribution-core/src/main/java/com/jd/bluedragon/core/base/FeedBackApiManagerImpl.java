package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.minlog.Log;
import com.jd.jdwl.feedback.api.FeedbackApi;
import com.jd.jdwl.feedback.api.TypeApi;
import com.jd.jdwl.feedback.common.dto.Result;
import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.FeedbackQueryDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;
import com.jd.jdwl.feedback.vo.FeedbackVo;
import com.jd.jdwl.feedback.vo.PageVo;
import com.jd.jdwl.feedback.vo.TypeVo;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
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
        CallerInfo info = Profiler.registerInfo("DMS.BASE.FeedBackApiManagerImpl.createFeedBack", false, true);
        Result feedback = null;
        try {
            feedback = feedbackApi.createFeedback(feedbackDto);
            if (feedback!=null && feedback.getCode()==1){
                return true;
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            Log.error("FeedBackApiManagerImpl.createFeedBack error",e);
        }
        log.info("FeedBackApiManagerImpl.createFeedBack begin param is {} 调用接口返回:{}", JSON.toJSONString(feedbackDto),JSON.toJSONString(feedback));
        return false;
    }

    @Override
    public Map<Long,String> queryFeedBackType(UserInfoDto userInfoDto) {
        log.info("FeedBackApiManagerImpl.queryFeedBackType begin param is {}", JSON.toJSONString(userInfoDto));
        CallerInfo info = Profiler.registerInfo("DMS.BASE.FeedBackApiManagerImpl.queryFeedBackType", false, true);
        Result<List<TypeVo>> feedback = null;
        try {
            feedback = typeApi.queryType(userInfoDto);
        } catch (Exception e) {
            Profiler.functionError(info);
            Log.error("FeedBackApiManagerImpl.queryFeedBackType error",e);
        }
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

    @Override
    public PageVo<FeedbackVo> queryFeedback(FeedbackQueryDto queryDto) {
        log.info("FeedBackApiManagerImpl.queryFeedback begin param is {}",JSON.toJSONString(queryDto));
        CallerInfo info = Profiler.registerInfo("DMS.BASE.FeedBackApiManagerImpl.queryFeedback", false, true);
        Result<PageVo<FeedbackVo>> pageVoResult = null;
        try {
            pageVoResult = feedbackApi.queryFeedback(queryDto);
            log.info("FeedBackApiManagerImpl.queryFeedback  param is {},接口调用返回结果：{}",JSON.toJSONString(queryDto),JSON.toJSONString(pageVoResult));
        } catch (Exception e) {
            Profiler.functionError(info);
            Log.error("FeedBackApiManagerImpl.queryFeedback error",e);
        }
        if (pageVoResult== null || pageVoResult.getCode() !=1){
            return null;
        }
        return pageVoResult.getData();
    }

    @Override
    public boolean checkHasFeedBack(String accountCode, Long appId) {
        FeedbackQueryDto queryDto = new FeedbackQueryDto();
        queryDto.setAppId(appId);
        queryDto.setUserAccount(accountCode);
        queryDto.setPageSize(1);
        queryDto.setIndex(0);
        PageVo<FeedbackVo> feedbackVoPageVo = this.queryFeedback(queryDto);
        Log.info("FeedBackApiManagerImpl.checkHasFeedBack end accountCode is:"+accountCode+" result is: "+JSON.toJSONString(feedbackVoPageVo));
        if (feedbackVoPageVo == null || CollectionUtils.isEmpty(feedbackVoPageVo.getItemList())){
            return false;
        }
        return true;
    }
}
