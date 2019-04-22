package com.jd.bluedragon.distribution.notice.service;

import com.jd.bluedragon.distribution.api.request.NoticeRequest;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName NoticeService
 * @date 2019/4/16
 */
public interface NoticeService {

    /**
     * 查询所有的通知信息
     *
     * @return
     */
    List<Notice> getAll(Integer limit);

    /**
     * 新增通知
     *
     * @param notice
     */
    void add(Notice notice);

    /**
     * 新增通知并上传附件
     *
     * @param files
     * @param request
     * @param userErp
     */
    void addAndUploadFile(MultipartFile[] files, NoticeRequest request, String userErp) throws IOException;

    /**
     * 根据id列表删除
     *
     * @param ids
     * @param userErp
     */
    Integer deleteByIds(List<Long> ids, String userErp);

}
