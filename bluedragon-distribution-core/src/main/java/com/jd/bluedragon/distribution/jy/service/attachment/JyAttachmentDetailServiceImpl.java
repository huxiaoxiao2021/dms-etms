package com.jd.bluedragon.distribution.jy.service.attachment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.attachment.JyAttachmentDetailDao;
import com.jd.bluedragon.dms.utils.DmsConstants;

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
		if(CollectionUtils.isEmpty(annexList)) {
			return 0;
		}
        return jyAttachmentDetailDao.batchInsert(annexList);
    }

    @Override
    public List<JyAttachmentDetailEntity> queryDataListByCondition(JyAttachmentDetailQuery query) {
    	List<JyAttachmentDetailEntity> emptyList = new ArrayList<>();
    	//站点是拆分键-不能为空
    	if(query == null
    			|| query.getSiteCode() == null) {
    		return emptyList;
    	}
		if(query.getPageSize() == null || query.getPageSize() <= 0) {
			query.setPageSize(DmsConstants.PAGE_SIZE_DEFAULT);
		};
		query.setOffset(0);
		query.setLimit(query.getPageSize());
		if(query.getPageNumber() != null && query.getPageNumber() > 0) {
			query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
		};
        return jyAttachmentDetailDao.queryDataListByCondition(query);
    }

	@Override
	public int delete(JyAttachmentDetailEntity entity) {
		return jyAttachmentDetailDao.delete(entity);
	}
}
