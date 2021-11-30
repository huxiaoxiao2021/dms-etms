package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;

import java.util.List;

/**
 * Description: 快递弃件暂存<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2021-03-31 11:32:59 周三
 */
public class DiscardedPackageStorageTempDao extends BaseDao<DiscardedPackageStorageTemp> {

    private final static String NAMESPACE = DiscardedPackageStorageTempDao.class.getName();

    /**
     * 按条件查询一行数据
     * @param query 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-03-31 11:32:59 周三
     */
    public DiscardedPackageStorageTemp selectOne(DiscardedPackageStorageTempQo query) {
        return this.getSqlSession().selectOne(DiscardedPackageStorageTempDao.NAMESPACE + ".selectOne", query);
    }

    /**
     * 按条件查询总数
     * @param query 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public Long selectCount(DiscardedPackageStorageTempQo query) {
        return this.getSqlSession().selectOne(DiscardedPackageStorageTempDao.NAMESPACE + ".selectCount", query);
    }

    /**
     * 按条件查询列表
     * @param query 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public List<DiscardedPackageStorageTemp> selectList(DiscardedPackageStorageTempQo query) {
        return this.getSqlSession().selectList(DiscardedPackageStorageTempDao.NAMESPACE + ".selectList", query);
    }

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public DiscardedPackageStorageTemp selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(DiscardedPackageStorageTempDao.NAMESPACE + ".selectByPrimaryKey", id);
    }

    /**
     * 新增
     * @param discardedPackageStorageTemp 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-03-31 11:32:59 周三
     */
    public int insertSelective(DiscardedPackageStorageTemp discardedPackageStorageTemp) {
        return this.getSqlSession().insert(DiscardedPackageStorageTempDao.NAMESPACE + ".insertSelective", discardedPackageStorageTemp);
    }

    /**
     * 批量新增
     * @param listParam
     * @return
     */
    public int batchInsert(List<DiscardedPackageStorageTemp> listParam) {
        return this.getSqlSession().insert(DiscardedPackageStorageTempDao.NAMESPACE + ".batchInsert", listParam);
    }

    /**
     * 按主键更新
     * @param discardedPackageStorageTemp 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-03-31 11:32:59 周三
     */
    public int updateByWaybillCode(DiscardedPackageStorageTemp discardedPackageStorageTemp) {
        return this.getSqlSession().update(DiscardedPackageStorageTempDao.NAMESPACE + ".updateByWaybillCode", discardedPackageStorageTemp);
    }

    /**
     * 批量更新
     * @param listParam
     * @return
     */
    public int batchUpdate(List<DiscardedPackageStorageTemp> listParam) {
        return this.getSqlSession().update(DiscardedPackageStorageTempDao.NAMESPACE + ".batchUpdate", listParam);
    }

    /**
     * 按主键删除
     * @param discardedPackageStorageTemp 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public int deleteByPrimaryKey(DiscardedPackageStorageTemp discardedPackageStorageTemp) {
        return this.getSqlSession().update(DiscardedPackageStorageTempDao.NAMESPACE + ".deleteByPrimaryKey", discardedPackageStorageTemp);
    }
    /**
     * 按主键更新
     * @param discardedPackageStorageTemp 参数
     * @return 数据列表
     */
    public int updateById(DiscardedPackageStorageTemp discardedPackageStorageTemp) {
        return this.getSqlSession().update(DiscardedPackageStorageTempDao.NAMESPACE + ".updateById", discardedPackageStorageTemp);
    }
}
