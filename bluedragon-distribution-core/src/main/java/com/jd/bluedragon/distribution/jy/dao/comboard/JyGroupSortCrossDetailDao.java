package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.comboard.request.CTTGroupDataReq;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDto;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyCTTGroupUpdateReq;

import java.util.HashMap;
import java.util.List;

public class JyGroupSortCrossDetailDao extends BaseDao<JyGroupSortCrossDetailEntity> {
    private final static String NAMESPACE = JyGroupSortCrossDetailDao.class.getName();

    public int deleteByPrimaryKey(Long id) {
        return this.getSqlSession().delete(NAMESPACE + ".deleteByPrimaryKey", id);
    }

    public int insert(JyGroupSortCrossDetailEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }
    public int insertSelective(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().insert(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public JyGroupSortCrossDetailEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".deleteByPrimaryKey", id);
    }
    public int updateByPrimaryKeySelective(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public int updateByPrimaryKey(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }

    /**
     * 批量新增
     * @param list
     * @return
     */
    public int batchInsert(List<JyGroupSortCrossDetailEntity> list) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", list);
    }

    /**
     * 查询当前场地的常用流向
     * @param siteCode
     * @return
     */
    public List<CTTGroupDto> queryCTTGroupDataBySiteCode(Long siteCode) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryCTTGroupDataBySiteCode", siteCode);
    }

    /**
     * 查询当前组的常用流向
     * @param groupCode
     * @param siteCode
     * @return
     */
    public List<CTTGroupDto> queryCTTGroupDataByGroup(String groupCode,int siteCode) {
        JyGroupSortCrossDetailEntity query = new JyGroupSortCrossDetailEntity();
        query.setGroupCode(groupCode);
        query.setStartSiteId((long) siteCode);
        return this.getSqlSession().selectList(NAMESPACE + ".queryCTTGroupDataByGroup",query);
    }

    /**
     * 查询是否存在当前流向
     * @param query
     * @return
     */
    public JyGroupSortCrossDetailEntity selectOneByGroupCrossTableTrolley(JyGroupSortCrossDetailEntity query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOneByGroupCrossTableTrolley", query);
    }

    /**
     * 根据templateCode获取名称
     * @param templateCode
     * @return
     */
    public String queryTemplateName(String templateCode) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryTemplateName", templateCode);
    }

    /**
     * 根据id批量删除
     * @param updateReq
     * @return
     */
    public int deleteByIds(JyCTTGroupUpdateReq updateReq) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByIds", updateReq);
    }
}
