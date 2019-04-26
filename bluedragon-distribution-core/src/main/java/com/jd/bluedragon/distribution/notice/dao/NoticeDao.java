package com.jd.bluedragon.distribution.notice.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.notice.domain.Notice;

import java.util.List;
import java.util.Map;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName NoticeDao
 * @date 2019/4/17
 */
public class NoticeDao extends BaseDao<Notice> {

    private final static String NAMESPACE = NoticeDao.class.getName();

    /**
     * 查询所有通知
     *
     * @return
     */
    public List<Notice> getByParam(Map<String, Object> parameter) {
        return this.getSqlSession().selectList(NoticeDao.NAMESPACE + ".getByParam", parameter);
    }

    /**
     * 新增
     *
     * @param notice
     * @return
     */
    public int add(Notice notice) {
        return this.getSqlSession().insert(NoticeDao.NAMESPACE + ".add", notice);
    }

    /**
     * 逻辑删除
     *
     * @param parameter
     * @return
     */
    public int logicDelete(Map<String, Object> parameter) {
        return this.getSqlSession().update(NoticeDao.NAMESPACE + ".logicDelete", parameter);
    }

}
