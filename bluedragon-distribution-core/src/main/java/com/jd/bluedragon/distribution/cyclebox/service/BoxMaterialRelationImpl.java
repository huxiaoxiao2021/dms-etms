package com.jd.bluedragon.distribution.cyclebox.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.distribution.box.constants.BoxMaterialBindFlagEnum;
import com.jd.bluedragon.distribution.cyclebox.dao.BoxMaterialRelationDao;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import com.jd.bluedragon.dms.utils.DmsMessageConstants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ql.dms.common.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service("boxMaterialRelationService")
public class BoxMaterialRelationImpl implements BoxMaterialRelationService {

    @Autowired
    @Qualifier("boxMaterialRelationDao")
    private BoxMaterialRelationDao boxMaterialRelationDao;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    private static final Logger log = LoggerFactory.getLogger(BoxMaterialRelationImpl.class);

    /**
     * 新增
     * @param mode
     * @return
     */
    @Override
    public int add(BoxMaterialRelation mode){
        return boxMaterialRelationDao.add(mode);
    }

    /**
     * 更新
     * @param mode
     * @return
     */
    @Override
    public int update(BoxMaterialRelation mode){
        return boxMaterialRelationDao.update(mode);
    }

    /**
     * 根据箱号获取数据条数
     * @param boxCode
     * @return
     */
    @Override
    public int getCountByBoxCode(String boxCode){
        return boxMaterialRelationDao.getCountByBoxCode(boxCode);
    }

    /**
     * 根据箱号查询单条数据
     * @param boxCode
     * @return
     */
    @Override
    public BoxMaterialRelation getDataByBoxCode(String boxCode){
        return boxMaterialRelationDao.getDataByBoxCode(boxCode);
    }

    @Override
    public BoxMaterialRelation getDataByMaterialCode(String materialCode) {
        return boxMaterialRelationDao.getDataByMaterialCode(materialCode);
    }

    /**
     * 根据集包袋编号查询固定条数数据
     * @param materialCode
     * @return
     */
    @Override
    public List<BoxMaterialRelation> getLimitDataByMaterialCode(String materialCode, Integer limit) {
        return boxMaterialRelationDao.getLimitDataByMaterialCode(materialCode,limit);
    }

    /**
     * 除了当前箱号，其他和此集包袋的绑定关系都解绑
     * @param boxMaterialRelation
     * @return
     */
    @Override
    public int updateUnBindByMaterialCode(BoxMaterialRelation boxMaterialRelation) {
        return boxMaterialRelationDao.updateUnBindByMaterialCode(boxMaterialRelation);
    }
    /**
     * 解绑此箱号和集包袋绑定关系
     * @param boxMaterialRelation
     * @return
     */
    @Override
    public int updateUnBindByMaterialCodeAndBoxCode(BoxMaterialRelation boxMaterialRelation) {
        return boxMaterialRelationDao.updateUnBindByMaterialCodeAndBoxCode(boxMaterialRelation);
    }


    @Override
    public BoxMaterialRelation getBoxMaterialRelationByMaterialCodeAndBoxcode(String boxCode, String materialCode){
        BoxMaterialRelation po = new BoxMaterialRelation();
        po.setBoxCode(boxCode);
        po.setMaterialCode(materialCode);
        return boxMaterialRelationDao.getDataByBean(po);
    }

    @Override
    public List<BoxMaterialRelation> findByMaterialCodeAndBoxCode(Map<String, Object> map) {
        return boxMaterialRelationDao.findByMaterialCodeAndBoxCode(map);
    }

    @Override
    public int countByMaterialCodeAndBoxCode(Map<String, Object> map) {
        return boxMaterialRelationDao.countByMaterialCodeAndBoxCode(map);
    }

    @Override
    public List<BoxMaterialRelation> getDataByBoxCodeList(List<String> boxCodeList) {
        return boxMaterialRelationDao.getDataByBoxCodeList(boxCodeList);
    }

    /**
     * @param boxMaterialRelation 绑定关系入参
     * @return 绑定结果包装类
     * @author fanggang7
     * @time 2024-02-23 17:35:49 周五
     */
    @Override
    @Transactional(value = "business_support", propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public Result<Boolean> upsertBoxMaterialRelationBind(BoxMaterialRelation boxMaterialRelation) {
        log.info("BoxMaterialRelationImpl.upsertBoxMaterialRelation param {}", JsonHelper.toJson(boxMaterialRelation));
        Result<Boolean> result = Result.success();

        String nxKey = CacheKeyConstants.BOX_BIND_MATERIAL_KEY + boxMaterialRelation.getBoxCode() + Constants.SEPARATOR_COLON + boxMaterialRelation.getBoxCode();
        boolean setKeySuccess = jimdbCacheService.setNx(nxKey, "lock", CacheKeyConstants.BOX_BIND_MATERIAL_LOCK_TIME, TimeUnit.SECONDS);
        try {
            if (!setKeySuccess) {
                if (log.isWarnEnabled()) {
                    log.warn("BoxMaterialRelationImpl.upsertBoxMaterialRelation 获得绑定Redis锁失败 param {}", JsonHelper.toJson(boxMaterialRelation));
                }
                result.toFail("箱号绑定物资获取锁失败，请稍后重试");
            }

            final BoxMaterialRelation boxMaterialRelationExist = boxMaterialRelationDao.getDataByBoxCode(boxMaterialRelation.getBoxCode());
            boolean needInsertFlag = false;
            // 如果已存在，则判断是否是相同物资
            if (boxMaterialRelationExist != null) {
                // 如果相同，则不做处理
                if(Objects.equals(boxMaterialRelationExist.getBindFlag(), BoxMaterialBindFlagEnum.BIND.getCode())
                        && Objects.equals(boxMaterialRelationExist.getSiteCode(), boxMaterialRelation.getSiteCode())
                        && Objects.equals(boxMaterialRelationExist.getMaterialCode(), boxMaterialRelation.getMaterialCode())){
                    return result;
                } else {
                    needInsertFlag = true;
                    // 解绑箱号原绑定关系
                    BoxMaterialRelation boxMaterialRelationUnbind = new BoxMaterialRelation();
                    boxMaterialRelationUnbind.setBoxCode(boxMaterialRelationExist.getBoxCode());
                    boxMaterialRelationUnbind.setBindFlag(BoxMaterialBindFlagEnum.UNBIND.getCode());
                    boxMaterialRelationDao.update(boxMaterialRelationUnbind);
                }
            } else {
                needInsertFlag = true;
            }

            // 新增绑定关系
            if(needInsertFlag){
                boxMaterialRelationDao.add(boxMaterialRelation);
            }
        } catch (Exception e) {
            result.toFail("系统异常");
            log.error("BoxMaterialRelationImpl.upsertBoxMaterialRelation exception param {}", JsonHelper.toJson(boxMaterialRelation), e);
        } finally {
            jimdbCacheService.del(nxKey);
        }

        return result;
    }
}
