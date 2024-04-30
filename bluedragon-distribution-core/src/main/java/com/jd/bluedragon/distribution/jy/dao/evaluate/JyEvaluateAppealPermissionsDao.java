package com.jd.bluedragon.distribution.jy.dao.evaluate;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateAppealPermissionsEntity;


/***
 * 
 * @author pengchong28
 * @email pengchong28@jd.com
 * @date 2024-03-01 15:59:15
 */
public class JyEvaluateAppealPermissionsDao extends BaseDao<JyEvaluateAppealPermissionsEntity> {

    private final static String NAMESPACE = JyEvaluateAppealPermissionsDao.class.getName();

    /**
     * 根据条件查询JyEvaluateAppealPermissionsEntity
     * @param siteCode 站点代码
     * @return JyEvaluateAppealPermissionsEntity 对应条件的实体对象
     */
    public JyEvaluateAppealPermissionsEntity queryByCondition(Integer siteCode) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByCondition", siteCode);
    }

    /**
     * 更新申诉状态通过ID
     * @param entity 评价申诉权限实体
     * @return 更新影响的行数
     */
    public int updateAppealStatusById(JyEvaluateAppealPermissionsEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateAppealStatusById", entity);
    }

    /**
     * 插入评价申诉权限实体
     * @param entity 评价申诉权限实体
     * @return 插入操作结果
     */
    public int insert(JyEvaluateAppealPermissionsEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    /**
     * 通过ID更新评价状态
     * @param entity 评价申诉权限实体
     * @return 更新的行数
     */
    public int updateEvaluateStatusById(JyEvaluateAppealPermissionsEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateEvaluateStatusById", entity);
    }

    /**
     * 通过ID更新JyEvaluateAppealPermissionsEntity实体
     * @param entity 要更新的实体对象
     * @return 更新操作影响的行数
     */
    public int updateById(JyEvaluateAppealPermissionsEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateById", entity);
    }
}
