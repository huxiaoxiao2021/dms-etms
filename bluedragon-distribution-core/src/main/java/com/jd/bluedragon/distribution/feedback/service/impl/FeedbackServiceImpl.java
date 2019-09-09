package com.jd.bluedragon.distribution.feedback.service.impl;

import com.jd.bluedragon.core.base.MrdFeedbackManager;
import com.jd.bluedragon.distribution.basic.FileUtils;
import com.jd.bluedragon.distribution.feedback.domain.Feedback;
import com.jd.bluedragon.distribution.feedback.service.FeedbackService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.mrd.delivery.rpc.sdk.feedback.dto.UserFeedbackContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private JssService jssService;

    @Value("${jss.feedback.bucket}")
    private String bucket;

    @Override
    public Map<Integer, String> getFeedbackType(String name) {
        return mrdFeedbackManager.getFeedType(name);
    }

    @Override
    public boolean add(Feedback feedback) throws IOException {
        List<String> urlList = this.batchUploadFile(feedback.getImages());
        return mrdFeedbackManager.saveFeedback(this.toUserFeedbackContent(feedback, urlList));
    }

    /**
     * 转换为意见反馈系统对象
     *
     * @param feedback
     * @param urlList
     * @return
     */
    private UserFeedbackContent toUserFeedbackContent(Feedback feedback, List<String> urlList) {
        UserFeedbackContent userFeedbackContent = new UserFeedbackContent();
        userFeedbackContent.setPackageName(feedback.getAppPackageName());
        userFeedbackContent.setPin(feedback.getUserErp());
        userFeedbackContent.setFeedbackType(feedback.getType());
        userFeedbackContent.setContactInfo(feedback.getContactInfo());
        userFeedbackContent.setFeedbackContent(feedback.getContent());
        userFeedbackContent.setAppVersion("1");
        userFeedbackContent.setOs("PC");
        userFeedbackContent.setRoleIndex("-1");
        userFeedbackContent.setImages(urlList);
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
