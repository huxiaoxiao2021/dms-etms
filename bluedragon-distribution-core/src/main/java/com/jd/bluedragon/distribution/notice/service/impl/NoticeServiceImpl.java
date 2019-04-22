package com.jd.bluedragon.distribution.notice.service.impl;

import com.jd.bluedragon.distribution.api.request.NoticeRequest;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.notice.dao.NoticeDao;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;
import com.jd.bluedragon.distribution.notice.service.NoticeAttachmentService;
import com.jd.bluedragon.distribution.notice.service.NoticeService;
import com.jd.bluedragon.distribution.notice.utils.NoticeLevelEnum;
import com.jd.bluedragon.distribution.notice.utils.NoticeTypeEnum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author lixin39
 * @Description 通知栏服务
 * @ClassName NoticeServiceImpl
 * @date 2019/4/16
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private final Logger logger = Logger.getLogger(NoticeServiceImpl.class);

    @Autowired
    private NoticeDao noticeDao;

    @Autowired
    private JssService jssService;

    @Autowired
    private NoticeAttachmentService noticeAttachmentService;

    @Value("${jss.notice.bucket}")
    private String bucket;

    @Override
    public List<Notice> getAll(Integer limit) {
        Map<String, Object> parameter = new HashMap<>(1);
        if (limit == null || limit <= 0) {
            // 限制展示数量 为100
            limit = 100;
        }
        parameter.put("limit", limit);
        return this.buildTextName(noticeDao.getByParam(parameter));
    }

    private List<Notice> buildTextName(List<Notice> data) {
        if (data.size() > 0) {
            for (Notice notice : data) {
                if (notice.getType() != null) {
                    NoticeTypeEnum typeEnum = NoticeTypeEnum.getEnum(notice.getType());
                    if (typeEnum != null) {
                        notice.setTypeText(typeEnum.getName());
                    }
                }
                if (notice.getLevel() != null) {
                    NoticeLevelEnum levelEnum = NoticeLevelEnum.getEnum(notice.getLevel());
                    if (levelEnum != null) {
                        notice.setLevelText(levelEnum.getName());
                    }
                }
            }
        }
        return data;
    }

    @Override
    public void add(Notice notice) {
        noticeDao.add(notice);
    }

    @Override
    public void addAndUploadFile(MultipartFile[] files, NoticeRequest request, String userErp) throws IOException {
        Notice notice = this.getNotice(request, userErp);
        // 新增
        this.add(notice);
        // 上传附件
        this.batchUploadFile(files, notice.getId());
    }

    /**
     * 批量上传文件
     *
     * @param files
     * @return
     * @throws IOException
     */
    private void batchUploadFile(MultipartFile[] files, Long noticeId) throws IOException {
        int index = 0;
        List<NoticeAttachment> attachments = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String keyName = this.getKeyName(index, this.getFileExtName(fileName));
            jssService.uploadFile(bucket, keyName, file.getSize(), file.getInputStream());
            index++;

            NoticeAttachment attachment = new NoticeAttachment();
            attachment.setNoticeId(noticeId);
            attachment.setSize(String.valueOf(file.getSize()));
            attachment.setKeyName(keyName);
            attachment.setFileName(fileName);
            attachment.setType(this.getFileExtName(fileName));
            attachments.add(attachment);
        }
        noticeAttachmentService.batchAdd(attachments);
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

    /**
     * 根据文件全名获取文件扩展名
     *
     * @param fullFileName
     * @return
     */
    private String getFileExtName(String fullFileName) {
        int index = fullFileName.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return fullFileName.substring(index + 1);
    }

    private Notice getNotice(NoticeRequest request, String userErp) {
        Notice notice = new Notice();
        notice.setTheme(request.getTheme());
        notice.setLevel(request.getLevel());
        notice.setType(request.getType());
        // 默认都展示
        notice.setIsDisplay(1);
        notice.setIsTopDisplay(request.getIsTopDisplay());
        notice.setUploadTime(new Date());
        notice.setCreateUser(userErp);
        return notice;
    }

    @Override
    public Integer deleteByIds(List<Long> ids, String userErp) {
        if (ids != null && ids.size() > 0) {
            Map<String, Object> parameter = new HashMap<>(2);
            parameter.put("ids", ids);
            parameter.put("updateUser", userErp);
            int result = noticeDao.logicDelete(parameter);
            if (result > 0) {
                for (Long id : ids){
                    noticeAttachmentService.delete(id);
                }
            }
            return result;
        }
        return 0;
    }

}
