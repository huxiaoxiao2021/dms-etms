package com.jd.bluedragon.distribution.feedback.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.core.base.FeedBackApiManager;
import com.jd.bluedragon.core.base.MrdFeedbackManager;
import com.jd.bluedragon.distribution.basic.FileUtils;
import com.jd.bluedragon.distribution.feedback.domain.FeedBackResponse;
import com.jd.bluedragon.distribution.feedback.domain.FeedbackNew;
import com.jd.bluedragon.distribution.feedback.domain.ReplyResponse;
import com.jd.bluedragon.distribution.feedback.service.FeedbackService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.jdwl.feedback.dto.FeedbackDto;
import com.jd.jdwl.feedback.dto.FeedbackQueryDto;
import com.jd.jdwl.feedback.dto.UserInfoDto;
import com.jd.jdwl.feedback.vo.FeedbackVo;
import com.jd.jdwl.feedback.vo.PageVo;
import com.jd.jdwl.feedback.vo.ReplyVo;
import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName FeedbackServiceImpl
 * @date 2019/5/27
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private MrdFeedbackManager mrdFeedbackManager;
    @Autowired
    private FeedBackApiManager feedBackApiManager;

    @Autowired
    private JssService jssService;

    @Value("${jss.feedback.bucket}")
    private String bucket;

    @Override
    public Map<Integer, String> getFeedbackType(String name) {
        return mrdFeedbackManager.getFeedType(name);
    }

    @Override
    public boolean add(FeedbackNew feedback) throws IOException {
        List<String> urlList = this.batchUploadFile(feedback.getImgs());
        return feedBackApiManager.createFeedBack(this.toUserFeedbackContent(feedback, urlList));
    }

    @Override
    public Map<Long, String> getFeedbackTypeNew(Long appId, String userAccount, Integer orgType) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setAppId(appId);
        userInfoDto.setUserAccount(userAccount);
        userInfoDto.setOrgType(orgType);
        return feedBackApiManager.queryFeedBackType(userInfoDto);
    }

    @Override
    public boolean checkHasFeedBack(Long appId, String userAccount) {
        return feedBackApiManager.checkHasFeedBack(userAccount, appId);
    }

    @Override
    public PagerResult<FeedBackResponse> queryFeedBackPage(BasePagerCondition pagerCondition, String userCode, Long appId) {
        FeedbackQueryDto queryDto = new FeedbackQueryDto();
        queryDto.setUserAccount(userCode);
        queryDto.setAppId(appId);
        int currentPage = pagerCondition.getOffset()/pagerCondition.getLimit()+1;
        queryDto.setIndex(currentPage);
        queryDto.setPageSize(pagerCondition.getLimit());
        PageVo<FeedbackVo> feedbackVoPageVo = feedBackApiManager.queryFeedback(queryDto);
        PagerResult<FeedBackResponse> result= new PagerResult<>();
        if (feedbackVoPageVo == null){
            return result;
        }
        return fillResponse(feedbackVoPageVo);
    }

    private PagerResult<FeedBackResponse> fillResponse(PageVo<FeedbackVo> feedbackVoPageVo) {
        if (feedbackVoPageVo == null || CollectionUtils.isEmpty(feedbackVoPageVo.getItemList())){
            return new PagerResult<>();
        }
        PagerResult<FeedBackResponse> result =  new PagerResult<>();
        List<FeedBackResponse> responseList =  new ArrayList<>();
        for (FeedbackVo feedbackVo : feedbackVoPageVo.getItemList()) {
            FeedBackResponse response = new FeedBackResponse();
            response.setContent(feedbackVo.getContent());
            response.setCreateTime(feedbackVo.getCreateTime());
            response.setStatus(feedbackVo.getStatus());
            response.setStatusName(feedbackVo.getStatusName());
            response.setTypeName(feedbackVo.getTypeName());
            response.setUserName(feedbackVo.getUserName());
            response.setUserAccount(feedbackVo.getUserAccount());
            response.setAttachmentList(feedbackVo.getAttachment());
            if (CollectionUtils.isNotEmpty(feedbackVo.getReplys())){
                List<ReplyResponse> replyResponses = new ArrayList<>();
                for (ReplyVo reply : feedbackVo.getReplys()) {
                    ReplyResponse replyResponse = new ReplyResponse();
                    replyResponse.setContent(reply.getContent());
                    replyResponse.setCreateTime(reply.getCreateTime());
                    replyResponse.setUserAccount(reply.getUserAccount());
                    replyResponse.setImgs(reply.getImg());
                    replyResponse.setType(reply.getType());
                    replyResponses.add(replyResponse);
                }
                response.setReplys(replyResponses);
            }
            response.setViewDataJsonStr(JSON.toJSONString(response));
            responseList.add(response);
        }
        result.setRows(responseList);
        result.setTotal(feedbackVoPageVo.getTotalItems());
        return result;
    }

    /**
     * 转换为意见反馈系统对象
     *
     * @param feedback
     * @param urlList
     * @return
     */
    private FeedbackDto toUserFeedbackContent(FeedbackNew feedback, List<String> urlList) {
        FeedbackDto userFeedbackContent = new FeedbackDto();
        userFeedbackContent.setAppId(feedback.getAppId());
        userFeedbackContent.setTypeId(feedback.getType());
        userFeedbackContent.setContent(feedback.getContent());
        userFeedbackContent.setUserAccount(feedback.getUserAccount());
        userFeedbackContent.setUserName(feedback.getUserName());
        userFeedbackContent.setImg(urlList);
        return userFeedbackContent;
    }

    /**
     * 批量上传文件
     *
     * @param files
     * @return
     * @throws IOException
     */
    private List<String> batchUploadFile(List<MultipartFile> files) throws IOException {
        if (files != null && files.size() > 0) {
            List<String> urlList = new ArrayList<>(files.size());
            int index = 0;
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String keyName = this.getKeyName(index, FileUtils.getFileExtName(fileName));
                jssService.uploadFile(bucket, keyName, file.getSize(), file.getInputStream());
                urlList.add(jssService.getPublicBucketUrl(bucket, keyName));
                index++;
            }
            return urlList;
        }
        return Collections.emptyList();
    }

    /**
     * 生成一个JSS文件系统的KeyName，由于调用频率极低，所以采用时间戳+1000以内随机数拼接形式
     *
     * @return
     */
    private String getKeyName(int index, String extFileName) {
        Random random = new Random();
        return String.valueOf(System.currentTimeMillis()) + String.valueOf(random.nextInt(1000) + String.valueOf(index)) + "." + extFileName;
    }

}
