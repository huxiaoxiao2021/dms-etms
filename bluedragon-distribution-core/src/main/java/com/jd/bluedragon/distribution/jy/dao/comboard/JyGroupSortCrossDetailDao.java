package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.comboard.request.CTTGroupDataReq;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDataResp;
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
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", record);
    }
    public JyGroupSortCrossDetailEntity selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectByPrimaryKey", id);
    }
    public int updateByPrimaryKeySelective(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".deleteByPrimaryKey", record);
    }
    public int updateByPrimaryKey(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKey", record);
    }

    public List<JyGroupSortCrossDetailEntity> listSendFlowByTemplateCodeOrEndSiteCode(JyGroupSortCrossDetailEntity record) {
        return this.getSqlSession().selectList(NAMESPACE + ".listSendFlowByTemplateCodeOrEndSiteCode", record);
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
     * 查询常用流向
     * @param entity
     * @return
     */
    public List<CTTGroupDto> queryCommonCTTGroupData(JyGroupSortCrossDetailEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryCommonCTTGroupData", entity);
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

    /**
     * 根据目的地站点或滑道笼车查询流向
     * @param entity
     * @return
     */
    public List<CTTGroupDto> listCTTGroupData(JyGroupSortCrossDetailEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".listCTTGroupData",entity);
    }
}
