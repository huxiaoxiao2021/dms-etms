package com.jd.bluedragon.distribution.jy.service.attachment;

import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;

import java.util.List;
import java.util.Map;

/**
 * 拣运-附件接口
 *
 * @author hujiping
 * @date 2023/4/19 8:48 PM
 */
public interface JyAttachmentDetailService {

    /**
     * 批量新增
     *
     * @param annexList
     */
    Integer batchInsert(List<JyAttachmentDetailEntity> annexList);

    /**
     * 查询附件列表
     * @param query
     * @return
     */
    List<JyAttachmentDetailEntity> queryDataListByCondition(JyAttachmentDetailQuery query);


    /**
     * 根据bizId和siteCode逻辑删除
     * @param entity
     * @return
     */
    int delete(JyAttachmentDetailEntity entity);

}
