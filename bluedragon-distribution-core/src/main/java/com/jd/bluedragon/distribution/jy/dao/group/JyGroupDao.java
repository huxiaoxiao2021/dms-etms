package com.jd.bluedragon.distribution.jy.dao.group;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.group.JyGroupEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupQuery;

/**
 * 工作小组表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyGroupDao extends BaseDao<JyGroupEntity> {

    final static String NAMESPACE = JyGroupDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyGroupEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    /**
     * 根据岗位码查询
     * @param query
     * @return
     */
	public JyGroupEntity queryGroupByPosition(JyGroupQuery query) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryGroupByPosition", query);
	}
	/**
	 * 查询小组信息
	 * @param groupCode
	 * @return
	 */
	public JyGroupEntity queryByGroupCode(String groupCode) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryByGroupCode", groupCode);
	}
	public JyGroupEntity queryGroupByGroupCode(String groupCode) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryGroupByGroupCode", groupCode);
	}
}
