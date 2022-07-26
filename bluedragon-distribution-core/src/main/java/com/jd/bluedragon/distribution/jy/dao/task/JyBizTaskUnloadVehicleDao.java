package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卸车业务任务表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:34
 */
public class JyBizTaskUnloadVehicleDao extends BaseDao<JyBizTaskUnloadVehicleEntity> {

    final static String NAMESPACE = JyBizTaskUnloadVehicleDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyBizTaskUnloadVehicleEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    /**
     * 根据bizId获取数据
     * @return
     */
    public JyBizTaskUnloadVehicleEntity findByBizId(String bizId){
        Map<String,Object> params = new HashMap<>();
        params.put("bizId",bizId);
        return this.getSqlSession().selectOne(NAMESPACE + ".findByBizId", params);
    }

    /**
     * 根据派车明细编码获取数据
     * @param transWorkItemCode
     * @return
     */
    public JyBizTaskUnloadVehicleEntity findByTransWorkItemCode(String transWorkItemCode) {
        Map<String,Object> params = new HashMap<>();
        params.put("transWorkItemCode",transWorkItemCode);
        return this.getSqlSession().selectOne(NAMESPACE + ".findByTransWorkItemCode", params);
    }
    /**
     * 根据bizId获取数据只返回逻辑主键
     * @return
     */
    public Long findIdByBizId(String bizId){
        Map<String,Object> params = new HashMap<>();
        params.put("bizId",bizId);
        return this.getSqlSession().selectOne(NAMESPACE + ".findIdByBizId", params);
    }
    /**
     * 根据bizId获取数据只返回逻辑主键和状态
     * @return
     */
    public JyBizTaskUnloadVehicleEntity findIdAndStatusByBizId(String bizId){
        Map<String,Object> params = new HashMap<>();
        params.put("bizId",bizId);
        return this.getSqlSession().selectOne(NAMESPACE + ".findIdAndStatusByBizId", params);
    }

    /**
     * 按状态返回统计数量
     * @param entity
     * @param statuses
     * @return
     */
    public List<JyBizTaskUnloadCountDto> findStatusCountByCondition4Status(JyBizTaskUnloadVehicleEntity entity, List<Integer> statuses, List<String> sealCarCodes){
        Map<String,Object> params = new HashMap<>();
        params.put("entity",entity);
        params.put("statuses",statuses);
        if (CollectionUtils.isNotEmpty(sealCarCodes)) {
            params.put("sealCarCodes", sealCarCodes);
        }
        return this.getSqlSession().selectList(NAMESPACE + ".findStatusCountByCondition4Status", params);

    }

    /**
     * 按状态车辆类型返回统计数量
     * @param entity
     * @param statuses
     * @return
     */
    public List<JyBizTaskUnloadCountDto> findStatusCountByCondition4StatusAndLine(JyBizTaskUnloadVehicleEntity entity, List<Integer> statuses, List<String> sealCarCodes){
        Map<String,Object> params = new HashMap<>();
        params.put("entity",entity);
        params.put("statuses",statuses);
        if (CollectionUtils.isNotEmpty(sealCarCodes)) {
            params.put("sealCarCodes", sealCarCodes);
        }
        return this.getSqlSession().selectList(NAMESPACE + ".findStatusCountByCondition4StatusAndLine", params);

    }

    /**
     * 分页获取数据
     * @param condition
     * @param typeEnum
     * @param offset
     * @param limit
     * @param sealCarCodes
     * @return
     */
    public List<JyBizTaskUnloadVehicleEntity> findByConditionOfPage(JyBizTaskUnloadVehicleEntity condition, JyBizTaskUnloadOrderTypeEnum typeEnum, Integer offset, Integer limit, List<String> sealCarCodes) {
        Map<String,Object> params = new HashMap<>();
        params.put("entity",condition);
        if(typeEnum != null) {
            params.put("orderType",typeEnum.getCode());
        }
        params.put("offset",offset);
        params.put("limit",limit);
        if (CollectionUtils.isNotEmpty(sealCarCodes)) {
            params.put("sealCarCodes", sealCarCodes);
        }
        return this.getSqlSession().selectList(NAMESPACE + ".findByConditionOfPage", params);
    }

    /**
     * 根据业务主键更新状态
     * @param entity
     * @return
     */
    public int changeStatus(JyBizTaskUnloadVehicleEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateStatusByBizId",entity);
    }

    /**
     * 根据BIZ_ID 更新基础数据
     * @param entity
     * @return
     */
    public int updateOfBaseInfoById(JyBizTaskUnloadVehicleEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateOfBaseInfoById",entity);
    }


    /**
     * 根据BIZ_ID 更新其他业务数据
     * @param entity
     * @return
     */
    public int updateOfBusinessInfoById(JyBizTaskUnloadVehicleEntity entity) {
        return this.getSqlSession().update(NAMESPACE + ".updateOfBusinessInfoById",entity);
    }
    /**
     * 根据车牌、状态、目的地查询任务数量
     * @param condition
     * @return
     */
	public Long countByVehicleNumberAndStatus(JyBizTaskUnloadVehicleEntity condition){
		return this.getSqlSession().selectOne(NAMESPACE + ".countByVehicleNumberAndStatus",condition);
	}


    public List<JyBizTaskUnloadVehicleEntity> listUnloadVehicleTask(JyBizTaskUnloadVehicleEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".listUnloadVehicleTask",entity);
    }
}
