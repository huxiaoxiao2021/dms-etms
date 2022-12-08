package com.jd.bluedragon.distribution.goodsPhoto.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;
import com.jd.bluedragon.distribution.goodsPhoto.domain.GoodsPhotoInfo;

import java.util.List;

public class GoodsPhotoDao extends BaseDao<GoodsPhotoInfo> {
    public static final String namespace = GoodsPhotoDao.class.getName();

    public int deleteByPrimaryKey(Long id){
        return this.getSqlSession().delete(namespace+".deleteByPrimaryKey", id);
    }

    public int insert(GoodsPhotoInfo record){
        return this.getSqlSession().insert(namespace+".insert", record);
    }


    public int insertSelective(GoodsPhotoInfo record){
        return this.getSqlSession().insert(namespace+".insertSelective", record);
    }

    public GoodsPhotoInfo selectByBarCode(String barCode ){
        return this.getSqlSession().selectOne(namespace+".selectByBarCode", barCode);
    }

    public GoodsPhotoInfo selectOneBySiteCodeAndBarCode(GoodsPhotoInfo info){
        return this.getSqlSession().selectOne(namespace+".selectByBarCode",info);
    }

}