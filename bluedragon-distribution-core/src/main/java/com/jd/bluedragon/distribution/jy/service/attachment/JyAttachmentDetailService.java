package com.jd.bluedragon.distribution.jy.service.attachment;

import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;

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
     * 根据查询条件分页查询数据
     * 
     * @param paramsMap
     * @return
     */
    List<JyAttachmentDetailEntity> queryPageListByCondition(Map<String, Object> paramsMap);
}
