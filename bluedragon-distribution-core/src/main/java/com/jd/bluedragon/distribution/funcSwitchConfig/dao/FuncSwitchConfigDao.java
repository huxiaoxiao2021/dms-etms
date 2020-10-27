package com.jd.bluedragon.distribution.funcSwitchConfig.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigCondition;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;

import java.util.List;

/**
 * 功能开关配置DAO
 *
 * @author: hujiping
 * @date: 2020/9/17 10:28
 */
public class FuncSwitchConfigDao extends BaseDao<FuncSwitchConfigDto> {

    public static final String namespace = FuncSwitchConfigDao.class.getName();

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(FuncSwitchConfigDto detail){
        return this.getSqlSession().insert(namespace + ".add",detail);
    }

    /**
     * 根据条件分页查询
     * @param condition
     * @return*/
    public List<FuncSwitchConfigDto> queryByCondition(FuncSwitchConfigCondition condition){
        return this.getSqlSession().selectList(namespace + ".queryByCondition",condition);
    }

    /**
     * 根据条件查询某条记录YN
     */
    public Integer  queryYnByCondition(FuncSwitchConfigCondition condition){
        return  this.getSqlSession().selectOne(namespace+".queryYnByCondition",condition);
    }


    /**
     * 查询总数
     * @param condition
     * @return*/
    public Integer queryCountByCondition(FuncSwitchConfigCondition condition){
        return this.getSqlSession().selectOne(namespace + ".queryCountByCondition",condition);
    }

    /**
     * 批量新增
     * @param dataList
     */
    public int batchAdd(List<FuncSwitchConfigDto> dataList) {
        return this.getSqlSession().insert(namespace + ".batchAdd",dataList);
    }

    /**
     * 逻辑删除
     *  置为无效
     * @param ids
     */
    public void logicalDelete(List<Long> ids) {
        this.getSqlSession().delete(namespace + ".logicalDelete",ids);
    }

    /**
     * 根据条件查询
     * @param dto
     * @return
     */
    public List<FuncSwitchConfigDto> selectByFuncSwitchConfig(FuncSwitchConfigDto dto) {
        return this.getSqlSession().selectList(namespace + ".selectByFuncSwitchConfig",dto);
    }

    /**
     * 根据条件更新
     * @param funcSwitchConfigDto
     * @return
     */
    public int updateByFuncSwitchConfig(FuncSwitchConfigDto funcSwitchConfigDto) {
        return this.getSqlSession().update(namespace + ".updateByFuncSwitchConfig",funcSwitchConfigDto);
    }

    public int selectConfiguredCount(FuncSwitchConfigDto dto) {
        return this.getSqlSession().selectOne(namespace + ".selectConfiguredCount",dto);
    }
}
