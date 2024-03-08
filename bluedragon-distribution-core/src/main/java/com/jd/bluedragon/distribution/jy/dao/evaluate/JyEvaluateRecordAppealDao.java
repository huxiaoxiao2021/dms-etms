package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealDto;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealUpdateDto;

import java.util.ArrayList;
import java.util.List;

/***
 * 
 * @author pengchong28
 * @email pengchong28@jd.com
 * @date 2024-03-01 15:59:15
 */
public class JyEvaluateRecordAppealDao extends BaseDao<JyEvaluateRecordAppealEntity> {

    private final static String NAMESPACE = JyEvaluateRecordAppealDao.class.getName();

    public List<JyEvaluateRecordAppealEntity> queryListByCondition(List<JyEvaluateRecordAppealEntity> conditions) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryListByCondition", conditions);
    }

    public int batchInsert(List<JyEvaluateRecordAppealDto> entityList) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", entityList);
    }

    public List<JyEvaluateRecordAppealEntity> queryDetailByCondition(JyEvaluateRecordAppealDto condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryDetailByCondition", condition);
    }

    public List<JyEvaluateRecordAppealEntity> queryByIdList(List<Long> idList) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryDetailByCondition", idList);

    }
    /**
     * 批量更新指定ID的记录状态
     * @param dto 要更新的评价记录申诉实体列表
     * @return 更新影响的行数
     */
    public int batchUpdateStatusByIds(JyEvaluateRecordAppealUpdateDto dto) {
        return this.getSqlSession().update(NAMESPACE + ".batchUpdatePassStatusByIds", dto);
    }

    public Integer getAppealRejectCount(Long loadSiteCode) {
        return this.getSqlSession().selectOne(NAMESPACE + ".getAppealRejectCount", loadSiteCode);
    }
}
