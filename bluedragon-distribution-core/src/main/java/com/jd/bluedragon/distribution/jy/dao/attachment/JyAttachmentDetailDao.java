package com.jd.bluedragon.distribution.jy.dao.attachment;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * 拣运-附件DAO
 *
 * @author hujiping
 * @date 2023/3/28 3:48 PM
 */
public class JyAttachmentDetailDao extends BaseDao<JyAttachmentDetailEntity> {

    private final static String NAMESPACE = JyAttachmentDetailDao.class.getName();

    private static final String DB_TABLE_NAME = "jy_attachment_detail";

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    public int insert(JyAttachmentDetailEntity entity){
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int batchInsert(List<JyAttachmentDetailEntity> entityList){
        for (JyAttachmentDetailEntity entity : entityList) {
            entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        }
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", entityList);
    }

    public int delete(JyAttachmentDetailEntity entity){
        return this.getSqlSession().delete(NAMESPACE + ".delete", entity);
    }

    public JyAttachmentDetailEntity queryOneByBiz(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryOneByBiz", bizId);
    }

	public List<JyAttachmentDetailEntity> queryDataListByCondition(JyAttachmentDetailQuery query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryDataListByCondition", query);
	}
    public Integer countByCondition(JyAttachmentDetailQuery condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countByCondition", condition);
    }
    
    public List<JyAttachmentDetailEntity> queryAllByTs(JyAttachmentDetailQuery condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryAllByTs", condition);
    }
    public JyAttachmentDetailEntity queryOneById(JyAttachmentDetailQuery condition) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryOneById", condition);
    }
    public int insertWithId(JyAttachmentDetailEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    
}
