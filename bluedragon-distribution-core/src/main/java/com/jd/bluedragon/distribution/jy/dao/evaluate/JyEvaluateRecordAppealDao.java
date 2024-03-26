package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealDto;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordAppealUpdateDto;

import java.util.List;

/***
 * 
 * @author pengchong28
 * @email pengchong28@jd.com
 * @date 2024-03-01 15:59:15
 */
public class JyEvaluateRecordAppealDao extends BaseDao<JyEvaluateRecordAppealEntity> {

    private final static String NAMESPACE = JyEvaluateRecordAppealDao.class.getName();

    /**
     * 根据条件查询评价记录申诉实体列表
     * @param list 条件列表
     * @return 符合条件的评价记录申诉实体列表
     */
    public List<JyEvaluateRecordAppealEntity> queryListByCondition(List<String> list) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryListByCondition", list);
    }

    /**
     * 批量插入评价记录申诉数据
     * @param entityList 实体列表
     * @return 插入操作影响的行数
     */
    public int batchInsert(List<JyEvaluateRecordAppealDto> entityList) {
        return this.getSqlSession().insert(NAMESPACE + ".batchInsert", entityList);
    }

    /**
     * 根据条件查询评价记录申诉详情
     * @param condition 查询条件对象
     * @return 符合条件的评价记录申诉详情列表
     */
    public List<JyEvaluateRecordAppealEntity> queryDetailByCondition(JyEvaluateRecordAppealDto condition) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryDetailByCondition", condition);
    }

    /**
     * 根据ID列表查询评价记录申诉实体列表
     * @param list ID列表
     * @return 评价记录申诉实体列表
     */
    public List<JyEvaluateRecordAppealEntity> queryByIdList(List<Long> list) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryByIdList", list);

    }
    /**
     * 批量更新指定ID的记录状态
     * @param dto 要更新的评价记录申诉实体列表
     * @return 更新影响的行数
     */
    public int batchUpdateStatusByIds(JyEvaluateRecordAppealUpdateDto dto) {
        return this.getSqlSession().update(NAMESPACE + ".batchUpdateStatusByIds", dto);
    }

    /**
     * 根据申诉结果获取申诉数量
     * @param dto 装车站点代码
     * @return 申诉被拒绝的次数
     */
    public Integer getAppealCount(JyEvaluateRecordAppealDto dto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".getAppealCount", dto);
    }
}
