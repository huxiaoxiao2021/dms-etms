package com.jd.bluedragon.distribution.notice.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.request.NoticeRequest;
import com.jd.bluedragon.distribution.notice.domain.Notice;
import com.jd.bluedragon.distribution.notice.request.NoticeQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
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
     * 分页查询通知数据
     * @param query 查询参数
     * @return 分页数据结果
     * @author fanggang7
     * @date 2020-07-02 16:41:33 周四
     */
    PageDto<Notice> queryPageList(NoticeQuery query);

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
    void addAndUploadFile(List<MultipartFile> files, NoticeRequest request, String userErp) throws IOException;

    /**
     * 按主键查询
     * @param id 主键ID
     * @return Notice 通知详情
     * @author fanggang7
     * @date 2020-07-07 19:52:00 周二
     */
    Response<Notice> getByPrimaryKey(Long id);

    /**
     * 根据id列表删除
     *
     * @param ids
     * @param userErp
     */
    Integer deleteByIds(List<Long> ids, String userErp);

    /**
     * 按主键更新
     *
     * @param notice Notice
     * @return Response 更新条数
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    Response<Boolean> updateByPrimaryKey(Notice notice);

    /**
     * 按主键伪删除
     *
     * @param notice Notice
     * @return Response 更新条数
     * @author fanggang7
     * @date 2020-07-01 20:00:46 周三
     */
    Response<Boolean> deleteByPrimaryKey(Notice notice);

}
