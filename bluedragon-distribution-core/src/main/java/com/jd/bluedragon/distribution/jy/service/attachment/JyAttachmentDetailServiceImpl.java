package com.jd.bluedragon.distribution.jy.service.attachment;

import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.attachment.JyAttachmentDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 拣运-附件接口实现类
 *
 * @author hujiping
 * @date 2023/4/19 8:48 PM
 */
@Service("jyAttachmentDetailService")
public class JyAttachmentDetailServiceImpl implements JyAttachmentDetailService {
    
    @Autowired
    private JyAttachmentDetailDao jyAttachmentDetailDao;

    @Override
    public Integer batchInsert(List<JyAttachmentDetailEntity> annexList) {
        return jyAttachmentDetailDao.batchInsert(annexList);
    }

    @Override
    public List<JyAttachmentDetailEntity> queryPageListByCondition(Map<String, Object> paramsMap) {
        return jyAttachmentDetailDao.queryPageListByCondition(paramsMap);
    }
}
