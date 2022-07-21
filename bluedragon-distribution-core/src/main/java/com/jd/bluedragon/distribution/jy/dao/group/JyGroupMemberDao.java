package com.jd.bluedragon.distribution.jy.dao.group;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.group.GroupMemberQueryRequest;
import com.jd.bluedragon.common.dto.group.JyGroupMemberCountData;
import com.jd.bluedragon.common.dto.group.JyGroupMemberData;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.group.JyGroupMemberQuery;

/**
 * 组成员表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyGroupMemberDao extends BaseDao<JyGroupMemberEntity> {

    final static String NAMESPACE = JyGroupMemberDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyGroupMemberEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    /**
     * 批量新增
     * @param memberList
     * @return
     */
	public int batchInsert(List<JyGroupMemberEntity> memberList) {
		return this.getSqlSession().insert(NAMESPACE + ".batchInsert", memberList);
	}
    /**
     * 查询数量
     * @param query
     * @return
     */
	public Long queryCountByGroup(GroupMemberQueryRequest query) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryCountByGroup", query);
	}
    /**
     * 查询签到id列表
     * @param query
     * @return
     */
	public List<Long> querySignIdListByGroup(GroupMemberQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".querySignIdListByGroup", query);
	}
	/**
	 * 根据签到记录查询小组成员
	 * @param signRecordId
	 * @return
	 */
	public JyGroupMemberEntity queryBySignRecordId(Long signRecordId) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryBySignRecordId", signRecordId);
	}
	/**
	 * 剔除小组成员
	 * @param removeMemberData
	 * @return
	 */
	public int removeMember(JyGroupMemberEntity removeMemberData) {
		return this.getSqlSession().update(NAMESPACE + ".removeMember", removeMemberData);
	}
	/**
	 * 查询小组成员列表
	 * @param query
	 * @return
	 */
	public List<JyGroupMemberEntity> queryMemberListByGroup(JyGroupMemberQuery query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryMemberListByGroup", query);
	}
	/**
	 * 查询小组成员数量
	 * @param groupCode
	 * @return
	 */
	public Integer queryGroupMemberNum(String groupCode) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryGroupMemberNum", groupCode);
	}
	public List<JyGroupMemberCountData> queryMemberDataCountByGroup(GroupMemberQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryMemberDataCountByGroup", query);
	}
	public List<JyGroupMemberData> queryMemberDataListByGroup(GroupMemberQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryMemberDataListByGroup", query);
	}
	public JyGroupMemberEntity queryByMemberCode(String memberCode) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryByMemberCode", memberCode);
	}
}
