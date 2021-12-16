package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedPackageScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.*;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedWaybillStorageTemp;

import java.util.List;

/**
 * Description: 快递弃件暂存运单纬度dao<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 *
 * @author fanggang7
 * @time 2021-12-02 14:21:03 周四
 */
public class DiscardedWaybillStorageTempDao extends BaseDao<DiscardedWaybillStorageTemp> {

    private final static String NAMESPACE = DiscardedWaybillStorageTempDao.class.getName();

    /**
     * 按条件查询一行数据
     * @param query 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-03-31 11:32:59 周三
     */
    public DiscardedWaybillStorageTemp selectOne(DiscardedPackageStorageTempQo query) {
        return this.getSqlSession().selectOne(DiscardedWaybillStorageTempDao.NAMESPACE + ".selectOne", query);
    }

    /**
     * 按条件查询总数
     * @param query 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public Long selectCount(DiscardedPackageStorageTempQo query) {
        return this.getSqlSession().selectOne(DiscardedWaybillStorageTempDao.NAMESPACE + ".selectCount", query);
    }

    /**
     * 按条件查询列表
     * @param query 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public List<DiscardedWaybillStorageTemp> selectList(DiscardedPackageStorageTempQo query) {
        return this.getSqlSession().selectList(DiscardedWaybillStorageTempDao.NAMESPACE + ".selectList", query);
    }

    /**
     * 按主键查询
     * @param id 主键ID
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public DiscardedWaybillStorageTemp selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne(DiscardedWaybillStorageTempDao.NAMESPACE + ".selectByPrimaryKey", id);
    }

    /**
     * 新增
     * @param model 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-03-31 11:32:59 周三
     */
    public int insertSelective(DiscardedWaybillStorageTemp model) {
        return this.getSqlSession().insert(DiscardedWaybillStorageTempDao.NAMESPACE + ".insertSelective", model);
    }

    /**
     * 批量新增
     * @param listParam
     * @return
     */
    public int batchInsert(List<DiscardedWaybillStorageTemp> listParam) {
        return this.getSqlSession().insert(DiscardedWaybillStorageTempDao.NAMESPACE + ".batchInsert", listParam);
    }

    /**
     * 按主键更新
     * @param model 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-03-31 11:32:59 周三
     */
    public int updateByWaybillCode(DiscardedWaybillStorageTemp model) {
        return this.getSqlSession().update(DiscardedWaybillStorageTempDao.NAMESPACE + ".updateByWaybillCode", model);
    }

    /**
     * 批量更新
     * @param listParam
     * @return
     */
    public int batchUpdate(List<DiscardedWaybillStorageTemp> listParam) {
        return this.getSqlSession().update(DiscardedWaybillStorageTempDao.NAMESPACE + ".batchUpdate", listParam);
    }

    /**
     * 按主键删除
     * @param model 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2020-07-02 16:27:55 周四
     */
    public int deleteByPrimaryKey(DiscardedWaybillStorageTemp model) {
        return this.getSqlSession().update(DiscardedWaybillStorageTempDao.NAMESPACE + ".deleteByPrimaryKey", model);
    }
    /**
     * 按主键更新
     * @param model 参数
     * @return 数据列表
     */
    public int updateById(DiscardedWaybillStorageTemp model) {
        return this.getSqlSession().update(DiscardedWaybillStorageTempDao.NAMESPACE + ".updateById", model);
    }

    /**
     * 查询未提交已扫描的弃件扫描数据
     * @param paramObj 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-12-06 21:57:48 周一
     */
    public List<DiscardedWaybillScanResultItemDto> selectUnSubmitDiscardedWaybillList(UnSubmitDiscardedListQo paramObj) {
        return this.getSqlSession().selectList(DiscardedWaybillStorageTempDao.NAMESPACE + ".selectUnSubmitDiscardedWaybillList", paramObj);
    }

    /**
     * 查询未扫描全部包裹的数据
     * @param paramObj 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-12-06 21:57:48 周一
     */
    public List<DiscardedWaybillScanResultItemDto> selectUnFinishScanDiscardedWaybillList(UnFinishScanDiscardedPackageQo paramObj){
        return this.getSqlSession().selectList(DiscardedWaybillStorageTempDao.NAMESPACE + ".selectUnFinishScanDiscardedWaybillList", paramObj);
    }

    /**
     * 查询扫描完成、未完成统计数
     * @param paramObj 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-12-06 21:57:48 周一
     */
    public DiscardedPackageFinishStatisticsDto selectDiscardedPackageFinishStatistics(UnSubmitDiscardedListQo paramObj){
        return this.getSqlSession().selectOne(DiscardedWaybillStorageTempDao.NAMESPACE + ".selectDiscardedPackageFinishStatistics", paramObj);
    }

    /**
     * 完成提交
     * @param updateObj 更新数据
     * @author fanggang
     * @time 2021-12-06 21:57:48 周一
     */
    public int finishSubmitDiscarded(FinishSubmitDiscardedUo updateObj){
        return this.getSqlSession().update(DiscardedWaybillStorageTempDao.NAMESPACE + ".finishSubmitDiscarded", updateObj);
    }
}
