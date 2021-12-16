package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedPackageScanResultItemDto;
import com.jd.bluedragon.common.dto.wastepackagestorage.dto.DiscardedWaybillScanResultItemDto;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.DiscardedPackageStorageTempQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.UnFinishScanDiscardedPackageQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto.UnSubmitDiscardedListQo;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;
import com.jd.bluedragon.distribution.discardedPackageStorageTemp.vo.DiscardedPackageStorageTempVo;

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
    public List<DiscardedPackageStorageTempVo> selectList(DiscardedPackageStorageTempQo query) {
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
     * 按包裹号更新
     * @param discardedPackageStorageTemp 参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-03-31 11:32:59 周三
     */
    public int updateByPackageCode(DiscardedPackageStorageTemp discardedPackageStorageTemp) {
        return this.getSqlSession().update(DiscardedPackageStorageTempDao.NAMESPACE + ".updateByPackageCode", discardedPackageStorageTemp);
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

    /**
     * 查询未提交已扫描的弃件扫描数据
     * @param paramObj 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-12-06 21:57:48 周一
     */
    public List<DiscardedWaybillScanResultItemDto> selectUnSubmitDiscardedPackageList(UnSubmitDiscardedListQo paramObj) {
        return this.getSqlSession().selectList(DiscardedPackageStorageTempDao.NAMESPACE + ".selectUnSubmitDiscardedPackageList", paramObj);
    }

    /**
     * 查询未提交已扫描的弃件扫描总数
     * @param paramObj 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-12-06 21:57:48 周一
     */
    public int selectUnSubmitDiscardedPackageCount(UnSubmitDiscardedListQo paramObj) {
        return this.getSqlSession().selectOne(DiscardedPackageStorageTempDao.NAMESPACE + ".selectUnSubmitDiscardedPackageCount", paramObj);
    }

    /**
     * 查询未扫描全部包裹的数据
     * @param paramObj 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-12-06 21:57:48 周一
     */
    public long selectUnFinishScanDiscardedPackageCount(UnFinishScanDiscardedPackageQo paramObj){
        return this.getSqlSession().selectOne(DiscardedPackageStorageTempDao.NAMESPACE + ".selectUnFinishScanDiscardedPackageCount", paramObj);
    }

    /**
     * 查询未扫描全部包裹的数据
     * @param paramObj 查询参数
     * @return 数据列表
     * @author fanggang7
     * @date 2021-12-06 21:57:48 周一
     */
    public List<DiscardedPackageScanResultItemDto> selectUnFinishScanDiscardedPackageList(UnFinishScanDiscardedPackageQo paramObj){
        return this.getSqlSession().selectList(DiscardedPackageStorageTempDao.NAMESPACE + ".selectUnFinishScanDiscardedPackageList", paramObj);
    }

}
