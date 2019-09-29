package com.jd.bluedragon.distribution.cyclebox.service;

import com.jd.bluedragon.distribution.cyclebox.dao.BoxMaterialRelationDao;
import com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("boxMaterialRelationService")
public class BoxMaterialRelationImpl implements BoxMaterialRelationService {

    @Autowired
    @Qualifier("boxMaterialRelationDao")
    private BoxMaterialRelationDao boxMaterialRelationDao;

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
}
