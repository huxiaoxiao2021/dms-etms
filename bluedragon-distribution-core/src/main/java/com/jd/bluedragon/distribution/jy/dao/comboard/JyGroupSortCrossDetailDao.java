package com.jd.bluedragon.distribution.jy.dao.comboard;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.comboard.response.CTTGroupDto;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntityQueryDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyCTTGroupUpdateReq;

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
        return this.getSqlSession().update(NAMESPACE + ".updateByPrimaryKeySelective", record);
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
    public JyGroupSortCrossDetailEntity selectOneByFlowAndTemplateCode(JyGroupSortCrossDetailEntity query) {
        return this.getSqlSession().selectOne(NAMESPACE + ".selectOneByFlowAndTemplateCode", query);
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
    public List<CTTGroupDto> listGroupByEndSiteCodeOrCTTCode(JyGroupSortCrossDetailEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".listGroupByEndSiteCodeOrCTTCode",entity);
    }

    public List<CTTGroupDto> listCountByTemplateCode(JyGroupSortCrossDetailEntity condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".listCountByTemplateCode",condition);
    }


    /**
     * 按参数查询
     */
    public List<JyGroupSortCrossDetailEntity> selectByCondition(JyGroupSortCrossDetailEntityQueryDto query) {
        return this.getSqlSession().selectList(NAMESPACE + ".selectByCondition", query);
    }

    /**
     * 完成混扫任务
     * @param condition
     * @return
     */
    public int mixScanTaskComplete(JyGroupSortCrossDetailEntity condition) {
        return this.getSqlSession().update(NAMESPACE + ".mixScanTaskComplete", condition);
    }

    /**
     * 按条件统计条数
     * @param queryDto
     * @return
     */
    public int countByCondition(JyGroupSortCrossDetailEntityQueryDto queryDto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countByCondition", queryDto);
    }

    public int deleteBySiteAndBizId(JyGroupSortCrossDetailEntity condition) {
        return this.getSqlSession().update(NAMESPACE + ".deleteBySiteAndBizId", condition);
    }

    /**
     * 查询岗位下的网格信息
     * @param code
     * @return
     */
    public List<String> queryGroupCodeByFuncCode(String code) {
        JyGroupSortCrossDetailEntityQueryDto condition = new JyGroupSortCrossDetailEntityQueryDto();
        condition.setFuncType(code);
        return this.getSqlSession().selectList(NAMESPACE + ".queryGroupCodeByFuncCode", condition);
    }

    /**
     * 根据网格删除混扫任务
     * @param queryDto
     */
    public int deleteCTTGroupDataByGroupCode(JyGroupSortCrossDetailEntityQueryDto queryDto) {
        return this.getSqlSession().update(NAMESPACE + ".deleteCTTGroupDataByGroupCode", queryDto);
    }

    /**
     * 查询limit条最新创建的混扫任务的最小ID
     * @param queryDto
     * @return
     */
    public Long queryMinIdByGroupCode(JyGroupSortCrossDetailEntityQueryDto queryDto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryMinIdByGroupCode", queryDto);
    }
    
}
