package com.jd.bluedragon.distribution.notice.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.notice.domain.NoticeAttachment;

import java.util.List;
import java.util.Map;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName NoticeAttachmentDao
 * @date 2019/4/18
 */
public class NoticeAttachmentDao extends BaseDao<NoticeAttachment> {

    private final static String NAMESPACE = NoticeAttachmentDao.class.getName();

    public NoticeAttachment getById(Long id) {
        return this.getSqlSession().selectOne(NoticeAttachmentDao.NAMESPACE + ".getById", id);
    }

    /**
     * 查询所有通知
     *
     * @return
     */
    public List<NoticeAttachment> getByNoticeId(Map<String, Object> parameter) {
        return this.getSqlSession().selectList(NoticeAttachmentDao.NAMESPACE + ".getByNoticeId", parameter);
    }

    /**
     * 新增
     *
     * @param attachment
     * @return
     */
    public int add(NoticeAttachment attachment) {
        return this.getSqlSession().insert(NoticeAttachmentDao.NAMESPACE + ".add", attachment);
    }

    /**
     * 批量新增
     *
     * @param attachments
     * @return
     */
    public int batchAdd(List<NoticeAttachment> attachments) {
        return this.getSqlSession().insert(NoticeAttachmentDao.NAMESPACE + ".batchAdd", attachments);
    }

    /**
     * 逻辑删除
     *
     * @param parameter
     * @return
     */
    public int deleteByNoticeId(Map<String, Object> parameter) {
        return this.getSqlSession().update(NoticeAttachmentDao.NAMESPACE + ".deleteByNoticeId", parameter);
    }

}
