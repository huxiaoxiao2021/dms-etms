package com.jd.bluedragon.distribution.jy.dao.task;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger(JyBizTaskUnloadVehicleDao.class);

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
     * 根据bizId获取实际解封车顺序
     * @return
     */
    public JyBizTaskUnloadVehicleEntity findRealRankingByBizId(JyBizTaskUnloadVehicleEntity entity){
        return this.getSqlSession().selectOne(NAMESPACE + ".findRealRankingByBizId", entity);
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
     * 根据bizId获取数据只返回逻辑主键  不依赖YN状态
     * @return
     */
    public Long findIdByBizIdWithoutYn(String bizId){
        Map<String,Object> params = new HashMap<>();
        params.put("bizId",bizId);
        return this.getSqlSession().selectOne(NAMESPACE + ".findIdByBizIdWithoutYn", params);
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

    /**
     * 根据条件清理数据，将数据yn字段设置成0,等待卸数使用
     * @param condition
     * @return
     */
	public int cleanByParam(JyBizTaskUnloadVehicleEntity condition){
	    if(condition.getCreateTime() == null && condition.getUpdateTime() == null && condition.getUnloadFinishTime() == null){
	        // 必填时间范围防止全部清理
            logger.error("JyBizTaskUnloadVehicleDao.cleanByParam param error! {}", JsonHelper.toJson(condition));
	        return 0;
        }
        return this.getSqlSession().update(NAMESPACE + ".cleanByParam",condition);
    }

    /**
     * 根据状态获取需要清理的数据的场地ID(因为带入状态后不走索引需要移除状态限制，多返回一些数据无碍)
     * @param condition
     * @return
     */
    public List<Integer> needCleanSite(JyBizTaskUnloadVehicleEntity condition){
        return this.getSqlSession().selectList(NAMESPACE + ".needCleanSite",condition);
    }

    /**
     * 按条件查询当前场内卸车信息
     * @param entity
     * @return
     */
    public List<JyBizTaskUnloadVehicleEntity> listUnloadVehicleTask(JyBizTaskUnloadVehicleEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".listUnloadVehicleTask",entity);
    }
}
